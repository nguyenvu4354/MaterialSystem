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

        // Validate estimatedPrice
        if (purchaseRequest.getEstimatedPrice() < 0) {
            errors.put("estimatedPrice", "Estimated price must be greater than or equal to 0.");
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

    public static Map<String, String> validatePurchaseRequestFormData(String reason, String estimatedPriceStr) {
        Map<String, String> errors = new HashMap<>();

        // Validate reason
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason for request cannot be empty.");
        }

        // Validate estimatedPrice
        if (estimatedPriceStr == null || estimatedPriceStr.trim().isEmpty()) {
            errors.put("estimatedPrice", "Estimated price cannot be empty.");
        } else {
            try {
                double estimatedPrice = Double.parseDouble(estimatedPriceStr);
                if (estimatedPrice < 0) {
                    errors.put("estimatedPrice", "Estimated price must be greater than or equal to 0.");
                }
            } catch (NumberFormatException e) {
                errors.put("estimatedPrice", "Invalid estimated price.");
            }
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetails(String[] materialNames, String[] categoryIds, String[] quantities) {
        Map<String, String> errors = new HashMap<>();

        if (materialNames == null || categoryIds == null || quantities == null) {
            errors.put("details", "Detail information is invalid.");
            return errors;
        }

        boolean hasValidDetail = false;

        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            String categoryIdStr = categoryIds[i];
            String quantityStr = quantities[i];

            // Skip if material name is empty
            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            } else if (!materialName.matches("^[a-zA-Z0-9\sÀ-ỹà-ỹ.,-]+$")) {
                errors.put("materialName", "Material name cannot contain special characters.");
                return errors;
            }

            // Validate categoryId
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                if (categoryId <= 0) {
                    errors.put("categoryId", "Invalid category.");
                    return errors;
                }
            } catch (NumberFormatException e) {
                errors.put("categoryId", "Invalid category.");
                return errors;
            }

            // Validate quantity
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    errors.put("quantity", "Quantity must be greater than 0.");
                    return errors;
                }
            } catch (NumberFormatException e) {
                errors.put("quantity", "Invalid quantity.");
                return errors;
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

        // Validate materialName
        if (detail.getMaterialName() == null || detail.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        } else if (!detail.getMaterialName().matches("^[a-zA-Z0-9\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("materialName", "Material name cannot contain special characters.");
        }

        // Validate categoryId
        if (detail.getCategoryId() <= 0) {
            errors.put("categoryId", "Invalid category.");
        }

        // Validate quantity
        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetailFormData(String materialName, String categoryIdStr, String quantityStr) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialName
        if (materialName == null || materialName.trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        } else if (!materialName.matches("^[a-zA-Z0-9\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("materialName", "Material name cannot contain special characters.");
        }

        // Validate categoryId
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            errors.put("categoryId", "Category cannot be empty.");
        } else {
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                if (categoryId <= 0) {
                    errors.put("categoryId", "Invalid category.");
                }
            } catch (NumberFormatException e) {
                errors.put("categoryId", "Invalid category.");
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