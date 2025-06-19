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
    
    public List<PurchaseRequestDetail> getPurchaseRequestDetailByPurchaseRequestId(int id){
        List<PurchaseRequestDetail> list = new ArrayList<>();
        try{
        String sql = "SELECT * FROM material_management.purchase_request_details where purchase_request_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public static void main(String[] args) {
        PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();
        System.out.println(prdd.getPurchaseRequestDetailByPurchaseRequestId(56));
    }
}
