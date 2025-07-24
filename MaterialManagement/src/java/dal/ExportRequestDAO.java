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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportRequestDAO extends DBContext {
    
    private static final Logger LOGGER = Logger.getLogger(ExportRequestDAO.class.getName());
    
    public ExportRequestDAO() {
        super();
    }
    
    public List<ExportRequest> getAll(String status, String search, String recipientId, int page, int itemsPerPage) {
        List<ExportRequest> requests = new ArrayList<>();
        LOGGER.info("Starting getAll method with status=" + status + ", search=" + search + ", recipientId=" + recipientId + ", page=" + page + ", itemsPerPage=" + itemsPerPage);

        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return requests;
        }

        StringBuilder sql = new StringBuilder()
            .append("SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, ")
            .append("COALESCE(r.full_name, 'Unknown') as recipientName, ")
            .append("COALESCE(a.full_name, 'Unknown') as approverName ")
            .append("FROM Export_Requests er ")
            .append("LEFT JOIN Users u ON er.user_id = u.user_id ")
            .append("LEFT JOIN Users r ON er.recipient_user_id = r.user_id ")
            .append("LEFT JOIN Users a ON er.approved_by = a.user_id ")
            .append("WHERE er.disable = 0 ");

        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND (er.request_code LIKE ? OR u.full_name LIKE ? OR r.full_name LIKE ?) ");
        }
        if (recipientId != null && !recipientId.isEmpty()) {
            sql.append("AND er.recipient_user_id = ? ");
        }

        sql.append("ORDER BY er.export_request_id ASC ");

        if (page > 0 && itemsPerPage > 0) {
            sql.append("LIMIT ? OFFSET ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (recipientId != null && !recipientId.isEmpty()) {
                ps.setInt(paramIndex++, Integer.parseInt(recipientId));
            }
            if (page > 0 && itemsPerPage > 0) {
                ps.setInt(paramIndex++, itemsPerPage);
                ps.setInt(paramIndex, (page - 1) * itemsPerPage);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToExportRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests: " + e.getMessage(), e);
        }
        return requests;
    }
    
    public int getTotalCount(String status, String search) {
        int count = 0;
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return count;
        }

        StringBuilder sql = new StringBuilder()
            .append("SELECT COUNT(*) FROM Export_Requests er ")
            .append("LEFT JOIN Users u ON er.user_id = u.user_id ")
            .append("LEFT JOIN Users r ON er.recipient_user_id = r.user_id ")
            .append("WHERE er.disable = 0 ");

        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND (er.request_code LIKE ? OR u.full_name LIKE ? OR r.full_name LIKE ?) ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting export requests: " + e.getMessage(), e);
        }
        return count;
    }
    
    public ExportRequest getById(int id) {
        connection = getConnection();
        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, " +
                     "COALESCE(r.full_name, 'Unknown') as recipientName, " +
                     "COALESCE(a.full_name, 'Unknown') as approverName " +
                     "FROM Export_Requests er " +
                     "LEFT JOIN Users u ON er.user_id = u.user_id " +
                     "LEFT JOIN Users r ON er.recipient_user_id = r.user_id " +
                     "LEFT JOIN Users a ON er.approved_by = a.user_id " +
                     "WHERE er.export_request_id = ? AND er.disable = 0";
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
                    return request;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean update(ExportRequest request) {
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return false;
        }

        String sql = "UPDATE Export_Requests SET "
                + "status = ?, "
                + "approved_by = ?, "
                + (("approved".equals(request.getStatus()) || "rejected".equals(request.getStatus())) ? "approved_at = CURRENT_TIMESTAMP, " : "")
                + "approval_reason = ?, "
                + "rejection_reason = ? "
                + "WHERE export_request_id = ? AND disable = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, request.getStatus());
            ps.setInt(idx++, request.getApprovedBy());
            ps.setString(idx++, request.getApprovalReason());
            ps.setString(idx++, request.getRejectionReason());
            ps.setInt(idx++, request.getExportRequestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating export request: " + e.getMessage(), e);
            return false;
        }
    }
    
    public boolean delete(int id) {
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return false;
        }

        String sql = "UPDATE Export_Requests SET disable = 1 WHERE export_request_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting export request: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean add(ExportRequest request, List<ExportRequestDetail> details) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Export_Requests (request_code, user_id, recipient_user_id, status, delivery_date, reason) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            }
            String detailSql = "INSERT INTO Export_Request_Details (export_request_id, material_id, quantity, status) "
                             + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {
                for (ExportRequestDetail detail : details) {
                    ps.setInt(1, request.getExportRequestId());
                    ps.setInt(2, detail.getMaterialId());
                    ps.setInt(3, detail.getQuantity());
                    ps.setString(4, detail.getStatus());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: " + ex.getMessage(), ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Error adding export request: " + e.getMessage(), e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection: " + e.getMessage(), e);
                }
            }
        }
    }

    public int getNextCodeSequence() {
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return 1;
        }

        String sql = "SELECT COUNT(*) + 1 FROM Export_Requests WHERE YEAR(request_date) = YEAR(CURRENT_DATE)";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next code sequence: " + e.getMessage(), e);
        }
        return 1;
    }

    public List<ExportRequest> getAllSortedByRequestDate(String order) {
        return getAllSorted("request_date", order);
    }

    public List<ExportRequest> getAllSortedByStatus(String order) {
        return getAllSorted("status", order);
    }

    public List<ExportRequest> getAllSortedByRequestCode(String order) {
        return getAllSorted("request_code", order);
    }

    private List<ExportRequest> getAllSorted(String sortColumn, String order) {
        List<ExportRequest> requests = new ArrayList<>();
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return requests;
        }

        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, " +
                     "COALESCE(r.full_name, 'Unknown') as recipientName, " +
                     "COALESCE(a.full_name, 'Unknown') as approverName " +
                     "FROM Export_Requests er " +
                     "LEFT JOIN Users u ON er.user_id = u.user_id " +
                     "LEFT JOIN Users r ON er.recipient_user_id = r.user_id " +
                     "LEFT JOIN Users a ON er.approved_by = a.user_id " +
                     "WHERE er.disable = 0 " +
                     "ORDER BY er." + sortColumn + " " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapResultSetToExportRequest(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests sorted by " + sortColumn + ": " + e.getMessage(), e);
        }
        return requests;
    }

    private ExportRequest mapResultSetToExportRequest(ResultSet rs) throws SQLException {
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
        return request;
    }

    public List<Integer> getAllRecipientUserIds() {
        List<Integer> recipientIds = new ArrayList<>();
        String sql = "SELECT DISTINCT recipient_user_id FROM Export_Requests WHERE recipient_user_id IS NOT NULL AND recipient_user_id > 0 AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                recipientIds.add(rs.getInt("recipient_user_id"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching recipient user ids: " + e.getMessage(), e);
        }
        return recipientIds;
    }
}