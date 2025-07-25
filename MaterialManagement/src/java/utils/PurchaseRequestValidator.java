package utils;

import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseRequestValidator {

    public static Map<String, String> validatePurchaseRequest(PurchaseRequest purchaseRequest) {
        Map<String, String> errors = new HashMap<>();

        // Validate reason
        if (purchaseRequest.getReason() == null || purchaseRequest.getReason().trim().isEmpty()) {
            errors.put("reason", "Reason for request cannot be empty.");
        }

        // Validate userId
        if (purchaseRequest.getUserId() <= 0) {
            errors.put("userId", "Invalid user ID.");
        }

        // Validate details
        if (purchaseRequest.getDetails() == null || purchaseRequest.getDetails().isEmpty()) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    // validatePurchaseRequestFormData chỉ nhận reason, không còn estimatedPriceStr
    public static Map<String, String> validatePurchaseRequestFormData(String reason) {
        Map<String, String> errors = new HashMap<>();
        // Validate reason
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason for request cannot be empty.");
        }
        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetails(String[] materialNames, String[] quantities) {
        Map<String, String> errors = new HashMap<>();

        if (materialNames == null || quantities == null) {
            errors.put("details", "Detail information is invalid.");
            return errors;
        }

        boolean hasValidDetail = false;

        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            String quantityStr = quantities[i];

            // Skip if material name is empty
            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            }

            // Validate quantity
            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                errors.put("quantity_" + i, "Quantity cannot be empty for material " + materialName + ".");
            } else {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        errors.put("quantity_" + i, "Quantity must be greater than 0 for material " + materialName + ".");
                    }
                } catch (NumberFormatException e) {
                    errors.put("quantity_" + i, "Invalid quantity format for material " + materialName + ".");
                }
            }

            hasValidDetail = true;
        }

        if (!hasValidDetail) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetail(PurchaseRequestDetail detail) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialId
        if (detail.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid material ID.");
        }

        // Validate quantity
        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetailFormData(String materialIdStr, String quantityStr) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialId
        if (materialIdStr == null || materialIdStr.trim().isEmpty()) {
             errors.put("materialId", "Material must be selected.");
        } else {
            try {
                int materialId = Integer.parseInt(materialIdStr);
                if (materialId <= 0) {
                     errors.put("materialId", "Invalid material.");
                }
            } catch (NumberFormatException e) {
                errors.put("materialId", "Invalid material ID format.");
            }
        }

        // Validate quantity
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            errors.put("quantity", "Quantity cannot be empty.");
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    errors.put("quantity", "Quantity must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                errors.put("quantity", "Invalid quantity.");
            }
        }

        return errors;
    }
}
