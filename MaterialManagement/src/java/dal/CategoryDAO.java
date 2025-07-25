package dal;

import entity.Category;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

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
     * Tìm kiếm danh mục theo nhiều điều kiện kết hợp: name (LIKE), priority, status, sort
     */
    public List<Category> searchCategories(String name, String priority, String status, String sortBy, String sortOrder) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Categories WHERE disable = 0");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND category_name LIKE ?");
            params.add("%" + name.trim() + "%");
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

    // Lấy số lớn nhất sau tiền tố 'CAT' trong mã category
    public int getMaxCategoryNumber() {
        int max = 0;
        String sql = "SELECT MAX(CAST(SUBSTRING(code, 4) AS UNSIGNED)) AS max_num FROM categories WHERE code LIKE 'CAT%'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                max = rs.getInt("max_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    /**
     * Lấy tên của parent category dựa trên parent_id
     */
    public String getParentCategoryName(Integer parentId) {
        if (parentId == null) {
            return "None";
        }
        String sql = "SELECT category_name FROM Categories WHERE category_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("category_name");
            }
        } catch (Exception e) {
            lastError = "Error getParentCategoryName: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return "Unknown";
    }

    /**
     * Kiểm tra xem category name đã tồn tại chưa (không phân biệt hoa thường)
     */
    public boolean isCategoryNameExists(String categoryName, Integer excludeCategoryId) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE LOWER(category_name) = LOWER(?) AND disable = 0";
        if (excludeCategoryId != null) {
            sql += " AND category_id != ?";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoryName.trim());
            if (excludeCategoryId != null) {
                ps.setInt(2, excludeCategoryId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            lastError = "Error isCategoryNameExists: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy cây danh mục phân cấp, giới hạn số cấp và số con mỗi node
     */
    public List<Category> getCategoryTree(int maxLevel, int maxChildrenPerNode) {
        List<Category> all = getAllCategories();
        List<Category> tree = new ArrayList<>();
        buildTree(tree, all, null, 1, maxLevel, maxChildrenPerNode);
        return tree;
    }

    private void buildTree(List<Category> tree, List<Category> all, Integer parentId, int level, int maxLevel, int maxChildren) {
        if (level > maxLevel) return;
        int count = 0;
        for (Category c : all) {
            if ((parentId == null && c.getParent_id() == null) || (parentId != null && parentId.equals(c.getParent_id()))) {
                tree.add(c);
                count++;
                if (count >= maxChildren) break;
                buildTree(tree, all, c.getCategory_id(), level + 1, maxLevel, maxChildren);
            }
        }
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