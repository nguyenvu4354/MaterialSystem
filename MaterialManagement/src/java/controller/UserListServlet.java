package controller;

import dal.DepartmentDAO;
import dal.UserDAO;
import dal.RoleDAO;
import dal.RolePermissionDAO;
import entity.Department;
import entity.User;
import entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserListServlet", urlPatterns = {"/UserList"})
public class UserListServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || !rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_LIST_USER")) {
            request.setAttribute("error", "Bạn không có quyền truy cập danh sách người dùng.");
            request.getRequestDispatcher("UserList.jsp").forward(request, response);
            return;
        }

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
        String departmentIdStr = request.getParameter("departmentId");
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
        DepartmentDAO departmentDAO = new DepartmentDAO();
        RoleDAO roleDAO = new RoleDAO();

        List<Department> departmentList = departmentDAO.getAllDepartments();
        List<Role> roleList = roleDAO.getAllRoles();
        request.setAttribute("departmentList", departmentList);
        request.setAttribute("roleList", roleList);

        List<User> userList = userDAO.getUsersByPageAndFilter(page, pageSize, usernameFilter, statusFilter, roleIdFilter, departmentIdFilter);

        int totalUsers = userDAO.getUserCountByFilter(usernameFilter, statusFilter, roleIdFilter, departmentIdFilter);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        request.setAttribute("userList", userList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("usernameFilter", usernameFilter);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("roleIdFilter", roleIdFilter);
        request.setAttribute("departmentIdFilter", departmentIdFilter);
        request.setAttribute("rolePermissionDAO", rolePermissionDAO);

        request.getRequestDispatcher("UserList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || !rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_LIST_USER")) {
            request.setAttribute("error", "Bạn không có quyền truy cập danh sách người dùng.");
            request.getRequestDispatcher("UserList.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");
        UserDAO userDAO = new UserDAO();

        String usernameFilter = request.getParameter("usernameFilter");
        String statusFilter = request.getParameter("statusFilter");
        String roleIdFilterStr = request.getParameter("roleIdFilter");
        String departmentIdFilterStr = request.getParameter("departmentIdFilter");
        String pageStr = request.getParameter("page");

        StringBuilder queryString = new StringBuilder("UserList?");
        if (pageStr != null && !pageStr.isEmpty()) {
            queryString.append("page=").append(pageStr).append("&");
        }
        if (usernameFilter != null && !usernameFilter.isEmpty()) {
            queryString.append("username=").append(usernameFilter).append("&");
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            queryString.append("status=").append(statusFilter).append("&");
        }
        if (roleIdFilterStr != null && !roleIdFilterStr.isEmpty()) {
            queryString.append("roleId=").append(roleIdFilterStr).append("&");
        }
        if (departmentIdFilterStr != null && !departmentIdFilterStr.isEmpty()) {
            queryString.append("departmentId=").append(departmentIdFilterStr).append("&");
        }

        if (userIdStr == null) {
            request.getSession().setAttribute("error", "Thiếu userId.");
            response.sendRedirect(queryString.toString());
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            if ("delete".equals(action)) {
                if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "DELETE_USER")) {
                    request.getSession().setAttribute("error", "Bạn không có quyền xóa người dùng.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                boolean deleted = userDAO.deleteUserById(userId);
                if (deleted) {
                    request.getSession().setAttribute("message", "Xóa người dùng thành công.");
                } else {
                    request.getSession().setAttribute("error", "Xóa người dùng thất bại.");
                }
            } else if ("updateDepartment".equals(action)) {
                if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "UPDATE_USER")) {
                    request.getSession().setAttribute("error", "Bạn không có quyền cập nhật thông tin người dùng.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                String departmentIdStr = request.getParameter("departmentId");
                Integer departmentId = null;
                if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                    try {
                        departmentId = Integer.parseInt(departmentIdStr);
                    } catch (NumberFormatException ignored) {
                    }
                }
                boolean updated = userDAO.updateDepartment(userId, departmentId);
                if (updated) {
                    request.getSession().setAttribute("message", "Cập nhật phòng ban thành công.");
                } else {
                    request.getSession().setAttribute("error", "Cập nhật phòng ban thất bại.");
                }
            } else if ("updateStatus".equals(action)) {
                if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "UPDATE_USER")) {
                    request.getSession().setAttribute("error", "Bạn không có quyền cập nhật thông tin người dùng.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                String statusStr = request.getParameter("status");
                if (statusStr == null || (!statusStr.equals("active") && !statusStr.equals("inactive"))) {
                    request.getSession().setAttribute("error", "Trạng thái không hợp lệ.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                User.Status status = User.Status.valueOf(statusStr);
                boolean updated = userDAO.updateStatus(userId, status);
                if (updated) {
                    request.getSession().setAttribute("message", "Cập nhật trạng thái thành công.");
                } else {
                    request.getSession().setAttribute("error", "Cập nhật trạng thái thất bại.");
                }
            } else if ("updateRole".equals(action)) {
                if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "UPDATE_USER")) {
                    request.getSession().setAttribute("error", "Bạn không có quyền cập nhật thông tin người dùng.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                String roleIdStr = request.getParameter("roleId");
                if (roleIdStr == null) {
                    request.getSession().setAttribute("error", "Thiếu roleId.");
                    response.sendRedirect(queryString.toString());
                    return;
                }
                int roleId = Integer.parseInt(roleIdStr);
                boolean updated = userDAO.updateRole(userId, roleId);
                if (updated) {
                    request.getSession().setAttribute("message", "Cập nhật vai trò thành công.");
                } else {
                    request.getSession().setAttribute("error", "Cập nhật vai trò thất bại.");
                }
            } else {
                request.getSession().setAttribute("error", "Hành động không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Dữ liệu không hợp lệ.");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(queryString.toString());
    }
}