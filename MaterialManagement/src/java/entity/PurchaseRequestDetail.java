package entity;

import java.sql.Timestamp;

public class PurchaseRequestDetail {
    private int purchaseRequestDetailId;
    private int purchaseRequestId;
    private String materialName;
    private int categoryId;
    private String categoryName; 
    private int quantity;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PurchaseRequestDetail() {
    }

    public int getPurchaseRequestDetailId() {
        return purchaseRequestDetailId;
    }

    public void setPurchaseRequestDetailId(int purchaseRequestDetailId) {
        this.purchaseRequestDetailId = purchaseRequestDetailId;
    }

    public int getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
}