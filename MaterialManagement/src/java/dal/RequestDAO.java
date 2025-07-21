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
import java.util.ArrayList;
import java.util.List;

public class RequestDAO extends DBContext {

    public List<ExportRequest> getExportRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<ExportRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT er.*, u1.full_name AS user_name, u2.full_name AS recipient_name, u3.full_name AS approver_name "
                + "FROM Export_Requests er "
                + "JOIN Users u1 ON er.user_id = u1.user_id "
                + "JOIN Users u2 ON er.recipient_user_id = u2.user_id "
                + "LEFT JOIN Users u3 ON er.approved_by = u3.user_id "
                + "LEFT JOIN Export_Request_Details erd ON er.export_request_id = erd.export_request_id "
                + "LEFT JOIN Materials m ON erd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

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
            e.printStackTrace();
        }
        return null;
    }

    private List<ExportRequestDetail> getExportRequestDetails(int exportRequestId) {
        List<ExportRequestDetail> details = new ArrayList<>();
        String sql = "SELECT erd.detail_id, erd.export_request_id, erd.material_id, erd.quantity, erd.created_at, erd.updated_at, "
                + "m.material_code, m.material_name, u.unit_name "
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
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private List<PurchaseRequestDetail> getPurchaseRequestDetails(int purchaseRequestId) {
        List<PurchaseRequestDetail> details = new ArrayList<>();
        String sql = "SELECT prd.*, m.material_name, m.material_code, u.unit_name "
                + "FROM Purchase_Request_Details prd "
                + "JOIN Materials m ON prd.material_id = m.material_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "WHERE prd.purchase_request_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setPurchaseRequestDetailId(rs.getInt("detail_id"));
                detail.setPurchaseRequestId(rs.getInt("purchase_request_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setNotes(rs.getString("notes"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

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
            e.printStackTrace();
        }
        return details;
    }

    public int getExportRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT er.export_request_id) FROM Export_Requests er "
                + "LEFT JOIN Export_Request_Details erd ON er.export_request_id = erd.export_request_id "
                + "LEFT JOIN Materials m ON erd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
            e.printStackTrace();
        }
        return 0;
    }

    public List<PurchaseRequest> getPurchaseRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<PurchaseRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT pr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Purchase_Requests pr "
                + "JOIN Users u ON pr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON pr.approved_by = u2.user_id "
                + "LEFT JOIN Purchase_Request_Details prd ON pr.purchase_request_id = prd.purchase_request_id "
                + "LEFT JOIN Materials m ON prd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

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
            e.printStackTrace();
        }
        return null;
    }

    public int getPurchaseRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT pr.purchase_request_id) FROM Purchase_Requests pr "
                + "LEFT JOIN Purchase_Request_Details prd ON pr.purchase_request_id = prd.purchase_request_id "
                + "LEFT JOIN Materials m ON prd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
            e.printStackTrace();
        }
        return 0;
    }

    public List<RepairRequest> getRepairRequestsByUser(int userId, int page, int pageSize, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<RepairRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT rr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.user_id = u.user_id "
                + "LEFT JOIN Users u2 ON rr.approved_by = u2.user_id "
                + "LEFT JOIN Repair_Request_Details rrd ON rr.repair_request_id = rrd.repair_request_id "
                + "LEFT JOIN Materials m ON rrd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

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
            e.printStackTrace();
        }
        return null;
    }

    public int getRepairRequestCountByUser(int userId, String status, String requestCode, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT rr.repair_request_id) FROM Repair_Requests rr "
                + "LEFT JOIN Repair_Request_Details rrd ON rr.repair_request_id = rrd.repair_request_id "
                + "LEFT JOIN Materials m ON rrd.material_id = m.material_id "
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

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
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
            e.printStackTrace();
        }
        return 0;
    }

    public boolean cancelExportRequest(int exportRequestId, int userId) {
        String sql = "UPDATE Export_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE export_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelPurchaseRequest(int purchaseRequestId, int userId) {
        String sql = "UPDATE Purchase_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE purchase_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRepairRequest(int repairRequestId, int userId) {
        String sql = "UPDATE Repair_Requests SET status = 'cancel', updated_at = CURRENT_TIMESTAMP "
                + "WHERE repair_request_id = ? AND user_id = ? AND status = 'pending' AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
