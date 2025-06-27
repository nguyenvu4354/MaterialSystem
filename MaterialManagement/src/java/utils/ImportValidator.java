package utils;

import dal.SupplierDAO;
import entity.Import;
import entity.ImportDetail;
import java.util.HashMap;
import java.util.Map;

public class ImportValidator {

    // Validate Import entity 
    public static Map<String, String> validateImport(Import imp, SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate supplierId
        if (imp.getSupplierId() == null || supplierDAO.getSupplierByID(imp.getSupplierId()) == null) {
            errors.put("supplierId", "Supplier is required and must exist.");
        }

        // Validate destination
        if (imp.getDestination() == null || imp.getDestination().trim().isEmpty()) {
            errors.put("destination", "Destination cannot be empty.");
        } else if (imp.getDestination().trim().length() > 200) {
            errors.put("destination", "Destination cannot exceed 200 characters.");
        }

        // Validate batchNumber
        if (imp.getBatchNumber() == null || imp.getBatchNumber().trim().isEmpty()) {
            errors.put("batchNumber", "Batch Number cannot be empty.");
        } else if (imp.getBatchNumber().trim().length() > 50) {
            errors.put("batchNumber", "Batch Number cannot exceed 50 characters.");
        }

        // Validate actualArrival
        if (imp.getActualArrival() == null) {
            errors.put("actualArrival", "Actual Arrival is required.");
        }

        // Validate note (optional)
        if (imp.getNote() != null && imp.getNote().trim().length() > 200) {
            errors.put("note", "Note cannot exceed 200 characters.");
        }

        return errors;
    }

    // Validate ImportDetail entity 
    public static Map<String, String> validateImportDetail(ImportDetail detail) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialId
        if (detail.getMaterialId() <= 0) {
            errors.put("materialId", "Material is required.");
        }

        // Validate quantity
        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
        }

        // Validate unitPrice
        if (detail.getUnitPrice() < 0) {
            errors.put("unitPrice", "Unit price cannot be negative.");
        }

        // Validate materialCondition
        if (detail.getMaterialCondition() == null || detail.getMaterialCondition().trim().isEmpty()) {
            errors.put("materialCondition", "Material condition is required.");
        }

        return errors;
    }

    // Validate import form data 
    public static Map<String, String> validateImportFormData(String supplierIdStr, String destination, String batchNumber, String actualArrivalStr, String note, SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate supplierId
        Integer supplierId = null;
        try {
            supplierId = (supplierIdStr != null && !supplierIdStr.isEmpty()) ? Integer.parseInt(supplierIdStr) : null;
        } catch (NumberFormatException e) {
            errors.put("supplierId", "Invalid supplier ID format.");
        }
        if (supplierId == null || supplierDAO.getSupplierByID(supplierId) == null) {
            errors.put("supplierId", "Supplier is required and must exist.");
        }

        // Validate destination
        if (destination == null || destination.trim().isEmpty()) {
            errors.put("destination", "Destination cannot be empty.");
        } else if (destination.trim().length() > 200) {
            errors.put("destination", "Destination cannot exceed 200 characters.");
        }

        // Validate batchNumber
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            errors.put("batchNumber", "Batch Number cannot be empty.");
        } else if (batchNumber.trim().length() > 50) {
            errors.put("batchNumber", "Batch Number cannot exceed 50 characters.");
        }

        // Validate actualArrival
        if (actualArrivalStr == null || actualArrivalStr.isEmpty()) {
            errors.put("actualArrival", "Actual Arrival is required.");
        } else {
            try {
                java.time.LocalDateTime.parse(actualArrivalStr);
            } catch (Exception e) {
                errors.put("actualArrival", "Invalid actual arrival date format.");
            }
        }

        // Validate note (optional)
        if (note != null && note.trim().length() > 200) {
            errors.put("note", "Note cannot exceed 200 characters.");
        }

        return errors;
    }
} 