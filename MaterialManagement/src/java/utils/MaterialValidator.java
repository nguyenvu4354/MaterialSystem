package utils;

import dal.MaterialDAO;
import entity.Material;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        } else if (!material.getMaterialName().matches("^[a-zA-Z0-9\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("materialName", "Material name cannot contain special characters.");
        }

        // Validate materialStatus
        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

//        // Validate price
//        if (material.getPrice() <= 0) {
//            errors.put("price", "Price must be greater than 0.");
//        }

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
        } else if (!material.getMaterialName().matches("^[a-zA-Z0-9\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("materialName", "Material name cannot contain special characters.");
        }

        // Validate materialStatus
        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        // Thêm validate cho status
        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("status", "Material status cannot be empty.");
        } else if (!List.of("new", "used", "damaged").contains(material.getMaterialStatus())) {
            errors.put("status", "Invalid material status.");
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
                                                             String materialStatus, String categoryIdStr, 
                                                             String unitIdStr) {
        Map<String, String> errors = new HashMap<>();

        // Validate materialCode
        if (materialCode == null || materialCode.trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        }

        // Validate materialName
        if (materialName == null || materialName.trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        } else if (!materialName.matches("^[a-zA-Z0-9\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
            errors.put("materialName", "Material name cannot contain special characters.");
        }

        // Validate materialStatus
        if (materialStatus == null || materialStatus.trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        } else if (!List.of("new", "used", "damaged").contains(materialStatus)) {
            errors.put("materialStatus", "Invalid material status.");
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