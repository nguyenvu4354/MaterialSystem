package dal;

import entity.DBContext;
import entity.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDAO extends DBContext {

    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
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
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(User.Status.valueOf(rs.getString("status")));
                user.setDisable(rs.getBoolean("disable"));
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                System.out.println("✅ Đăng nhập thành công: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Sai thông tin đăng nhập hoặc tài khoản bị vô hiệu hóa.");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
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
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setDescription(rs.getString("description"));
                user.setStatus(User.Status.valueOf(rs.getString("status")));
                user.setDisable(rs.getBoolean("disable"));
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
        String sql = "UPDATE Users SET full_name = ?, email = ?, phone_number = ?, address = ?, user_picture = ?, date_of_birth = ?, gender = ?, description = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // In log chi tiết trước khi set tham số
            System.out.println("🔄 Cập nhật user với user_id = " + user.getUserId());
            System.out.println("full_name = " + user.getFullName());
            System.out.println("email = " + user.getEmail());
            System.out.println("phone_number = " + user.getPhoneNumber());
            System.out.println("address = " + user.getAddress());
            System.out.println("user_picture = " + user.getUserPicture());
            System.out.println("date_of_birth = " + (user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "null"));
            System.out.println("gender = " + (user.getGender() != null ? user.getGender().toString() : "null"));
            System.out.println("description = " + user.getDescription());

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getUserPicture());
            ps.setObject(6, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(7, user.getGender() != null ? user.getGender().toString() : null);
            ps.setString(8, user.getDescription());
            ps.setInt(9, user.getUserId());

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
        String sql = "INSERT INTO Users (username, password, full_name, email, phone_number, address, user_picture, role_id, date_of_birth, gender, description, status, isActive, disable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', 1, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Lưu mật khẩu đã hash (nếu có)
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getUserPicture());
            ps.setInt(8, user.getRoleId());
            ps.setObject(9, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(10, user.getGender() != null ? user.getGender().toString() : null);
            ps.setString(11, user.getDescription());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected when creating user: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("❌ Lỗi createUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        // Lấy user cần update (ví dụ user_id = 1)
        User user = userDAO.getUserById(1); // Giả sử user ID = 1 tồn tại

        if (user != null) {
            // Cập nhật thông tin mới
            user.setFullName("Nguyễn Văn A");
            user.setEmail("nguyenvana@example.com");
            user.setPhoneNumber("0909999999");
            user.setAddress("123 Đường ABC, TP.HCM");
            user.setUserPicture("avatar_updated.jpg");
            user.setDateOfBirth(LocalDate.of(1995, 5, 20));
            user.setGender(User.Gender.male);
            user.setDescription("Đã cập nhật hồ sơ");

            // Gọi hàm update
            boolean result = userDAO.updateUser(user);

            if (result) {
                System.out.println("✔️ Cập nhật thành công");
            } else {
                System.out.println("❌ Cập nhật thất bại");
            }
        } else {
            System.out.println("⚠️ Không tìm thấy user để cập nhật");
        }
    }
}
