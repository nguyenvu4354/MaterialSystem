package controller;

import dal.ExportRequestDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
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

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportRequestListServlet.class.getName());
    private final ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final int MAX_ITEMS_PER_PAGE = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean canView = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_EXPORT_REQUEST_LIST");
        request.setAttribute("canViewExportRequest", canView);
        if (!canView) {
            request.setAttribute("error", "You do not have permission to view export requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        String status = request.getParameter("status");
        String search = request.getParameter("search");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        int page = DEFAULT_PAGE;
        int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = DEFAULT_PAGE;
            }
            String itemsPerPageStr = request.getParameter("itemsPerPage");
            if (itemsPerPageStr != null && !itemsPerPageStr.isEmpty()) {
                itemsPerPage = Integer.parseInt(itemsPerPageStr);
                if (itemsPerPage < 1) itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
                if (itemsPerPage > MAX_ITEMS_PER_PAGE) itemsPerPage = MAX_ITEMS_PER_PAGE;
            }
        } catch (NumberFormatException e) {
        }
        List<ExportRequest> exportRequests;
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "requestDate":
                    exportRequests = exportRequestDAO.getAllSortedByRequestDate(sortOrder);
                    break;
                case "status":
                    exportRequests = exportRequestDAO.getAllSortedByStatus(sortOrder);
                    break;
                case "requestCode":
                    exportRequests = exportRequestDAO.getAllSortedByRequestCode(sortOrder);
                    break;
                default:
                    exportRequests = exportRequestDAO.getAll(status, search, null, page, itemsPerPage);
            }
        } else {
            exportRequests = exportRequestDAO.getAll(status, search, null, page, itemsPerPage);
        }
        int totalItems = exportRequestDAO.getTotalCount(status, search, null);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        request.setAttribute("exportRequests", exportRequests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("status", status);
        request.setAttribute("search", search);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
    }
}