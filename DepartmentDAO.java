// Department list
import java.sql.*;
import java.util.*;

public class DepartmentDAO {
    private Connection conn;

    public DepartmentDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Departments WHERE disable = 0";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Department d = new Department();
                d.setDepartmentId(rs.getInt("department_id"));
                d.setDepartmentName(rs.getString("department_name"));
                d.setDescription(rs.getString("description"));
                d.setDisable(rs.getInt("disable") == 1);
                list.add(d);
            }
        }

        return list;
    }
}

// Create department
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartmentDAO {
    private Connection conn;

    public DepartmentDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean createDepartment(Department department) {
        String sql = "INSERT INTO Departments (department_name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getDescription());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();  // Replace with proper logging in production
            return false;
        }
    }
}

