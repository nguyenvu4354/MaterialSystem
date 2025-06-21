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

import java.util.List;

import java.util.List;

import entity.PurchaseRequestDetail;

/**
 *
 * @author Admin
 */
public class PurchaseRequestDetailDAO extends DBContext {

    public List<PurchaseRequestDetail> getPurchaseRequestDetailByPurchaseRequestId(int id) {
        List<PurchaseRequestDetail> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM material_management.purchase_request_details where purchase_request_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
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

    public void insertPurchaseRequestDetails(int purchaseRequestId, List<PurchaseRequestDetail> details) {
        String sql = "INSERT INTO Purchase_Request_Details(purchase_request_id, material_name, category_id, quantity, notes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (PurchaseRequestDetail detail : details) {
                ps.setInt(1, purchaseRequestId);
                ps.setString(2, detail.getMaterialName());
                ps.setInt(3, detail.getCategoryId());
                ps.setInt(4, detail.getQuantity());
                ps.setString(5, detail.getNotes());
                ps.addBatch();
            }
            ps.executeBatch(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();
        System.out.println(prdd.getPurchaseRequestDetailByPurchaseRequestId(56));
    }
}
