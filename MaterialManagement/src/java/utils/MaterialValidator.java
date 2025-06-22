package utils;

import dal.MaterialDAO;
import entity.Material;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MaterialValidator {

    public static Map<String, String> validateMaterial(Material material, MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialCode
        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        } else if (materialDAO.isMaterialCodeExists(material.getMaterialCode())) {
            errors.put("materialCode", "Material code already exists, please enter a different one!");
        }

        // Validate materialName
        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        // Validate materialStatus
        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        // Validate price
        if (material.getPrice() <= 0) {
            errors.put("price", "Price must be greater than 0.");
        }

        // Validate conditionPercentage
        if (material.getConditionPercentage() < 0 || material.getConditionPercentage() > 100) {
            errors.put("conditionPercentage", "Condition percentage must be between 0 and 100.");
        }

        // Validate category
        if (material.getCategory() == null || material.getCategory().getCategory_id() == 0) {
            errors.put("categoryId", "Category cannot be empty.");
        }

        // Validate unit
        if (material.getUnit() == null || material.getUnit().getId() == 0) {
            errors.put("unitId", "Unit cannot be empty.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialUpdate(Material material, MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialId
        if (material.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid Material ID.");
        }

        // Validate materialCode
        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        }

        // Validate materialName
        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        // Validate materialStatus
        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        // Validate price
        if (material.getPrice() <= 0) {
            errors.put("price", "Price must be greater than 0.");
        }

        // Validate conditionPercentage
        if (material.getConditionPercentage() < 0 || material.getConditionPercentage() > 100) {
            errors.put("conditionPercentage", "Condition percentage must be between 0 and 100.");
        }

        // Validate category
        if (material.getCategory() == null || material.getCategory().getCategory_id() == 0) {
            errors.put("categoryId", "Category cannot be empty.");
        }

        // Validate unit
        if (material.getUnit() == null || material.getUnit().getId() == 0) {
            errors.put("unitId", "Unit cannot be empty.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialFormData(String materialCode, String materialName, 
                                                             String materialStatus, String priceStr, 
                                                             String conditionPercentageStr, String categoryIdStr, 
                                                             String unitIdStr) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialCode
        if (materialCode == null || materialCode.trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        }

        // Validate materialName
        if (materialName == null || materialName.trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        // Validate materialStatus
        if (materialStatus == null || materialStatus.trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        // Validate price
        if (priceStr == null || priceStr.trim().isEmpty()) {
            errors.put("price", "Price cannot be empty.");
        } else {
            try {
                BigDecimal price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.put("price", "Price must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                errors.put("price", "Invalid price format.");
            }
        }

        // Validate conditionPercentage
        if (conditionPercentageStr == null || conditionPercentageStr.trim().isEmpty()) {
            errors.put("conditionPercentage", "Condition percentage cannot be empty.");
        } else {
            try {
                int conditionPercentage = Integer.parseInt(conditionPercentageStr);
                if (conditionPercentage < 0 || conditionPercentage > 100) {
                    errors.put("conditionPercentage", "Condition percentage must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                errors.put("conditionPercentage", "Invalid condition percentage format.");
            }
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
                errors.put("categoryId", "Invalid category format.");
            }
        }

        // Validate unitId
        if (unitIdStr == null || unitIdStr.trim().isEmpty()) {
            errors.put("unitId", "Unit cannot be empty.");
        } else {
            try {
                int unitId = Integer.parseInt(unitIdStr);
                if (unitId <= 0) {
                    errors.put("unitId", "Invalid unit.");
                }
            } catch (NumberFormatException e) {
                errors.put("unitId", "Invalid unit format.");
            }
        }

        return errors;
    }
} 