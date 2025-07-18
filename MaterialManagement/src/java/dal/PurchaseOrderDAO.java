package dal;

import entity.DBContext;
import entity.PurchaseOrder;
import entity.PurchaseOrderDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseOrderDAO extends DBContext {
    
    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderDAO.class.getName());
    
    public PurchaseOrderDAO() {
        super();
    }

    // Lấy danh sách Purchase Orders với pagination và filter
    public List<PurchaseOrder> getPurchaseOrders(int page, int pageSize, String status, String poCode, LocalDate startDate, LocalDate endDate) {
        List<PurchaseOrder> orders = new ArrayList<>();
        LOGGER.info("Starting getPurchaseOrders method with page=" + page + ", pageSize=" + pageSize + ", status=" + status + ", poCode=" + poCode);
        
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is null");
            return orders;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT po.*, u1.full_name AS created_by_name, u2.full_name AS approved_by_name, "
                + "pr.request_code AS purchase_request_code, "
                + "COALESCE(SUM(pod.quantity * pod.unit_price), 0) AS total_amount "
                + "FROM Purchase_Orders po "
                + "JOIN Users u1 ON po.created_by = u1.user_id "
                + "LEFT JOIN Users u2 ON po.approved_by = u2.user_id "
                + "JOIN Purchase_Requests pr ON po.purchase_request_id = pr.purchase_request_id "
                + "LEFT JOIN Purchase_Order_Details pod ON po.po_id = pod.po_id "
                + "WHERE po.disable = 0 "
        );
        
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND po.status = ? ");
            params.add(status.trim());
        }

        if (poCode != null && !poCode.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
            params.add("%" + poCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        sql.append("GROUP BY po.po_id ");
        sql.append("ORDER BY po.po_id ASC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            LOGGER.info("Executing SQL: " + sql.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseOrder order = mapResultSetToPurchaseOrder(rs);
                orders.add(order);
            }
            LOGGER.info("Retrieved " + orders.size() + " purchase orders");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase orders: " + e.getMessage(), e);
        }
        return orders;
    }

    // Đếm tổng số Purchase Orders
    public int getPurchaseOrderCount(String status, String poCode, LocalDate startDate, LocalDate endDate) {
        int count = 0;
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is null");
            return count;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM Purchase_Orders po WHERE po.disable = 0 "
        );
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND po.status = ? ");
            params.add(status.trim());
        }

        if (poCode != null && !poCode.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
            params.add("%" + poCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting purchase orders: " + e.getMessage(), e);
        }
        return count;
    }

    // Lấy Purchase Order theo ID
    public PurchaseOrder getPurchaseOrderById(int poId) {
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is null");
            return null;
        }

        String sql = "SELECT po.*, u1.full_name AS created_by_name, u2.full_name AS approved_by_name, "
                + "pr.request_code AS purchase_request_code, "
                + "COALESCE(SUM(pod.quantity * pod.unit_price), 0) AS total_amount "
                + "FROM Purchase_Orders po "
                + "JOIN Users u1 ON po.created_by = u1.user_id "
                + "LEFT JOIN Users u2 ON po.approved_by = u2.user_id "
                + "JOIN Purchase_Requests pr ON po.purchase_request_id = pr.purchase_request_id "
                + "LEFT JOIN Purchase_Order_Details pod ON po.po_id = pod.po_id "
                + "WHERE po.po_id = ? AND po.disable = 0 "
                + "GROUP BY po.po_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PurchaseOrder order = mapResultSetToPurchaseOrder(rs);
                // Lấy chi tiết
                order.setDetails(getPurchaseOrderDetails(poId));
                LOGGER.info("Retrieved purchase order: " + order.getPoCode());
                return order;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase order by ID: " + e.getMessage(), e);
        }
        return null;
    }

    // Lấy chi tiết Purchase Order
    public List<PurchaseOrderDetail> getPurchaseOrderDetails(int poId) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is null");
            return details;
        }

        String sql = "SELECT pod.*, m.material_id, m.material_name, m.materials_url, c.category_name, s.supplier_name "
                + "FROM Purchase_Order_Details pod "
                + "LEFT JOIN materials m ON pod.material_id = m.material_id "
                + "LEFT JOIN Categories c ON pod.category_id = c.category_id "
                + "LEFT JOIN Suppliers s ON pod.supplier_id = s.supplier_id "
                + "WHERE pod.po_id = ? "
                + "ORDER BY pod.po_detail_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseOrderDetail detail = mapResultSetToPurchaseOrderDetail(rs);
                details.add(detail);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase order details: " + e.getMessage(), e);
        }
        return details;
    }

    // Cập nhật status của Purchase Order
    public boolean updatePurchaseOrderStatus(int poId, String status, Integer approvedBy, String approvalReason, String rejectionReason) {
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is null");
            return false;
        }

        String sql = "UPDATE Purchase_Orders SET status = ?, approved_by = ?, approve_reason = ?, "
                + "rejection_reason = ?, approved_at = CASE WHEN ? = 'approved' THEN CURRENT_TIMESTAMP ELSE NULL END, "
                + "sent_to_supplier_at = CASE WHEN ? = 'sent_to_supplier' THEN CURRENT_TIMESTAMP ELSE sent_to_supplier_at END, "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE po_id = ? AND disable = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setObject(2, approvedBy);
            ps.setString(3, approvalReason);
            ps.setString(4, rejectionReason);
            ps.setString(5, status);
            ps.setString(6, status);
            ps.setInt(7, poId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Successfully updated purchase order status, ID: " + poId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating purchase order status: " + e.getMessage(), e);
        }
        return false;
    }

    // Helper method để map ResultSet thành PurchaseOrder object
    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder order = new PurchaseOrder();
        order.setPoId(rs.getInt("po_id"));
        order.setPoCode(rs.getString("po_code"));
        order.setPurchaseRequestId(rs.getInt("purchase_request_id"));
        order.setCreatedBy(rs.getInt("created_by"));
        order.setCreatedByName(rs.getString("created_by_name"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        order.setStatus(rs.getString("status"));
        order.setNote(rs.getString("note"));
        order.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
        order.setApprovedByName(rs.getString("approved_by_name"));
        order.setApprovedAt(rs.getTimestamp("approved_at"));
        order.setRejectionReason(rs.getString("rejection_reason"));
        order.setSentToSupplierAt(rs.getTimestamp("sent_to_supplier_at"));
        order.setDisable(rs.getBoolean("disable"));
        order.setPurchaseRequestCode(rs.getString("purchase_request_code"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        return order;
    }

    // Helper method để map ResultSet thành PurchaseOrderDetail object
    private PurchaseOrderDetail mapResultSetToPurchaseOrderDetail(ResultSet rs) throws SQLException {
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPoDetailId(rs.getInt("po_detail_id"));
        detail.setPoId(rs.getInt("po_id"));
        detail.setMaterialId(rs.getInt("material_id"));
        detail.setMaterialName(rs.getString("material_name"));
        detail.setMaterialImageUrl(rs.getString("materials_url"));
        detail.setCategoryId(rs.getInt("category_id"));
        detail.setCategoryName(rs.getString("category_name"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getBigDecimal("unit_price"));
        detail.setSupplierId(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null);
        detail.setSupplierName(rs.getString("supplier_name"));
        detail.setNote(rs.getString("note"));
        detail.setCreatedAt(rs.getTimestamp("created_at"));
        detail.setUpdatedAt(rs.getTimestamp("updated_at"));
        return detail;
    }

    // Sinh mã PO code tự động theo định dạng PO001, PO002, ...
    public String generateNextPOCode() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(po_code, 3) AS UNSIGNED)) AS max_num FROM Purchase_Orders WHERE po_code LIKE 'PO%'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                int nextNum = 1;
                if (rs.next()) {
                    nextNum = rs.getInt("max_num") + 1;
                }
                return String.format("PO%03d", nextNum);
            }
        }
    }

    // Tạo Purchase Order mới
    public boolean createPurchaseOrder(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> details) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // Insert Purchase Order
            String insertPOSql = "INSERT INTO Purchase_Orders (po_code, purchase_request_id, created_by, status, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(insertPOSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Sinh mã PO code tự động nếu chưa có
                if (purchaseOrder.getPoCode() == null || purchaseOrder.getPoCode().isEmpty()) {
                    purchaseOrder.setPoCode(generateNextPOCode());
                }
                ps.setString(1, purchaseOrder.getPoCode());
                ps.setInt(2, purchaseOrder.getPurchaseRequestId());
                ps.setInt(3, purchaseOrder.getCreatedBy());
                ps.setString(4, purchaseOrder.getStatus());
                ps.setString(5, purchaseOrder.getNote());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating purchase order failed, no rows affected.");
                }
                // Get generated PO ID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int poId = generatedKeys.getInt(1);
                        // Insert Purchase Order Details
                        String insertDetailSql = "INSERT INTO Purchase_Order_Details (po_id, material_id, category_id, quantity, unit_price, supplier_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                        try (PreparedStatement detailPs = conn.prepareStatement(insertDetailSql)) {
                            for (PurchaseOrderDetail detail : details) {
                                detailPs.setInt(1, poId);
                                detailPs.setInt(2, detail.getMaterialId());
                                detailPs.setInt(3, detail.getCategoryId());
                                detailPs.setInt(4, detail.getQuantity());
                                detailPs.setBigDecimal(5, detail.getUnitPrice());
                                if (detail.getSupplierId() != null) {
                                    detailPs.setInt(6, detail.getSupplierId());
                                } else {
                                    detailPs.setNull(6, java.sql.Types.INTEGER);
                                }
                                int detailRowsAffected = detailPs.executeUpdate();
                                if (detailRowsAffected == 0) {
                                    throw new SQLException("Creating purchase order detail failed, no rows affected.");
                                }
                            }
                        }
                        conn.commit();
                        LOGGER.info("Successfully created purchase order with ID: " + poId);
                        return true;
                    } else {
                        throw new SQLException("Creating purchase order failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { LOGGER.log(Level.SEVERE, "Error rolling back transaction", rollbackEx); }
            }
            LOGGER.log(Level.SEVERE, "Error creating purchase order: " + e.getMessage(), e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error closing connection", e); }
            }
        }
    }

    // Lấy connection để sử dụng trong servlet
    public java.sql.Connection getConnection() {
        return connection;
    }
} 