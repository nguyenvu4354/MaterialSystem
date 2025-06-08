package controller;

import dal.DepartmentDAO; // New DAO for departments
import dal.UserDAO;
import entity.Department;
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

        request.setCharacterEncoding("UTF-8");

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

        String usernameFilter = request.getParameter("username");
        String statusFilter = request.getParameter("status");
        String roleIdStr = request.getParameter("roleId");
        String departmentIdStr = request.getParameter("departmentId"); // New filter parameter
        Integer roleIdFilter = null;
        Integer departmentIdFilter = null;

        if (roleIdStr != null && !roleIdStr.isEmpty()) {
            try {
                roleIdFilter = Integer.parseInt(roleIdStr);
            } catch (NumberFormatException ignored) {
            }
        }

        if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
            try {
                departmentIdFilter = Integer.parseInt(departmentIdStr);
            } catch (NumberFormatException ignored) {
            }
        }

        UserDAO userDAO = new UserDAO();
        DepartmentDAO departmentDAO = new DepartmentDAO(); // New DAO instance

        // Fetch department list for filter dropdown
        List<Department> departmentList = departmentDAO.getAllDepartments();
        request.setAttribute("departmentList", departmentList);

        // Fetch users with filters and pagination
        List<User> userList = userDAO.getUsersByPageAndFilter(page, pageSize, usernameFilter, statusFilter, roleIdFilter, departmentIdFilter);

        // Count total users for pagination
        int totalUsers = userDAO.getUserCountByFilter(usernameFilter, statusFilter, roleIdFilter, departmentIdFilter);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        request.setAttribute("userList", userList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("usernameFilter", usernameFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("roleIdFilter", roleIdFilter);
        request.setAttribute("departmentIdFilter", departmentIdFilter);

        request.getRequestDispatcher("UserList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");
        UserDAO userDAO = new UserDAO();

        if (userIdStr == null) {
            request.setAttribute("error", "Thiếu userId.");
            doGet(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            if ("delete".equals(action)) {
                // Handle soft delete
                boolean deleted = userDAO.deleteUserById(userId);
                if (deleted) {
                    request.setAttribute("message", "Xóa người dùng thành công.");
                } else {
                    request.setAttribute("error", "Xóa người dùng thất bại. Không tìm thấy người dùng hoặc đã bị xóa.");
                }
            } else {
                // Handle status and role update
                String roleIdStr = request.getParameter("roleId");
                String statusStr = request.getParameter("status");

                if (roleIdStr == null || statusStr == null) {
                    request.setAttribute("error", "Thiếu dữ liệu cập nhật.");
                    doGet(request, response);
                    return;
                }

                if (!statusStr.equals("active") && !statusStr.equals("inactive")) {
                    request.setAttribute("error", "Trạng thái không hợp lệ. Chỉ cho phép 'active' hoặc 'inactive'.");
                    doGet(request, response);
                    return;
                }

                int roleId = Integer.parseInt(roleIdStr);
                User.Status status = User.Status.valueOf(statusStr);

                boolean updated = userDAO.updateStatusAndRole(userId, status, roleId);
                if (!updated) {
                    request.setAttribute("error", "Cập nhật thất bại.");
                } else {
                    request.setAttribute("message", "Cập nhật người dùng thành công.");
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ (userId hoặc roleId).");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        doGet(request, response);
    }
}