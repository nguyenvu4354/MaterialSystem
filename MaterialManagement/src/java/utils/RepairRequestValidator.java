package utils;

import dal.MaterialDAO;
import entity.Material;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairRequestValidator {

    public static Map<String, String> validateRepairRequest(RepairRequest repairRequest) {
        Map<String, String> errors = new HashMap<>();

        if (repairRequest.getReason() == null || repairRequest.getReason().trim().isEmpty()) {
            errors.put("reason", "Reason for repair request cannot be empty.");
        }

        if (repairRequest.getUserId() <= 0) {
            errors.put("userId", "Invalid user ID.");
        }

        if (repairRequest.getDetails() == null || repairRequest.getDetails().isEmpty()) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validateRepairRequestFormData(String reason, String supplierId) {
        Map<String, String> errors = new HashMap<>();
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason for repair request cannot be empty.");
        }
        if (supplierId == null || supplierId.trim().isEmpty()) {
            errors.put("supplierId", "Please select a repairer.");
        }
        return errors;
    }

    public static Map<String, String> validateRepairRequestDetails(String[] materialNames, String[] quantities, String[] damageDescriptions) {
        Map<String, String> errors = new HashMap<>();

        if (materialNames == null || quantities == null || damageDescriptions == null) {
            errors.put("details", "Detail information is invalid.");
            return errors;
        }

        boolean hasValidDetail = false;
        MaterialDAO materialDAO = new MaterialDAO();

        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            String quantityStr = quantities[i];
            String damageDescription = damageDescriptions[i];

            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            }

            // Check if material exists in database
            String trimmedMaterialName = materialName.trim();
            // Xử lý trường hợp tên vật tư có "(damaged)" ở cuối
            if (trimmedMaterialName.endsWith(" (damaged)")) {
                trimmedMaterialName = trimmedMaterialName.substring(0, trimmedMaterialName.length() - 10); // Bỏ "(damaged)"
            }
            
            Material material = materialDAO.getInformationByNameAndStatus(trimmedMaterialName, "damaged");
            if (material == null) {
                errors.put("material_" + i, "Material '" + materialName.trim() + "' does not exist in inventory.");
                continue;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                errors.put("quantity_" + i, "Quantity cannot be empty for material " + trimmedMaterialName + ".");
            } else {
                String trimmedQuantity = quantityStr.trim();
                if (trimmedQuantity.contains(".") || trimmedQuantity.contains(",")) {
                    errors.put("quantity_" + i, "Quantity must be a whole number (no decimals) for material " + trimmedMaterialName + ".");
                } else {
                    try {
                        int quantity = Integer.parseInt(trimmedQuantity);
                        if (quantity <= 0) {
                            errors.put("quantity_" + i, "Quantity must be greater than 0 for material " + trimmedMaterialName + ".");
                        }
                    } catch (NumberFormatException e) {
                        errors.put("quantity_" + i, "Invalid quantity format for material " + trimmedMaterialName + ".");
                    }
                }
            }

            if (damageDescription == null || damageDescription.trim().isEmpty()) {
                errors.put("damageDescription_" + i, "Damage description cannot be empty for material " + trimmedMaterialName + ".");
            }

            hasValidDetail = true;
        }

        if (!hasValidDetail) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validateRepairRequestDetail(RepairRequestDetail detail) {
        Map<String, String> errors = new HashMap<>();

        if (detail.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid material ID.");
        }

        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
        }

        if (detail.getDamageDescription() == null || detail.getDamageDescription().trim().isEmpty()) {
            errors.put("damageDescription", "Damage description cannot be empty.");
        }

        return errors;
    }

    // New method to validate material names against database
    public static Map<String, String> validateMaterialNames(String[] materialNames) {
        Map<String, String> errors = new HashMap<>();
        
        if (materialNames == null) {
            return errors;
        }

        MaterialDAO materialDAO = new MaterialDAO();
        
        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            
            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            }

            String trimmedMaterialName = materialName.trim();
            Material material = materialDAO.getMaterialByName(trimmedMaterialName);
            
            if (material == null) {
                errors.put("material_" + i, "Material '" + trimmedMaterialName + "' does not exist in inventory.");
            }
        }

        return errors;
    }
} 