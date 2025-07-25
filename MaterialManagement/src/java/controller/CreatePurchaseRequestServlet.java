package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.MaterialDAO;
import entity.Category;
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
            List<User> users = userDAO.getAllUsers();
            List<Category> categories = categoryDAO.getAllCategories();
            List<entity.Material> materials = materialDAO.getAllProducts();

            String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                    + "-" + (int) (Math.random() * 1000);
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

            Map<String, String> formErrors = PurchaseRequestValidator.validatePurchaseRequestFormData(reason);
            Map<String, String> detailErrors = PurchaseRequestValidator.validatePurchaseRequestDetails(materialNames, quantities);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                String requestCode = request.getParameter("requestCode");
                if (requestCode == null || requestCode.isEmpty()) {
                    requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                        + "-" + (int) (Math.random() * 1000);
                }
                String requestDate = request.getParameter("requestDate");
                if (requestDate == null || requestDate.isEmpty()) {
                    requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());
                }
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate);
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

            String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                    + "-" + (int) (Math.random() * 1000);

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setRequestCode(requestCode);
            purchaseRequest.setUserId(currentUser.getUserId());
            purchaseRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            purchaseRequest.setStatus("PENDING");
            purchaseRequest.setReason(reason.trim());

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
                    String subject = "[Notification] A new purchase request has been created";
                    StringBuilder content = new StringBuilder();
                    content.append("Dear Director,\n\n");
                    content.append("A new purchase request has just been created.\n");
                    content.append("Request Code: ").append(requestCode).append("\n");
                    content.append("Creator: ").append(currentUser.getFullName()).append(" (ID: ").append(currentUser.getUserId()).append(")\n");
                    content.append("Reason: ").append(reason).append("\n");
                    content.append("Time: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n\n");
                    content.append("Please log in to the system to view details and approve.");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                System.out.println("Sending email to: " + manager.getEmail());
                                utils.EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                                System.out.println("Email sent successfully to: " + manager.getEmail());
                            } catch (Exception e) {
                                System.out.println("Failed to send email to: " + manager.getEmail());
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
