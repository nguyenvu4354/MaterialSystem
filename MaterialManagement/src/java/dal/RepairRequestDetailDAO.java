/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.Category;
import entity.DBContext;
import entity.Material;
import entity.RepairRequestDetail;
import entity.Unit;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequestDetailDAO extends DBContext {

    public List<RepairRequestDetail> getRepairRequestDetailsByRequestId(int repairRequestId) {
        List<RepairRequestDetail> list = new ArrayList<>();
        String sql = """
        SELECT 
            rrd.detail_id,
            rrd.repair_request_id,
            rrd.material_id,
            rrd.quantity,
            rrd.damage_description,
            rrd.repair_cost,
            rrd.created_at AS detail_created_at,
            rrd.updated_at AS detail_updated_at,

            m.material_code,
            m.material_name,
            m.materials_url,
            m.material_status,
            m.condition_percentage,
            m.price,
            m.created_at AS material_created_at,
            m.updated_at AS material_updated_at,
            m.disable,

            c.category_id,
            c.category_name,

            u.unit_id,
            u.unit_name

        FROM Repair_Request_Details rrd
        JOIN Materials m ON rrd.material_id = m.material_id
        LEFT JOIN Categories c ON m.category_id = c.category_id
        LEFT JOIN Units u ON m.unit_id = u.unit_id
        WHERE rrd.repair_request_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();

                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getDouble("repair_cost"));
                detail.setCreatedAt(rs.getTimestamp("detail_created_at"));
                detail.setUpdatedAt(rs.getTimestamp("detail_updated_at"));

                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialCode(rs.getString("material_code"));
                material.setMaterialName(rs.getString("material_name"));
                material.setMaterialsUrl(rs.getString("materials_url"));
                material.setMaterialStatus(rs.getString("material_status"));
                material.setConditionPercentage(rs.getInt("condition_percentage"));
                material.setPrice(rs.getDouble("price"));
                material.setCreatedAt(rs.getTimestamp("material_created_at"));
                material.setUpdatedAt(rs.getTimestamp("material_updated_at"));
                material.setDisable(rs.getBoolean("disable"));

                Category category = new Category();
                category.setCategory_id(rs.getInt("category_id"));
                category.setCategory_name(rs.getString("category_name"));

                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));

                material.setCategory(category);
                material.setUnit(unit);
                detail.setMaterial(material);

                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void main(String[] args) {
        RepairRequestDetailDAO dao = new RepairRequestDetailDAO();
        List<RepairRequestDetail> list = dao.getRepairRequestDetailsByRequestId(1); // ví dụ: role_id = 3 là "Thủ kho"
        for (RepairRequestDetail d : list) {
            System.out.println(d.toString());
        }
    }
}
