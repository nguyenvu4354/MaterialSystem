package dal;

import entity.DBContext;
import entity.Department;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO extends DBContext {

    public List<Department> getAllDepartments() {
        List<Department> departmentList = new ArrayList<>();
        String sql = "SELECT * FROM Departments WHERE status != 'deleted'";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                dept.setDepartmentCode(rs.getString("department_code"));
                dept.setPhoneNumber(rs.getString("phone_number"));
                dept.setEmail(rs.getString("email"));
                dept.setLocation(rs.getString("location"));
                dept.setDescription(rs.getString("description"));
                dept.setStatus(rs.getString("status") != null ? Department.Status.valueOf(rs.getString("status")) : null);
                dept.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                dept.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                departmentList.add(dept);
            }
            System.out.println("✅ Lấy danh sách phòng ban thành công, số lượng: " + departmentList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllDepartments: " + e.getMessage());
            e.printStackTrace();
        }
        return departmentList;
    }
}