package controller;

import dal.ExportRequestDAO;
import dal.UserDAO;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.ExportRequest;
import entity.User;
import java.util.List;
import java.util.logging.Level;
import java.util.Set;
import java.util.HashSet;
import dal.RolePermissionDAO;

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportRequestListServlet.class.getName());
    private final ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    
    // Hằng số cho phân trang
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final int MAX_ITEMS_PER_PAGE = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("ExportRequestListServlet.doGet() started");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            LOGGER.warning("No user session found, redirecting to Login.jsp");
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        LOGGER.info("User role_id: " + user.getRoleId() + ", username: " + user.getUsername());
        boolean canView = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_EXPORT_REQUEST_LIST");
        LOGGER.info("Permission VIEW_EXPORT_REQUEST_LIST for role_id " + user.getRoleId() + ": " + canView);
        request.setAttribute("canViewExportRequest", canView);
        if (!canView) {
            LOGGER.warning("Unauthorized access attempt by user: " + user.getUsername() + " with role_id: " + user.getRoleId());
            request.setAttribute("error", "You do not have permission to view export requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Lấy và xử lý các tham số tìm kiếm
        String status = request.getParameter("status");
        String search = request.getParameter("search");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String searchRecipient = request.getParameter("searchRecipient");
        
        // Xử lý phân trang
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
            LOGGER.warning("Invalid pagination parameters, using defaults: " + e.getMessage());
        }
        
        LOGGER.info("Parameters: status=" + status + ", search=" + search + 
                   ", fromDate=" + fromDate + ", toDate=" + toDate +
                   ", page=" + page + ", itemsPerPage=" + itemsPerPage +
                   ", sortBy=" + sortBy + ", sortOrder=" + sortOrder +
                   ", searchRecipient=" + searchRecipient);

        // Lấy dữ liệu theo điều kiện sắp xếp
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
                    exportRequests = exportRequestDAO.getAll(status, search, searchRecipient, page, itemsPerPage);
            }
        } else {
            exportRequests = exportRequestDAO.getAll(status, search, searchRecipient, page, itemsPerPage);
        }

        int totalItems = exportRequestDAO.getTotalCount(status, search);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        LOGGER.info("Retrieved data: totalItems=" + totalItems + ", totalPages=" + totalPages + 
                   ", exportRequests size=" + (exportRequests != null ? exportRequests.size() : "null"));

        if (exportRequests == null) {
            LOGGER.severe("exportRequests is null from DAO");
            request.setAttribute("error", "An error occurred while loading data. Please try again later.");
        } else if (exportRequests.isEmpty()) {
            LOGGER.info("No export requests found");
        }

        // Lấy danh sách tất cả recipient user thực tế cho dropdown
        List<Integer> recipientIds = exportRequestDAO.getAllRecipientUserIds();
        List<User> recipients = new ArrayList<>();
        for (Integer rid : recipientIds) {
            User recipient = userDAO.getUserById(rid);
            if (recipient != null) recipients.add(recipient);
        }
        request.setAttribute("recipients", recipients);

        // Set các thuộc tính cho JSP
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
        request.setAttribute("searchRecipient", searchRecipient);

        LOGGER.info("Forwarding to ExportRequestList.jsp");
        request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
        LOGGER.info("ExportRequestListServlet.doGet() completed successfully");
    }
}