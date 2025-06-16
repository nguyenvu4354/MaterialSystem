/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import entity.PurchaseRequestDetail;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class PurchaseRequestDetailDAO extends DBContext {

    public List<PurchaseRequestDetail> getPurchaseRequestDetailById(int purchaseRequestId) {
        List<PurchaseRequestDetail> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM material_management.purchase_request_details WHERE purchase_request_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, purchaseRequestId);

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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean addPurchaseRequestDetail(PurchaseRequestDetail prd) {
        try {
            String sql = "INSERT INTO material_management.purchase_request_details "
                    + "(purchase_request_id, material_name, category_id, quantity, notes) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, prd.getPurchaseRequestId());
            ps.setString(2, prd.getMaterialName());
            ps.setInt(3, prd.getCategoryId());
            ps.setInt(4, prd.getQuantity());
            ps.setString(5, prd.getNotes());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updatePurchaseRequestDetail(PurchaseRequestDetail prd) {
        try {
            String sql = "UPDATE material_management.purchase_request_details "
                    + "SET material_name = ?, category_id = ?, quantity = ?, notes = ? "
                    + "WHERE detail_id = ? AND purchase_request_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, prd.getMaterialName());
            ps.setInt(2, prd.getCategoryId());
            ps.setInt(3, prd.getQuantity());
            ps.setString(4, prd.getNotes());
            ps.setInt(5, prd.getPurchaseRequestDetailId());
            ps.setInt(6, prd.getPurchaseRequestId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deletePurchaseRequestDetail(int detailId, int purchaseRequestId) {
        try {
            String sql = "DELETE FROM material_management.purchase_request_details "
                    + "WHERE detail_id = ? AND purchase_request_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, detailId);
            ps.setInt(2, purchaseRequestId);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
