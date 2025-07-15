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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/depairmentlist"})
public class DepartmentServlet extends HttpServlet {
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

        // Check VIEW_LIST_DEPARTMENT permission
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_DEPARTMENT")) {
            request.setAttribute("error", "Bạn không có quyền xem danh sách phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        String searchKeyword = request.getParameter("search");

        try {
            // Get all departments
            List<Department> departments = departmentDAO.getAllDepartments();

            // Filter by searchKeyword if provided
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = searchKeyword.trim().toUpperCase();
                departments = departments.stream()
                        .filter(d -> d.getDepartmentCode().toUpperCase().contains(keyword))
                        .collect(Collectors.toList());
            }

            request.setAttribute("departments", departments);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi lấy danh sách phòng ban: " + e.getMessage());
            request.setAttribute("error", "Lỗi khi lấy danh sách phòng ban: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Department List Servlet";
    }
}