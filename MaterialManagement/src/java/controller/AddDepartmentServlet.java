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
    private DepartmentDAO departmentDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        departmentDAO = new DepartmentDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (!rolePermissionDAO.hasPermission(roleId, "CREATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền thêm phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.getRequestDispatcher("DepartmentForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (!rolePermissionDAO.hasPermission(roleId, "CREATE_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền thêm phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String location = request.getParameter("location");
            String description = request.getParameter("description");

            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Tên phòng ban không được để trống.");
                request.setAttribute("name", name);
                request.setAttribute("phone", phone);
                request.setAttribute("email", email);
                request.setAttribute("location", location);
                request.setAttribute("description", description);
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

            departmentDAO.addDepartment(dept);
            response.sendRedirect(request.getContextPath() + "/depairmentlist");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi thêm phòng ban: " + e.getMessage());
            request.setAttribute("name", request.getParameter("name"));
            request.setAttribute("phone", request.getParameter("phone"));
            request.setAttribute("email", request.getParameter("email"));
            request.setAttribute("location", request.getParameter("location"));
            request.setAttribute("description", request.getParameter("description"));
            request.getRequestDispatcher("DepartmentForm.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Add Department Servlet";
    }
}