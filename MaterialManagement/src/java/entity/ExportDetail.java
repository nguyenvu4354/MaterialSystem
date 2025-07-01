package entity;

import java.time.LocalDateTime;

public class ExportDetail {
    private int exportDetailId;
    private int exportId;
    private int materialId;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getExportDetailId() { return exportDetailId; }
    public void setExportDetailId(int exportDetailId) { this.exportDetailId = exportDetailId; }
    public int getExportId() { return exportId; }
    public void setExportId(int exportId) { this.exportId = exportId; }
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}