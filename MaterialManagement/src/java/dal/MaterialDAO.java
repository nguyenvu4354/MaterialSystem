package dal;

import entity.Category;
import entity.DBContext;
import entity.Material;
import entity.MaterialDetails;
import entity.Supplier;
import entity.Unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialDAO extends DBContext {

    // Phuong thuc tao ket noi toi database MySQL
    // search theo name hoặc code và status
    public List<Material> searchMaterials(String keyword, String status, int pageIndex, int pageSize, String sortOption) {
        List<Material> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT m.*, c.category_name, u.unit_name ")
                    .append("FROM materials m ")
                    .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
                    .append("LEFT JOIN units u ON m.unit_id = u.unit_id ")
                    .append("WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (m.material_name LIKE ? OR m.material_code LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND m.material_status = ? ");
                params.add(status);
            }
            String sortBy = "m.material_code"; // default
            String sortOrder = "ASC";

            switch (sortOption) {
                case "name_asc":
                    sortBy = "m.material_name";
                    sortOrder = "ASC";
                    break;
                case "name_desc":
                    sortBy = "m.material_name";
                    sortOrder = "DESC";
                    break;
                case "code_asc":
                    sortBy = "m.material_code";
                    sortOrder = "ASC";
                    break;
                case "code_desc":
                    sortBy = "m.material_code";
                    sortOrder = "DESC";
                    break;
                case "condition_asc":
                    sortBy = "m.condition_percentage";
                    sortOrder = "ASC";
                    break;
                case "condition_desc":
                    sortBy = "m.condition_percentage";
                    sortOrder = "DESC";
                    break;
            }
            sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder).append(" ");

            sql.append("LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((pageIndex - 1) * pageSize);

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setConditionPercentage(rs.getInt("condition_percentage"));
                m.setPrice(rs.getDouble("price"));

                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                m.setUnit(u);

                list.add(m);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public int countMaterials(String keyword, String status) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM materials m WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (m.material_name LIKE ? OR m.material_code LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND m.material_status = ? ");
                params.add(status);
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    public void deleteMaterial(int materialId) {
        String sql = "UPDATE Materials SET disable = 1 "
                + "WHERE material_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Material getInformation(int materialId) {
        Material m = new Material();
        try {
            String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, "
                    + "m.material_status, m.condition_percentage, m.price, "
                    + "c.category_id, c.category_name, c.description AS category_description, "
                    + "u.unit_id, u.unit_name, u.symbol, u.description AS unit_description, "
                    + "m.created_at, m.updated_at, m.disable "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN units u ON m.unit_id = u.unit_id "
                    + "WHERE m.material_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setConditionPercentage(rs.getInt("condition_percentage"));
                m.setPrice(rs.getDouble("price"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                c.setDescription(rs.getString("category_description"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                u.setSymbol(rs.getString("symbol"));
                u.setDescription(rs.getString("unit_description"));
                m.setUnit(u);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public void updateMaterial(Material m) {
        String sql = "UPDATE Materials SET material_code = ?, material_name = ?, materials_url = ?, "
                + "material_status = ?, condition_percentage = ?, price = ?, category_id = ?, "
                + "unit_id = ?, updated_at = CURRENT_TIMESTAMP, disable = ? WHERE material_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, m.getMaterialCode());
            st.setString(2, m.getMaterialName());
            st.setString(3, m.getMaterialsUrl());
            st.setString(4, m.getMaterialStatus());
            st.setInt(5, m.getConditionPercentage());
            st.setDouble(6, m.getPrice());
            st.setInt(7, m.getCategory().getCategory_id());
            st.setInt(8, m.getUnit().getId());
            st.setBoolean(9, m.isDisable());
            st.setInt(10, m.getMaterialId());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addMaterial(Material m) {
        try {
            String sql = "INSERT INTO materials ("
                    + "material_code, material_name, materials_url, material_status, "
                    + "condition_percentage, price, category_id, unit_id, disable"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, m.getMaterialCode());
            ps.setString(2, m.getMaterialName());
            ps.setString(3, m.getMaterialsUrl());
            ps.setString(4, m.getMaterialStatus());
            ps.setInt(5, m.getConditionPercentage());
            ps.setDouble(6, m.getPrice());
            ps.setInt(7, m.getCategory().getCategory_id());
            ps.setInt(8, m.getUnit().getId());
            ps.setBoolean(9, m.isDisable());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Phương thức lấy chi tiết sản phẩm theo materialId
    public Material getProductById(int materialId) {
        Material product = null;
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
                + "WHERE m.material_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                // Tạo đối tượng Material với các thuộc tính phù hợp
                product = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    // Phương thức lấy danh sách tất cả sản phẩm
    public List<Material> getAllProducts() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                // Tạo đối tượng Material với các thuộc tính phù hợp
                Material m = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Phương thức tìm kiếm sản phẩm theo tên (có thể chứa một phần tên)
    public List<Material> searchProductsByName(String keyword) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
                + "WHERE m.material_name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }
                // Tạo đối tượng Material với các thuộc tính phù hợp
                Material m = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức tìm kiếm sản phẩm theo mã
    public List<Material> searchProductsByCode(String materialCode) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
                + "WHERE m.material_code LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + materialCode + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                // Tạo đối tượng Material với các thuộc tính phù hợp
                Material m = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức tìm kiếm sản phẩm theo categoryId
    public List<Material> searchMaterialsByCategoriesID(int categoryId) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
                + "WHERE m.category_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                // Tạo đối tượng Material với các thuộc tính phù hợp
                Material m = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức sắp xếp sản phẩm theo tên
    public List<Material> sortMaterialsByName() {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
                + "ORDER BY m.material_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Tạo đối tượng Category
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                // Tạo đối tượng Unit (nếu có)
                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                // Tạo đối tượng Material với các thuộc tính phù hợp
                Material m = new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_code"),
                        rs.getString("material_name"),
                        rs.getString("materials_url"),
                        rs.getString("material_status"),
                        rs.getInt("condition_percentage"),
                        rs.getDouble("price"),
                        category,
                        unit,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getBoolean("disable")
                );
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Phương thức lấy sản phẩm theo supplierId
//    public List<Material> getMaterialsBySupplierId(int supplierId) {
//        List<Material> products = new ArrayList<>();
//        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
//                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
//                + "FROM Materials m "
//                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
//                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
//                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id "
//                + "WHERE m.supplier_id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, supplierId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                // Tạo đối tượng Category
//                Category category = new Category(
//                        rs.getInt("category_id"),
//                        rs.getString("category_name")
//                );
//
//                // Tạo đối tượng Unit (nếu có)
//                Unit unit = null;
//                if (rs.getObject("unit_id") != null) {
//                    unit = new Unit(
//                            rs.getInt("unit_id"),
//                            rs.getString("unit_name")
//                    );
//                }
//
//                // Tạo đối tượng Material với các thuộc tính phù hợp
//                Material m = new Material(
//                        rs.getInt("material_id"),
//                        rs.getString("material_code"),
//                        rs.getString("material_name"),
//                        rs.getString("materials_url"),
//                        rs.getString("material_status"),
//                        rs.getInt("condition_percentage"),
//                        rs.getDouble("price"),
//                        category,
//                        unit,
//                        rs.getTimestamp("created_at"),
//                        rs.getTimestamp("updated_at"),
//                        rs.getBoolean("disable")
//                );
//                products.add(m);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return products;
//    }

    // Phương thức lấy tất cả sản phẩm kèm thông tin nhà cung cấp
//    public List<Material> getAllMaterialsWithSupplier() {
//        List<Material> materials = new ArrayList<>();
//        String sql = "SELECT m.*, u.unit_name, c.category_name, s.supplier_name, s.contact_info, s.address, "
//                + "s.created_at AS supplier_created_at, s.phone_number, s.email, s.description, s.tax_id, s.disable AS supplier_disable "
//                + "FROM Materials m "
//                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
//                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
//                + "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                // Tạo đối tượng Category
//                Category category = new Category(
//                        rs.getInt("category_id"),
//                        rs.getString("category_name")
//                );
//
//                // Tạo đối tượng Unit (nếu có)
//                Unit unit = null;
//                if (rs.getObject("unit_id") != null) {
//                    unit = new Unit(
//                            rs.getInt("unit_id"),
//                            rs.getString("unit_name")
//                    );
//                }
//
//                // Tạo đối tượng Material với các thuộc tính phù hợp
//                Material m;
//                m = new Material(
//                        rs.getInt("material_id"),
//                        rs.getString("material_code"),
//                        rs.getString("material_name"),
//                        rs.getString("materials_url"),
//                        rs.getString("material_status"),
//                        rs.getInt("condition_percentage"),
//                        rs.getDouble("price"),
//                        category,
//                        unit,
//                        rs.getTimestamp("created_at"),
//                        rs.getTimestamp("updated_at"),
//                        rs.getBoolean("disable")
//                );
//                materials.add(m);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return materials;
//    }

    public static void main(String[] args) {
        MaterialDAO md = new MaterialDAO();

        // Lấy thông tin vật tư trước khi update
        Material m = md.getInformation(4);
        System.out.println("Trước khi update:");
        System.out.println("Tên: " + m.getMaterialName());
        System.out.println("Updated_at: " + m.getUpdatedAt());

        // Thay đổi tên vật tư (hoặc trường bất kỳ)
        m.setMaterialName(m.getMaterialName() + " (test update)");

        // Gọi hàm update
        md.updateMaterial(m);

        // Lấy lại thông tin vật tư sau khi update
        Material m2 = md.getInformation(5);
        System.out.println("Sau khi update:");
        System.out.println("Tên: " + m2.getMaterialName());
        System.out.println("Updated_at: " + m2.getUpdatedAt());
    }

}
