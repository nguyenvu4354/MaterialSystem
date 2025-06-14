package controller;

import dal.ExportRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.ExportRequest;
import entity.User;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            System.out.println("DEBUG: User is null, redirecting to Login.jsp with returnUrl");
            response.sendRedirect(request.getContextPath() + "/Login.jsp?returnUrl=/ExportRequestList");
            return;
        }

        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) {
            status = ""; // Mặc định lấy tất cả nếu không lọc
        }
        String search = request.getParameter("search");
        if (search == null) {
            search = "";
        }
        int page;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            page = 1;
        }

        int itemsPerPage = 10;
        List<ExportRequest> exportRequests = exportRequestDAO.getAll(status, search, page, itemsPerPage);
        int totalItems = exportRequestDAO.getTotalCount(status, search);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        System.out.println("DEBUG: Retrieved " + exportRequests.size() + " requests, total items: " + totalItems + ", status: " + status + ", search: " + search + ", page: " + page);
        request.setAttribute("exportRequests", exportRequests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("status", status);
        request.setAttribute("search", search);

        request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
    }
}