package controller;

import dal.PurchaseRequestDAO;
import dal.PurchaseRequestDetailDAO;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@WebServlet(name="ListPurchaseDetailServlet", urlPatterns={"/PurchaseRequestDetailServlet"})
public class PurchaseRequestDetailServlet extends HttpServlet {

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

        if (user.getRoleId() != 2) {
            request.setAttribute("error", "You don't have permission to access this page. Only directors can view purchase request details.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            PurchaseRequest purchaseRequest = prd.getPurchaseRequestById(purchaseRequestId);
            
            if (purchaseRequest == null) {
                request.setAttribute("error", "Purchase request not found.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
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
            
            PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();
            
            int totalItems = prdd.count(purchaseRequestId);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }
            
            List<PurchaseRequestDetail> purchaseRequestDetailList;
            
            if (totalItems > pageSize) {
                purchaseRequestDetailList = prdd.paginationOfDetails(purchaseRequestId, currentPage, pageSize);
                request.setAttribute("currentPage", currentPage);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("totalItems", totalItems);
                request.setAttribute("showPagination", true);
            } else {
                purchaseRequestDetailList = prdd.paginationOfDetails(purchaseRequestId, 1, Integer.MAX_VALUE);
                request.setAttribute("showPagination", false);
            }

            request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
            request.setAttribute("purchaseRequest", purchaseRequest);
            
            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
        } catch(Exception ex) {
            System.out.println("Error in PurchaseRequestDetailServlet: " + ex.getMessage());
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request");
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        User user = (User) session.getAttribute("user");

        if (user.getRoleId() != 2) {
            request.setAttribute("error", "You don't have permission to perform this action. Only directors can approve/reject purchase requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            String action = request.getParameter("action");
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            String reason = request.getParameter("approvalReason");
            
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            boolean success = false;
            
            if ("approve".equals(action)) {
                success = prd.updatePurchaseRequestStatus(purchaseRequestId, "approved", user.getUserId(), reason);
            } else if ("reject".equals(action)) {
                success = prd.updatePurchaseRequestStatus(purchaseRequestId, "rejected", user.getUserId(), reason);
            }
            
            if (success) {
                request.setAttribute("success", "The request has been " + ("approve".equals(action) ? "approved" : "rejected") + " successfully!");
                doGet(request, response);
            } else {
                request.setAttribute("error", "Could not " + ("approve".equals(action) ? "approve" : "reject") + " the request. Please try again.");
                doGet(request, response);
            }
            
        } catch(Exception ex) {
            System.out.println("Error in PurchaseRequestDetailServlet POST: " + ex.getMessage());
            ex.printStackTrace();
            request.setAttribute("error", "An error occurred while processing the request: " + ex.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for handling purchase request details and actions.";
    }
}
