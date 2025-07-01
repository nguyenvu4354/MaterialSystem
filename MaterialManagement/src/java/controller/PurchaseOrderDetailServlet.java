package controller;

import dal.PurchaseOrderDAO;
import entity.PurchaseOrder;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "PurchaseOrderDetailServlet", urlPatterns = {"/PurchaseOrderDetail"})
public class PurchaseOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        // Chỉ Admin và Director mới có quyền xem Purchase Order Detail
        if (user.getRoleId() != 1 && user.getRoleId() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            int poId = Integer.parseInt(request.getParameter("id"));
            PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);

            if (purchaseOrder == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase Order not found");
                return;
            }

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("purchaseOrder", purchaseOrder);
            request.getRequestDispatcher("/PurchaseOrderDetail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Purchase Order ID");
        } catch (Exception e) {
            throw new ServletException("Error fetching purchase order details", e);
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
        if (user.getRoleId() != 1 && user.getRoleId() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            String action = request.getParameter("action");
            int poId = Integer.parseInt(request.getParameter("poId"));

            if ("updateStatus".equals(action)) {
                String status = request.getParameter("status");
                String approvalReason = request.getParameter("approvalReason");
                String rejectionReason = request.getParameter("rejectionReason");

                PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
                boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, status, user.getUserId(), approvalReason, rejectionReason);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + poId + "&message=Status updated successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + poId + "&message=Error updating status");
                }
            } else {
                doGet(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + request.getParameter("poId") + "&message=Error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for viewing purchase order details";
    }
} 