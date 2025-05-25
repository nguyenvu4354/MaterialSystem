
import entity.DBContext;
import entity.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DBContext{
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    // Phương thức lấy chi tiết sản phẩm theo materialId
    public Product getProductById(int materialId) {
        Product product = null;
        String sql = "SELECT * FROM Materials WHERE material_id = ? AND disable = 0";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = new Product(
                    rs.getInt("material_id"),
                    rs.getString("material_code"),
                    rs.getString("material_name"),
                    rs.getString("materials_url"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getDouble("rating"),
                    rs.getInt("category_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    // Phương thức lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE disable = 0";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("material_id"),
                    rs.getString("material_code"),
                    rs.getString("material_name"),
                    rs.getString("materials_url"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getDouble("rating"),
                    rs.getInt("category_id")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        List<Product> list = dao.getAllProducts();
        for (Product product : list) {
            System.out.println(product.toString());
        }
    }
}