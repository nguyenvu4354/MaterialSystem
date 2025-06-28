package controller;

import dal.PurchaseRequestDAO;
import dal.PurchaseRequestDetailDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
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
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name="PurchaseRequestDetailServlet", urlPatterns={"/PurchaseRequestDetail"})
public class PurchaseRequestDetailServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PurchaseRequestDetailServlet.class.getName());
    private final PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
    private final PurchaseRequestDetailDAO purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("PurchaseRequestDetailServlet.doGet() started");
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
        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_PURCHASE_REQUEST_LIST");
        LOGGER.info("Permission VIEW_PURCHASE_REQUEST_LIST for role_id " + currentUser.getRoleId() + ": " + hasPermission);
        if (!hasPermission) {
            LOGGER.warning("Unauthorized access attempt by user: " + currentUser.getUsername() + " with role_id: " + currentUser.getRoleId());
            request.setAttribute("error", "You do not have permission to view purchase request details.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            
            if (purchaseRequest == null) {
                LOGGER.warning("Purchase request not found for ID: " + purchaseRequestId);
                request.setAttribute("error", "Purchase request not found.");
                request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
                return;
            }

            int pageSize = 10;
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid page parameter: " + pageParam);
                    currentPage = 1;
                }
            }
            
            int totalItems = purchaseRequestDetailDAO.count(purchaseRequestId);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }
            
            List<PurchaseRequestDetail> purchaseRequestDetailList;
            
            if (totalItems > pageSize) {
                purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, currentPage, pageSize);
                request.setAttribute("currentPage", currentPage);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("totalItems", totalItems);
                request.setAttribute("showPagination", true);
            } else {
                purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, Integer.MAX_VALUE);
                request.setAttribute("showPagination", false);
            }

            // Lấy tên người yêu cầu
            User requester = userDAO.getUserById(purchaseRequest.getUserId());
            String requesterName = requester != null ? requester.getFullName() : "Không xác định";

            request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
            request.setAttribute("purchaseRequest", purchaseRequest);
            request.setAttribute("requesterName", requesterName);
            request.setAttribute("requester", requester);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.setAttribute("hasHandleRequestPermission", rolePermissionDAO.hasPermission(currentUser.getRoleId(), "HANDLE_REQUEST"));

            LOGGER.info("Forwarding to PurchaseRequestDetail.jsp");
            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
            LOGGER.info("PurchaseRequestDetailServlet.doGet() completed successfully");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error in PurchaseRequestDetailServlet: " + ex.getMessage(), ex);
            request.setAttribute("error", "An error occurred while processing your request: " + ex.getMessage());
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("PurchaseRequestDetailServlet.doPost() started");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            LOGGER.warning("No user session found, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        LOGGER.info("User role_id: " + currentUser.getRoleId() + ", username: " + currentUser.getUsername());
        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "HANDLE_REQUEST");
        LOGGER.info("Permission HANDLE_REQUEST for role_id " + currentUser.getRoleId() + ": " + hasPermission);
        if (!hasPermission) {
            LOGGER.warning("Unauthorized action attempt by user: " + currentUser.getUsername() + " with role_id: " + currentUser.getRoleId());
            request.setAttribute("error", "You do not have permission to approve or reject purchase requests.");
            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
            return;
        }

        try {
            String action = request.getParameter("action");
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            String reason = request.getParameter("approvalReason");
            
            LOGGER.info("Action: " + action + ", PurchaseRequestId: " + purchaseRequestId + ", Reason: " + reason);

            boolean success = false;
            if ("approve".equals(action)) {
                success = purchaseRequestDAO.updatePurchaseRequestStatus(purchaseRequestId, "approved", currentUser.getUserId(), reason);
            } else if ("reject".equals(action)) {
                success = purchaseRequestDAO.updatePurchaseRequestStatus(purchaseRequestId, "rejected", currentUser.getUserId(), reason);
            } else {
                LOGGER.warning("Invalid action: " + action);
                request.setAttribute("error", "Invalid action.");
                request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
                return;
            }
            
            if (success) {
                LOGGER.info("Purchase request " + purchaseRequestId + " " + action + " successfully");
                request.setAttribute("success", "The request has been " + ("approve".equals(action) ? "approved" : "rejected") + " successfully!");
                doGet(request, response);
            } else {
                LOGGER.warning("Failed to " + action + " purchase request " + purchaseRequestId);
                request.setAttribute("error", "Could not " + ("approve".equals(action) ? "approve" : "reject") + " the request. Please try again.");
                doGet(request, response);
            }
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error in PurchaseRequestDetailServlet POST: " + ex.getMessage(), ex);
            request.setAttribute("error", "An error occurred while processing the request: " + ex.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for handling purchase request details and actions.";
    }
}