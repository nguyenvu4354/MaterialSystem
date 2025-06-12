package entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequest {
    private int repairRequestId;
    private String requestCode;
    private int userId;
    private LocalDateTime requestDate;
    private String repairPersonPhoneNumber;
    private String repairPersonEmail;
    private String repairLocation;
    private LocalDateTime estimatedReturnDate;
    private String status;
    private String reason;
    private Integer approvedBy; // Nullable theo FOREIGN KEY
    private String approvalReason;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean disable;
    private List<RepairRequestDetail> details; // Added field for details

    // Constructor mặc định
    public RepairRequest() {
    }

    // Constructor với tất cả các thuộc tính
    public RepairRequest(int repairRequestId, String requestCode, int userId, LocalDateTime requestDate,
                         String repairPersonPhoneNumber, String repairPersonEmail, String repairLocation,
                         LocalDateTime estimatedReturnDate, String status, String reason, Integer approvedBy,
                         String approvalReason, LocalDateTime approvedAt, String rejectionReason,
                         LocalDateTime createdAt, LocalDateTime updatedAt, boolean disable) {
        this.repairRequestId = repairRequestId;
        this.requestCode = requestCode;
        this.userId = userId;
        this.requestDate = requestDate;
        this.repairPersonPhoneNumber = repairPersonPhoneNumber;
        this.repairPersonEmail = repairPersonEmail;
        this.repairLocation = repairLocation;
        this.estimatedReturnDate = estimatedReturnDate;
        this.status = status;
        this.reason = reason;
        this.approvedBy = approvedBy;
        this.approvalReason = approvalReason;
        this.approvedAt = approvedAt;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
    }

    // Getters và Setters
    public int getRepairRequestId() {
        return repairRequestId;
    }

    public void setRepairRequestId(int repairRequestId) {
        this.repairRequestId = repairRequestId;
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

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getRepairPersonPhoneNumber() {
        return repairPersonPhoneNumber;
    }

    public void setRepairPersonPhoneNumber(String repairPersonPhoneNumber) {
        this.repairPersonPhoneNumber = repairPersonPhoneNumber;
    }

    public String getRepairPersonEmail() {
        return repairPersonEmail;
    }

    public void setRepairPersonEmail(String repairPersonEmail) {
        this.repairPersonEmail = repairPersonEmail;
    }

    public String getRepairLocation() {
        return repairLocation;
    }

    public void setRepairLocation(String repairLocation) {
        this.repairLocation = repairLocation;
    }

    public LocalDateTime getEstimatedReturnDate() {
        return estimatedReturnDate;
    }

    public void setEstimatedReturnDate(LocalDateTime estimatedReturnDate) {
        this.estimatedReturnDate = estimatedReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public List<RepairRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepairRequestDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RepairRequest{" +
                "repairRequestId=" + repairRequestId +
                ", requestCode='" + requestCode + '\'' +
                ", userId=" + userId +
                ", requestDate=" + requestDate +
                ", repairPersonPhoneNumber='" + repairPersonPhoneNumber + '\'' +
                ", repairPersonEmail='" + repairPersonEmail + '\'' +
                ", repairLocation='" + repairLocation + '\'' +
                ", estimatedReturnDate=" + estimatedReturnDate +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", approvedBy=" + approvedBy +
                ", approvalReason='" + approvalReason + '\'' +
                ", approvedAt=" + approvedAt +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", disable=" + disable +
                ", details=" + details +
                '}';
    }
}