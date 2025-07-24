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
            LocalDate deliveryLocalDate = deliveryDate.toLocalDate();
            LocalDate today = LocalDate.now();
            
            if (deliveryLocalDate.isBefore(today)) {
                throw new Exception("Delivery date cannot be in the past. Please select today or a future date.");
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
            String[] statuses = request.getParameterValues("statuses[]");
            if (materialIds == null || quantities == null || statuses == null ||
                materialIds.length == 0 || quantities.length == 0 || statuses.length == 0) {
                throw new Exception("At least one material is required.");
            }
            java.util.Map<Integer, Integer> materialQuantityMap = new java.util.HashMap<>();
            List<ExportRequestDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                String status = statuses[i];
                if (quantity <= 0) {
                    throw new Exception("Quantity must be positive.");
                }
                if (!List.of("new", "used", "damaged").contains(status)) {
                    throw new Exception("Invalid material status.");
                }
                materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, 0) + quantity);
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setStatus(status);
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
                // Gửi email thông báo cho giám đốc
                try {
                    sendExportRequestNotification(exportRequest, details, user);
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi gửi email thông báo export request: " + e.getMessage());
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            } else {
                request.setAttribute("error", "Failed to create export request.");
                doGet(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    private void sendExportRequestNotification(ExportRequest exportRequest, 
                                            List<ExportRequestDetail> details, 
                                            User creator) {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            List<User> directors = new ArrayList<>();
            
            // Lấy danh sách giám đốc (roleId = 2)
            for (User u : allUsers) {
                if (u.getRoleId() == 2) {
                    directors.add(u);
                }
            }
            
            // Tạo nội dung email
            String subject = "[Thông báo] Export Request mới đã được tạo";
            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>Export Request mới đã được tạo</h2>");
            content.append("<p><strong>Mã Export Request:</strong> ").append(exportRequest.getRequestCode()).append("</p>");
            content.append("<p><strong>Người tạo:</strong> ").append(creator.getFullName()).append(" (ID: ").append(creator.getUserId()).append(")</p>");
            content.append("<p><strong>Ngày giao hàng:</strong> ").append(exportRequest.getDeliveryDate()).append("</p>");
            content.append("<p><strong>Lý do:</strong> ").append(exportRequest.getReason()).append("</p>");
            content.append("<p><strong>Thời gian tạo:</strong> ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</p>");
            
            content.append("<h3>Chi tiết vật tư:</h3>");
            content.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            content.append("<tr><th>Vật tư</th><th>Số lượng</th><th>Tình trạng</th></tr>");
            
            for (ExportRequestDetail detail : details) {
                Material material = materialDAO.getInformation(detail.getMaterialId());
                String materialName = (material != null) ? material.getMaterialName() : "Unknown Material";
                content.append("<tr>");
                content.append("<td>").append(materialName).append("</td>");
                content.append("<td>").append(detail.getQuantity()).append("</td>");
                content.append("<td>").append(detail.getStatus()).append("</td>");
                content.append("</tr>");
            }
            content.append("</table>");
            content.append("<p>Vui lòng đăng nhập hệ thống để xem chi tiết và xử lý export request.</p>");
            content.append("</body></html>");
            
            // Chỉ gửi email cho giám đốc
            for (User director : directors) {
                if (director.getEmail() != null && !director.getEmail().trim().isEmpty()) {
                    try {
                        EmailUtils.sendEmail(director.getEmail(), subject, content.toString());
                        System.out.println("✅ Đã gửi email thông báo export request cho giám đốc: " + director.getEmail());
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi gửi email cho giám đốc " + director.getEmail() + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email thông báo export request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateRequestCode() {
        String prefix = "ER";
        String sql = "SELECT request_code FROM Export_Requests WHERE request_code LIKE ? ORDER BY request_code DESC LIMIT 1";
        String likePattern = prefix + "%";
        try (java.sql.Connection conn = exportRequestDAO.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                if (rs.next()) {
                    String lastCode = rs.getString("request_code");
                    String numberPart = lastCode.replace(prefix, "");
                    try {
                        nextSeq = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException ignore) {}
                }
                return prefix + String.format("%03d", nextSeq);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "001";
        }
    }
}