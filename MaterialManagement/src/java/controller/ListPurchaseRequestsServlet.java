package controller;

import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
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
import java.util.HashMap;
@WebServlet(name = "ListPurchaseRequestsServlet", urlPatterns = {"/ListPurchaseRequests"})
public class ListPurchaseRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO prd = new PurchaseRequestDAO();
        UserDAO userDAO = new UserDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_PURCHASE_REQUEST_LIST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to view purchase requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
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

            List<PurchaseRequest> list = prd.searchPurchaseRequest(keyword, status, pageIndex, pageSize, sortOption);
            int totalItems = prd.countPurchaseRequest(keyword, status);

            // Lọc bỏ các đơn có trạng thái cancel
            for (int i = list.size() - 1; i >= 0; i--) {
                if ("cancel".equalsIgnoreCase(list.get(i).getStatus())) {
                    list.remove(i);
                }
            }
            // Cập nhật lại totalItems sau khi lọc
            totalItems = list.size();

            // Tạo Map userId -> tên requester
            HashMap<Integer, String> userIdToName = new HashMap<>();
            for (PurchaseRequest pr : list) {
                int uid = pr.getUserId();
                if (!userIdToName.containsKey(uid)) {
                    User requester = userDAO.getUserById(uid);
                    userIdToName.put(uid, requester != null ? requester.getFullName() : "Không xác định");
                }
            }

            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            request.setAttribute("purchaseRequests", list);
            request.setAttribute("userIdToName", userIdToName);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO); // Để JSP sử dụng
            request.setAttribute("canApprove", rolePermissionDAO.hasPermission(currentUser.getRoleId(), "HANDLE_REQUEST"));

            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
