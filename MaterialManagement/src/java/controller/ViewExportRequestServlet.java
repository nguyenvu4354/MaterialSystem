package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ViewExportRequestServlet", urlPatterns = {"/ViewExportRequest"})
public class ViewExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;
    private UserDAO userDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
        userDAO = new UserDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean canView = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_EXPORT_REQUEST_LIST");
        boolean hasHandleRequestPermission = (user.getRoleId() == 1 || user.getRoleId() == 2) || 
                                           rolePermissionDAO.hasPermission(user.getRoleId(), "HANDLE_REQUEST");
        request.setAttribute("canViewExportRequest", canView);
        request.setAttribute("hasHandleRequestPermission", hasHandleRequestPermission);
        if (!canView) {
            request.setAttribute("error", "You do not have permission to view export requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            ExportRequest exportRequest = exportRequestDAO.getById(requestId);
            if (exportRequest == null) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            }
            List<ExportRequestDetail> details = exportRequestDetailDAO.getByRequestId(requestId);
            int itemsPerPage = 10;
            int currentPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                try {
                    currentPage = Integer.parseInt(pageStr);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException ignore) {}
            }
            int totalItems = details.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int start = (currentPage - 1) * itemsPerPage;
            int end = Math.min(start + itemsPerPage, totalItems);
            List<ExportRequestDetail> paginatedDetails = (start < end) ? details.subList(start, end) : java.util.Collections.emptyList();
            User sender = userDAO.getUserById(exportRequest.getUserId());
            String senderImg = (sender == null || sender.getUserPicture() == null || sender.getUserPicture().isEmpty())
                    ? "images/placeholder.png"
                    : (sender.getUserPicture().startsWith("http") || sender.getUserPicture().startsWith("/")
                        ? sender.getUserPicture()
                        : "images/profiles/" + sender.getUserPicture());
            request.setAttribute("exportRequest", exportRequest);
            request.setAttribute("details", paginatedDetails);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("sender", sender);
            request.setAttribute("senderImg", senderImg);
            request.setAttribute("roleId", user.getRoleId());
            request.setAttribute("user", user);
            request.getRequestDispatcher("ViewExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/ExportRequestList");
        }
    }
}