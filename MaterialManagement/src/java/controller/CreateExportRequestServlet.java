package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.MaterialDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.Material;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import utils.EmailUtils;

@WebServlet(name = "CreateExportRequestServlet", urlPatterns = {"/CreateExportRequest"})
public class CreateExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;
    private MaterialDAO materialDAO;
    private UserDAO userDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
        materialDAO = new MaterialDAO();
        userDAO = new UserDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_EXPORT_REQUEST");
        request.setAttribute("hasCreateExportRequestPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create export requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        try {
            String requestCode = generateRequestCode();
            request.setAttribute("requestCode", requestCode);
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            request.setAttribute("materials", materials);
            List<User> staffUsers = userDAO.getUsersByRoleId(3);
            request.setAttribute("users", staffUsers);
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("materials", new ArrayList<Material>());
            request.setAttribute("users", new ArrayList<User>());
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_EXPORT_REQUEST");
        request.setAttribute("hasCreateExportRequestPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create export requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        try {
            String requestCode = request.getParameter("requestCode");
            String deliveryDateStr = request.getParameter("deliveryDate");
            String reason = request.getParameter("reason");
            String recipientIdStr = request.getParameter("recipientId");
            if (requestCode == null || requestCode.trim().isEmpty() ||
                deliveryDateStr == null || deliveryDateStr.trim().isEmpty() ||
                reason == null || reason.trim().isEmpty()) {
                throw new Exception("Request code, delivery date, and reason are required.");
            }
            Date deliveryDate = Date.valueOf(deliveryDateStr);
            if (deliveryDate.toLocalDate().isBefore(LocalDate.now())) {
                throw new Exception("Delivery date cannot be in the past.");
            }
            int recipientId = 0;
            if (recipientIdStr != null && !recipientIdStr.trim().isEmpty()) {
                recipientId = Integer.parseInt(recipientIdStr);
                User recipient = userDAO.getUserById(recipientId);
                if (recipient == null || recipient.getRoleId() != 3) {
                    throw new Exception("Recipient must be a valid staff member.");
                }
            }
            ExportRequest exportRequest = new ExportRequest();
            exportRequest.setRequestCode(requestCode);
            exportRequest.setDeliveryDate(deliveryDate);
            exportRequest.setReason(reason);
            exportRequest.setUserId(user.getUserId());
            exportRequest.setRecipientId(recipientId);
            exportRequest.setStatus("pending");
            String[] materialIds = request.getParameterValues("materials[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] conditions = request.getParameterValues("conditions[]");
            if (materialIds == null || quantities == null || conditions == null ||
                materialIds.length == 0 || quantities.length == 0 || conditions.length == 0) {
                throw new Exception("At least one material is required.");
            }
            java.util.Map<Integer, Integer> materialQuantityMap = new java.util.HashMap<>();
            List<ExportRequestDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                String condition = conditions[i];
                if (quantity <= 0) {
                    throw new Exception("Quantity must be positive.");
                }
                if (!List.of("new", "used", "refurbished").contains(condition)) {
                    throw new Exception("Invalid export condition.");
                }
                materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, 0) + quantity);
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setExportCondition(condition);
                details.add(detail);
            }
            for (Integer materialId : materialQuantityMap.keySet()) {
                int totalQuantity = materialQuantityMap.get(materialId);
                Material material = materialDAO.getInformation(materialId);
                int stock = material.getQuantity();
                if (totalQuantity > stock) {
                    throw new Exception("Not enough stock for material: " + material.getMaterialName());
                }
            }
            boolean success = exportRequestDAO.add(exportRequest, details);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
            } else {
                request.setAttribute("error", "Failed to create export request.");
                doGet(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    private String generateRequestCode() {
        String prefix = "EXP";
        String year = String.valueOf(LocalDate.now().getYear());
        String sql = "SELECT request_code FROM Export_Requests WHERE request_code LIKE ? ORDER BY request_code DESC LIMIT 1";
        String likePattern = prefix + "-" + year + "-%";
        try (java.sql.Connection conn = exportRequestDAO.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                if (rs.next()) {
                    String lastCode = rs.getString("request_code");
                    String[] parts = lastCode.split("-");
                    if (parts.length == 3) {
                        try {
                            nextSeq = Integer.parseInt(parts[2]) + 1;
                        } catch (NumberFormatException ignore) {}
                    }
                }
                return prefix + "-" + year + "-" + String.format("%03d", nextSeq);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "-" + year + "-001";
        }
    }
}