package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.MaterialDAO;
import entity.Category;
import entity.Material;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import utils.PurchaseRequestValidator;

/**
 * Servlet for handling creation of purchase requests.
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequest"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            List<User> users = userDAO.getAllUsers();
            List<Category> categories = categoryDAO.getAllCategories();
            List<entity.Material> materials = materialDAO.getAllProducts();

            String requestCode = prd.generateNextRequestCode();
            String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

            request.setAttribute("users", users);
            request.setAttribute("categories", categories);
            request.setAttribute("materials", materials);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("requestDate", requestDate);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);

            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO prd = new PurchaseRequestDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        UserDAO userDAO = new UserDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            String reason = request.getParameter("reason");

            String[] materialNames = request.getParameterValues("materialName");
            String[] materialIds = request.getParameterValues("materialId");
            String[] quantities = request.getParameterValues("quantity");
            String[] notes = request.getParameterValues("note");
            
            // Debug: Check if reason is being read correctly
           
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                
            }
            
            

            Map<String, String> formErrors = PurchaseRequestValidator.validatePurchaseRequestFormData(reason);
            Map<String, String> detailErrors = PurchaseRequestValidator.validatePurchaseRequestDetails(materialNames, quantities);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                // Always generate a new request code for retry
                String requestCode = prd.generateNextRequestCode();
                String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());
                
                // Preserve form data for retry
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate);
                
                // Preserve submitted form data
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            List<PurchaseRequestDetail> purchaseRequestDetails = new ArrayList<>();
            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                int quantity = Integer.parseInt(quantities[i]);
                int materialId = 0;
                if (materialIds != null && materialIds.length > i && materialIds[i] != null && !materialIds[i].isEmpty()) {
                    materialId = Integer.parseInt(materialIds[i]);
                }
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setMaterialName(materialName.trim());
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                String note = (notes != null && notes.length > i) ? notes[i] : null;
                detail.setNotes(note != null && !note.trim().isEmpty() ? note.trim() : null);
                purchaseRequestDetails.add(detail);
            }

            String requestCode = prd.generateNextRequestCode();

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setRequestCode(requestCode);
            purchaseRequest.setUserId(currentUser.getUserId());
            purchaseRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            purchaseRequest.setStatus("PENDING");
            
            // Ensure reason is properly set
            String finalReason = reason != null ? reason.trim() : "";
            if (finalReason.isEmpty()) {
                finalReason = "Test reason for debugging";
            }
            purchaseRequest.setReason(finalReason);
            
           
            for (int i = 0; i < purchaseRequestDetails.size(); i++) {
                PurchaseRequestDetail detail = purchaseRequestDetails.get(i);
               
            }

            boolean success = prd.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);
           

            if (success) {
                List<User> allUsers = userDAO.getAllUsers();
                List<User> managers = new ArrayList<>();
                for (User u : allUsers) {
                    if (rolePermissionDAO.hasPermission(u.getRoleId(), "HANDLE_REQUEST")) {
                        managers.add(u);
                    }
                }

                if (!managers.isEmpty()) {
                    String subject = "🔔 New Purchase Request Created - " + requestCode;
                    StringBuilder content = new StringBuilder();
                    content.append("<!DOCTYPE html>");
                    content.append("<html>");
                    content.append("<head>");
                    content.append("<meta charset='UTF-8'>");
                    content.append("<style>");
                    content.append("body { font-family: Arial, sans-serif; line-height: 1.4; color: #000; margin: 0; padding: 20px; background-color: #fff; }");
                    content.append(".container { max-width: 600px; margin: 0 auto; background: white; border: 1px solid #ccc; }");
                    content.append(".header { background: #000; color: white; padding: 20px; text-align: center; }");
                    content.append(".header h1 { margin: 0; font-size: 20px; font-weight: bold; }");
                    content.append(".content { padding: 20px; }");
                    content.append(".info-section { border: 1px solid #ccc; padding: 15px; margin: 15px 0; }");
                    content.append(".info-row { display: flex; justify-content: space-between; margin: 8px 0; padding: 5px 0; border-bottom: 1px solid #eee; }");
                    content.append(".info-row:last-child { border-bottom: none; }");
                    content.append(".label { font-weight: bold; color: #000; }");
                    content.append(".value { color: #000; }");
                    content.append(".request-code { background: #000; color: white; padding: 8px 12px; font-weight: bold; font-size: 16px; display: inline-block; margin: 10px 0; }");
                    content.append(".reason-box { border: 1px solid #ccc; padding: 12px; margin: 15px 0; }");
                    content.append(".reason-title { font-weight: bold; color: #000; margin-bottom: 8px; }");
                    content.append(".action-section { text-align: center; margin: 20px 0; }");
                    content.append(".btn { display: inline-block; background: #000; color: white; padding: 10px 20px; text-decoration: none; font-weight: bold; margin: 10px; }");
                    content.append(".footer { border-top: 1px solid #ccc; padding: 15px; text-align: center; color: #666; font-size: 12px; }");
                    content.append(".materials-section { margin: 15px 0; }");
                    content.append(".materials-table { width: 100%; border-collapse: collapse; margin: 15px 0; border: 1px solid #ccc; }");
                    content.append(".materials-table th { background: #000; color: white; padding: 10px 8px; text-align: left; font-weight: bold; font-size: 12px; }");
                    content.append(".materials-table td { padding: 8px; border: 1px solid #ccc; font-size: 12px; }");
                    content.append(".materials-table tr:nth-child(even) { background: #f9f9f9; }");
                    content.append("</style>");
                    content.append("</head>");
                    content.append("<body>");
                    content.append("<div class='container'>");
                    content.append("<div class='header'>");
                    content.append("<h1>PURCHASE REQUEST</h1>");
                    content.append("<p>A new purchase request has been submitted and requires your approval</p>");
                    content.append("</div>");
                    content.append("<div class='content'>");
                    content.append("<div class='info-section'>");
                    content.append("<div class='request-code'>").append(requestCode).append("</div>");
                    content.append("<div class='info-row'>");
                    content.append("<span class='label'>Requested By:</span>");
                    content.append("<span class='value'>").append(currentUser.getFullName()).append(" (ID: ").append(currentUser.getUserId()).append(")</span>");
                    content.append("</div>");
                    content.append("<div class='info-row'>");
                    content.append("<span class='label'>Submitted:</span>");
                    content.append("<span class='value'>").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("</span>");
                    content.append("</div>");
                    content.append("<div class='info-row'>");
                    content.append("<span class='label'>Email:</span>");
                    content.append("<span class='value'>").append(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A").append("</span>");
                    content.append("</div>");
                    content.append("<div class='info-row'>");
                    content.append("<span class='label'>Phone:</span>");
                    content.append("<span class='value'>").append(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "N/A").append("</span>");
                    content.append("</div>");
                    content.append("</div>");
                    content.append("<div class='reason-box'>");
                    content.append("<div class='reason-title'>Request Reason:</div>");
                    content.append("<p>").append(reason).append("</p>");
                    content.append("</div>");
                    content.append("<div class='materials-section'>");
                    content.append("<h3 style='color: #000; margin-bottom: 15px; font-weight: bold;'>REQUESTED MATERIALS:</h3>");
                    content.append("<table class='materials-table'>");
                    content.append("<thead>");
                    content.append("<tr>");
                    content.append("<th>Material</th>");
                    content.append("<th>Quantity</th>");
                    content.append("<th>Category</th>");
                    content.append("<th>Unit</th>");
                    content.append("<th>Status</th>");
                    content.append("<th>Notes</th>");
                    content.append("</tr>");
                    content.append("</thead>");
                    content.append("<tbody>");
                    for (PurchaseRequestDetail detail : purchaseRequestDetails) {
                        Material material = null;
                        if (detail.getMaterialId() > 0) {
                            material = materialDAO.getProductById(detail.getMaterialId());
                        }
                        
                        content.append("<tr>");
                        content.append("<td><strong>").append(detail.getMaterialName()).append("</strong></td>");
                        content.append("<td>").append(detail.getQuantity()).append("</td>");
                        content.append("<td>").append(material != null && material.getCategory() != null && material.getCategory().getCategory_name() != null ? material.getCategory().getCategory_name() : "N/A").append("</td>");
                        content.append("<td>").append(material != null && material.getUnit() != null && material.getUnit().getUnitName() != null ? material.getUnit().getUnitName() : "N/A").append("</td>");
                        content.append("<td>").append(material != null && material.getMaterialStatus() != null ? material.getMaterialStatus() : "N/A").append("</td>");
                        content.append("<td>").append(detail.getNotes() != null && !detail.getNotes().trim().isEmpty() ? detail.getNotes() : "-").append("</td>");
                        content.append("</tr>");
                    }
                    content.append("</tbody>");
                    content.append("</table>");
                    content.append("</div>");
                    content.append("<div class='action-section'>");
                    content.append("<p style='color: #000; margin-bottom: 20px; font-weight: bold;'>Please review and take action on this purchase request:</p>");
                    content.append("<a href='").append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath()).append("/ListPurchaseRequests' class='btn'>VIEW IN SYSTEM</a>");
                    content.append("</div>");
                    content.append("</div>");
                    content.append("<div class='footer'>");
                    content.append("<p>This is an automated notification from the Material Management System</p>");
                    content.append("<p>If you have any questions, please contact the system administrator</p>");
                    content.append("</div>");
                    content.append("</div>");
                    content.append("</body>");
                    content.append("</html>");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                
                                utils.EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                                
                            } catch (Exception e) {
                                
                                e.printStackTrace();
                            }
                        }
                    }
                }
                response.sendRedirect("ListPurchaseRequests?success=created");
            } else {
                request.setAttribute("error", "Could not create purchase request. Please try again.");
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while processing the request: " + e.getMessage());
            List<Category> categories = categoryDAO.getAllCategories();
            List<entity.Material> materials = materialDAO.getAllProducts();
            request.setAttribute("categories", categories);
            request.setAttribute("materials", materials);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
