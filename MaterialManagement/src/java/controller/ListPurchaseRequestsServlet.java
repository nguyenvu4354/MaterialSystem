package controller;

import dal.PurchaseRequestDAO;
import entity.PurchaseRequest;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import dal.UserDAO;

@WebServlet(name = "ListPurchaseRequestsServlet", urlPatterns = {"/ListPurchaseRequests"})
public class ListPurchaseRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect("LoginServlet");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 2 && user.getRoleId() != 3 && user.getRoleId() != 4) {
            request.setAttribute("error", "You do not have permission to view this page.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        PurchaseRequestDAO prd = new PurchaseRequestDAO();
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortOption = request.getParameter("sort");
        int pageIndex = 1;
        int pageSize = 10;

        if (request.getParameter("page") != null) {
            try {
                pageIndex = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }

        if (sortOption == null || sortOption.isEmpty()) {
            sortOption = "date_desc";
        }

        List<PurchaseRequest> list;
        int totalItems;
        if (user.getRoleId() == 2 || user.getRoleId() == 3 || user.getRoleId() == 4) {
            // Giám đốc, nhân viên kho và nhân viên đều xem tất cả (trừ cancel)
            list = prd.searchPurchaseRequest(keyword, status, pageIndex, pageSize, sortOption);
            totalItems = prd.countPurchaseRequest(keyword, status);
            
            // Lọc bỏ các đơn có trạng thái cancel
            for (int i = list.size() - 1; i >= 0; i--) {
                if ("cancel".equalsIgnoreCase(list.get(i).getStatus())) {
                    list.remove(i);
                }
            }
            // Cập nhật lại totalItems sau khi lọc
            totalItems = list.size();
        } else {
            // Các role khác không có quyền xem
            request.setAttribute("error", "You do not have permission to view this page.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Tạo Map userId -> tên requester
        UserDAO userDAO = new UserDAO();
        java.util.Map<Integer, String> userIdToName = new java.util.HashMap<>();
        for (PurchaseRequest pr : list) {
            int uid = pr.getUserId();
            if (!userIdToName.containsKey(uid)) {
                User requester = userDAO.getUserById(uid);
                userIdToName.put(uid, requester != null ? requester.getFullName() : "Không xác định");
            }
        }

        request.setAttribute("purchaseRequests", list);
        request.setAttribute("userIdToName", userIdToName);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("sortOption", sortOption);
        request.setAttribute("canApprove", user.getRoleId() == 2 || user.getRoleId() == 3 || user.getRoleId() == 4);

        request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles listing, searching, sorting, and pagination of purchase requests.";
    }
}
