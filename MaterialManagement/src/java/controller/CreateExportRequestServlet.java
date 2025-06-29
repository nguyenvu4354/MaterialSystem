package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.MaterialDAO;
import dal.UserDAO;
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

    @Override
    public void init() throws ServletException {
        try {
            exportRequestDAO = new ExportRequestDAO();
            exportRequestDetailDAO = new ExportRequestDetailDAO();
            materialDAO = new MaterialDAO();
            userDAO = new UserDAO();
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: Entering CreateExportRequestServlet.doGet");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            System.out.println("DEBUG: User not logged in, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        int roleId = user.getRoleId();
        if (roleId != 4) {
            System.out.println("DEBUG: Unauthorized access for role_id " + roleId + ", redirecting to HomePage.jsp");
            response.sendRedirect("HomePage.jsp");
            return;
        }

        try {
            String requestCode = generateRequestCode();
            System.out.println("DEBUG: Generated requestCode: " + requestCode);
            request.setAttribute("requestCode", requestCode);

            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            System.out.println("DEBUG: Loaded " + materials.size() + " materials");
            request.setAttribute("materials", materials);

            List<User> staffUsers = userDAO.getUsersByRoleId(3);
            System.out.println("DEBUG: Loaded " + staffUsers.size() + " staff users");
            request.setAttribute("users", staffUsers);

            System.out.println("DEBUG: Forwarding to CreateExportRequest.jsp");
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("ERROR: Exception in doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("materials", new ArrayList<Material>());
            request.setAttribute("users", new ArrayList<User>());
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: Entering CreateExportRequestServlet.doPost");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            System.out.println("DEBUG: User not logged in, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        int roleId = user.getRoleId();
        if (roleId != 4) {
            System.out.println("DEBUG: Unauthorized access for role_id " + roleId + ", redirecting to HomePage.jsp");
            response.sendRedirect("HomePage.jsp");
            return;
        }

        try {
            String requestCode = request.getParameter("requestCode");
            String deliveryDateStr = request.getParameter("deliveryDate");
            String reason = request.getParameter("reason");
            String recipientIdStr = request.getParameter("recipientId");

            System.out.println("DEBUG: requestCode=" + requestCode + ", deliveryDate=" + deliveryDateStr + ", reason=" + reason + ", recipientId=" + recipientIdStr);

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
                if (recipient == null) {
                    throw new Exception("Invalid recipient selected.");
                }
                if (recipient.getRoleId() != 3) {
                    throw new Exception("Recipient must be a staff member.");
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

            System.out.println("DEBUG: Form parameters:");
            System.out.println("DEBUG: materialIds=" + (materialIds != null ? String.join(",", materialIds) : "null"));
            System.out.println("DEBUG: quantities=" + (quantities != null ? String.join(",", quantities) : "null"));
            System.out.println("DEBUG: conditions=" + (conditions != null ? String.join(",", conditions) : "null"));

            if (materialIds == null || quantities == null || conditions == null ||
                materialIds.length == 0 || quantities.length == 0 || conditions.length == 0) {
                throw new Exception("At least one material is required.");
            }

            // Cộng dồn quantity cho từng materialId
            java.util.Map<Integer, Integer> materialQuantityMap = new java.util.HashMap<>();
            List<ExportRequestDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                String condition = conditions[i];

                System.out.println("DEBUG: Material " + i + ": ID=" + materialId + ", Quantity=" + quantity + ", Condition=" + condition);

                if (quantity <= 0) {
                    throw new Exception("Quantity must be positive.");
                }
                if (!List.of("new", "used", "refurbished").contains(condition)) {
                    throw new Exception("Invalid export condition.");
                }

                // Cộng dồn quantity cho từng materialId
                materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, 0) + quantity);

                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setExportCondition(condition);
                details.add(detail);
            }

            // Kiểm tra tồn kho cho từng materialId
            for (Integer materialId : materialQuantityMap.keySet()) {
                int totalQuantity = materialQuantityMap.get(materialId);
                entity.Material material = materialDAO.getInformation(materialId);
                int stock = material.getQuantity();
                if (totalQuantity > stock) {
                    throw new Exception("Material '" + material.getMaterialName() + "' only has " + stock + " in stock, but requested total is " + totalQuantity + ".");
                }
            }

            System.out.println("DEBUG: Export Request details:");
            System.out.println("DEBUG: requestCode=" + exportRequest.getRequestCode());
            System.out.println("DEBUG: userId=" + exportRequest.getUserId());
            System.out.println("DEBUG: recipientId=" + exportRequest.getRecipientId());
            System.out.println("DEBUG: status=" + exportRequest.getStatus());
            System.out.println("DEBUG: deliveryDate=" + exportRequest.getDeliveryDate());
            System.out.println("DEBUG: reason=" + exportRequest.getReason());
            System.out.println("DEBUG: Number of details=" + details.size());

            boolean success = exportRequestDAO.add(exportRequest, details);
            System.out.println("DEBUG: Export request creation " + (success ? "successful" : "failed"));

            if (success) {
                // Gửi email cho giám đốc
                List<User> allUsers = userDAO.getAllUsers();
                List<User> directors = new ArrayList<>();
                for (User u : allUsers) {
                    if (u.getRoleId() == 2) {
                        directors.add(u);
                    }
                }
                if (!directors.isEmpty()) {
                    String subject = "[Notification] A new export request has been created";
                    StringBuilder content = new StringBuilder();
                    content.append("Dear Director,<br><br>");
                    content.append("A new export request has just been created.<br>");
                    content.append("<b>Request Code:</b> ").append(exportRequest.getRequestCode()).append("<br>");
                    content.append("<b>Creator:</b> ").append(user.getFullName()).append(" (ID: ").append(user.getUserId()).append(")<br>");
                    content.append("<b>Delivery Date:</b> ").append(exportRequest.getDeliveryDate()).append("<br>");
                    content.append("<b>Reason:</b> ").append(exportRequest.getReason()).append("<br>");
                    content.append("<br>Please log in to the system to view details and approve.<br>");
                    for (User director : directors) {
                        if (director.getEmail() != null && !director.getEmail().trim().isEmpty()) {
                            try {
                                utils.EmailUtils.sendEmail(director.getEmail(), subject, content.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/HomePage.jsp");
            } else {
                throw new Exception("Failed to create export request.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Exception in doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("requestCode", request.getParameter("requestCode"));
            request.setAttribute("deliveryDate", request.getParameter("deliveryDate"));
            request.setAttribute("reason", request.getParameter("reason"));
            request.setAttribute("recipientId", request.getParameter("recipientId"));
            request.setAttribute("materialIds", request.getParameterValues("materials[]"));
            request.setAttribute("quantities", request.getParameterValues("quantities[]"));
            request.setAttribute("conditions", request.getParameterValues("conditions[]"));
            try {
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                System.out.println("DEBUG: Loaded " + materials.size() + " materials in error case");
                request.setAttribute("materials", materials);
                List<User> staffUsers = userDAO.getUsersByRoleId(3);
                System.out.println("DEBUG: Loaded " + staffUsers.size() + " staff users in error case");
                request.setAttribute("users", staffUsers);
            } catch (Exception ex) {
                System.out.println("ERROR: Failed to load materials/users in error case: " + ex.getMessage());
                ex.printStackTrace();
                request.setAttribute("materials", new ArrayList<Material>());
                request.setAttribute("users", new ArrayList<User>());
            }
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
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