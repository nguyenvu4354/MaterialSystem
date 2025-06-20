package controller;

import dal.ImportDAO;
import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Material;
import entity.Supplier;
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
import java.util.*;
import java.util.UUID;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {
    private ImportDAO importDAO;
    private SupplierDAO supplierDAO;
    private MaterialDAO materialDAO;
    private InventoryDAO inventoryDAO;

    @Override
    public void init() throws ServletException {
        importDAO = new ImportDAO();
        supplierDAO = new SupplierDAO();
        materialDAO = new MaterialDAO();
        inventoryDAO = new InventoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadDataAndForward(request, response);
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
            } else if ("import".equals(action)) {
                handleImport(request, response, session, user);
            } else if ("remove".equals(action)) {
                handleRemoveMaterial(request, response, session);
            } else if ("update".equals(action)) {
                handleUpdateQuantity(request, response, session);
            } else {
                request.setAttribute("error", "Invalid action.");
                loadDataAndForward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            loadDataAndForward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format provided.");
            loadDataAndForward(request, response);
        }
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        String unitPriceStr = request.getParameter("unitPrice");
        String condition = request.getParameter("materialCondition");

        if (materialIdStr == null || materialIdStr.isEmpty() || 
            quantityStr == null || quantityStr.isEmpty() || 
            unitPriceStr == null || unitPriceStr.isEmpty() ||
            condition == null || condition.isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            loadDataAndForward(request, response);
            return;
        }
        
        int materialId = Integer.parseInt(materialIdStr);
        int quantity = Integer.parseInt(quantityStr);
        double unitPrice = Double.parseDouble(unitPriceStr);

        if (quantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response);
            return;
        }

        if (unitPrice < 0) {
            request.setAttribute("error", "Unit price cannot be negative.");
            loadDataAndForward(request, response);
            return;
        }

        Integer tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == null || tempImportId == 0) {
            Import imports = new Import();
            imports.setImportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
            imports.setImportDate(LocalDateTime.now());
            imports.setImportedBy(user.getUserId());
            tempImportId = importDAO.createImport(imports);
            session.setAttribute("tempImportId", tempImportId);
        }

        ImportDetail detail = new ImportDetail();
        detail.setImportId(tempImportId);
        detail.setMaterialId(materialId);
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setMaterialCondition(condition);
        detail.setStatus("draft");

        importDAO.createImportDetails(Collections.singletonList(detail));
        request.setAttribute("success", "Material added to import list successfully.");
        loadDataAndForward(request, response);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String materialCondition = request.getParameter("materialCondition");
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");

        if (tempImportId != null && tempImportId != 0) {
            importDAO.removeImportDetail(tempImportId, materialId, quantity, materialCondition);
            request.setAttribute("success", "Material removed from import list successfully.");
        } else {
            request.setAttribute("error", "No import list found to remove material from.");
        }
        loadDataAndForward(request, response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int newQuantity = Integer.parseInt(request.getParameter("newQuantity"));
        String materialCondition = request.getParameter("materialCondition");
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");

        if (newQuantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response);
            return;
        }

        if (tempImportId != null && tempImportId != 0) {
            ImportDetail detail = importDAO.getImportDetailByMaterialAndCondition(tempImportId, materialId, materialCondition);
            if (detail != null) {
                importDAO.updateImportDetailQuantity(detail.getImportDetailId(), newQuantity);
                request.setAttribute("success", "Quantity updated successfully for material ID: " + materialId);
            } else {
                request.setAttribute("error", "Material not found in import list.");
            }
        } else {
            request.setAttribute("error", "No import list found to update.");
        }
        loadDataAndForward(request, response);
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == null || tempImportId == 0) {
            request.setAttribute("error", "No materials selected for import.");
            loadDataAndForward(request, response);
            return;
        }

        List<ImportDetail> details = importDAO.getDraftImportDetails(tempImportId);
        if (details.isEmpty()) {
            request.setAttribute("error", "Import list is empty.");
            loadDataAndForward(request, response);
            return;
        }

        Integer supplierId = request.getParameter("supplierId") != null && !request.getParameter("supplierId").isEmpty()
                ? Integer.parseInt(request.getParameter("supplierId")) : null;
        String destination = request.getParameter("destination");
        String batchNumber = request.getParameter("batchNumber");
        String actualArrivalStr = request.getParameter("actualArrival");
        String note = request.getParameter("note");
        
        if (supplierId == null || destination == null || destination.trim().isEmpty() || 
            batchNumber == null || batchNumber.trim().isEmpty() || 
            actualArrivalStr == null || actualArrivalStr.isEmpty()) {
            request.setAttribute("error", "Please fill all required fields in the confirmation form.");
            loadDataAndForward(request, response);
            return;
        }

        LocalDateTime actualArrival;
        try {
            actualArrival = LocalDateTime.parse(actualArrivalStr);
        } catch (Exception e) {
            request.setAttribute("error", "Invalid actual arrival date format.");
            loadDataAndForward(request, response);
            return;
        }

        Import imports = new Import();
        imports.setImportId(tempImportId);
        imports.setImportCode("IMP-" + UUID.randomUUID().toString().substring(0, 8));
        imports.setImportDate(LocalDateTime.now());
        imports.setImportedBy(user.getUserId());
        imports.setSupplierId(supplierId);
        imports.setDestination(destination);
        imports.setBatchNumber(batchNumber);
        imports.setActualArrival(actualArrival);
        imports.setNote(note);

        importDAO.updateImport(imports);
        importDAO.confirmImport(tempImportId);
        importDAO.updateInventoryByImportId(tempImportId, user.getUserId());

        session.setAttribute("tempImportId", 0);
        request.setAttribute("success", "Import completed successfully with code: " + imports.getImportCode());
        loadDataAndForward(request, response);
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // 1. Load primary data for dropdowns and display
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            List<Material> allMaterials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
            
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("materials", allMaterials);

            // 2. Load the temporary import details from session
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            if (tempImportId == null) {
                tempImportId = 0;
                session.setAttribute("tempImportId", 0);
            }

            List<ImportDetail> importDetails = (tempImportId != 0)
                    ? importDAO.getDraftImportDetails(tempImportId)
                    : new ArrayList<>();
            request.setAttribute("importDetails", importDetails);

            // 3. Create necessary maps for the JSP to render details efficiently
            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material m : allMaterials) {
                materialMap.put(m.getMaterialId(), m);
            }
            
            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ImportDetail detail : importDetails) {
                stockMap.put(detail.getMaterialId(), inventoryDAO.getStockByMaterialId(detail.getMaterialId()));
            }

            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);

        } catch (SQLException e) {
            // Set a detailed error message for better debugging
            request.setAttribute("error", "Error loading page data: " + e.getMessage());
            e.printStackTrace(); 
        }
        request.getRequestDispatcher("/ImportMaterial.jsp").forward(request, response);
    }
} 