/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class Import {

    private int importId;
    private String importCode;
    private LocalDateTime importDate;
    private int importedBy;
    private Integer supplierId;
    private String destination;
    private LocalDateTime actualArrival;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String importedByName;
    private String supplierName;
    private int totalQuantity;
    private double totalValue;

    public Import(int importId, String importCode, LocalDateTime importDate, int importedBy, Integer supplierId, String destination, LocalDateTime actualArrival, String note, LocalDateTime createdAt, LocalDateTime updatedAt, String importedByName, String supplierName, int totalQuantity, double totalValue) {
        this.importId = importId;
        this.importCode = importCode;
        this.importDate = importDate;
        this.importedBy = importedBy;
        this.supplierId = supplierId;
        this.destination = destination;
        this.actualArrival = actualArrival;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.importedByName = importedByName;
        this.supplierName = supplierName;
        this.totalQuantity = totalQuantity;
        this.totalValue = totalValue;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    public int getImportedBy() {
        return importedBy;
    }

    public void setImportedBy(int importedBy) {
        this.importedBy = importedBy;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
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

    public String getImportedByName() {
        return importedByName;
    }

    public void setImportedByName(String importedByName) {
        this.importedByName = importedByName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

}
