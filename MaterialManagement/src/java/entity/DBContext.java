package entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {
    protected Connection connection;
    public DBContext() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "123456";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Kết nối thành công tới MySQL!");

        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new DBContext();
    }
}
