package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.UserDAO;
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
 *
 * @author Admin
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequestServlet"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }
        User user = (User) session.getAttribute("user");

        if (user.getRoleId() != 4) {
            request.setAttribute("error", "You don't have permission to access this page. Only employees can create purchase requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        
        List<User> users = userDAO.getAllUsers();
        List<Category> categories = categoryDAO.getAllCategories();
        
        String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                + "-" + (int) (Math.random() * 1000);
        String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());
        
        request.setAttribute("users", users);
        request.setAttribute("categories", categories);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("requestDate", requestDate);
        
        request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        User user = (User) session.getAttribute("user");

        if (user.getRoleId() != 4) {
            request.setAttribute("error", "You don't have permission to access this page. Only employees can create purchase requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

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
            String firstError = formErrors.values().iterator().next();
            request.setAttribute("error", firstError);
            doGet(request, response);
            return;
        }

        double estimatedPrice = Double.parseDouble(estimatedPriceStr);

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

        try {
            String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                    + "-" + (int) (Math.random() * 1000);

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setRequestCode(requestCode);
            purchaseRequest.setUserId(user.getUserId());
            purchaseRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            purchaseRequest.setStatus("PENDING");
            purchaseRequest.setEstimatedPrice(estimatedPrice);
            purchaseRequest.setReason(reason.trim());

            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            boolean success = prd.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);

            if (success) {
                UserDAO userDAO = new UserDAO();
                List<User> allUsers = userDAO.getAllUsers();
                List<User> managers = new ArrayList<>();
                
                for (User u : allUsers) {
                    if (u.getRoleId() == 2) {
                        managers.add(u);
                    }
                }
                
                if (!managers.isEmpty()) {
                    String subject = "[Notification] A new purchase request has been created";
                    StringBuilder content = new StringBuilder();
                    content.append("Dear Director,\n\n");
                    content.append("A new purchase request has just been created.\n");
                    content.append("Request Code: ").append(requestCode).append("\n");
                    content.append("Creator: ").append(user.getFullName()).append(" (ID: ").append(user.getUserId()).append(")\n");
                    content.append("Reason: ").append(reason).append("\n");
                    content.append("Estimated Price: ").append(estimatedPrice).append("\n");
                    content.append("Time: ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n\n");
                    content.append("Please log in to the system to view details and approve.");
                    
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
                response.sendRedirect("ListPurchaseRequestsServlet?success=created");
            } else {
                request.setAttribute("error", "Could not create purchase request. Please try again.");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing the request: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handles creation and saving of purchase requests.";
    }// </editor-fold>

}
