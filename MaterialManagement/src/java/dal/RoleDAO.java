package dal;

import entity.DBContext;
import entity.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {

    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String sql = "SELECT * FROM Roles WHERE disable = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                role.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                role.setDisable(rs.getBoolean("disable"));
                roleList.add(role);
            }
            System.out.println("✅ Lấy danh sách role thành công, số lượng: " + roleList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllRoles: " + e.getMessage());
            e.printStackTrace();
        }
        return roleList;
    }
}