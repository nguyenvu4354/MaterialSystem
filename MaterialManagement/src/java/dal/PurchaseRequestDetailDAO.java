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
            String sql = "SELECT * FROM material_management.purchase_request_details " +
                        "WHERE purchase_request_id = ? " +
                        "ORDER BY detail_id " +
                        "LIMIT ? OFFSET ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequestDetail prd = new PurchaseRequestDetail();
                prd.setPurchaseRequestDetailId(rs.getInt("detail_id"));
                prd.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                prd.setMaterialName(rs.getString("material_name"));
                prd.setCategoryId(rs.getInt("category_id"));
                prd.setQuantity(rs.getInt("quantity"));
                prd.setNotes(rs.getString("notes"));
                prd.setCreatedAt(rs.getTimestamp("created_at"));
                prd.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(prd);
            }
        } catch (SQLException e) {
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
