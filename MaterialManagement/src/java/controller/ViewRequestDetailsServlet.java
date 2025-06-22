package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import entity.ExportRequest;
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

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
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
            int requestId = Integer.parseInt(request.getParameter("id"));
            ExportRequest exportRequest = exportRequestDAO.getById(requestId);

            if (exportRequest == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Export request not found");
                return;
            }

            boolean hasAccess = exportRequest.getUserId() == user.getUserId() || "Director".equals(user.getRoleName());
            if (!hasAccess) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("request", exportRequest);
            request.setAttribute("details", exportRequestDetailDAO.getByRequestId(requestId));
            request.setAttribute("isDirector", "director".equals(user.getRoleName()));
            request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
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
        if (!"Director".equals(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String action = request.getParameter("action");
            ExportRequest exportRequest = exportRequestDAO.getById(requestId);

            if (exportRequest == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Export request not found");
                return;
            }

            if (!"pending".equals(exportRequest.getStatus())) {
                request.setAttribute("error", "Only pending requests can be processed.");
                request.setAttribute("request", exportRequest);
                request.setAttribute("details", exportRequestDetailDAO.getByRequestId(requestId));
                request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
                return;
            }

            String reason = request.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) {
                request.setAttribute("error", "Reason is required.");
                request.setAttribute("request", exportRequest);
                request.setAttribute("details", exportRequestDetailDAO.getByRequestId(requestId));
                request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
                return;
            }

            if ("approve".equals(action)) {
                exportRequest.setStatus("approved");
                exportRequest.setApprovedBy(user.getUserId());
                exportRequest.setApprovalReason(reason);
            } else if ("reject".equals(action)) {
                exportRequest.setStatus("rejected");
                exportRequest.setApprovedBy(user.getUserId());
                exportRequest.setRejectionReason(reason);
            }

            if (exportRequestDAO.update(exportRequest)) {
                response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?id=" + requestId);
            } else {
                request.setAttribute("error", "Failed to process request.");
                request.setAttribute("request", exportRequest);
                request.setAttribute("details", exportRequestDetailDAO.getByRequestId(requestId));
                request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
        }
    }
}