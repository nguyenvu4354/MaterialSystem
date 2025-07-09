package utils;

import dal.UnitDAO;
import entity.Unit;
import java.util.HashMap;
import java.util.Map;

public class UnitValidator {

    public static Map<String, String> validateUnit(Unit unit, UnitDAO unitDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate unitName
        if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unit.getUnitName().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        // Validate symbol
        if (unit.getSymbol() == null || unit.getSymbol().trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!unit.getSymbol().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        // Validate description (optional field)
        if (unit.getDescription() != null && !unit.getDescription().trim().isEmpty()) {
            if (!unit.getDescription().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitUpdate(Unit unit, UnitDAO unitDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate unitId
        if (unit.getId() <= 0) {
            errors.put("unitId", "Invalid Unit ID.");
        }

        // Validate unitName
        if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unit.getUnitName().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        // Validate symbol
        if (unit.getSymbol() == null || unit.getSymbol().trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!unit.getSymbol().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        // Validate description (optional field)
        if (unit.getDescription() != null && !unit.getDescription().trim().isEmpty()) {
            if (!unit.getDescription().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitFormData(String unitName, String symbol, String description) {
        Map<String, String> errors = new HashMap<>();

        // Validate unitName
        if (unitName == null || unitName.trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unitName.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        // Validate symbol
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!symbol.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        // Validate description (optional field)
        if (description != null && !description.trim().isEmpty()) {
            if (!description.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitId(String unitIdStr) {
        Map<String, String> errors = new HashMap<>();

        if (unitIdStr == null || unitIdStr.trim().isEmpty()) {
            errors.put("unitId", "Unit ID cannot be empty.");
        } else {
            try {
                int unitId = Integer.parseInt(unitIdStr);
                if (unitId <= 0) {
                    errors.put("unitId", "Invalid unit ID.");
                }
            } catch (NumberFormatException e) {
                errors.put("unitId", "Invalid unit ID format.");
            }
        }

        return errors;
    }
} 