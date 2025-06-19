package controller;

import dal.ExportRequestDAO;
import entity.ExportRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import java.io.IOException;

@WebServlet(name = "ApproveExportRequestServlet", urlPatterns = {"/ApproveExportRequest"})
public class ApproveExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("User in session: " + (session != null ? session.getAttribute("user") : "null"));
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        System.out.println("User roleName: " + (user != null ? user.getRoleName() : "null"));
        if (user == null || !"director".equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String approvalReason = request.getParameter("approvalReason");

            ExportRequest req = exportRequestDAO.getById(requestId);
            if (req == null) {
                request.setAttribute("error", "Export request not found.");
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            }
            
            if (!"draft".equals(req.getStatus())) {
                request.setAttribute("error", "Only draft requests can be approved.");
                response.sendRedirect(request.getContextPath() + "/ViewExportRequest?id=" + requestId);
                return;
            }

            if (approvalReason == null || approvalReason.trim().isEmpty()) {
                request.setAttribute("error", "Approval reason is required.");
                request.getRequestDispatcher("/ViewExportRequest.jsp").forward(request, response);
                return;
            }

            req.setStatus("approved");
            req.setApprovedBy(user.getUserId());
            req.setApprovalReason(approvalReason);
            boolean success = exportRequestDAO.update(req);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ViewExportRequest?id=" + requestId);
            } else {
                request.setAttribute("error", "Failed to approve request.");
                request.getRequestDispatcher("/ViewExportRequest.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid request ID.");
            response.sendRedirect(request.getContextPath() + "/ExportRequestList");
        }
    }
}