package entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Hoang1062004";

    protected Connection connection;

    public DBContext() {
        connect(); // Tạo kết nối khi khởi tạo
    }

    // Phương thức tạo kết nối mới
    private void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Kết nối thành công tới MySQL!");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("❌ Lỗi kết nối MySQL: " + ex.getMessage());
        }
    }

    // Luôn đảm bảo trả về Connection đang hoạt động
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect(); // Kết nối lại nếu cần
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra kết nối: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        new DBContext();
    }
}
