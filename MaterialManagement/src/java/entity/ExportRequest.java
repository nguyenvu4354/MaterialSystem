package entity;

import java.util.Date;
import java.util.List;

public class ExportRequest {
    private int exportRequestId;
    private String requestCode;
    private int userId;
    private int recipientUserId;
    private Date requestDate;
    private String status;
    private Date deliveryDate;
    private String reason;
    private Integer approvedBy;
    private String approvalReason;
    private Date approvedAt;
    private String rejectionReason;
    private Date createdAt;
    private Date updatedAt;
    private boolean disable;
    
    // Additional fields for display
    private String userName;
    private String recipientName;
    private String approverName;
    private List<ExportRequestDetail> details;

    public ExportRequest() {
    }

    public ExportRequest(int exportRequestId, String requestCode, int userId, int recipientUserId, 
            Date requestDate, String status, Date deliveryDate, String reason, Integer approvedBy, 
            String approvalReason, Date approvedAt, String rejectionReason, Date createdAt, 
            Date updatedAt, boolean disable) {
        this.exportRequestId = exportRequestId;
        this.requestCode = requestCode;
        this.userId = userId;
        this.recipientUserId = recipientUserId;
        this.requestDate = requestDate;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.reason = reason;
        this.approvedBy = approvedBy;
        this.approvalReason = approvalReason;
        this.approvedAt = approvedAt;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
    }

    // Getters and Setters
    public int getExportRequestId() {
        return exportRequestId;
    }

    public void setExportRequestId(int exportRequestId) {
        this.exportRequestId = exportRequestId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(int recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public Date getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Date approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public List<ExportRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ExportRequestDetail> details) {
        this.details = details;
    }
} 