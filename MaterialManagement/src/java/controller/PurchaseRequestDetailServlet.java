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
@WebServlet(name="PurchaseRequestDetailServlet", urlPatterns={"/PurchaseRequestDetail"})
public class PurchaseRequestDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        PurchaseRequestDetailDAO purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
        UserDAO userDAO = new UserDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_PURCHASE_REQUEST_LIST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to view purchase request details.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            
            if (purchaseRequest == null) {
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

            User requester = userDAO.getUserById(purchaseRequest.getUserId());
            String requesterName = requester != null ? requester.getFullName() : "Không xác định";

            request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
            request.setAttribute("purchaseRequest", purchaseRequest);
            request.setAttribute("requesterName", requesterName);
            request.setAttribute("requester", requester);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.setAttribute("hasHandleRequestPermission", rolePermissionDAO.hasPermission(currentUser.getRoleId(), "HANDLE_REQUEST"));

            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);

        } catch (Exception ex) {
            request.setAttribute("error", "An error occurred while processing your request: " + ex.getMessage());
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

        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "HANDLE_REQUEST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to approve or reject purchase requests.");
            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
            return;
        }

        try {
            String action = request.getParameter("action");
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            String reason = request.getParameter("approvalReason");
            
            boolean success = false;
            if ("approve".equals(action)) {
                success = purchaseRequestDAO.updatePurchaseRequestStatus(purchaseRequestId, "approved", currentUser.getUserId(), reason);
            } else if ("reject".equals(action)) {
                success = purchaseRequestDAO.updatePurchaseRequestStatus(purchaseRequestId, "rejected", currentUser.getUserId(), reason);
            } else {
                request.setAttribute("error", "Invalid action.");
                request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
                return;
            }
            
            if (success) {
                request.setAttribute("success", "The request has been " + ("approve".equals(action) ? "approved" : "rejected") + " successfully!");
                doGet(request, response);
            } else {
                request.setAttribute("error", "Could not " + ("approve".equals(action) ? "approve" : "reject") + " the request. Please try again.");
                doGet(request, response);
            }
            
        } catch (Exception ex) {
            request.setAttribute("error", "An error occurred while processing the request: " + ex.getMessage());
            doGet(request, response);
        }
    }

   
}
