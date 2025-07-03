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
import java.util.logging.Logger;
import java.util.logging.Level;
import utils.PurchaseRequestValidator;

/**
 * Servlet for handling creation of purchase requests.
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequest"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CreatePurchaseRequestServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PurchaseRequestDAO prd = new PurchaseRequestDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("CreatePurchaseRequestServlet.doGet() started");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            LOGGER.warning("No user session found, redirecting to Login.jsp");
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        LOGGER.info("User role_id: " + currentUser.getRoleId() + ", username: " + currentUser.getUsername());
        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        LOGGER.info("Permission CREATE_PURCHASE_REQUEST for role_id " + currentUser.getRoleId() + ": " + hasPermission);
        if (!hasPermission) {
            LOGGER.warning("Unauthorized access attempt by user: " + currentUser.getUsername() + " with role_id: " + currentUser.getRoleId());
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

            LOGGER.info("Forwarding to PurchaseRequestForm.jsp");
            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
            LOGGER.info("CreatePurchaseRequestServlet.doGet() completed successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in CreatePurchaseRequestServlet: " + e.getMessage(), e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("CreatePurchaseRequestServlet.doPost() started");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            LOGGER.warning("No user session found, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        LOGGER.info("User role_id: " + currentUser.getRoleId() + ", username: " + currentUser.getUsername());
        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        LOGGER.info("Permission CREATE_PURCHASE_REQUEST for role_id " + currentUser.getRoleId() + ": " + hasPermission);
        if (!hasPermission) {
            LOGGER.warning("Unauthorized action attempt by user: " + currentUser.getUsername() + " with role_id: " + currentUser.getRoleId());
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            String estimatedPriceStr = request.getParameter("estimatedPrice");
            String reason = request.getParameter("reason");

            String[] materialNames = request.getParameterValues("materialName");
            String[] categoryIds = request.getParameterValues("categoryId");
            String[] quantities = request.getParameterValues("quantity");
            String[] notes = request.getParameterValues("note");

            Map<String, String> formErrors = PurchaseRequestValidator.validatePurchaseRequestFormData(reason, estimatedPriceStr);
            Map<String, String> detailErrors = PurchaseRequestValidator.validatePurchaseRequestDetails(materialNames, categoryIds, quantities);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                LOGGER.warning("Validation errors: " + formErrors);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            List<PurchaseRequestDetail> purchaseRequestDetails = new ArrayList<>();
            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                int categoryId = Integer.parseInt(categoryIds[i]);
                int quantity = Integer.parseInt(quantities[i]);

                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setMaterialName(materialName.trim());
                detail.setCategoryId(categoryId);
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
            purchaseRequest.setEstimatedPrice(Double.parseDouble(estimatedPriceStr));
            purchaseRequest.setReason(reason.trim());

            boolean success = prd.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);

            if (success) {
                LOGGER.info("Purchase request created successfully: " + requestCode);
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
                    content.append("Estimated Price: ").append(estimatedPriceStr).append("\n");
                    content.append("Time: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n\n");
                    content.append("Please log in to the system to view details and approve.");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                utils.EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                                LOGGER.info("Email sent to: " + manager.getEmail());
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Failed to send email to: " + manager.getEmail(), e);
                            }
                        }
                    }
                }
                response.sendRedirect("ListPurchaseRequests?success=created");
            } else {
                LOGGER.warning("Failed to create purchase request");
                request.setAttribute("error", "Could not create purchase request. Please try again.");
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in CreatePurchaseRequestServlet: " + e.getMessage(), e);
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
        return "Handles creation and saving of purchase requests.";
    }
}