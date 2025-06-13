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

@WebServlet(name = "ViewRequestDetailsServlet", urlPatterns = {"/ViewRequestDetails"})
public class ViewRequestDetailsServlet extends HttpServlet {

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
        try {
            String type = request.getParameter("type");
            int id = Integer.parseInt(request.getParameter("id"));
            Object requestObj = null;
            String requestType = "";

            if ("export".equalsIgnoreCase(type)) {
                requestObj = requestDAO.getExportRequestById(id);
                requestType = "Export";
            } else if ("purchase".equalsIgnoreCase(type)) {
                requestObj = requestDAO.getPurchaseRequestById(id);
                requestType = "Purchase";
            } else if ("repair".equalsIgnoreCase(type)) {
                requestObj = requestDAO.getRepairRequestById(id);
                requestType = "Repair";
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request type");
                return;
            }

            if (requestObj == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }

            Integer userId = user.getUserId();
            boolean hasAccess = false;
            if (requestObj instanceof ExportRequest) {
                hasAccess = ((ExportRequest) requestObj).getUserId() == userId;
            } else if (requestObj instanceof PurchaseRequest) {
                hasAccess = ((PurchaseRequest) requestObj).getUserId() == userId;
            } else if (requestObj instanceof RepairRequest) {
                hasAccess = ((RepairRequest) requestObj).getUserId() == userId;
            }

            if (!hasAccess) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("request", requestObj);
            request.setAttribute("requestType", requestType);
            request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error fetching request details", e);
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
                    response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + id + "&message=Error: Invalid request type");
                    return;
                }

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + id + "&message=Request canceled successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + id + "&message=Error: Cannot cancel request. It may not be in draft status or you lack permission.");
                }
            } else {
                doGet(request, response); // Fallback to GET for other POST requests
            }
        } catch (Exception e) {
            String type = request.getParameter("type");
            String id = request.getParameter("id");
            response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + id + "&message=Error: " + e.getMessage());
            throw new ServletException("Error processing request", e);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet for viewing and canceling details of a specific user request (export, purchase, repair)";
    }
}   