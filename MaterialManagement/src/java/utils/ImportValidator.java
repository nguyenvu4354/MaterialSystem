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
        } else if (imp.getDestination().trim().length() > 100) {
            errors.put("destination", "Destination cannot exceed 100 characters.");
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

        return errors;
    }

    // Validate import form data 
    public static Map<String, String> validateImportFormData(String supplierIdStr, String destination, String note, dal.SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();
        // Validate supplierId
        if (supplierIdStr == null || supplierIdStr.isEmpty()) {
            errors.put("supplierId", "Supplier is required.");
        } else {
            try {
                int supplierId = Integer.parseInt(supplierIdStr);
                if (supplierDAO.getSupplierByID(supplierId) == null) {
                    errors.put("supplierId", "Supplier does not exist.");
                }
            } catch (NumberFormatException e) {
                errors.put("supplierId", "Invalid supplier ID.");
            }
        }
        // Validate destination
        if (destination == null || destination.trim().isEmpty()) {
            errors.put("destination", "Destination is required.");
        } else if (destination.length() > 100) {
            errors.put("destination", "Destination must not exceed 100 characters.");
        }

        // Validate note
        if (note != null && note.length() > 255) {
            errors.put("note", "Note must not exceed 255 characters.");
        }
        return errors;
    }

    public static Map<String, String> validateAddMaterial(String materialIdStr, String quantityStr, String unitPriceStr, dal.MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();
        int materialId = 0;
        int quantity = 0;
        double unitPrice = 0;
        try {
            materialId = Integer.parseInt(materialIdStr);
            quantity = Integer.parseInt(quantityStr);
            unitPrice = Double.parseDouble(unitPriceStr);
        } catch (NumberFormatException e) {
            errors.put("numberFormat", "Invalid number format for material, quantity, or unit price.");
        }
        if (quantity <= 0) errors.put("quantity", "Quantity must be greater than 0.");
        if (unitPrice < 0) errors.put("unitPrice", "Unit price cannot be negative.");
        if (materialId <= 0 || materialDAO.getInformation(materialId) == null) errors.put("materialId", "Material is required and must exist.");
        return errors;
    }

    public static double calculateTotalImportValue(java.util.List<entity.ImportDetail> details) {
        double total = 0;
        for (entity.ImportDetail d : details) {
            total += d.getQuantity() * d.getUnitPrice();
        }
        return total;
    }
} 