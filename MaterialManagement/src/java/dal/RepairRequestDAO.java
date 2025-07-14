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

/**
 * Data Access Object for managing Repair Requests and their details.
 */
public class RepairRequestDAO extends DBContext {


    public boolean createRepairRequest(RepairRequest request, List<RepairRequestDetail> details) throws SQLException {
        String requestSql = "INSERT INTO Repair_Requests (request_code, user_id, repair_person_phone_number, repair_person_email, "
                + "repair_location, estimated_return_date, reason, status, request_date, created_at, updated_at, disable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO Repair_Request_Details "
                + "(repair_request_id, material_id, quantity, damage_description, repair_cost, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psRequest = connection.prepareStatement(requestSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psDetail = connection.prepareStatement(detailSql)) {
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
                    psDetail.setTimestamp(6, detail.getCreatedAt());
                    psDetail.setTimestamp(7, detail.getUpdatedAt());
                    psDetail.executeUpdate();
                }
            }
            return true;
        }
    }

    public List<RepairRequest> getAllRepairRequests() throws SQLException {
        List<RepairRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Requests WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RepairRequest request = new RepairRequest();
                request.setRepairRequestId(rs.getInt("repair_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
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
        return requests;
    }

    public List<RepairRequestDetail> getRepairRequestDetails(int repairRequestId) throws SQLException {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Request_Details WHERE repair_request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequestDetail detail = new RepairRequestDetail();
                    detail.setDetailId(rs.getInt("detail_id"));
                    detail.setRepairRequestId(rs.getInt("repair_request_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setDamageDescription(rs.getString("damage_description"));
                    detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                    detail.setCreatedAt(rs.getTimestamp("created_at"));
                    detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public List<RepairRequestDetail> getAllRepairRequestDetails() throws SQLException {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Request_Details";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setRepairRequestId(rs.getInt("repair_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setDamageDescription(rs.getString("damage_description"));
                detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        }
        return details;
    }

    public List<RepairRequest> getRepairRequestsByUser(int userId) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Requests WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest r = new RepairRequest();
                    r.setRepairRequestId(rs.getInt("repair_request_id"));
                    r.setRequestCode(rs.getString("request_code"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setStatus(rs.getString("status"));
                    r.setRequestDate(rs.getTimestamp("request_date"));
                    r.setReason(rs.getString("reason"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public void updateStatus(int repairRequestId, String action, Integer approvedBy, String reason) throws SQLException {
        // Validate input
        if (action == null || (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action))) {
            throw new IllegalArgumentException("Hành động không hợp lệ: " + action);
        }
        if (approvedBy == null || approvedBy <= 0) {
            throw new IllegalArgumentException("ID người duyệt không hợp lệ.");
        }

        // Check current status
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

    public List<RepairRequest> searchByRequestCode(String code) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Repair_Requests WHERE disable = 0 AND request_code LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + (code == null ? "" : code) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("repair_request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setRepairLocation(rs.getString("repair_location"));
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
        String sql = "SELECT * FROM Repair_Requests WHERE disable = 0 AND status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("repair_request_id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setRepairLocation(rs.getString("repair_location"));
                    req.setReason(rs.getString("reason"));
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public RepairRequest getRepairRequestById(int repairRequestId) throws SQLException {
        String sql = "SELECT * FROM Repair_Requests WHERE repair_request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RepairRequest request = new RepairRequest();
                    request.setRepairRequestId(rs.getInt("repair_request_id"));
                    request.setRequestCode(rs.getString("request_code"));
                    request.setRequestDate(rs.getTimestamp("request_date"));
                    request.setStatus(rs.getString("status"));
                    request.setUserId(rs.getInt("user_id"));
                    return request;
                }
            }
        }
        return null;
    }
}