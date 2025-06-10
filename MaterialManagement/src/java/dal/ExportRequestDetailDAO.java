package dal;

import entity.DBContext;
import entity.ExportRequestDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportRequestDetailDAO extends DBContext {
    
    public List<ExportRequestDetail> getByRequestId(int requestId) {
        List<ExportRequestDetail> details = new ArrayList<>();
        String sql = "SELECT erd.*, m.material_code, m.material_name, m.unit as material_unit "
                + "FROM Export_Request_Details erd "
                + "JOIN Materials m ON erd.material_id = m.material_id "
                + "WHERE erd.export_request_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setExportRequestId(rs.getInt("export_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setMaterialUnit(rs.getString("material_unit"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setExportCondition(rs.getString("export_condition"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    public boolean add(ExportRequestDetail detail) {
        String sql = "INSERT INTO Export_Request_Details (export_request_id, material_id, quantity, export_condition) "
                + "VALUES (?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, detail.getExportRequestId());
            ps.setInt(2, detail.getMaterialId());
            ps.setInt(3, detail.getQuantity());
            ps.setString(4, detail.getExportCondition());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean update(ExportRequestDetail detail) {
        String sql = "UPDATE Export_Request_Details SET "
                + "material_id = ?, "
                + "quantity = ?, "
                + "export_condition = ? "
                + "WHERE detail_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, detail.getMaterialId());
            ps.setInt(2, detail.getQuantity());
            ps.setString(3, detail.getExportCondition());
            ps.setInt(4, detail.getDetailId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int detailId) {
        String sql = "DELETE FROM Export_Request_Details WHERE detail_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, detailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteByRequestId(int requestId) {
        String sql = "DELETE FROM Export_Request_Details WHERE export_request_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 