package entity;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Admin
 */
public class PurchaseRequest {
    private int purchaseRequestId;
    private String requestCode;
    private int userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private Timestamp requestDate;
    private String status; // ENUM: draft, approved, rejected, cancel
    private double estimatedPrice;
    private String reason;
    private Integer approvedBy;
    private String approvalReason;
    private Timestamp approvedAt;
    private String rejectionReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean disable;
    private List<PurchaseRequestDetail> details;

    public PurchaseRequest() {
    }

    public PurchaseRequest(int purchaseRequestId, String requestCode, int userId, Timestamp requestDate, String status, 
                           double estimatedPrice, String reason, Integer approvedBy, String approvalReason, 
                           Timestamp approvedAt, String rejectionReason, Timestamp createdAt, Timestamp updatedAt, 
                           boolean disable) {
        this.purchaseRequestId = purchaseRequestId;
        this.requestCode = requestCode;
        this.userId = userId;
        this.requestDate = requestDate;
        this.status = status;
        this.estimatedPrice = estimatedPrice;
        this.reason = reason;
        this.approvedBy = approvedBy;
        this.approvalReason = approvalReason;
        this.approvedAt = approvedAt;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
    }

    public int getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
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

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public List<PurchaseRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseRequestDetail> details) {
        this.details = details;
    }
}