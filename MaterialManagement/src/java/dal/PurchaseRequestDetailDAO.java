package dal;

import entity.PurchaseRequestDetail;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDetailDAO extends DBContext {


    public List<PurchaseRequestDetail> paginationOfDetails(int id, int page, int pageSize) {
        List<PurchaseRequestDetail> list = new ArrayList<>();
        try {
            String sql = "SELECT d.detail_id, d.purchase_request_id, d.material_id, d.quantity, d.notes, d.created_at, d.updated_at, " +
                         "m.material_name " + // Select material_name from materials table
                         "FROM material_management.purchase_request_details d " +
                         "LEFT JOIN material_management.materials m ON d.material_id = m.material_id " + // Join with materials table
                         "WHERE d.purchase_request_id = ? " +
                         "ORDER BY d.detail_id " +
                         "LIMIT ? OFFSET ?";
            System.out.println("SQL Query: " + sql);
            System.out.println("Parameters: id=" + id + ", page=" + page + ", pageSize=" + pageSize);
            
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                PurchaseRequestDetail prd = new PurchaseRequestDetail();
                prd.setPurchaseRequestDetailId(rs.getInt("detail_id"));
                prd.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                prd.setMaterialId(rs.getInt("material_id"));
                prd.setQuantity(rs.getInt("quantity"));
                prd.setNotes(rs.getString("notes"));
                prd.setCreatedAt(rs.getTimestamp("created_at"));
                prd.setUpdatedAt(rs.getTimestamp("updated_at"));
                prd.setMaterialName(rs.getString("material_name")); // Set material_name
                list.add(prd);
                System.out.println("Found detail: ID=" + prd.getPurchaseRequestDetailId() + ", Material=" + prd.getMaterialName() + ", Quantity=" + prd.getQuantity());
            }
            System.out.println("Total details found: " + count);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int count(int purchaseRequestId) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) FROM material_management.purchase_request_details WHERE purchase_request_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
