/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */

import java.sql.Timestamp;

public class PurchaseRequestDetail {
    private int detailId;
    private int purchaseRequestId;
    private String materialName;
    private int categoryId;
    private int quantity;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    
    public PurchaseRequestDetail() {
    }

    public PurchaseRequestDetail(int detailId, int purchaseRequestId, String materialName, int categoryId, int quantity, String notes, Timestamp createdAt, Timestamp updatedAt) {
        this.detailId = detailId;
        this.purchaseRequestId = purchaseRequestId;
        this.materialName = materialName;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
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
