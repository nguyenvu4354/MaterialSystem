/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.DBContext;
import entity.Material;
import entity.MaterialDetails;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class MaterialDAO extends DBContext{
    
    public MaterialDetails getMaterialById(int id) throws SQLException {
        String sql = "SELECT m.*, c.category_name, c.description as category_description, " +
                    "s.supplier_name, s.contact_info, s.address, s.phone_number, s.email " +
                    "FROM Materials m " +
                    "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                    "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id " +
                    "WHERE m.material_id = ? AND m.disable = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialCode(rs.getString("material_code"));
                material.setMaterialName(rs.getString("material_name"));
                String materialsUrl = rs.getString("materials_url");
                if (materialsUrl == null || materialsUrl.trim().isEmpty()) {
                    material.setMaterialsUrl("https://placehold.co/200x200?text=" + material.getMaterialCode());
                } else {
                    material.setMaterialsUrl(materialsUrl);
                }
                material.setMaterialStatus(rs.getString("material_status"));
                material.setConditionPercentage(rs.getInt("condition_percentage"));
                material.setPrice(rs.getBigDecimal("price"));
                material.setQuantity(rs.getInt("quantity"));
                material.setCategoryId(rs.getInt("category_id"));
                material.setSupplierId(rs.getInt("supplier_id"));
                material.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                material.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                material.setDisable(rs.getBoolean("disable"));
                
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
        return null;
    }
    
    public int getTotalMaterials() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Materials WHERE disable = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Material> getMaterialsWithPagination(int page, int pageSize) throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.*, c.category_name, s.supplier_name " +
                    "FROM Materials m " +
                    "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                    "LEFT JOIN Suppliers s ON m.supplier_id = s.supplier_id " +
                    "WHERE m.disable = 0 " +
                    "ORDER BY m.material_id " +
                    "LIMIT ? OFFSET ?";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, pageSize);
            st.setInt(2, (page - 1) * pageSize);
            
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialCode(rs.getString("material_code"));
                material.setMaterialName(rs.getString("material_name"));
                String materialsUrl = rs.getString("materials_url");
                if (materialsUrl == null || materialsUrl.trim().isEmpty()) {
                    material.setMaterialsUrl("https://placehold.co/48x48?text=" + material.getMaterialCode());
                } else {
                    material.setMaterialsUrl(materialsUrl);
                }
                material.setMaterialStatus(rs.getString("material_status"));
                material.setConditionPercentage(rs.getInt("condition_percentage"));
                material.setPrice(rs.getBigDecimal("price"));
                material.setQuantity(rs.getInt("quantity"));
                material.setCategoryId(rs.getInt("category_id"));
                material.setSupplierId(rs.getInt("supplier_id"));
                material.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                material.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                material.setDisable(rs.getBoolean("disable"));
                materials.add(material);
            }
        }
        return materials;
    }
    
    public boolean updateMaterial(Material material) throws SQLException {
        String sql = "UPDATE Materials SET material_code = ?, material_name = ?, materials_url = ?, "
                + "material_status = ?, condition_percentage = ?, price = ?, quantity = ?, "
                + "category_id = ?, supplier_id = ?, updated_at = ? "
                + "WHERE material_id = ?";
        
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, material.getMaterialCode());
            st.setString(2, material.getMaterialName());
            st.setString(3, material.getMaterialsUrl());
            st.setString(4, material.getMaterialStatus());
            st.setInt(5, material.getConditionPercentage());
            st.setBigDecimal(6, material.getPrice());
            st.setInt(7, material.getQuantity());
            st.setInt(8, material.getCategoryId());
            st.setObject(9, material.getSupplierId());
            st.setObject(10, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            st.setInt(11, material.getMaterialId());
            
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
