/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author Admin
 */
public class Material {
    private int materialId;
    private String materialCode;
    private String materialName;
    private String materialsUrl;
    private String materialStatus; // Should be 'new', 'used', or 'damaged'
    private int conditionPercentage;
    private BigDecimal price;
    private int quantity;
    private int categoryId;
    private Integer supplierId; // nullable
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean disable;

    // Constructors
    public Material() {}

    public Material(int materialId, String materialCode, String materialName, String materialsUrl, String materialStatus,
                    int conditionPercentage, BigDecimal price, int quantity, int categoryId, Integer supplierId,
                    LocalDateTime createdAt, LocalDateTime updatedAt, boolean disable) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialsUrl = materialsUrl;
        this.materialStatus = materialStatus;
        this.conditionPercentage = conditionPercentage;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
    }

    // Getters and Setters
    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialsUrl() {
        return materialsUrl;
    }

    public void setMaterialsUrl(String materialsUrl) {
        this.materialsUrl = materialsUrl;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }

    public int getConditionPercentage() {
        return conditionPercentage;
    }

    public void setConditionPercentage(int conditionPercentage) {
        this.conditionPercentage = conditionPercentage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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
}
