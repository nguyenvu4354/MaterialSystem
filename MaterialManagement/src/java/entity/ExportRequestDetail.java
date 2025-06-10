package entity;

import java.util.Date;

public class ExportRequestDetail {
    private int detailId;
    private int exportRequestId;
    private int materialId;
    private String materialCode;
    private String materialName;
    private String materialUnit;
    private int quantity;
    private String exportCondition;
    private Date createdAt;
    private Date updatedAt;
    
    public ExportRequestDetail() {
    }

    public ExportRequestDetail(int detailId, int exportRequestId, int materialId, 
            String materialCode, String materialName, String materialUnit, int quantity, 
            String exportCondition, Date createdAt, Date updatedAt) {
        this.detailId = detailId;
        this.exportRequestId = exportRequestId;
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialUnit = materialUnit;
        this.quantity = quantity;
        this.exportCondition = exportCondition;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getExportRequestId() {
        return exportRequestId;
    }

    public void setExportRequestId(int exportRequestId) {
        this.exportRequestId = exportRequestId;
    }

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

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExportCondition() {
        return exportCondition;
    }

    public void setExportCondition(String exportCondition) {
        this.exportCondition = exportCondition;
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
} 