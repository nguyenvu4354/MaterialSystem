package dal;

import entity.DBContext;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExportRequestDAO extends DBContext {
    
    public List<ExportRequest> getAll(String status, String search, int page, int itemsPerPage) {
        List<ExportRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT er.*, u.fullName as userName, r.fullName as recipientName, a.fullName as approverName ");
        sql.append("FROM Export_Requests er ");
        sql.append("LEFT JOIN Users u ON er.user_id = u.user_id ");
        sql.append("LEFT JOIN Users r ON er.recipient_user_id = r.user_id ");
        sql.append("LEFT JOIN Users a ON er.approved_by = a.user_id ");
        sql.append("WHERE er.disable = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
            params.add(status);
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND er.request_code LIKE ? ");
            params.add("%" + search + "%");
        }
        sql.append("ORDER BY er.request_date DESC ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(itemsPerPage);
        params.add((page - 1) * itemsPerPage);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
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
                    
                    ExportRequestDetailDAO detailDAO = new ExportRequestDetailDAO();
                    request.setDetails(detailDAO.getByRequestId(request.getExportRequestId()));
                    
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public int getTotalCount(String status, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Export_Requests er ");
        sql.append("WHERE er.disable = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
            params.add(status);
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND er.request_code LIKE ? ");
            params.add("%" + search + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public ExportRequest getById(int id) {
        String sql = "SELECT er.*, u.fullName as userName, r.fullName as recipientName, a.fullName as approverName "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.user_id = u.user_id "
                + "LEFT JOIN Users r ON er.recipient_user_id = r.user_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.export_request_id = ? AND er.disable = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
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
                    
                    ExportRequestDetailDAO detailDAO = new ExportRequestDetailDAO();
                    request.setDetails(detailDAO.getByRequestId(request.getExportRequestId()));
                    
                    return request;
                }
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
                + "approved_at = CURRENT_TIMESTAMP, "
                + "approval_reason = ?, "
                + "rejection_reason = ? "
                + "WHERE export_request_id = ? AND disable = 0";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean add(ExportRequest request, List<ExportRequestDetail> details) {
        Connection con = connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(false);
            
            String sql = "INSERT INTO Export_Requests (request_code, user_id, recipient_user_id, status, delivery_date, reason) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getRequestCode());
            ps.setInt(2, request.getUserId());
            if (request.getRecipientId() > 0) {
                ps.setInt(3, request.getRecipientId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, request.getStatus());
            ps.setDate(5, request.getDeliveryDate());
            ps.setString(6, request.getReason());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setExportRequestId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating request failed, no ID obtained.");
                }
            }

            String detailSql = "INSERT INTO Export_Request_Details (export_request_id, material_id, quantity, export_condition) "
                             + "VALUES (?, ?, ?, ?)";
            
            ps = con.prepareStatement(detailSql);
            for (ExportRequestDetail detail : details) {
                ps.setInt(1, request.getExportRequestId());
                ps.setInt(2, detail.getMaterialId());
                ps.setInt(3, detail.getQuantity());
                ps.setString(4, detail.getExportCondition());
                ps.addBatch();
            }
            ps.executeBatch();

            con.commit();
            return true;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNextCodeSequence() {
        int sequence = 1;
        try {
            String sql = "SELECT COUNT(*) + 1 FROM Export_Requests WHERE YEAR(request_date) = YEAR(CURRENT_DATE)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sequence = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sequence;
    }
}