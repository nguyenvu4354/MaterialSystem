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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByName: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByCode(String code) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE code LIKE ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + code + "%");
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
            lastError = "Error searchCategoriesByCode: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByPriorityNameAndStatus(String priority, String keyword, String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE priority = ? AND category_name LIKE ? AND status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priority);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByPriorityNameAndStatus: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByPriorityAndName(String priority, String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE priority = ? AND category_name LIKE ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priority);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByPriorityAndName: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByPriorityAndStatus(String priority, String status) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE priority = ? AND status = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priority);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByPriorityAndStatus: " + e.getMessage();
            System.out.println("❌ " + lastError);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByNameAndStatus: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> searchCategoriesByPriority(String priority) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE priority = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priority);
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
            lastError = "Error searchCategoriesByPriority: " + e.getMessage();
            System.out.println("❌ " + lastError);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error searchCategoriesByStatus: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> getAllCategoriesSortedByName(String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE disable = 0 ORDER BY category_name " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
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
            lastError = "Error in getAllCategoriesSortedByName: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> getAllCategoriesSortedByStatus(String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE disable = 0 ORDER BY status " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
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
            lastError = "Error in getAllCategoriesSortedByStatus: " + e.getMessage();
            System.out.println("❌ " + lastError);
            e.printStackTrace();
        }
        return list;
    }

    public List<Category> getAllCategoriesSortedByCode(String order) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE disable = 0 ORDER BY code " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");
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
            lastError = "Error in getAllCategoriesSortedByCode: " + e.getMessage();
            System.out.println("❌ " + lastError);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error in searchCategoriesByNameSorted: " + e.getMessage();
            System.out.println("❌ " + lastError);
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
                category.setPriority(rs.getString("priority"));
                category.setCode(rs.getString("code"));
                list.add(category);
            }
        } catch (Exception e) {
            lastError = "Error in searchCategoriesByStatusSorted: " + e.getMessage();
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