package dal;

import entity.Category;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE disable = 0";
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
                    rs.getInt("priority")
                );
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error in getAllCategories: " + e.getMessage());
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
                category.setPriority(rs.getInt("priority"));
                return category;
            }
        } catch (Exception e) {
            System.out.println("❌ Error getCategoryById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> searchCategoriesByName(String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_name LIKE ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
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
            System.out.println("❌ Error searchCategoriesByName: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByIdNameAndStatus(int categoryId, String keyword, String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_id = ? AND category_name LIKE ? AND status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, status);
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
            System.out.println("❌ Error searchCategoriesByIdNameAndStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByIdAndName(int categoryId, String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_id = ? AND category_name LIKE ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, "%" + keyword + "%");
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
            System.out.println("❌ Error searchCategoriesByIdAndName: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByIdAndStatus(int categoryId, String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_id = ? AND status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, status);
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
            System.out.println("❌ Error searchCategoriesByIdAndStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByNameAndStatus(String keyword, String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_name LIKE ? AND status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, status);
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
            System.out.println("❌ Error searchCategoriesByNameAndStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertCategory(Category category) {
        if (connection == null) {
            System.out.println("❌ Kết nối cơ sở dữ liệu bị null");
            return false;
        }
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
            System.out.println("Thực thi câu lệnh SQL: " + ps.toString());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Thêm danh mục thành công: " + category.getCategory_name());
                return true;
            } else {
                System.out.println("❌ Không có hàng nào được thêm vào.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi thêm danh mục: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET category_name = ?, parent_id = ?, created_at = ?, disable = ?, status = ?, description = ?, priority = ? WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, category.getCategory_name());
            ps.setObject(2, category.getParent_id());
            ps.setTimestamp(3, category.getCreated_at());
            ps.setInt(4, category.getDisable());
            ps.setString(5, category.getStatus());
            ps.setString(6, category.getDescription());
            ps.setInt(7, category.getPriority());
            ps.setInt(8, category.getCategory_id());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Successfully updated category: ID = " + category.getCategory_id());
                return true;
            } else {
                System.out.println("❌ No category found to update: ID = " + category.getCategory_id());
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error in updateCategory: " + e.getMessage());
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
                System.out.println("❌ No category found to soft-delete: ID = " + categoryId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error deleteCategory: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> searchCategoriesById(int categoryId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
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
            System.out.println("❌ Error searchCategoriesById: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByStatus(String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
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
            System.out.println("❌ Error searchCategoriesByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    

   
   

    public List<Category> getAllCategoriesSortedByName(String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE disable = 0 ORDER BY category_name " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
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
                    rs.getInt("priority")
                );
                list.add(c);
            }
        } catch (Exception e) {
            System.out.println("❌ Error in getAllCategoriesSortedByName: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> getAllCategoriesSortedByStatus(String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE disable = 0 ORDER BY status " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
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
                    rs.getInt("priority")
                );
                list.add(c);
            }
        } catch (Exception e) {
            System.out.println("❌ Error in getAllCategoriesSortedByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByNameSorted(String keyword, String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE category_name LIKE ? AND disable = 0 ORDER BY category_name " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
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
            System.out.println("❌ Error in searchCategoriesByNameSorted: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByStatusSorted(String status, String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE status = ? AND disable = 0 ORDER BY status " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
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
            System.out.println("❌ Error in searchCategoriesByStatusSorted: " + e.getMessage());
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
            System.out.println("Created At: " + category.getCreated_at());
            System.out.println("--------------------------------");
        }
    }
}