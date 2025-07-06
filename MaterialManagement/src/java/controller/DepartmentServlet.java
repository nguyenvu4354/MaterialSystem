import dal.DepartmentDAO;
import entity.Department;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/depairmentlist"})
public class DepartmentServlet extends HttpServlet {

    DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        if (action.equals("edit")) {
            int editId = Integer.parseInt(request.getParameter("id"));
            Department editDept = departmentDAO.getAllDepartments()
                    .stream()
                    .filter(d -> d.getDepartmentId() == editId)
                    .findFirst().orElse(null);
            request.setAttribute("editDepartment", editDept);
        } else if (action.equals("delete")) {
            int deleteId = Integer.parseInt(request.getParameter("id"));
            departmentDAO.deleteDepartment(deleteId);
            request.setAttribute("message", "Xoá phòng ban thành công!");
        }

        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = request.getParameter("id").isEmpty() ? 0 : Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String location = request.getParameter("location");
        String description = request.getParameter("description");

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

        if (id == 0) {
            dept.setCreatedAt(LocalDateTime.now());
            departmentDAO.addDepartment(dept);
            request.setAttribute("message", "Thêm phòng ban thành công!");
        } else {
            departmentDAO.updateDepartment(dept);
            request.setAttribute("message", "Cập nhật phòng ban thành công!");
        }

        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Department Management Servlet";
    }
}
