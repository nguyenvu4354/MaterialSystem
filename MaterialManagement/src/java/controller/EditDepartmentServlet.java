package controller;

import dal.DepartmentDAO;
import dal.RolePermissionDAO;
import entity.Department;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = {"/editdepartment"})
public class EditDepartmentServlet extends HttpServlet {
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Check UPDATE_DEPARTMENT permission
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "UPDATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Department dept = departmentDAO.getDepartmentById(id);
            if (dept == null) {
                request.setAttribute("error", "Phòng ban không tồn tại hoặc đã bị xóa.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            request.setAttribute("department", dept);
            request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi lấy thông tin phòng ban: " + e.getMessage());
            request.setAttribute("error", "Lỗi khi lấy thông tin phòng ban: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Check UPDATE_DEPARTMENT permission
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "UPDATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String location = request.getParameter("location");
            String description = request.getParameter("description");
            String code = request.getParameter("code");

            // Validate input
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Tên phòng ban không được để trống.");
                request.setAttribute("department", departmentDAO.getDepartmentById(id));
                request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
                return;
            }
            if (code == null || code.trim().isEmpty()) {
                request.setAttribute("error", "Mã phòng ban không được để trống.");
                request.setAttribute("department", departmentDAO.getDepartmentById(id));
                request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
                return;
            }

            Department dept = new Department();
            dept.setDepartmentId(id);
            dept.setDepartmentName(name);
            dept.setDepartmentCode(code);
            dept.setPhoneNumber(phone);
            dept.setEmail(email);
            dept.setLocation(location);
            dept.setDescription(description);
            dept.setStatus(Department.Status.active);
            dept.setUpdatedAt(LocalDateTime.now());

            departmentDAO.updateDepartment(dept);
            request.setAttribute("message", "Cập nhật phòng ban thành công!");
            response.sendRedirect("depairmentlist");
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi cập nhật phòng ban: " + e.getMessage());
            request.setAttribute("error", "Lỗi khi cập nhật phòng ban: " + e.getMessage());
            request.setAttribute("department", departmentDAO.getDepartmentById(Integer.parseInt(request.getParameter("id"))));
            request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Edit Department Servlet";
    }
}