package dal;

import entity.DBContext;
import entity.Permission;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO extends DBContext {

    public List<Permission> getAllPermissions() {
        List<Permission> permissionList = new ArrayList<>();
        String sql = "SELECT * FROM Permissions";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setPermissionId(rs.getInt("permission_id"));
                permission.setPermissionName(rs.getString("permission_name"));
                permission.setDescription(rs.getString("description"));
                permissionList.add(permission);
            }
            System.out.println("✅ Lấy danh sách permission thành công, số lượng: " + permissionList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllPermissions: " + e.getMessage());
            e.printStackTrace();
        }
        return permissionList;
    }

    public List<Permission> getPermissionsByRole(int roleId) {
        List<Permission> permissionList = new ArrayList<>();
        String sql = "SELECT p.* FROM Permissions p JOIN Role_Permissions rp ON p.permission_id = rp.permission_id WHERE rp.role_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("permission_id"));
                    permission.setPermissionName(rs.getString("permission_name"));
                    permission.setDescription(rs.getString("description"));
                    permissionList.add(permission);
                }
                System.out.println("✅ Lấy danh sách permission cho role " + roleId + " thành công, số lượng: " + permissionList.size());
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getPermissionsByRole: " + e.getMessage());
            e.printStackTrace();
        }
        return permissionList;
    }
}
