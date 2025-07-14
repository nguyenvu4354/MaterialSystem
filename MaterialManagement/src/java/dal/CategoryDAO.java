package dal;

import entity.Category;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {
    private String lastError;

    public CategoryDAO() {
        super();
        lastError = null;
    }

    public String getLastError() {
        return lastError;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null,
                    rs.getTimestamp("created_at"),
                    rs.getInt("disable"),
                    rs.getString("status"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("code")
                );
                list.add(c);
            }
        } catch (Exception e) {
            lastError = "Error in getAllCategories: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE category_id = ? AND disable = 0";
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                return category;
            }
        } catch (Exception e) {
            lastError = "Error getCategoryById: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertCategory(Category category) {
        if (connection == null) {
            lastError = "Kết nối cơ sở dữ liệu bị null";
            System.out.println("❌ " + lastError);
            return false;
        }
        String sql = "INSERT INTO Categories (category_name, parent_id, created_at, disable, status, description, priority, code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getCategory_name());
            ps.setObject(2, category.getParent_id());
            ps.setTimestamp(3, category.getCreated_at());
            ps.setInt(4, category.getDisable());
            ps.setString(5, category.getStatus());
            ps.setString(6, category.getDescription() != null ? category.getDescription() : "");
            ps.setString(7, category.getPriority());
            ps.setString(8, category.getCode());
            int rowsAffected = ps.executeUpdate();
            System.out.println("CategoryDAO - Insert executed, rows affected: " + rowsAffected + ", Category: " + category.getCategory_name());
            if (rowsAffected > 0) {
                System.out.println("✅ Thêm danh mục thành công: " + category.getCategory_name());
                return true;
            } else {
                lastError = "Không có hàng nào được thêm vào. Kiểm tra dữ liệu đầu vào.";
                System.out.println("❌ " + lastError);
                return false;
            }
        } catch (Exception e) {
            lastError = "Lỗi khi thêm danh mục: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET category_name = ?, parent_id = ?, created_at = ?, disable = ?, status = ?, description = ?, priority = ?, code = ? WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getCategory_name());
            ps.setObject(2, category.getParent_id());
            ps.setTimestamp(3, category.getCreated_at());
            ps.setInt(4, category.getDisable());
            ps.setString(5, category.getStatus());
            ps.setString(6, category.getDescription() != null ? category.getDescription() : "");
            ps.setString(7, category.getPriority());
            ps.setString(8, category.getCode());
            ps.setInt(9, category.getCategory_id());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Successfully updated category: ID = " + category.getCategory_id());
                return true;
            } else {
                lastError = "No category found to update: ID = " + category.getCategory_id();
                System.out.println("❌ " + lastError);
                return false;
            }
        } catch (Exception e) {
            lastError = "Error in updateCategory: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "UPDATE Categories SET disable = 1 WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Successfully soft-deleted category: ID = " + categoryId);
                return true;
            } else {
                lastError = "No category found to soft-delete: ID = " + categoryId;
                System.out.println("❌ " + lastError);
                return false;
            }
        } catch (Exception e) {
            lastError = "Error deleteCategory: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm danh mục theo nhiều điều kiện kết hợp: code, priority, status, sort
     */
    public List<Category> searchCategories(String code, String priority, String status, String sortBy, String sortOrder) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Categories WHERE disable = 0");
        List<Object> params = new ArrayList<>();
        if (code != null && !code.trim().isEmpty()) {
            sql.append(" AND code LIKE ?");
            params.add("%" + code.trim() + "%");
        }
        if (priority != null && !priority.trim().isEmpty()) {
            sql.append(" AND priority = ?");
            params.add(priority.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }
        // Xử lý sort
        if (sortBy != null && !sortBy.isEmpty()) {
            String col = "category_name";
            switch (sortBy) {
                case "name": col = "category_name"; break;
                case "status": col = "status"; break;
                case "code": col = "code"; break;
                case "priority": col = "priority"; break;
            }
            String order = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
            sql.append(" ORDER BY ").append(col).append(" ").append(order);
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategories: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();

        System.out.println("===== Category List =====");
        for (Category category : categories) {
            System.out.println("ID: " + category.getCategory_id());
            System.out.println("Category Name: " + category.getCategory_name());
            System.out.println("Description: " + category.getDescription());
            System.out.println("Parent ID: " + category.getParent_id());
            System.out.println("Disable: " + category.getDisable());
            System.out.println("Status: " + category.getStatus());
            System.out.println("Priority: " + category.getPriority());
            System.out.println("Code: " + category.getCode());
            System.out.println("Created At: " + category.getCreated_at());
            System.out.println("--------------------------------");
        }
    }
}