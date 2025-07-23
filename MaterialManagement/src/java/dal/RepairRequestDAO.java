package dal;

import entity.DBContext;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepairRequestDAO extends DBContext {

    public boolean createRepairRequest(RepairRequest request, List<RepairRequestDetail> details) throws SQLException {
        String requestSql = "INSERT INTO Repair_Requests (request_code, user_id, repair_person_phone_number, repair_person_email, "
                + "repair_location, estimated_return_date, reason, status, request_date, created_at, updated_at, disable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO Repair_Request_Details "
                + "(repair_request_id, material_id, quantity, damage_description, repair_cost, supplier_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psRequest = connection.prepareStatement(requestSql, Statement.RETURN_GENERATED_KEYS); PreparedStatement psDetail = connection.prepareStatement(detailSql)) {
            // Validate input
            if (request == null || request.getRequestCode() == null || request.getUserId() <= 0) {
                throw new IllegalArgumentException("Invalid repair request data.");
            }

            // Insert into Repair_Requests
            psRequest.setString(1, request.getRequestCode());
            psRequest.setInt(2, request.getUserId());
            psRequest.setString(3, request.getRepairPersonPhoneNumber());
            psRequest.setString(4, request.getRepairPersonEmail());
            psRequest.setString(5, request.getRepairLocation());
            psRequest.setDate(6, request.getEstimatedReturnDate());
            psRequest.setString(7, request.getReason());
            psRequest.setString(8, request.getStatus());
            psRequest.setTimestamp(9, request.getRequestDate());
            psRequest.setTimestamp(10, request.getCreatedAt());
            psRequest.setTimestamp(11, request.getUpdatedAt());
            psRequest.setBoolean(12, request.isDisable());
            psRequest.executeUpdate();

            // Get generated repair request ID
            try (ResultSet rs = psRequest.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to retrieve generated repair request ID.");
                }
                int repairRequestId = rs.getInt(1);

                // Insert details into Repair_Request_Details
                for (RepairRequestDetail detail : details) {
                    if (detail == null || detail.getMaterialId() <= 0 || detail.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Invalid repair request detail data.");
                    }
                    psDetail.setInt(1, repairRequestId);
                    psDetail.setInt(2, detail.getMaterialId());
                    psDetail.setInt(3, detail.getQuantity());
                    psDetail.setString(4, detail.getDamageDescription());
                    psDetail.setObject(5, detail.getRepairCost(), java.sql.Types.DOUBLE);
                    psDetail.setObject(6, detail.getSupplierId() > 0 ? detail.getSupplierId() : null, java.sql.Types.INTEGER);
                    psDetail.setTimestamp(7, detail.getCreatedAt());
                    psDetail.setTimestamp(8, detail.getUpdatedAt());
                    psDetail.executeUpdate();
                }
            }
            return true;
        }
    }

    public List<RepairRequest> getRepairRequestsWithPagination(int offset, int pageSize, String searchKeyword, String status, String sortByName, String requestDateFrom, String requestDateTo) throws SQLException {
        List<RepairRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT rr.*, u.full_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "WHERE rr.disable = 0 AND rr.status != 'cancel'"
        );
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND rr.reason LIKE ?");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND rr.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND rr.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) <= ?");
        }
        if (sortByName != null && !sortByName.isEmpty()) {
            if ("asc".equalsIgnoreCase(sortByName)) {
                sql.append(" ORDER BY u.full_name ASC, rr.repair_request_id");
            } else if ("desc".equalsIgnoreCase(sortByName)) {
                sql.append(" ORDER BY u.full_name DESC, rr.repair_request_id");
            }
        } else {
            sql.append(" ORDER BY rr.repair_request_id");
        }
        sql.append(" LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }
            if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
            } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest request = new RepairRequest();
                    request.setRepairRequestId(rs.getInt("repair_request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setUserId(rs.getInt("user_id"));
                    request.setFullName(rs.getString("full_name"));
                    request.setRequestDate(rs.getTimestamp("request_date"));
                    request.setRepairPersonPhoneNumber(rs.getString("repair_person_phone_number"));
                    request.setRepairPersonEmail(rs.getString("repair_person_email"));
                    request.setRepairLocation(rs.getString("repair_location"));
                    request.setEstimatedReturnDate(rs.getDate("estimated_return_date"));
                    request.setStatus(rs.getString("status"));
                    request.setReason(rs.getString("reason"));
                    request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                    request.setApprovalReason(rs.getString("approval_reason"));
                    request.setApprovedAt(rs.getTimestamp("approved_at"));
                    request.setRejectionReason(rs.getString("rejection_reason"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setUpdatedAt(rs.getTimestamp("updated_at"));
                    request.setDisable(rs.getBoolean("disable"));
                    requests.add(request);
                }
            }
        }
        return requests;
    }

    public int getTotalRepairRequestCount(String searchKeyword, String status, String requestDateFrom, String requestDateTo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "WHERE rr.disable = 0 AND rr.status != 'cancel'"
        );
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND rr.reason LIKE ?");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND rr.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND rr.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) <= ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }
            if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
            } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void updateStatus(int repairRequestId, String action, Integer approvedBy, String reason) throws SQLException {
        if (action == null || (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action))) {
            throw new IllegalArgumentException("Hành động không hợp lệ: " + action);
        }
        if (approvedBy == null || approvedBy <= 0) {
            throw new IllegalArgumentException("ID người duyệt không hợp lệ.");
        }

        String checkSql = "SELECT status FROM Repair_Requests WHERE repair_request_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, repairRequestId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    String currentStatus = rs.getString("status");
                    if (!"pending".equalsIgnoreCase(currentStatus)) {
                        throw new IllegalStateException("Yêu cầu đã được xử lý. Không thể duyệt hoặc từ chối thêm lần nữa.");
                    }
                } else {
                    throw new SQLException("Không tìm thấy yêu cầu sửa chữa với ID: " + repairRequestId);
                }
            }
        }

        String sql;
        String status;
        if ("approve".equalsIgnoreCase(action)) {
            status = "approved";
            sql = "UPDATE Repair_Requests SET status = ?, approved_by = ?, approved_at = CURRENT_TIMESTAMP, approval_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE repair_request_id = ?";
        } else {
            status = "rejected";
            sql = "UPDATE Repair_Requests SET status = ?, approved_by = ?, approved_at = CURRENT_TIMESTAMP, rejection_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE repair_request_id = ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approvedBy);
            ps.setString(3, reason);
            ps.setInt(4, repairRequestId);
            ps.executeUpdate();
        }
    }

    public List<RepairRequest> searchByReason(String reason) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "WHERE rr.disable = 0 AND rr.status != 'cancel' AND rr.reason LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + (reason == null ? "" : reason) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("repair_request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setUserId(rs.getInt("user_id"));
                    req.setFullName(rs.getString("full_name"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setRepairPersonPhoneNumber(rs.getString("repair_person_phone_number"));
                    req.setRepairPersonEmail(rs.getString("repair_person_email"));
                    req.setRepairLocation(rs.getString("repair_location"));
                    req.setEstimatedReturnDate(rs.getDate("estimated_return_date"));
                    req.setReason(rs.getString("reason"));
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public List<RepairRequest> filterByStatus(String status) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "WHERE rr.disable = 0 AND rr.status != 'cancel' AND rr.status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("repair_request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setUserId(rs.getInt("user_id"));
                    req.setFullName(rs.getString("full_name"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setRepairPersonPhoneNumber(rs.getString("repair_person_phone_number"));
                    req.setRepairPersonEmail(rs.getString("repair_person_email"));
                    req.setRepairLocation(rs.getString("repair_location"));
                    req.setEstimatedReturnDate(rs.getDate("estimated_return_date"));
                    req.setReason(rs.getString("reason"));
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public RepairRequest getRepairRequestById(int repairRequestId) throws SQLException {
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "WHERE rr.repair_request_id = ? AND rr.disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RepairRequest request = new RepairRequest();
                    request.setRepairRequestId(rs.getInt("repair_request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setUserId(rs.getInt("user_id"));
                    request.setFullName(rs.getString("full_name"));
                    request.setRequestDate(rs.getTimestamp("request_date"));
                    request.setRepairPersonPhoneNumber(rs.getString("repair_person_phone_number"));
                    request.setRepairPersonEmail(rs.getString("repair_person_email"));
                    request.setRepairLocation(rs.getString("repair_location"));
                    request.setEstimatedReturnDate(rs.getDate("estimated_return_date"));
                    request.setStatus(rs.getString("status"));
                    request.setReason(rs.getString("reason"));
                    request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                    request.setApprovalReason(rs.getString("approval_reason"));
                    request.setApprovedAt(rs.getTimestamp("approved_at"));
                    request.setRejectionReason(rs.getString("rejection_reason"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setUpdatedAt(rs.getTimestamp("updated_at"));
                    request.setDisable(rs.getBoolean("disable"));
                    return request;
                }
            }
        }
        return null;
    }
}
