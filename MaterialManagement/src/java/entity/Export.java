package entity;

import java.time.LocalDateTime;

public class Export {
    private int exportId;
    private String exportCode;
    private LocalDateTime exportDate;
    private int exportedBy;
    private int recipientUserId;
    private String batchNumber;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters
    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public String getExportCode() {
        return exportCode;
    }

    public void setExportCode(String exportCode) {
        this.exportCode = exportCode;
    }

    public LocalDateTime getExportDate() {
        return exportDate;
    }

    public void setExportDate(LocalDateTime exportDate) {
        this.exportDate = exportDate;
    }

    public int getExportedBy() {
        return exportedBy;
    }

    public void setExportedBy(int exportedBy) {
        this.exportedBy = exportedBy;
    }

    public int getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(int recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}