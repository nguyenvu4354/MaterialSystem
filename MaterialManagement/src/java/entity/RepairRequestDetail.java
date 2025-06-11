/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequestDetail {
    private int detailId;
    private int repairRequestId;
    private int materialId;
    private int quantity;
    private String damageDescription;
    private Double repairCost; // Nullable theo DEFAULT NULL
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor mặc định
    public RepairRequestDetail() {
    }

    // Constructor với tất cả các thuộc tính
    public RepairRequestDetail(int detailId, int repairRequestId, int materialId, int quantity,
                              String damageDescription, Double repairCost, LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        this.detailId = detailId;
        this.repairRequestId = repairRequestId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.damageDescription = damageDescription;
        this.repairCost = repairCost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters và Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getRepairRequestId() {
        return repairRequestId;
    }

    public void setRepairRequestId(int repairRequestId) {
        this.repairRequestId = repairRequestId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public Double getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(Double repairCost) {
        this.repairCost = repairCost;
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

    @Override
    public String toString() {
        return "RepairRequestDetail{" +
                "detailId=" + detailId +
                ", repairRequestId=" + repairRequestId +
                ", materialId=" + materialId +
                ", quantity=" + quantity +
                ", damageDescription='" + damageDescription + '\'' +
                ", repairCost=" + repairCost +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
