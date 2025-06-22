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

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportRequestListServlet.class.getName());
    private final ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
    private final UserDAO userDAO = new UserDAO();
    
    // Hằng số cho phân trang
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final int MAX_ITEMS_PER_PAGE = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("ExportRequestListServlet.doGet() started");
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            LOGGER.info("Session ID: " + session.getId());
            LOGGER.info("User in session: " + (user != null ? user.getUsername() : "null"));

            if (user == null) {
                LOGGER.info("User is null, redirecting to Login.jsp");
                response.sendRedirect(request.getContextPath() + "/Login.jsp?returnUrl=" + request.getRequestURI());
                return;
            }

            // Phân quyền: Chỉ role_id = 2 (Giám đốc) mới được truy cập
            if (user.getRoleId() != 2) {
                LOGGER.warning("Unauthorized access attempt by user: " + user.getUsername() + " with role_id: " + user.getRoleId());
                response.sendRedirect("HomePage.jsp"); // Hoặc trang báo lỗi không có quyền
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
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ExportRequestListServlet: " + e.getMessage(), e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            try {
                request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error forwarding to error page: " + ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        }
    }
}