package dal;

import entity.DBContext;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO extends DBContext {

    // Fetch all export requests for a user with pagination and filtering
    public List<ExportRequest> getExportRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        List<ExportRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT er.*, u1.full_name AS user_name, u2.full_name AS recipient_name, u3.full_name AS approver_name "
                + "FROM Export_Requests er "
                + "JOIN Users u1 ON er.user_id = u1.user_id "
                + "JOIN Users u2 ON er.recipient_user_id = u2.user_id "
                + "LEFT JOIN Users u3 ON er.approved_by = u3.user_id "
                + "WHERE er.user_id = ? AND er.disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND er.status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND er.request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND er.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND er.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        sql.append("ORDER BY er.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("export_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
                request.setUserName(rs.getString("user_name"));
                request.setRecipientId(rs.getInt("recipient_user_id"));
                request.setRecipientName(rs.getString("recipient_name"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setDeliveryDate(rs.getDate("delivery_date"));
                request.setReason(rs.getString("reason"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approver_name"));
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                request.setDetails(getExportRequestDetails(request.getExportRequestId()));
                requests.add(request);
            }
            System.out.println("✅ Lấy danh sách export requests thành công, số lượng: " + requests.size());
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getExportRequestsByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    // Fetch a single export request by ID
    public ExportRequest getExportRequestById(int exportRequestId) {
        String sql = "SELECT er.*, u1.full_name AS user_name, u2.full_name AS recipient_name, u3.full_name AS approver_name "
                + "FROM Export_Requests er "
                + "JOIN Users u1 ON er.user_id = u1.user_id "
                + "JOIN Users u2 ON er.recipient_user_id = u2.user_id "
                + "LEFT JOIN Users u3 ON er.approved_by = u3.user_id "
                + "WHERE er.export_request_id = ? AND er.disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("export_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
                request.setUserName(rs.getString("user_name"));
                request.setRecipientId(rs.getInt("recipient_user_id"));
                request.setRecipientName(rs.getString("recipient_name"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setDeliveryDate(rs.getDate("delivery_date"));
                request.setReason(rs.getString("reason"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approver_name"));
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                request.setDetails(getExportRequestDetails(rs.getInt("export_request_id")));
                return request;
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getExportRequestById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Fetch details for a specific export request
    private List<ExportRequestDetail> getExportRequestDetails(int exportRequestId) {
        List<ExportRequestDetail> details = new ArrayList<>();
        String sql = "SELECT erd.*, m.material_code, m.material_name, u.unit_name "
                + "FROM Export_Request_Details erd "
                + "JOIN Materials m ON erd.material_id = m.material_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "WHERE erd.export_request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setExportRequestId(rs.getInt("export_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setMaterialUnit(rs.getString("unit_name"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setExportCondition(rs.getString("export_condition"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getExportRequestDetails: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }

    // Count export requests for a user with filtering
    public int getExportRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Export_Requests "
                + "WHERE user_id = ? AND disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getExportRequestCountByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Fetch all purchase requests for a user with pagination and filtering
    public List<PurchaseRequest> getPurchaseRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        List<PurchaseRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT pr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Purchase_Requests pr "
                + "JOIN Users u ON pr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON pr.approved_by = u2.user_id "
                + "WHERE pr.user_id = ? AND pr.disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND pr.status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND pr.request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND pr.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND pr.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        sql.append("ORDER BY pr.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequest request = new PurchaseRequest();
                request.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setEstimatedPrice(rs.getDouble("estimated_price"));
                request.setReason(rs.getString("reason"));
                request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setUpdatedAt(rs.getTimestamp("updated_at"));
                request.setDisable(rs.getBoolean("disable"));
                request.setDetails(getPurchaseRequestDetails(request.getPurchaseRequestId()));
                requests.add(request);
            }
            System.out.println("✅ Lấy danh sách purchase requests thành công, số lượng: " + requests.size());
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getPurchaseRequestsByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    // Fetch a single purchase request by ID
    public PurchaseRequest getPurchaseRequestById(int purchaseRequestId) {
        String sql = "SELECT pr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Purchase_Requests pr "
                + "JOIN Users u ON pr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON pr.approved_by = u2.user_id "
                + "WHERE pr.purchase_request_id = ? AND pr.disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PurchaseRequest request = new PurchaseRequest();
                request.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                request.setRequestCode(rs.getString("request_code"));
                request.setUserId(rs.getInt("user_id"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setEstimatedPrice(rs.getDouble("estimated_price"));
                request.setReason(rs.getString("reason"));
                request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                request.setApprovalReason(rs.getString("approval_reason"));
                request.setApprovedAt(rs.getTimestamp("approved_at"));
                request.setRejectionReason(rs.getString("rejection_reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setUpdatedAt(rs.getTimestamp("updated_at"));
                request.setDisable(rs.getBoolean("disable"));
                request.setDetails(getPurchaseRequestDetails(rs.getInt("purchase_request_id")));
                return request;
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getPurchaseRequestById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Fetch details for a specific purchase request
    private List<PurchaseRequestDetail> getPurchaseRequestDetails(int purchaseRequestId) {
        List<PurchaseRequestDetail> details = new ArrayList<>();
        String sql = "SELECT prd.*, c.category_name "
                + "FROM Purchase_Request_Details prd "
                + "JOIN Categories c ON prd.category_id = c.category_id "
                + "WHERE prd.purchase_request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setPurchaseRequestDetailId(rs.getInt("detail_id"));
                detail.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setCategoryId(rs.getInt("category_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setNotes(rs.getString("notes"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getPurchaseRequestDetails: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }

    // Count purchase requests for a user with filtering
    public int getPurchaseRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Purchase_Requests "
                + "WHERE user_id = ? AND disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getPurchaseRequestCountByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Fetch all repair requests for a user with pagination and filtering
    public List<RepairRequest> getRepairRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        List<RepairRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT rr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON rr.approved_by = u2.user_id "
                + "WHERE rr.user_id = ? AND rr.disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND rr.status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND rr.request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }
        if (startDate != null) {
            sql.append("AND rr.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND rr.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        sql.append("ORDER BY rr.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
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
                request.setDetails(getRepairRequestDetails(request.getRepairRequestId()));
                requests.add(request);
            }
            System.out.println("✅ Lấy danh sách repair requests thành công, số lượng: " + requests.size());
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getRepairRequestsByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    // Fetch a single repair request by ID
    public RepairRequest getRepairRequestById(int repairRequestId) {
        String sql = "SELECT rr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON rr.approved_by = u2.user_id "
                + "WHERE rr.repair_request_id = ? AND rr.disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
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
                request.setDetails(getRepairRequestDetails(rs.getInt("repair_request_id")));
                return request;
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getRepairRequestById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Fetch details for a specific repair request
    private List<RepairRequestDetail> getRepairRequestDetails(int repairRequestId) {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT rrd.*, m.material_code, m.material_name "
                + "FROM Repair_Request_Details rrd "
                + "JOIN Materials m ON rrd.material_id = m.material_id "
                + "WHERE rrd.repair_request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getRepairRequestDetails: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }

    // Count repair requests for a user with filtering
    public int getRepairRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Repair_Requests "
                + "WHERE user_id = ? AND disable = 0 "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (requestCode != null && !requestCode.trim().isEmpty()) {
            sql.append("AND request_code LIKE ? ");
            params.add("%" + requestCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getRepairRequestCountByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Cancel an export request
    public boolean cancelExportRequest(int exportRequestId, int userId) {
        String sql = "UPDATE Export_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE export_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Hủy export request thành công, ID: " + exportRequestId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi cancelExportRequest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cancel a purchase request
    public boolean cancelPurchaseRequest(int purchaseRequestId, int userId) {
        String sql = "UPDATE Purchase_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE purchase_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Hủy purchase request thành công, ID: " + purchaseRequestId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi cancelPurchaseRequest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cancel a repair request
    public boolean cancelRepairRequest(int repairRequestId, int userId) {
        String sql = "UPDATE Repair_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE repair_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Hủy repair request thành công, ID: " + repairRequestId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi cancelRepairRequest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}