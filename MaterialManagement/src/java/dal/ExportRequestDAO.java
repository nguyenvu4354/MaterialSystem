package dal;

import entity.DBContext;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ExportRequestDAO extends DBContext {
    
    private static final Logger LOGGER = Logger.getLogger(ExportRequestDAO.class.getName());
    
    public ExportRequestDAO() {
        super();
    }
    
    @Override
    public Connection getConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "Hoang1062004";
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting database connection", ex);
            throw new RuntimeException("Failed to get database connection", ex);
        }
    }
    
    public List<ExportRequest> getAll(String status, String search, int page, int itemsPerPage) {
        List<ExportRequest> requests = new ArrayList<>();
        LOGGER.info("Starting getAll method with status=" + status + ", search=" + search + ", page=" + page + ", itemsPerPage=" + itemsPerPage);

        // Kiểm tra connection
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            try {
                // Tạo kết nối mới nếu connection bị null
                String url = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                String username = "root";
                String password = "Hoang1062004";
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                LOGGER.info("Successfully created new database connection");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error creating new database connection: " + e.getMessage(), e);
                return requests;
            }
        }

        // Kiểm tra connection có còn active không
        try {
            if (connection.isClosed()) {
                LOGGER.warning("Connection was closed, creating new connection");
                String url = "jdbc:mysql://127.0.0.1:3306/material_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                String username = "root";
                String password = "Hoang1062004";
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                LOGGER.info("Successfully created new database connection after closed connection");
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error checking connection status: " + e.getMessage(), e);
            return requests;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, ")
           .append("COALESCE(r.full_name, 'Unknown') as recipientName, ")
           .append("COALESCE(a.full_name, 'Unknown') as approverName ")
           .append("FROM Export_Requests er ")
           .append("LEFT JOIN Users u ON er.user_id = u.user_id ")
           .append("LEFT JOIN Users r ON er.recipient_user_id = r.user_id ")
           .append("LEFT JOIN Users a ON er.approved_by = a.user_id ")
           .append("WHERE er.disable = 0 ");

        // Thêm điều kiện tìm kiếm
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND (er.request_code LIKE ? OR u.full_name LIKE ? OR r.full_name LIKE ?) ");
        }

        sql.append("ORDER BY er.request_date DESC ");

        // Thêm phân trang
        if (page > 0 && itemsPerPage > 0) {
            sql.append("LIMIT ? OFFSET ?");
        }

        LOGGER.info("Executing query: " + sql.toString());

        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            // Set các tham số tìm kiếm
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            // Set các tham số phân trang
            if (page > 0 && itemsPerPage > 0) {
                ps.setInt(paramIndex++, itemsPerPage);
                ps.setInt(paramIndex, (page - 1) * itemsPerPage);
            }

            try (ResultSet rs = ps.executeQuery()) {
                int rowCount = 0;
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
                    
                    requests.add(request);
                    rowCount++;
                    LOGGER.fine("Retrieved request: ID=" + request.getExportRequestId() + ", Code=" + request.getRequestCode());
                }
                LOGGER.info("Successfully retrieved " + rowCount + " export requests");
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

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM Export_Requests er ")
           .append("LEFT JOIN Users u ON er.user_id = u.user_id ")
           .append("LEFT JOIN Users r ON er.recipient_user_id = r.user_id ")
           .append("WHERE er.disable = 0 ");

        // Thêm điều kiện tìm kiếm
        if (status != null && !status.isEmpty()) {
            sql.append("AND er.status = ? ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append("AND (er.request_code LIKE ? OR u.full_name LIKE ? OR r.full_name LIKE ?) ");
        }

        LOGGER.info("Executing count query: " + sql.toString());

        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            // Set các tham số tìm kiếm
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
        LOGGER.info("Total count retrieved: " + count);
        return count;
    }
    
    public ExportRequest getById(int id) {
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return null;
        }

        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, COALESCE(r.full_name, 'Unknown') as recipientName, COALESCE(a.full_name, 'Unknown') as approverName "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.user_id = u.user_id "
                + "LEFT JOIN Users r ON er.recipient_user_id = r.user_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.export_request_id = ? AND er.disable = 0";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
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

                    // Tạm thời comment để kiểm tra dữ liệu cơ bản
                    // ExportRequestDetailDAO detailDAO = new ExportRequestDetailDAO();
                    // request.setDetails(detailDAO.getByRequestId(request.getExportRequestId()));
                    
                    return request;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export request by ID: " + e.getMessage(), e);
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
                + "approved_at = CURRENT_TIMESTAMP, "
                + "approval_reason = ?, "
                + "rejection_reason = ? "
                + "WHERE export_request_id = ? AND disable = 0";
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getStatus());
            ps.setInt(2, request.getApprovedBy());
            ps.setString(3, request.getApprovalReason());
            ps.setString(4, request.getRejectionReason());
            ps.setInt(5, request.getExportRequestId());
            
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
        
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting export request: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean add(ExportRequest request, List<ExportRequestDetail> details) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.io.FileWriter fw = null;
        try {
            // Get a new connection for this transaction
            con = getConnection();
            con.setAutoCommit(false);
            
            // Log to file
            try {
                fw = new java.io.FileWriter("debug.log", true);
                fw.write("\n=== Starting Export Request Creation ===\n");
                fw.write("Request Code: " + request.getRequestCode() + "\n");
                fw.write("User ID: " + request.getUserId() + "\n");
                fw.write("Recipient ID: " + request.getRecipientId() + "\n");
                fw.write("Status: " + request.getStatus() + "\n");
                fw.write("Delivery Date: " + request.getDeliveryDate() + "\n");
                fw.write("Reason: " + request.getReason() + "\n");
                fw.write("Number of Details: " + details.size() + "\n");
            } catch (java.io.IOException e) {
                System.err.println("Error writing to debug log: " + e.getMessage());
            }
            
            String sql = "INSERT INTO Export_Requests (request_code, user_id, recipient_user_id, status, delivery_date, reason) "
                       + "VALUES (?, ?, ?, ?, ?, ?)";
            
            if (fw != null) {
                try {
                    fw.write("SQL: " + sql + "\n");
                } catch (java.io.IOException e) {
                    System.err.println("Error writing to debug log: " + e.getMessage());
                }
            }
            
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
            if (fw != null) {
                try {
                    fw.write("Affected rows for Export_Requests insert: " + affectedRows + "\n");
                } catch (java.io.IOException e) {
                    System.err.println("Error writing to debug log: " + e.getMessage());
                }
            }
            
            if (affectedRows == 0) {
                if (fw != null) {
                    try {
                        fw.write("Error: No rows affected in Export_Requests insert\n");
                        fw.close();
                    } catch (java.io.IOException e) {
                        System.err.println("Error writing to debug log: " + e.getMessage());
                    }
                }
                throw new SQLException("Creating request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setExportRequestId(generatedKeys.getInt(1));
                    if (fw != null) {
                        try {
                            fw.write("Generated export request ID: " + request.getExportRequestId() + "\n");
                        } catch (java.io.IOException e) {
                            System.err.println("Error writing to debug log: " + e.getMessage());
                        }
                    }
                } else {
                    if (fw != null) {
                        try {
                            fw.write("Error: No ID obtained from Export_Requests insert\n");
                            fw.close();
                        } catch (java.io.IOException e) {
                            System.err.println("Error writing to debug log: " + e.getMessage());
                        }
                    }
                    throw new SQLException("Creating request failed, no ID obtained.");
                }
            }

            String detailSql = "INSERT INTO Export_Request_Details (export_request_id, material_id, quantity, export_condition) "
                             + "VALUES (?, ?, ?, ?)";
            
            if (fw != null) {
                try {
                    fw.write("SQL for details: " + detailSql + "\n");
                } catch (java.io.IOException e) {
                    System.err.println("Error writing to debug log: " + e.getMessage());
                }
            }
            
            ps = con.prepareStatement(detailSql);
            for (ExportRequestDetail detail : details) {
                if (fw != null) {
                    try {
                        fw.write("Detail: materialId=" + detail.getMaterialId() 
                              + ", quantity=" + detail.getQuantity() 
                              + ", condition=" + detail.getExportCondition() + "\n");
                    } catch (java.io.IOException e) {
                        System.err.println("Error writing to debug log: " + e.getMessage());
                    }
                }
                          
                ps.setInt(1, request.getExportRequestId());
                ps.setInt(2, detail.getMaterialId());
                ps.setInt(3, detail.getQuantity());
                ps.setString(4, detail.getExportCondition());
                ps.addBatch();
            }
            
            int[] batchResults = ps.executeBatch();
            if (fw != null) {
                try {
                    fw.write("Batch results: " + Arrays.toString(batchResults) + "\n");
                } catch (java.io.IOException e) {
                    System.err.println("Error writing to debug log: " + e.getMessage());
                }
            }

            con.commit();
            if (fw != null) {
                try {
                    fw.write("Transaction committed successfully\n");
                    fw.close();
                } catch (java.io.IOException e) {
                    System.err.println("Error writing to debug log: " + e.getMessage());
                }
            }
            return true;
        } catch (SQLException e) {
            if (fw != null) {
                try {
                    fw.write("SQL Error: " + e.getMessage() + "\n");
                    fw.write("SQL State: " + e.getSQLState() + "\n");
                    fw.write("Error Code: " + e.getErrorCode() + "\n");
                    fw.close();
                } catch (java.io.IOException ex) {
                    System.err.println("Error writing to debug log: " + ex.getMessage());
                }
            }
            
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (java.io.IOException e) {
                        System.err.println("Error closing debug log: " + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNextCodeSequence() {
        if (connection == null) {
            LOGGER.severe("Database connection is null");
            return 1;
        }

        int sequence = 1;
        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) + 1 FROM Export_Requests WHERE YEAR(request_date) = YEAR(CURRENT_DATE)");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                sequence = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next code sequence: " + e.getMessage(), e);
        }
        return sequence;
    }

    public List<ExportRequest> getAllSortedByRequestDate(String order) {
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
                     "ORDER BY er.request_date " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportRequest request = mapResultSetToExportRequest(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests sorted by date: " + e.getMessage(), e);
        }
        return requests;
    }

    public List<ExportRequest> getAllSortedByStatus(String order) {
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
                     "ORDER BY er.status " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportRequest request = mapResultSetToExportRequest(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests sorted by status: " + e.getMessage(), e);
        }
        return requests;
    }

    public List<ExportRequest> getAllSortedByRequestCode(String order) {
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
                     "ORDER BY er.request_code " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (Connection conn = connection;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportRequest request = mapResultSetToExportRequest(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests sorted by code: " + e.getMessage(), e);
        }
        return requests;
    }

    // Helper method to map ResultSet to ExportRequest
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
}