package dal;

import entity.DBContext;
import entity.Material;
import entity.MaterialDetails;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO extends DBContext {

    // Phuong thuc tao ket noi toi database MySQL
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "12345";
        return java.sql.DriverManager.getConnection(url, username, password);
    }

    // Lay chi tiet thong tin cua 1 vat tu theo id
    public MaterialDetails getMaterialById(int id) throws SQLException {
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.condition_percentage, m.price, m.quantity, m.category_id, m.supplier_id, m.created_at, m.updated_at, m.disable, c.category_name, c.description as category_description, s.supplier_name, s.contact_info, s.address, s.phone_number, s.email " +
                "FROM Materials m " +
                "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id " +
                "WHERE m.material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tao doi tuong Material va set cac thuoc tinh tu CSDL
                    Material material = new Material();
                    material.setMaterialId(rs.getInt("material_id"));
                    material.setMaterialCode(rs.getString("material_code"));
                    material.setMaterialName(rs.getString("material_name"));
                    material.setMaterialsUrl(rs.getString("materials_url"));
                    material.setMaterialStatus(rs.getString("material_status"));
                    material.setConditionPercentage(rs.getInt("condition_percentage"));
                    material.setPrice(rs.getBigDecimal("price"));
                    material.setQuantity(rs.getInt("quantity"));
                    material.setCategoryId(rs.getInt("category_id"));
                    material.setSupplierId(rs.getInt("supplier_id"));
                    material.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    material.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    material.setDisable(rs.getBoolean("disable"));

                    // Tao doi tuong MaterialDetails tu Material
                    MaterialDetails details = new MaterialDetails(material);
                    details.setCategoryName(rs.getString("category_name"));
                    details.setCategoryDescription(rs.getString("category_description"));
                    details.setSupplierName(rs.getString("supplier_name"));
                    details.setSupplierContact(rs.getString("contact_info"));
                    details.setSupplierAddress(rs.getString("address"));
                    details.setSupplierPhone(rs.getString("phone_number"));
                    details.setSupplierEmail(rs.getString("email"));
                    return details;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In loi ra console
        }
        return null;
    }

    // Lay danh sach vat tu co phan trang, tim kiem, loc trang thai, sap xep
    public List<Material> getMaterials(String searchTerm, String status, String sortBy, int page, int pageSize) {
        List<Material> list = new ArrayList<>();
        String query = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.condition_percentage, m.price, m.quantity, m.category_id, m.supplier_id, m.created_at, m.updated_at, m.disable " +
                "FROM Materials m WHERE m.disable = 0 ";
        // Tim kiem theo ten hoac ma vat tu
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            query += "AND (m.material_name LIKE ? OR m.material_code LIKE ?) ";
        }
        // Loc theo trang thai vat tu
        if (status != null && !status.trim().isEmpty()) {
            query += "AND m.material_status = ? ";
        }
        // Sap xep theo thuoc tinh
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy) {
                case "name_asc": query += "ORDER BY m.material_name ASC "; break;
                case "name_desc": query += "ORDER BY m.material_name DESC "; break;
                case "price_asc": query += "ORDER BY m.price ASC "; break;
                case "price_desc": query += "ORDER BY m.price DESC "; break;
                case "condition_asc": query += "ORDER BY m.condition_percentage ASC "; break;
                case "condition_desc": query += "ORDER BY m.condition_percentage DESC "; break;
                case "quantity_asc": query += "ORDER BY m.quantity ASC "; break;
                case "quantity_desc": query += "ORDER BY m.quantity DESC "; break;
                default: query += "ORDER BY m.material_id DESC ";
            }
        } else {
            query += "ORDER BY m.material_id DESC ";
        }
        query += "LIMIT ? OFFSET ?"; // Phan trang

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchTerm + "%");
                ps.setString(paramIndex++, "%" + searchTerm + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Tao doi tuong Material va them vao danh sach
                    Material material = new Material();
                    material.setMaterialId(rs.getInt("material_id"));
                    material.setMaterialCode(rs.getString("material_code"));
                    material.setMaterialName(rs.getString("material_name"));
                    material.setMaterialsUrl(rs.getString("materials_url"));
                    material.setMaterialStatus(rs.getString("material_status"));
                    material.setConditionPercentage(rs.getInt("condition_percentage"));
                    material.setPrice(rs.getBigDecimal("price"));
                    material.setQuantity(rs.getInt("quantity"));
                    material.setCategoryId(rs.getInt("category_id"));
                    material.setSupplierId(rs.getInt("supplier_id"));
                    material.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    material.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    material.setDisable(rs.getBoolean("disable"));
                    list.add(material);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Dem tong so vat tu (phuc vu phan trang)
    public int countMaterials(String searchTerm, String status) {
        String sql = "SELECT COUNT(*) FROM Materials WHERE disable = 0 ";
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql += "AND (material_name LIKE ? OR material_code LIKE ?) ";
        }
        if (status != null && !status.trim().isEmpty()) {
            sql += "AND material_status = ? ";
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchTerm + "%");
                ps.setString(paramIndex++, "%" + searchTerm + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Lay gia tri dem duoc
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tao moi 1 vat tu trong database
    public boolean createMaterial(Material material) {
        String sql = "INSERT INTO Materials (material_code, material_name, materials_url, material_status, condition_percentage, price, quantity, category_id, supplier_id, created_at, updated_at, disable) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, material.getMaterialCode());
            st.setString(2, material.getMaterialName());
            st.setString(3, material.getMaterialsUrl());
            st.setString(4, material.getMaterialStatus());
            st.setInt(5, material.getConditionPercentage());
            st.setBigDecimal(6, material.getPrice());
            st.setInt(7, material.getQuantity());
            st.setInt(8, material.getCategoryId());
            st.setObject(9, material.getSupplierId());
            st.setObject(10, java.sql.Timestamp.valueOf(material.getCreatedAt()));
            st.setObject(11, java.sql.Timestamp.valueOf(material.getUpdatedAt()));
            st.setBoolean(12, material.isDisable());
            return st.executeUpdate() > 0; // Tra ve true neu insert thanh cong
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cap nhat thong tin cua 1 vat tu
    public boolean updateMaterial(Material material) {
        String sql = "UPDATE Materials SET material_code = ?, material_name = ?, materials_url = ?, material_status = ?, condition_percentage = ?, price = ?, quantity = ?, category_id = ?, supplier_id = ?, updated_at = ?, disable = ? WHERE material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, material.getMaterialCode());
            st.setString(2, material.getMaterialName());
            st.setString(3, material.getMaterialsUrl());
            st.setString(4, material.getMaterialStatus());
            st.setInt(5, material.getConditionPercentage());
            st.setBigDecimal(6, material.getPrice());
            st.setInt(7, material.getQuantity());
            st.setInt(8, material.getCategoryId());
            st.setObject(9, material.getSupplierId());
            st.setObject(10, java.sql.Timestamp.valueOf(LocalDateTime.now())); // Cap nhat ngay sua
            st.setBoolean(11, material.isDisable());
            st.setInt(12, material.getMaterialId());
            return st.executeUpdate() > 0; // Tra ve true neu update thanh cong
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xoa hoan toan vat tu (delete cung)
    public boolean deleteMaterial(int materialId) {
        String sql = "UPDATE Materials SET disable = 1 WHERE material_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, materialId);
            return st.executeUpdate() > 0; // Tra ve true neu xoa thanh cong
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
