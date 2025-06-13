package controller;

import dal.RequestDAO;
import entity.ExportRequest;
import entity.PurchaseRequest;
import entity.RepairRequest;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "ViewRequestsServlet", urlPatterns = {"/ViewRequests"})
public class ViewRequestsServlet extends HttpServlet {

    private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        requestDAO = new RequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            Integer userId = user.getUserId();

            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int pageSize = 10;
            String status = request.getParameter("status");
            String requestCode = request.getParameter("requestCode");
            LocalDate startDate = null;
            LocalDate endDate = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                startDate = LocalDate.parse(request.getParameter("startDate"), formatter);
            }
            if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty()) {
                endDate = LocalDate.parse(request.getParameter("endDate"), formatter);
            }

            List<ExportRequest> exportRequests = requestDAO.getExportRequestsByUser(userId, page, pageSize, status, requestCode, startDate, endDate);
            List<PurchaseRequest> purchaseRequests = requestDAO.getPurchaseRequestsByUser(userId, page, pageSize, status, requestCode, startDate, endDate);
            List<RepairRequest> repairRequests = requestDAO.getRepairRequestsByUser(userId, page, pageSize, status, requestCode, startDate, endDate);

            int exportCount = requestDAO.getExportRequestCountByUser(userId, status, requestCode, startDate, endDate);
            int purchaseCount = requestDAO.getPurchaseRequestCountByUser(userId, status, requestCode, startDate, endDate);
            int repairCount = requestDAO.getRepairRequestCountByUser(userId, status, requestCode, startDate, endDate);

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("exportRequests", exportRequests);
            request.setAttribute("purchaseRequests", purchaseRequests);
            request.setAttribute("repairRequests", repairRequests);
            request.setAttribute("exportTotalPages", (int) Math.ceil((double) exportCount / pageSize));
            request.setAttribute("purchaseTotalPages", (int) Math.ceil((double) purchaseCount / pageSize));
            request.setAttribute("repairTotalPages", (int) Math.ceil((double) repairCount / pageSize));
            request.setAttribute("currentPage", page);
            request.setAttribute("status", status);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("startDate", request.getParameter("startDate"));
            request.setAttribute("endDate", request.getParameter("endDate"));

            request.getRequestDispatcher("/ViewRequests.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error fetching requests", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            String action = request.getParameter("action");
            if ("cancel".equalsIgnoreCase(action)) {
                String type = request.getParameter("type");
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = false;

                if ("export".equalsIgnoreCase(type)) {
                    success = requestDAO.cancelExportRequest(id, user.getUserId());
                } else if ("purchase".equalsIgnoreCase(type)) {
                    success = requestDAO.cancelPurchaseRequest(id, user.getUserId());
                } else if ("repair".equalsIgnoreCase(type)) {
                    success = requestDAO.cancelRepairRequest(id, user.getUserId());
                } else {
                    response.sendRedirect(request.getContextPath() + "/ViewRequests?message=Error: Invalid request type");
                    return;
                }

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/ViewRequests?message=Request canceled successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/ViewRequests?message=Error: Cannot cancel request. It may not be in draft status or you lack permission.");
                }
            } else {
                doGet(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ViewRequests?message=Error: " + e.getMessage());
            throw new ServletException("Error processing request", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for listing and canceling user requests (export, purchase, repair)";
    }
}