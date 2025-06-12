package entity;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public class PurchaseRequest {
    private int id;
    private String requestCode;
    private int userId;
    private Date requestDate;
    private String status;
    private Double estimatedPrice;
    private String reason;
    private Integer approvedBy;
    private String approvalReason;
    private Date approvedAt;
    private String rejectionReason;
    private Date createdAt;
    private Date updatedAt;
    private boolean disable;
    private List<PurchaseRequestDetail> details; // Added field for details

    public PurchaseRequest() {
    }

    public PurchaseRequest(int id, String requestCode, int userId, Date requestDate, String status, 
                           Double estimatedPrice, String reason, Integer approvedBy, String approvalReason, 
                           Date approvedAt, String rejectionReason, Date createdAt, Date updatedAt, 
                           boolean disable) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
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

    public List<PurchaseRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseRequestDetail> details) {
        this.details = details;
    }
}