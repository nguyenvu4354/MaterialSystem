import dal.DepartmentDAO;
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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/depairmentlist"})
public class DepartmentServlet extends HttpServlet {

    private DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 1) {
            response.sendRedirect("error.jsp");
            return;
        }

        String action = request.getParameter("action");
        String searchKeyword = request.getParameter("search");
        if (action == null) action = "list";

        List<Department> departments = departmentDAO.getAllDepartments();

        // Filter by searchKeyword if provided
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toUpperCase();
            departments = departments.stream()
                    .filter(d -> d.getDepartmentCode().toUpperCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        if (action.equals("delete")) {
            int deleteId = Integer.parseInt(request.getParameter("id"));
            departmentDAO.deleteDepartment(deleteId);
            request.setAttribute("message", "Department deleted successfully!");
            // Refresh list after deletion
            departments = departmentDAO.getAllDepartments();
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = searchKeyword.trim().toUpperCase();
                departments = departments.stream()
                        .filter(d -> d.getDepartmentCode().toUpperCase().contains(keyword))
                        .collect(Collectors.toList());
            }
        }

        request.setAttribute("departments", departments);
        request.setAttribute("searchKeyword", searchKeyword);
        request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 1) {
            response.sendRedirect("accessDenied.jsp");
            return;
        }

        int id = request.getParameter("id") == null || request.getParameter("id").isEmpty() ? 0 : Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String location = request.getParameter("location");
        String description = request.getParameter("description");
        String code = request.getParameter("code"); // For edit case
        String searchKeyword = request.getParameter("search");

        Department dept = new Department();
        dept.setDepartmentId(id);
        dept.setDepartmentName(name);
        dept.setPhoneNumber(phone);
        dept.setEmail(email);
        dept.setLocation(location);
        dept.setDescription(description);
        dept.setStatus(Department.Status.active);
        dept.setUpdatedAt(LocalDateTime.now());

        if (id == 0) {
            // Generate unique 3-letter department code
            code = departmentDAO.generateUniqueDepartmentCode();
            dept.setDepartmentCode(code);
            dept.setCreatedAt(LocalDateTime.now());
            departmentDAO.addDepartment(dept);
            request.setAttribute("message", "Add department successfully!");
        } else {
            dept.setDepartmentCode(code); // Use existing code for update
            departmentDAO.updateDepartment(dept);
            request.setAttribute("message", "Department update successful!");
        }

        List<Department> departments = departmentDAO.getAllDepartments();
        // Reapply search filter after add/update
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim().toUpperCase();
            departments = departments.stream()
                    .filter(d -> d.getDepartmentCode().toUpperCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        request.setAttribute("departments", departments);
        request.setAttribute("searchKeyword", searchKeyword);
        request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Department Management Servlet";
    }
}