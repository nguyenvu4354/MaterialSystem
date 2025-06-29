package controller;

import dal.DepartmentDAO;
import dal.ExportDAO;
import dal.MaterialDAO;
import dal.InventoryDAO;
import dal.UserDAO;
import entity.Department;
import entity.Export;
import entity.ExportDetail;
import entity.Material;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "ExportMaterialServlet", urlPatterns = {"/ExportMaterial"})
public class ExportMaterialServlet extends HttpServlet {

    private ExportDAO exportDAO;
    private DepartmentDAO departmentDAO;
    private InventoryDAO inventoryDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        exportDAO = new ExportDAO();
        departmentDAO = new DepartmentDAO();
        inventoryDAO = new InventoryDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            List<Department> departments = departmentDAO.getDepartments();
            List<Material> materials = departmentDAO.getMaterials();
            List<User> users = userDAO.getAllUsers();
            Integer tempExportId = (Integer) session.getAttribute("tempExportId");
            List<ExportDetail> exportDetails = (tempExportId != null && tempExportId != 0) 
                    ? exportDAO.getDraftExportDetails(tempExportId) 
                    : new ArrayList<>();

            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material material : materials) {
                materialMap.put(material.getMaterialId(), material);
            }

            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ExportDetail detail : exportDetails) {
                int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
                stockMap.put(detail.getMaterialId(), stock);
            }

            request.setAttribute("departments", departments);
            request.setAttribute("materials", materials);
            request.setAttribute("users", users);
            request.setAttribute("exportDetails", exportDetails);
            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);

            if (tempExportId == null) {
                session.setAttribute("tempExportId", 0);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving data: " + e.getMessage());
        }
        request.getRequestDispatcher("/ExportMaterial.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                handleAddMaterial(request, response, session, user);
            } else if ("export".equals(action)) {
                handleExport(request, response, session, user);
            } else if ("remove".equals(action)) {
                handleRemoveMaterial(request, response, session);
            } else if ("updateQuantity".equals(action)) {
                handleUpdateQuantity(request, response, session);
            } else {
                request.setAttribute("error", "Invalid action.");
                doGet(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input data: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        String condition = request.getParameter("materialCondition");
        List<ExportDetail> detailsToAdd = new ArrayList<>();
        List<Material> materials = departmentDAO.getMaterials();
        Map<Integer, Material> materialMap = new HashMap<>();
        for (Material material : materials) {
            materialMap.put(material.getMaterialId(), material);
        }

        if (materialIdStr == null || materialIdStr.isEmpty() || 
            quantityStr == null || quantityStr.isEmpty() || 
            condition == null || condition.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields.");
            loadDataAndForward(request, response);
            return;
        }

        try {
            int materialId = Integer.parseInt(materialIdStr);
            int quantity = Integer.parseInt(quantityStr);

            if (quantity <= 0) {
                request.setAttribute("error", "Quantity must be greater than 0.");
                loadDataAndForward(request, response);
                return;
            }

            int currentStock = inventoryDAO.getStockByMaterialId(materialId);
            if (currentStock < quantity) {
                request.setAttribute("error", "Insufficient stock for material ID: " + materialId + ". Available: " + currentStock);
                loadDataAndForward(request, response);
                return;
            }

            if (!materialMap.containsKey(materialId)) {
                request.setAttribute("error", "Selected material is invalid.");
                loadDataAndForward(request, response);
                return;
            }

            ExportDetail detail = new ExportDetail();
            detail.setMaterialId(materialId);
            detail.setQuantity(quantity);
            detail.setMaterialCondition(condition);
            detail.setStatus("draft");
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());
            detailsToAdd.add(detail);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for material ID or quantity.");
            loadDataAndForward(request, response);
            return;
        }

        int tempExportId = (int) session.getAttribute("tempExportId");
        if (tempExportId == 0) {
            Export export = new Export();
            export.setExportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
            export.setExportDate(LocalDateTime.now());
            export.setExportedBy(user.getUserId());
            export.setRecipientUserId(user.getUserId()); // Default to current user, will be updated
            export.setCreatedAt(LocalDateTime.now());
            export.setUpdatedAt(LocalDateTime.now());
            tempExportId = exportDAO.createExport(export);
            session.setAttribute("tempExportId", tempExportId);
        }

        for (ExportDetail detail : detailsToAdd) {
            detail.setExportId(tempExportId);
        }
        exportDAO.createExportDetails(detailsToAdd);
        request.setAttribute("success", "Material added to export list.");
        loadDataAndForward(request, response);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String materialCondition = request.getParameter("materialCondition");
        int tempExportId = (int) session.getAttribute("tempExportId");

        exportDAO.removeExportDetail(tempExportId, materialId, quantity, materialCondition);
        request.setAttribute("success", "Material removed from export list.");
        loadDataAndForward(request, response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String materialCondition = request.getParameter("materialCondition");
        int tempExportId = (int) session.getAttribute("tempExportId");

        if (quantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response);
            return;
        }

        int currentStock = inventoryDAO.getStockByMaterialId(materialId);
        if (currentStock < quantity) {
            request.setAttribute("error", "Insufficient stock for material ID: " + materialId + ". Available: " + currentStock);
            loadDataAndForward(request, response);
            return;
        }

        ExportDetail existingDetail = exportDAO.getExportDetailByMaterialAndCondition(tempExportId, materialId, materialCondition);
        if (existingDetail == null) {
            request.setAttribute("error", "Export detail not found for material ID: " + materialId);
            loadDataAndForward(request, response);
            return;
        }

        exportDAO.updateExportDetailQuantity(existingDetail.getExportDetailId(), quantity);
        request.setAttribute("success", "Quantity updated successfully for material ID: " + materialId);
        loadDataAndForward(request, response);
    }

    private void handleExport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        int tempExportId = (int) session.getAttribute("tempExportId");
        if (tempExportId == 0)  {
            request.setAttribute("error", "No materials selected for export.");
            loadDataAndForward(request, response);
            return;
        }

        List<ExportDetail> details = exportDAO.getDraftExportDetails(tempExportId);
        if (details.isEmpty()) {
            request.setAttribute("error", "Export list is empty.");
            loadDataAndForward(request, response);
            return;
        }

        for (ExportDetail detail : details) {
            int currentStock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
            if (currentStock < detail.getQuantity()) {
                request.setAttribute("error", "Insufficient stock for material ID: " + detail.getMaterialId() + ". Available: " + currentStock);
                loadDataAndForward(request, response);
                return;
            }
        }

        int recipientUserId = Integer.parseInt(request.getParameter("recipientUserId"));
        String batchNumber = request.getParameter("batchNumber");
        String note = request.getParameter("note");

        Export export = new Export();
        export.setExportId(tempExportId);
        export.setExportCode("EXP-" + UUID.randomUUID().toString().substring(0, 8));
        export.setExportDate(LocalDateTime.now());
        export.setExportedBy(user.getUserId());
        export.setRecipientUserId(recipientUserId);
        export.setBatchNumber(batchNumber);
        export.setNote(note);
        export.setUpdatedAt(LocalDateTime.now());

        exportDAO.updateExport(export);
        exportDAO.confirmExport(tempExportId);
        exportDAO.updateInventoryByExportId(tempExportId, user.getUserId());

        session.setAttribute("tempExportId", 0);
        request.setAttribute("success", "Export completed successfully with code: " + export.getExportCode());
        loadDataAndForward(request, response);
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Department> departments = departmentDAO.getDepartments();
            List<Material> materials = departmentDAO.getMaterials();
            List<User> users = userDAO.getAllUsers();
            Integer tempExportId = (Integer) request.getSession().getAttribute("tempExportId");
            List<ExportDetail> exportDetails = (tempExportId != null && tempExportId != 0) 
                    ? exportDAO.getDraftExportDetails(tempExportId) 
                    : new ArrayList<>();

            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material material : materials) {
                materialMap.put(material.getMaterialId(), material);
            }

            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ExportDetail detail : exportDetails) {
                int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
                stockMap.put(detail.getMaterialId(), stock);
            }

            request.setAttribute("departments", departments);
            request.setAttribute("materials", materials);
            request.setAttribute("users", users);
            request.setAttribute("exportDetails", exportDetails);
            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving data: " + e.getMessage());
        }
        request.getRequestDispatcher("/ExportMaterial.jsp").forward(request, response);
    }
}