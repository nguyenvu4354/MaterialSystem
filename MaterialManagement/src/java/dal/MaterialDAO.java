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
            sql.append("SELECT m.*, c.category_name, s.supplier_name, u.unit_name ")
                    .append("FROM materials m ")
                    .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
                    .append("LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id ")
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

                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                m.setSupplier(s);

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
        try {
            String sql = "UPDATE materials m\n"
                    + "SET disable = 1\n"
                    + "WHERE m.material_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, materialId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Material getInformation(int materialId) {
        Material m = new Material();
        try {
            String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, "
                    + "m.material_status, m.condition_percentage, m.price, "
                    + "c.category_id, c.category_name, c.description AS category_description, "
                    + "s.supplier_id, s.supplier_name, s.address, s.phone_number, s.email, s.contact_info, s.description AS supplier_description, s.tax_id, "
                    + "u.unit_id, u.unit_name, u.symbol, u.description AS unit_description, "
                    + "m.created_at, m.updated_at, m.disable "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id "
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

                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setAddress(rs.getString("address"));
                s.setPhoneNumber(rs.getString("phone_number"));
                s.setEmail(rs.getString("email"));
                s.setContactInfo(rs.getString("contact_info"));
                s.setDescription(rs.getString("supplier_description"));
                s.setTaxId(rs.getString("tax_id"));
                m.setSupplier(s);

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
                + "supplier_id = ?, unit_id = ?, updated_at = CURRENT_TIMESTAMP, disable = ? WHERE material_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, m.getMaterialCode());
            st.setString(2, m.getMaterialName());
            st.setString(3, m.getMaterialsUrl());
            st.setString(4, m.getMaterialStatus());
            st.setInt(5, m.getConditionPercentage());
            st.setDouble(6, m.getPrice());
            st.setInt(7, m.getCategory().getCategory_id());
            st.setInt(8, m.getSupplier().getSupplierId());
            st.setInt(9, m.getUnit().getId());
            st.setBoolean(10, m.isDisable());
            st.setInt(11, m.getMaterialId());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addMaterial(Material m) {
        try {
            String sql = "INSERT INTO materials ("
                    + "material_code, material_name, materials_url, material_status, "
                    + "condition_percentage, price, category_id, supplier_id, unit_id, disable"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, m.getMaterialCode());
            ps.setString(2, m.getMaterialName());
            ps.setString(3, m.getMaterialsUrl());
            ps.setString(4, m.getMaterialStatus());
            ps.setInt(5, m.getConditionPercentage());
            ps.setDouble(6, m.getPrice());
            ps.setInt(7, m.getCategory().getCategory_id());
            ps.setInt(8, m.getSupplier().getSupplierId());
            ps.setInt(9, m.getUnit().getId());
            ps.setBoolean(10, m.isDisable());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    

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
