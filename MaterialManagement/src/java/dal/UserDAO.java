package dal;

import entity.DBContext;
import entity.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;

public class UserDAO extends DBContext {

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT u.*, r.role_name FROM Users u "
                   + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                   + "WHERE u.username = ? AND u.password = ?";

        String md5Password = md5(password);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, md5Password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                System.out.println("✅ Đăng nhập thành công: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Sai thông tin đăng nhập hoặc tài khoản bị xóa.");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE u.status != 'deleted'";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                userList.add(user);
            }
            System.out.println("✅ Lấy danh sách user thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.role_name FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE u.user_id = ? AND u.status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                System.out.println("✅ Lấy được user: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Không tìm thấy user với user_id: " + userId);
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUserById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET full_name = ?, email = ?, phone_number = ?, address = ?, user_picture = ?, date_of_birth = ?, gender = ?, description = ?, status = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println("🔄 Cập nhật user với user_id = " + user.getUserId());
            System.out.println("full_name = " + user.getFullName());
            System.out.println("email = " + user.getEmail());
            System.out.println("phone_number = " + user.getPhoneNumber());
            System.out.println("address = " + user.getAddress());
            System.out.println("user_picture = " + user.getUserPicture());
            System.out.println("date_of_birth = " + (user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "null"));
            System.out.println("gender = " + (user.getGender() != null ? user.getGender().toString() : "null"));
            System.out.println("description = " + user.getDescription());
            System.out.println("status = " + (user.getStatus() != null ? user.getStatus().toString() : "null"));

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getUserPicture());
            ps.setObject(6, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(7, user.getGender() != null ? user.getGender().toString() : null);
            ps.setString(8, user.getDescription());
            ps.setString(9, user.getStatus() != null ? user.getStatus().toString() : null);
            ps.setInt(10, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected + " for user_id: " + user.getUserId());
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật thông tin user thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + user.getUserId());
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (username, password, full_name, email, phone_number, address, user_picture, role_id, date_of_birth, gender, description, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getUserPicture());
            ps.setInt(8, user.getRoleId());
            ps.setObject(9, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(10, user.getGender() != null ? user.getGender().name() : null);
            ps.setString(11, user.getDescription());
            ps.setString(12, user.getStatus() != null ? user.getStatus().name() : "active");

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean deleteUserById(int id) {
        String sql = "UPDATE Users SET status = 'deleted', updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Xóa mềm user thành công với user_id: " + id);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để xóa với user_id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi deleteUserById: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi kiểm tra username tồn tại: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatusAndRole(int userId, User.Status status, int roleId) {
        String sql = "UPDATE Users SET status = ?, role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status != 'inactive'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, roleId);
            ps.setInt(3, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật status và role thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateStatusAndRole: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getUsersByPageAndFilter(int page, int pageSize, String username, String status, Integer roleId) {
        List<User> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT u.*, r.role_name FROM Users u ");
        sql.append("LEFT JOIN Roles r ON u.role_id = r.role_id ");
        sql.append("WHERE u.status != 'deleted' ");

        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND u.username LIKE ? ");
            params.add("%" + username.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("deleted")) {
            sql.append("AND u.status = ? ");
            params.add(status.trim());
        }

        if (roleId != null) {
            sql.append("AND u.role_id = ? ");
            params.add(roleId);
        }

        sql.append("ORDER BY u.user_id LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                userList.add(user);
            }
            System.out.println("✅ Lấy danh sách user phân trang thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUsersByPageAndFilter: " + e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }

    public int getUserCountByFilter(String username, String status, Integer roleId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE status != 'deleted' ");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND username LIKE ? ");
            params.add("%" + username.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("deleted")) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (roleId != null) {
            sql.append("AND role_id = ? ");
            params.add(roleId);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUserCountByFilter: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) {
        String username = "john12";
        String inputPassword = "123";  // mật khẩu người dùng nhập

        UserDAO userDAO = new UserDAO();

        User user = userDAO.login(username, inputPassword);

        if (user != null) {
            System.out.println("Đăng nhập thành công: " + user.getUsername());
        } else {
            System.out.println("Đăng nhập thất bại");
        }
    }

}
