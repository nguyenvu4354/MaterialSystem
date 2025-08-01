package dal;

import entity.PurchaseRequestDetail;
import entity.DBContext;
import entity.PurchaseRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDAO extends DBContext {

    public int countPurchaseRequest(String keyword, String status, String startDate, String endDate) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM material_management.purchase_requests pr WHERE pr.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (pr.request_code LIKE ? OR pr.purchase_request_id LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND pr.status = ? ");
                params.add(status);
            }

            if (startDate != null && !startDate.isEmpty()) {
                sql.append("AND DATE(pr.request_date) >= ? ");
                params.add(startDate);
            }

            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATE(pr.request_date) <= ? ");
                params.add(endDate);
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    public List<PurchaseRequest> searchPurchaseRequest(String keyword, String status, String startDate, String endDate, int pageIndex, int pageSize, String sortOption) {
        List<PurchaseRequest> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM material_management.purchase_requests WHERE disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND (request_code LIKE ? OR purchase_request_id LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND status = ? ");
                params.add(status);
            }

            if (startDate != null && !startDate.isEmpty()) {
                sql.append("AND DATE(request_date) >= ? ");
                params.add(startDate);
            }

            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATE(request_date) <= ? ");
                params.add(endDate);
            }

            String sortBy = "request_date";
            String sortOrder = "DESC";

            if (sortOption != null) {
                switch (sortOption) {
                    case "code_asc":
                        sortBy = "request_code";
                        sortOrder = "ASC";
                        break;
                    case "code_desc":
                        sortBy = "request_code";
                        sortOrder = "DESC";
                        break;
                    case "date_asc":
                        sortBy = "request_date";
                        sortOrder = "ASC";
                        break;
                    case "date_desc":
                        sortBy = "request_date";
                        sortOrder = "DESC";
                        break;
                    case "id_asc":
                        sortBy = "purchase_request_id";
                        sortOrder = "ASC";
                        break;
                    case "id_desc":
                        sortBy = "purchase_request_id";
                        sortOrder = "DESC";
                        break;
                    case "status_asc":
                        sortBy = "status";
                        sortOrder = "ASC";
                        break;
                    case "status_desc":
                        sortBy = "status";
                        sortOrder = "DESC";
                        break;
                }
            }

            // For code sorting, we'll sort in Java to handle numeric order properly
            if (sortOption != null && (sortOption.equals("code_asc") || sortOption.equals("code_desc"))) {
                sql.append(" ORDER BY request_date DESC "); // Get all data first
                sql.append("LIMIT 1000 OFFSET 0 "); // Get more data for sorting
            } else {
                sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder).append(" ");
                sql.append("LIMIT ? OFFSET ? ");
                params.add(pageSize);
                params.add((pageIndex - 1) * pageSize);
            }
            
            // Debug: Log the SQL query
            System.out.println("=== DEBUG: SQL Query ===");
            System.out.println("SQL: " + sql.toString());
            System.out.println("Params: " + params);

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                pr.setRequestCode(rs.getString("request_code"));
                pr.setUserId(rs.getInt("user_id"));
                pr.setRequestDate(rs.getTimestamp("request_date"));
                pr.setStatus(rs.getString("status"));
                String reasonFromDB = rs.getString("reason");
                pr.setReason(reasonFromDB);
                
                // Debug: Check what's being read from database
                System.out.println("=== DEBUG: Reading from DB ===");
                System.out.println("Request Code: " + pr.getRequestCode());
                System.out.println("Reason from DB: '" + reasonFromDB + "'");
                System.out.println("Reason length: " + (reasonFromDB != null ? reasonFromDB.length() : "null"));
                pr.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                pr.setApprovalReason(rs.getString("approval_reason"));
                pr.setApprovedAt(rs.getTimestamp("approved_at"));
                pr.setRejectionReason(rs.getString("rejection_reason"));
                pr.setCreatedAt(rs.getTimestamp("created_at"));
                pr.setUpdatedAt(rs.getTimestamp("updated_at"));
                pr.setDisable(rs.getBoolean("disable"));
                list.add(pr);
            }
            
            // Sort by code numerically if needed
            if (sortOption != null && (sortOption.equals("code_asc") || sortOption.equals("code_desc"))) {
                list.sort((pr1, pr2) -> {
                    try {
                        String code1 = pr1.getRequestCode();
                        String code2 = pr2.getRequestCode();
                        
                        // Extract numbers from PR codes
                        int num1 = Integer.parseInt(code1.replace("PR", ""));
                        int num2 = Integer.parseInt(code2.replace("PR", ""));
                        
                        if (sortOption.equals("code_asc")) {
                            return Integer.compare(num1, num2);
                        } else {
                            return Integer.compare(num2, num1);
                        }
                    } catch (NumberFormatException e) {
                        // Fallback to string comparison
                        return sortOption.equals("code_asc") ? 
                            pr1.getRequestCode().compareTo(pr2.getRequestCode()) :
                            pr2.getRequestCode().compareTo(pr1.getRequestCode());
                    }
                });
                
                // Apply pagination after sorting
                int startIndex = (pageIndex - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, list.size());
                if (startIndex < list.size()) {
                    list = list.subList(startIndex, endIndex);
                } else {
                    list.clear();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public PurchaseRequest getPurchaseRequestById(int purchaseRequestId) {
        try {
            String sql = "SELECT pr.*, u.full_name, u.email, u.phone_number "
                    + "FROM material_management.purchase_requests pr "
                    + "LEFT JOIN material_management.users u ON pr.user_id = u.user_id "
                    + "WHERE pr.purchase_request_id = ? AND pr.disable = 0";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, purchaseRequestId);

            ResultSet rs = ps.executeQuery();
            PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();

            if (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                pr.setRequestCode(rs.getString("request_code"));
                pr.setUserId(rs.getInt("user_id"));
                pr.setRequestDate(rs.getTimestamp("request_date"));
                pr.setStatus(rs.getString("status"));
                pr.setReason(rs.getString("reason"));

                int approvedBy = rs.getInt("approved_by");
                if (!rs.wasNull()) {
                    pr.setApprovedBy(approvedBy);
                } else {
                    pr.setApprovedBy(null);
                }

                pr.setApprovalReason(rs.getString("approval_reason"));
                pr.setApprovedAt(rs.getTimestamp("approved_at"));
                pr.setRejectionReason(rs.getString("rejection_reason"));
                pr.setCreatedAt(rs.getTimestamp("created_at"));
                pr.setUpdatedAt(rs.getTimestamp("updated_at"));
                pr.setDisable(rs.getBoolean("disable"));

                List<PurchaseRequestDetail> details = prdd.paginationOfDetails(pr.getPurchaseRequestId(), 1, Integer.MAX_VALUE);
                pr.setDetails(details);

                return pr;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean updatePurchaseRequestStatus(int requestId, String status, Integer userId, String reason) {
        String sql;
        if ("approved".equalsIgnoreCase(status)) {
            sql = "UPDATE material_management.purchase_requests "
                    + "SET status = ?, approved_by = ?, approval_reason = ?, approved_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP "
                    + "WHERE purchase_request_id = ? AND disable = 0";
        } else if ("rejected".equalsIgnoreCase(status)) {
            sql = "UPDATE material_management.purchase_requests "
                    + "SET status = ?, approved_by = ?, rejection_reason = ?, approved_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP "
                    + "WHERE purchase_request_id = ? AND disable = 0";
        } else {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);

            if (userId != null) {
                ps.setInt(2, userId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            if (reason != null && !reason.trim().isEmpty()) {
                ps.setString(3, reason);
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }

            ps.setInt(4, requestId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createPurchaseRequestWithDetails(PurchaseRequest request, List<PurchaseRequestDetail> details) {
        String insertRequestSQL = "INSERT INTO material_management.purchase_requests (request_code, user_id, request_date, status, reason) VALUES (?, ?, ?, ?, ?)";
        String insertDetailSQL = "INSERT INTO material_management.purchase_request_details (purchase_request_id, material_id, quantity, notes) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psRequest = connection.prepareStatement(insertRequestSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                System.out.println("=== DEBUG: Database Insert ===");
                System.out.println("Request Code: " + request.getRequestCode());
                System.out.println("User ID: " + request.getUserId());
                System.out.println("Status: " + request.getStatus());
                System.out.println("Reason: '" + request.getReason() + "'");
                System.out.println("Reason length: " + (request.getReason() != null ? request.getReason().length() : "null"));
                
                psRequest.setString(1, request.getRequestCode());
                psRequest.setInt(2, request.getUserId());
                psRequest.setTimestamp(3, request.getRequestDate());
                psRequest.setString(4, request.getStatus());
                psRequest.setString(5, request.getReason());

                int affectedRows = psRequest.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int purchaseRequestId = generatedKeys.getInt(1);
                        
                        if (details != null && !details.isEmpty()) {
                            try (PreparedStatement psDetail = connection.prepareStatement(insertDetailSQL)) {
                                for (PurchaseRequestDetail detail : details) {
                                    psDetail.setInt(1, purchaseRequestId);
                                    psDetail.setInt(2, detail.getMaterialId());
                                    psDetail.setInt(3, detail.getQuantity());
                                    
                                    if (detail.getNotes() != null) {
                                        psDetail.setString(4, detail.getNotes());
                                    } else {
                                        psDetail.setNull(4, Types.VARCHAR);
                                    }
                                    
                                    psDetail.addBatch();
                                }
                                psDetail.executeBatch();
                            }
                        }

                        connection.commit();
                        return true;
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Lấy danh sách Purchase Requests đã được approved
    public List<PurchaseRequest> getApprovedPurchaseRequests() {
        List<PurchaseRequest> list = new ArrayList<>();
        try {
            String sql = "SELECT pr.*, u.full_name, u.email, u.phone_number "
                    + "FROM material_management.purchase_requests pr "
                    + "LEFT JOIN material_management.users u ON pr.user_id = u.user_id "
                    + "WHERE pr.status = 'APPROVED' AND pr.disable = 0 "
                    + "ORDER BY pr.request_date DESC";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();

            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                pr.setRequestCode(rs.getString("request_code"));
                pr.setUserId(rs.getInt("user_id"));
                pr.setRequestDate(rs.getTimestamp("request_date"));
                pr.setStatus(rs.getString("status"));
                pr.setReason(rs.getString("reason"));

                int approvedBy = rs.getInt("approved_by");
                if (!rs.wasNull()) {
                    pr.setApprovedBy(approvedBy);
                } else {
                    pr.setApprovedBy(null);
                }

                pr.setApprovalReason(rs.getString("approval_reason"));
                pr.setApprovedAt(rs.getTimestamp("approved_at"));
                pr.setRejectionReason(rs.getString("rejection_reason"));
                pr.setCreatedAt(rs.getTimestamp("created_at"));
                pr.setUpdatedAt(rs.getTimestamp("updated_at"));
                pr.setDisable(rs.getBoolean("disable"));

                // Load details for each purchase request
                List<PurchaseRequestDetail> details = prdd.paginationOfDetails(pr.getPurchaseRequestId(), 1, Integer.MAX_VALUE);
                pr.setDetails(details);

                list.add(pr);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public String generateNextRequestCode() {
        String prefix = "PR";
        String sql = "SELECT request_code FROM purchase_requests WHERE request_code LIKE ?";
        String likePattern = prefix + "%";
        
        System.out.println("=== DEBUG: Generate Next Request Code ===");
        System.out.println("SQL: " + sql);
        System.out.println("Like Pattern: " + likePattern);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                List<String> allCodes = new ArrayList<>();
                
                // Collect all existing codes
                while (rs.next()) {
                    String code = rs.getString("request_code");
                    allCodes.add(code);
                }
                
                System.out.println("All existing codes: " + allCodes);
                
                if (!allCodes.isEmpty()) {
                    // Sort codes numerically
                    allCodes.sort((code1, code2) -> {
                        try {
                            int num1 = Integer.parseInt(code1.replace(prefix, ""));
                            int num2 = Integer.parseInt(code2.replace(prefix, ""));
                            return Integer.compare(num1, num2);
                        } catch (NumberFormatException e) {
                            return code1.compareTo(code2);
                        }
                    });
                    
                    // Get the highest number
                    String lastCode = allCodes.get(allCodes.size() - 1);
                    String numberPart = lastCode.replace(prefix, "");
                    try {
                        nextSeq = Integer.parseInt(numberPart) + 1;
                        System.out.println("Last Code: " + lastCode);
                        System.out.println("Number Part: " + numberPart);
                        System.out.println("Next Sequence: " + nextSeq);
                    } catch (NumberFormatException ignore) {
                        System.out.println("NumberFormatException for: " + numberPart);
                    }
                } else {
                    System.out.println("No existing codes found");
                }
                String result = prefix + nextSeq;
                System.out.println("Generated Code: " + result);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "1";
        }
    }
    
    // Debug method to see all request codes
    public void debugAllRequestCodes() {
        try {
            String sql = "SELECT request_code, request_date, status FROM purchase_requests ORDER BY request_date DESC";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("=== DEBUG: All Request Codes ===");
            while (rs.next()) {
                String code = rs.getString("request_code");
                String date = rs.getString("request_date");
                String status = rs.getString("status");
                System.out.println("Code: " + code + " | Date: " + date + " | Status: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
