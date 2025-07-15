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
import java.time.LocalDateTime;

@WebServlet(urlPatterns = {"/adddepartment"})
public class AddDepartmentServlet extends HttpServlet {
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

        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền thêm phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("DepartmentForm.jsp").forward(request, response);
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

        System.out.println("✅ POST request received for /adddepartment at " + LocalDateTime.now());
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền thêm phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String location = request.getParameter("location");
            String description = request.getParameter("description");

            System.out.println("✅ Received data - Name: " + name + ", Phone: " + phone + ", Email: " + email);

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Tên phòng ban không được để trống.");
                request.getRequestDispatcher("DepartmentForm.jsp").forward(request, response);
                return;
            }

            Department dept = new Department();
            dept.setDepartmentName(name);
            dept.setPhoneNumber(phone);
            dept.setEmail(email);
            dept.setLocation(location);
            dept.setDescription(description);
            dept.setStatus(Department.Status.active);
            dept.setCreatedAt(LocalDateTime.now());
            dept.setUpdatedAt(LocalDateTime.now());
            dept.setDepartmentCode(departmentDAO.generateUniqueDepartmentCode());
            System.out.println("✅ Generated department code: " + dept.getDepartmentCode());

            departmentDAO.addDepartment(dept);
            System.out.println("✅ Department added successfully, ID: " + dept.getDepartmentId());
            request.setAttribute("message", "Thêm phòng ban thành công!");
            response.sendRedirect(request.getContextPath() + "/depairmentlist");
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi thêm phòng ban: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi thêm phòng ban: " + e.getMessage());
            request.getRequestDispatcher("DepartmentForm.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Add Department Servlet";
    }
}