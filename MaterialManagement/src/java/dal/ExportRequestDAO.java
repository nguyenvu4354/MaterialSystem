package dal;

import entity.DBContext;
import entity.ExportRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportRequestDAO extends DBContext {
    
    public List<ExportRequest> getAll() {
        List<ExportRequest> requests = new ArrayList<>();
        String sql = "SELECT er.*, u.fullName as userName, r.fullName as recipientName, "
                + "a.fullName as approverName "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.user_id = u.user_id "
                + "LEFT JOIN Users r ON er.recipient_user_id = r.user_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.disable = 0 "
                + "ORDER BY er.request_date DESC";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("export_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setDeliveryDate(rs.getDate("delivery_date"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("reason"));
                request.setUserId(rs.getInt("user_id"));
                request.setUserName(rs.getString("userName"));
                request.setRecipientId(rs.getInt("recipient_user_id"));
                request.setRecipientName(rs.getString("recipientName"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approverName"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                
                // Get details using ExportRequestDetailDAO
                ExportRequestDetailDAO detailDAO = new ExportRequestDetailDAO();
                request.setDetails(detailDAO.getByRequestId(request.getExportRequestId()));
                
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public ExportRequest getById(int id) {
        String sql = "SELECT er.*, u.fullName as userName, r.fullName as recipientName, "
                + "a.fullName as approverName "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.user_id = u.user_id "
                + "LEFT JOIN Users r ON er.recipient_user_id = r.user_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.export_request_id = ? AND er.disable = 0";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("export_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setDeliveryDate(rs.getDate("delivery_date"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("reason"));
                request.setUserId(rs.getInt("user_id"));
                request.setUserName(rs.getString("userName"));
                request.setRecipientId(rs.getInt("recipient_user_id"));
                request.setRecipientName(rs.getString("recipientName"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approverName"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                
                // Get details using ExportRequestDetailDAO
                ExportRequestDetailDAO detailDAO = new ExportRequestDetailDAO();
                request.setDetails(detailDAO.getByRequestId(request.getExportRequestId()));
                
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean update(ExportRequest request) {
        String sql = "UPDATE Export_Requests SET "
                + "status = ?, "
                + "approved_by = ?, "
                + "approved_at = NOW(), "
                + "approval_reason = ?, "
                + "rejection_reason = ? "
                + "WHERE export_request_id = ? AND disable = 0";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getStatus());
            ps.setInt(2, request.getApprovedBy());
            ps.setString(3, request.getApprovalReason());
            ps.setString(4, request.getRejectionReason());
            ps.setInt(5, request.getExportRequestId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int id) {
        String sql = "UPDATE Export_Requests SET disable = 1 WHERE export_request_id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 