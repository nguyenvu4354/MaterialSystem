package dal;

import entity.Category;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategory_id(rs.getInt("category_id"));
                category.setCategory_name(rs.getString("category_name"));
                category.setParent_id(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
                category.setCreated_at(rs.getTimestamp("created_at"));
                category.setDisable(rs.getInt("disable"));
                category.setStatus(rs.getString("status"));
                category.setDescription(rs.getString("description"));
                category.setPriority(rs.getInt("priority"));
                list.add(category);
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllCategories: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Category category = new Category();
                category.setCategory_id(rs.getInt("category_id"));
                category.setCategory_name(rs.getString("category_name"));
                category.setParent_id(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
                category.setCreated_at(rs.getTimestamp("created_at"));
                category.setDisable(rs.getInt("disable"));
                category.setStatus(rs.getString("status"));
                category.setDescription(rs.getString("description"));
                category.setPriority(rs.getInt("priority"));
                return category;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getCategoryById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean createCategory(Category category) {
        String sql = "INSERT INTO Categories (category_name, parent_id, created_at, disable, status, description, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getCategory_name());
            if (category.getParent_id() != null) {
                ps.setInt(2, category.getParent_id());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(3, category.getCreated_at());
            ps.setInt(4, category.getDisable());
            ps.setString(5, category.getStatus());
            ps.setString(6, category.getDescription());
            ps.setInt(7, category.getPriority());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Lỗi createCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET category_name = ?, parent_id = ?, disable = ?, status = ?, description = ?, priority = ? WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getCategory_name());
            if (category.getParent_id() != null) {
                ps.setInt(2, category.getParent_id());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, category.getDisable());
            ps.setString(4, category.getStatus());
            ps.setString(5, category.getDescription());
            ps.setInt(6, category.getPriority());
            ps.setInt(7, category.getCategory_id());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Lỗi deleteCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

        public static void main(String[] args) {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();

        System.out.println("===== Danh sách danh mục =====");
        for (Category category : categories) {
            System.out.println("ID: " + category.getCategory_id());
            System.out.println("Tên danh mục: " + category.getCategory_name());
            System.out.println("Mô tả: " + category.getDescription());
            System.out.println("Parent ID: " + category.getParent_id());
            System.out.println("Disable: " + category.getDisable());
            System.out.println("Trạng thái: " + category.getStatus());
            System.out.println("Priority: " + category.getPriority());
            System.out.println("Ngày tạo: " + category.getCreated_at());
            System.out.println("--------------------------------");
        }
    }
}
