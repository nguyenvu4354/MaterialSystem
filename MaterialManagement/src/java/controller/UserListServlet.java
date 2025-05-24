package controller;



import dal.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserListServlet", urlPatterns = {"/UserList"})
public class UserListServlet extends HttpServlet {

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8"); // nếu cần

    int page = 1;
    int pageSize = 5;
    String pageStr = request.getParameter("page");
    if (pageStr != null) {
        try {
            page = Integer.parseInt(pageStr);
            if (page < 1) {
                page = 1;
            }
        } catch (NumberFormatException ignored) {
        }
    }

    // Lấy các tham số filter (có thể null hoặc rỗng)
    String usernameFilter = request.getParameter("username");
    String statusFilter = request.getParameter("status");
    String roleIdStr = request.getParameter("roleId");
    Integer roleIdFilter = null;
    if (roleIdStr != null && !roleIdStr.isEmpty()) {
        try {
            roleIdFilter = Integer.parseInt(roleIdStr);
        } catch (NumberFormatException ignored) {
        }
    }

    UserDAO userDAO = new UserDAO();

    // Lấy danh sách users theo filter và phân trang
    List<User> userList = userDAO.getUsersByPageAndFilter(page, pageSize, usernameFilter, statusFilter, roleIdFilter);

    // Đếm tổng số user theo filter để phân trang
    int totalUsers = userDAO.getUserCountByFilter(usernameFilter, statusFilter, roleIdFilter);
    int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

    // Truyền dữ liệu về JSP
    request.setAttribute("userList", userList);
    request.setAttribute("currentPage", page);
    request.setAttribute("totalPages", totalPages);

    request.setAttribute("usernameFilter", usernameFilter);
    request.setAttribute("statusFilter", statusFilter);
    request.setAttribute("roleIdFilter", roleIdFilter);

    request.getRequestDispatcher("UserList.jsp").forward(request, response);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // nếu cần để nhận tiếng Việt

        String userIdStr = request.getParameter("userId");
        String roleIdStr = request.getParameter("roleId");
        String statusStr = request.getParameter("status");

        if (userIdStr == null || roleIdStr == null || statusStr == null) {
            request.setAttribute("error", "Thiếu dữ liệu cập nhật.");
            doGet(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int roleId = Integer.parseInt(roleIdStr);
            User.Status status = User.Status.valueOf(statusStr);

            UserDAO userDAO = new UserDAO();
            boolean updated = userDAO.updateStatusAndRole(userId, status, roleId);
            if (!updated) {
                request.setAttribute("error", "Cập nhật thất bại.");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        // Quay lại trang danh sách (về trang 1 hoặc trang hiện tại)
        response.sendRedirect("UserList");
    }
}
