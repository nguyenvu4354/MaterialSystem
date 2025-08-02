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
                    + "WHERE (pr.status = 'APPROVED' OR pr.status = 'approved') AND pr.disable = 0 "
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

    // Generate sequential request codes (PR1, PR2, PR3, etc.)
    public String generateNextRequestCode() {
        String prefix = "PR";
        String sql = "SELECT request_code FROM purchase_requests WHERE request_code LIKE ?";
        String likePattern = prefix + "%";
        
        System.out.println("=== DEBUG: Generating next request code ===");
        System.out.println("Looking for codes like: " + likePattern);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                List<String> allCodes = new ArrayList<>();
                
                while (rs.next()) {
                    allCodes.add(rs.getString("request_code"));
                }
                
                System.out.println("Found " + allCodes.size() + " existing codes: " + allCodes);
                
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
                    
                    String lastCode = allCodes.get(allCodes.size() - 1);
                    String numberPart = lastCode.replace(prefix, "");
                    try {
                        nextSeq = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException ignore) {
                        // If parsing fails, use 1
                    }
                }
                
                String newCode = prefix + nextSeq;
                System.out.println("Generated new code: " + newCode);
                return newCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "1";
        }
    }

    // Debug method to show all request codes
    public void debugAllRequestCodes() {
        String sql = "SELECT request_code FROM purchase_requests ORDER BY request_code";
        System.out.println("=== DEBUG: All request codes in database ===");
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<String> codes = new ArrayList<>();
                while (rs.next()) {
                    codes.add(rs.getString("request_code"));
                }
                System.out.println("Total codes: " + codes.size());
                System.out.println("All codes: " + codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Updated searchPurchaseRequest with date filtering support
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

            if (sortOption != null && sortOption.equals("date_asc")) {
                sortBy = "request_date";
                sortOrder = "ASC";
            }

            sql.append("ORDER BY ").append(sortBy).append(" ").append(sortOrder);
            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((pageIndex - 1) * pageSize);

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

                list.add(pr);
            }



        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Updated countPurchaseRequest with date filtering support
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
}
