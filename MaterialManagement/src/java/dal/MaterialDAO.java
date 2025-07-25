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

    public List<Material> searchMaterials(String keyword, String status, int pageIndex, int pageSize, String sortOption) {
        if (sortOption == null) sortOption = "";
        List<Material> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT m.*, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS quantity ")
                    .append("FROM materials m ")
                    .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
                    .append("LEFT JOIN units u ON m.unit_id = u.unit_id ")
                    .append("LEFT JOIN inventory i ON m.material_id = i.material_id ")
                    .append("WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND m.material_name LIKE ? ");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND m.material_status = ? ");
                params.add(status);
            }
            String sortBy = "m.material_code"; 
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
                m.setQuantity(rs.getInt("quantity"));

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
                    + "m.material_status, "
                    + "c.category_id, c.category_name, c.description AS category_description, "
                    + "u.unit_id, u.unit_name, u.symbol, u.description AS unit_description, "
                    + "m.created_at, m.updated_at, m.disable, "
                    + "IFNULL(i.stock, 0) AS quantity "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN units u ON m.unit_id = u.unit_id "
                    + "LEFT JOIN inventory i ON m.material_id = i.material_id "
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
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                m.setQuantity(rs.getInt("quantity"));

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
                + "material_status = ?, category_id = ?, "
                + "unit_id = ?, updated_at = CURRENT_TIMESTAMP, disable = ? WHERE material_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, m.getMaterialCode());
            st.setString(2, m.getMaterialName());
            st.setString(3, m.getMaterialsUrl());
            st.setString(4, m.getMaterialStatus());
            // Bỏ setDouble cho price, cập nhật lại chỉ số các tham số phía sau
            st.setInt(5, m.getCategory().getCategory_id());
            st.setInt(6, m.getUnit().getId());
            st.setBoolean(7, m.isDisable());
            st.setInt(8, m.getMaterialId());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addMaterial(Material m) {
        try {
            String sql = "INSERT INTO materials ("
                    + "material_code, material_name, materials_url, material_status, "
                    + "category_id, unit_id, disable"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, m.getMaterialCode());
            ps.setString(2, m.getMaterialName());
            ps.setString(3, m.getMaterialsUrl());
            ps.setString(4, m.getMaterialStatus());
            ps.setInt(5, m.getCategory().getCategory_id());
            ps.setInt(6, m.getUnit().getId());
            ps.setBoolean(7, m.isDisable());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Phương thức lấy chi tiết sản phẩm theo materialId
    public Material getProductById(int materialId) {
        Material product = null;
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
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
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.created_at, m.updated_at, m.disable, u.unit_id, u.unit_name, c.category_id, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id";

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
    public static void main(String[] args) {
        MaterialDAO dao = new MaterialDAO();
        List <Material> list = dao.getAllProducts();
        for (Material material : list) {
            System.out.println(material.toString());
        }
    }

    // Phương thức tìm kiếm sản phẩm theo tên (có thể chứa một phần tên)
    public List<Material> searchProductsByName(String keyword) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.material_name LIKE ? AND m.disable = 0";

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
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.material_code LIKE ? AND m.disable = 0";

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
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.category_id = ? AND m.disable = 0";

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
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
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

    public List<Material> getMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT material_id, material_name FROM Materials WHERE disable = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialName(rs.getString("material_name"));
                materials.add(material);
            }
            System.out.println("✅ Lấy danh sách vật tư thành công, số lượng: " + materials.size());
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getMaterials: " + e.getMessage());
            throw e;
        }
        return materials;
    }

    // Kiểm tra mã vật tư đã tồn tại chưa
    public boolean isMaterialCodeExists(String materialCode) {
        String sql = "SELECT 1 FROM materials WHERE material_code = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialCode);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getMaterialIdByName(String name) {
        int materialId = 0;
        try {
            String sql = "SELECT material_id FROM materials WHERE material_name = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                materialId = rs.getInt("material_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materialId;
    }

    // Method để lấy Map chứa thông tin hình ảnh của các material
    public java.util.Map<Integer, String> getMaterialImages(List<Integer> materialIds) {
        java.util.Map<Integer, String> materialImages = new java.util.HashMap<>();
        if (materialIds == null || materialIds.isEmpty()) {
            return materialImages;
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT material_id, materials_url FROM materials WHERE material_id IN (");

            // Tạo placeholders cho IN clause
            for (int i = 0; i < materialIds.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < materialIds.size(); i++) {
                ps.setInt(i + 1, materialIds.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int materialId = rs.getInt("material_id");
                String materialsUrl = rs.getString("materials_url");
                materialImages.put(materialId, materialsUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materialImages;
    }

    // Lấy số lớn nhất sau tiền tố 'MAT' trong mã vật tư
    public int getMaxMaterialNumber() {
        int max = 0;
        String sql = "SELECT MAX(CAST(SUBSTRING(material_code, 4) AS UNSIGNED)) AS max_num FROM materials WHERE material_code LIKE 'MAT%'";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                max = rs.getInt("max_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialName(rs.getString("material_name"));
                // ... set các trường khác nếu cần
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

//    public static void main(String[] args) {
//        MaterialDAO md = new MaterialDAO();
//
//        // Lấy thông tin vật tư trước khi update
//        Material m = md.getInformation(4);
//        System.out.println("Trước khi update:");
//        System.out.println("Tên: " + m.getMaterialName());
//        System.out.println("Updated_at: " + m.getUpdatedAt());
//
//        // Thay đổi tên vật tư (hoặc trường bất kỳ)
//        m.setMaterialName(m.getMaterialName() + " (test update)");
//
//        // Gọi hàm update
//        md.updateMaterial(m);
//
//        // Lấy lại thông tin vật tư sau khi update
//        Material m2 = md.getInformation(5);
//        System.out.println("Sau khi update:");
//        System.out.println("Tên: " + m2.getMaterialName());
//        System.out.println("Updated_at: " + m2.getUpdatedAt());
//    }

    public List<Material> searchMaterialsByPrice(Double minPrice, Double maxPrice, int pageIndex, int pageSize, String sortOption) {
        List<Material> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT m.*, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS quantity ")
                    .append("FROM materials m ")
                    .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
                    .append("LEFT JOIN units u ON m.unit_id = u.unit_id ")
                    .append("LEFT JOIN inventory i ON m.material_id = i.material_id ")
                    .append("WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            // Xử lý sắp xếp (bỏ price)
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
            }
            sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder).append(" ");

            // Phân trang
            sql.append("LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((pageIndex - 1) * pageSize);

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            // Gán tham số
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
                m.setQuantity(rs.getInt("quantity"));

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

    // Kiểm tra tên vật tư và trạng thái đã tồn tại chưa
    public boolean isMaterialNameAndStatusExists(String materialName, String materialStatus) {
        String sql = "SELECT 1 FROM materials WHERE material_name = ? AND material_status = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialName);
            ps.setString(2, materialStatus);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy vật tư theo tên và trạng thái
    public Material getInformationByNameAndStatus(String name, String status) {
        String sql = "SELECT * FROM materials WHERE material_name = ? AND material_status = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                // Set category
                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                m.setCategory(c);
                // Set unit
                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                m.setUnit(u);
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
