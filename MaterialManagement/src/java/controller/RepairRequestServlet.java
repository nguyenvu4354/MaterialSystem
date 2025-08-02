package controller;

import dal.MaterialDAO;
import dal.RepairRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
import entity.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.EmailUtils;
import utils.RepairRequestValidator;

import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RepairRequestServlet", urlPatterns = {"/repairrequest"})
public class RepairRequestServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_REPAIR_REQUEST")) {
            request.setAttribute("error", "You do not have permission to access the repair request creation page.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        MaterialDAO materialDAO = new MaterialDAO();
        List<Material> materialList = materialDAO.getAllProducts();

        SupplierDAO supplierDAO = new SupplierDAO();
        List<Supplier> supplierList = supplierDAO.getAllSuppliers();

        // Generate request code and date
        String requestCode = generateRequestCode();
        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

        request.setAttribute("materialList", materialList);
        request.setAttribute("supplierList", supplierList);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("requestDate", requestDate);

        // Forward đến trang tạo yêu cầu sửa chữa
        request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("Login.jsp");
                return;
            }

            if (!rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_REPAIR_REQUEST")) {
                request.setAttribute("error", "You do not have permission to create a repair request.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            int userId = user.getUserId();

            String requestCode = generateRequestCode();

            String supplierIdStr = request.getParameter("supplierId");
            String reason = request.getParameter("reason");
            String[] materialNames = request.getParameterValues("materialName");
            String[] quantities = request.getParameterValues("quantity");
            String[] damageDescriptions = request.getParameterValues("damageDescription");
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // Validate form data
            Map<String, String> formErrors = RepairRequestValidator.validateRepairRequestFormData(reason, supplierIdStr);
            Map<String, String> detailErrors = RepairRequestValidator.validateRepairRequestDetails(materialNames, quantities, damageDescriptions);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                // Preserve form data for retry
                MaterialDAO materialDAO = new MaterialDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                List<Material> materialList = materialDAO.getAllProducts();
                List<Supplier> supplierList = supplierDAO.getAllSuppliers();

                String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

                request.setAttribute("materialList", materialList);
                request.setAttribute("supplierList", supplierList);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate);
                request.setAttribute("errors", formErrors);
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedDamageDescriptions", damageDescriptions);

                request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                return;
            }

            // Parse supplierId after validation
            int supplierId = 0;
            try {
                supplierId = Integer.parseInt(supplierIdStr);
            } catch (NumberFormatException e) {
                // This will be caught by validation above
            }

            RepairRequest requestObj = new RepairRequest();
            requestObj.setRequestCode(requestCode);
            requestObj.setUserId(userId);
            requestObj.setRepairPersonPhoneNumber(null); 
            requestObj.setRepairPersonEmail(null); 
            requestObj.setRepairLocation(null); 
            requestObj.setReason(reason);
            requestObj.setRequestDate(now);
            requestObj.setStatus("Pending");
            requestObj.setCreatedAt(now);
            requestObj.setUpdatedAt(now);
            requestObj.setDisable(false);

            List<RepairRequestDetail> detailList = new ArrayList<>();
            MaterialDAO materialDAO = new MaterialDAO();

            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                
                String trimmedMaterialName = materialName.trim();
                Material material = materialDAO.getMaterialByName(trimmedMaterialName);
                
                if (material == null) {
                    continue; // Already validated above
                }
                
                int quantity = Integer.parseInt(quantities[i]);
                String damageDescription = damageDescriptions[i];
                
                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setMaterialId(material.getMaterialId());
                detail.setQuantity(quantity);
                detail.setDamageDescription(damageDescription);
                detail.setSupplierId(supplierId); 
                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);

                detailList.add(detail);
            }

            boolean success = new RepairRequestDAO().createRepairRequest(requestObj, detailList);
            System.out.println("[DEBUG] [doPost] Kết quả lưu yêu cầu vào DB: " + success);

            if (success) {
                UserDAO userDAO = new UserDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                Supplier supplier = supplierDAO.getSupplierByID(supplierId);
                List<User> allUsers = userDAO.getAllUsers();
                List<User> managers = new ArrayList<>();
                for (User u : allUsers) {
                    if (u.getRoleId() == 2) {
                        managers.add(u);
                    }
                }

                if (!managers.isEmpty()) {
                    String subject = "[Notification] New Repair Request";

                    // Build HTML email content
                    StringBuilder htmlContent = new StringBuilder();
                    htmlContent.append("<!DOCTYPE html>");
                    htmlContent.append("<html>");
                    htmlContent.append("<head>");
                    htmlContent.append("<meta charset='UTF-8'>");
                    htmlContent.append("<style>");
                    htmlContent.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }");
                    htmlContent.append(".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }");
                    htmlContent.append(".header { background-color: #E9B775; padding: 20px; text-align: center; }");
                    htmlContent.append(".header h1 { color: #000000; margin: 0; font-size: 24px; }");
                    htmlContent.append(".content { padding: 30px; }");
                    htmlContent.append(".info-row { margin-bottom: 15px; }");
                    htmlContent.append(".info-label { font-weight: bold; color: #000000; display: inline-block; width: 120px; }");
                    htmlContent.append(".info-value { color: #333333; }");
                    htmlContent.append(".materials-table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
                    htmlContent.append(".materials-table th, .materials-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
                    htmlContent.append(".materials-table th { background-color: #E9B775; color: #000000; font-weight: bold; }");
                    htmlContent.append(".materials-table td { background-color: #f9f9f9; }");
                    htmlContent.append(".btn { display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold; margin-top: 20px; }");
                    htmlContent.append(".footer { background-color: #f8f9fa; padding: 20px; text-align: center; color: #666; }");
                    htmlContent.append("</style>");
                    htmlContent.append("</head>");
                    htmlContent.append("<body>");
                    htmlContent.append("<div class='container'>");
                    htmlContent.append("<div class='header'>");
                    htmlContent.append("<h1>New Repair Request</h1>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='content'>");
                    htmlContent.append("<p>Hello Director,</p>");
                    htmlContent.append("<p>A new repair request has been created in the system.</p>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Request Code:</span>");
                    htmlContent.append("<span class='info-value'>").append(requestCode).append("</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Requested By:</span>");
                    htmlContent.append("<span class='info-value'>").append(user.getFullName()).append(" (ID: ").append(user.getUserId()).append(")</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Submitted:</span>");
                    htmlContent.append("<span class='info-value'>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now)).append("</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Email:</span>");
                    htmlContent.append("<span class='info-value'>").append(user.getEmail() != null ? user.getEmail() : "N/A").append("</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Phone:</span>");
                    htmlContent.append("<span class='info-value'>").append(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A").append("</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Repairer:</span>");
                    htmlContent.append("<span class='info-value'>").append(supplier != null ? supplier.getSupplierName() : "N/A").append("</span>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='info-row'>");
                    htmlContent.append("<span class='info-label'>Reason:</span>");
                    htmlContent.append("<span class='info-value'>").append(reason).append("</span>");
                    htmlContent.append("</div>");
                    
                    // Materials table
                    htmlContent.append("<h3>Materials for Repair</h3>");
                    htmlContent.append("<table class='materials-table'>");
                    htmlContent.append("<thead>");
                    htmlContent.append("<tr>");
                    htmlContent.append("<th>Material Name</th>");
                    htmlContent.append("<th>Quantity</th>");
                    htmlContent.append("<th>Category</th>");
                    htmlContent.append("<th>Unit</th>");
                    htmlContent.append("<th>Status</th>");
                    htmlContent.append("<th>Damage Description</th>");
                    htmlContent.append("</tr>");
                    htmlContent.append("</thead>");
                    htmlContent.append("<tbody>");
                    
                    for (RepairRequestDetail detail : detailList) {
                        Material material = materialDAO.getProductById(detail.getMaterialId());
                        if (material != null) {
                            htmlContent.append("<tr>");
                            htmlContent.append("<td>").append(material.getMaterialName()).append("</td>");
                            htmlContent.append("<td>").append(detail.getQuantity()).append("</td>");
                            htmlContent.append("<td>").append(material.getCategory() != null ? material.getCategory().getCategory_name() : "N/A").append("</td>");
                            htmlContent.append("<td>").append(material.getUnit() != null ? material.getUnit().getUnitName() : "N/A").append("</td>");
                            htmlContent.append("<td>").append(material.getMaterialStatus()).append("</td>");
                            htmlContent.append("<td>").append(detail.getDamageDescription()).append("</td>");
                            htmlContent.append("</tr>");
                        }
                    }
                    
                    htmlContent.append("</tbody>");
                    htmlContent.append("</table>");
                    
                    htmlContent.append("<div style='text-align: center;'>");
                    htmlContent.append("<a href='http://localhost:8080/MaterialManagement/repairrequestlist' class='btn'>VIEW IN SYSTEM</a>");
                    htmlContent.append("</div>");
                    htmlContent.append("</div>");
                    htmlContent.append("<div class='footer'>");
                    htmlContent.append("<p>Please log into the system to view details and process the request.</p>");
                    htmlContent.append("</div>");
                    htmlContent.append("</div>");
                    htmlContent.append("</body>");
                    htmlContent.append("</html>");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                EmailUtils.sendEmail(manager.getEmail(), subject, htmlContent.toString());
                            } catch (Exception e) {
                                System.out.println("[DEBUG] [MAIL] Error sending email to manager: " + manager.getEmail());
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("[DEBUG] [MAIL] Manager has no valid email: " + manager.getFullName());
                        }
                    }

                    if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                        try {
                            System.out.println("[DEBUG] [MAIL] Sending email to user: " + user.getEmail());
                            System.out.println("[DEBUG] [MAIL] Subject: " + subject);
                            EmailUtils.sendEmail(user.getEmail(), subject, htmlContent.toString());
                        } catch (Exception e) {
                            System.out.println("[DEBUG] [MAIL] Error sending email to user: " + user.getEmail());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("[DEBUG] [MAIL] User has no valid email: " + user.getFullName());
                    }
                }
            }

            response.sendRedirect("repairrequestlist");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error when sending repair request: " + e.getMessage());
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        }
    }

    private String generateRequestCode() {
        RepairRequestDAO repairRequestDAO = new RepairRequestDAO();
        return repairRequestDAO.generateNextRequestCode();
    }
}
