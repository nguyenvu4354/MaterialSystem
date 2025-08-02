package utils;

import dal.MaterialDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.Supplier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PurchaseOrderValidator {
    
    public static Map<String, String> validatePurchaseOrderFormData(String poCode, String purchaseRequestId, String note) {
        Map<String, String> errors = new HashMap<>();
        
        if (poCode == null || poCode.trim().isEmpty()) {
            errors.put("poCode", "Purchase Order code cannot be empty.");
        }
        
        if (purchaseRequestId == null || purchaseRequestId.trim().isEmpty()) {
            errors.put("purchaseRequestId", "Please select a purchase request.");
        } else {
            try {
                int requestId = Integer.parseInt(purchaseRequestId);
                if (requestId <= 0) {
                    errors.put("purchaseRequestId", "Please select a valid purchase request.");
                }
            } catch (NumberFormatException e) {
                errors.put("purchaseRequestId", "Please select a valid purchase request.");
            }
        }
        
        // Note is optional, so no validation needed
        
        return errors;
    }
    
    public static Map<String, String> validatePurchaseOrderDetails(String[] materialIds, String[] quantities, 
                                                                 String[] unitPrices, String[] suppliers) {
        Map<String, String> errors = new HashMap<>();
        
        // Debug: Print array lengths
        System.out.println("DEBUG - materialIds.length: " + (materialIds != null ? materialIds.length : "null"));
        System.out.println("DEBUG - quantities.length: " + (quantities != null ? quantities.length : "null"));
        System.out.println("DEBUG - unitPrices.length: " + (unitPrices != null ? unitPrices.length : "null"));
        System.out.println("DEBUG - suppliers.length: " + (suppliers != null ? suppliers.length : "null"));
        
        if (materialIds == null || quantities == null || unitPrices == null || suppliers == null) {
            errors.put("general", "At least one material is required.");
            return errors;
        }
        
        if (materialIds.length == 0) {
            errors.put("general", "At least one material is required.");
            return errors;
        }
        
        // Handle case where some arrays might be empty but materialIds exists
        int expectedLength = materialIds.length;
        if (quantities.length != expectedLength || unitPrices.length != expectedLength || suppliers.length != expectedLength) {
            System.out.println("DEBUG - Array length mismatch. Expected: " + expectedLength + ", quantities: " + quantities.length + ", unitPrices: " + unitPrices.length + ", suppliers: " + suppliers.length);
            errors.put("general", "All material fields must be filled.");
            return errors;
        }
        
        MaterialDAO materialDAO = new MaterialDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        
        for (int i = 0; i < materialIds.length; i++) {
            String materialId = materialIds[i];
            String quantity = quantities[i];
            String unitPrice = unitPrices[i];
            String supplier = suppliers[i];
            
            // Debug: Print individual values
            System.out.println("DEBUG - Material " + i + " - materialId: '" + materialId + "', quantity: '" + quantity + "', unitPrice: '" + unitPrice + "', supplier: '" + supplier + "'");
            
            // Validate material ID
            if (materialId == null || materialId.trim().isEmpty()) {
                errors.put("material_" + i, "Please select a material.");
            } else {
                try {
                    int matId = Integer.parseInt(materialId);
                    Material material = materialDAO.getInformation(matId);
                    if (material == null) {
                        errors.put("material_" + i, "Selected material does not exist.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("material_" + i, "Please select a valid material.");
                }
            }
            
            // Validate quantity
            if (quantity == null || quantity.trim().isEmpty()) {
                errors.put("quantity_" + i, "Quantity cannot be empty.");
            } else {
                try {
                    int qty = Integer.parseInt(quantity);
                    if (qty <= 0) {
                        errors.put("quantity_" + i, "Quantity must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("quantity_" + i, "Please enter a valid quantity.");
                }
            }
            
            // Validate unit price
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                errors.put("unitPrice_" + i, "Unit price cannot be empty.");
            } else {
                try {
                    BigDecimal price = new BigDecimal(unitPrice);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put("unitPrice_" + i, "Unit price must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("unitPrice_" + i, "Please enter a valid unit price.");
                }
            }
            
            // Validate supplier
            if (supplier == null || supplier.trim().isEmpty()) {
                errors.put("supplier_" + i, "Please select a supplier.");
            } else {
                try {
                    int supId = Integer.parseInt(supplier);
                    Supplier sup = supplierDAO.getSupplierByID(supId);
                    if (sup == null) {
                        errors.put("supplier_" + i, "Selected supplier does not exist.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("supplier_" + i, "Please select a valid supplier.");
                }
            }
        }
        
        return errors;
    }
} 