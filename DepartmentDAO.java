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
