package utils;

import dal.SupplierDAO;
import entity.Supplier;
import java.util.HashMap;
import java.util.Map;

public class SupplierValidator {

    public static Map<String, String> validateSupplier(Supplier supplier, SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate supplierCode
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        } else if (supplierDAO.isSupplierCodeExists(supplier.getSupplierCode())) {
            errors.put("supplierCode", "Supplier code already exists, please enter a different one!");
        }

        // Validate supplierName
        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            errors.put("supplierName", "Supplier name cannot be empty.");
        } else if (supplier.getSupplierName().trim().length() > 100) {
            errors.put("supplierName", "Supplier name cannot exceed 100 characters.");
        } else if (!supplier.getSupplierName().trim().matches(".*\\S.*")) {
            errors.put("supplierName", "Supplier name cannot be just spaces.");
        }

        // Validate contactInfo
        if (supplier.getContactInfo() == null || supplier.getContactInfo().trim().isEmpty()) {
            errors.put("contactInfo", "Contact Info is required.");
        } else if (supplier.getContactInfo().trim().length() > 100) {
            errors.put("contactInfo", "Contact Info cannot exceed 100 characters.");
        }

        // Validate address
        if (supplier.getAddress() == null || supplier.getAddress().trim().isEmpty()) {
            errors.put("address", "Address is required.");
        } else if (supplier.getAddress().trim().length() > 200) {
            errors.put("address", "Address cannot exceed 200 characters.");
        }

        // Validate phoneNumber
        if (supplier.getPhoneNumber() == null || supplier.getPhoneNumber().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone Number is required.");
        } else if (!supplier.getPhoneNumber().trim().matches("[0-9\\-\\s]{10,15}")) {
            errors.put("phoneNumber", "Please enter a valid phone number (10-15 digits, numbers, dash or space allowed).");
        }

        // Validate email
        if (supplier.getEmail() == null || supplier.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!supplier.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.put("email", "Please enter a valid email address.");
        }

        // Validate taxId
        if (supplier.getTaxId() == null || supplier.getTaxId().trim().isEmpty()) {
            errors.put("taxId", "Tax ID is required.");
        } else if (!supplier.getTaxId().trim().matches("[A-Za-z0-9]{1,20}")) {
            errors.put("taxId", "Please enter a valid Tax ID (letters and numbers only, max 20 characters).");
        }

        // Validate description (optional field)
        if (supplier.getDescription() != null && supplier.getDescription().trim().length() > 200) {
            errors.put("description", "Description cannot exceed 200 characters.");
        }

        return errors;
    }

    public static Map<String, String> validateSupplierUpdate(Supplier supplier, SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();

        // Validate supplierId
        if (supplier.getSupplierId() <= 0) {
            errors.put("supplierId", "Invalid Supplier ID.");
        }

        // Validate supplierCode
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        }

        // Validate supplierName
        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            errors.put("supplierName", "Supplier name cannot be empty.");
        } else if (supplier.getSupplierName().trim().length() > 100) {
            errors.put("supplierName", "Supplier name cannot exceed 100 characters.");
        } else if (!supplier.getSupplierName().trim().matches(".*\\S.*")) {
            errors.put("supplierName", "Supplier name cannot be just spaces.");
        }

        // Validate contactInfo
        if (supplier.getContactInfo() == null || supplier.getContactInfo().trim().isEmpty()) {
            errors.put("contactInfo", "Contact Info is required.");
        } else if (supplier.getContactInfo().trim().length() > 100) {
            errors.put("contactInfo", "Contact Info cannot exceed 100 characters.");
        }

        // Validate address
        if (supplier.getAddress() == null || supplier.getAddress().trim().isEmpty()) {
            errors.put("address", "Address is required.");
        } else if (supplier.getAddress().trim().length() > 200) {
            errors.put("address", "Address cannot exceed 200 characters.");
        }

        // Validate phoneNumber
        if (supplier.getPhoneNumber() == null || supplier.getPhoneNumber().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone Number is required.");
        } else if (!supplier.getPhoneNumber().trim().matches("[0-9\\-\\s]{10,15}")) {
            errors.put("phoneNumber", "Please enter a valid phone number (10-15 digits, numbers, dash or space allowed).");
        }

        // Validate email
        if (supplier.getEmail() == null || supplier.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!supplier.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.put("email", "Please enter a valid email address.");
        }

        // Validate taxId
        if (supplier.getTaxId() == null || supplier.getTaxId().trim().isEmpty()) {
            errors.put("taxId", "Tax ID is required.");
        } else if (!supplier.getTaxId().trim().matches("[A-Za-z0-9]{1,20}")) {
            errors.put("taxId", "Please enter a valid Tax ID (letters and numbers only, max 20 characters).");
        }

        // Validate description (optional field)
        if (supplier.getDescription() != null && supplier.getDescription().trim().length() > 200) {
            errors.put("description", "Description cannot exceed 200 characters.");
        }

        return errors;
    }

    public static Map<String, String> validateSupplierFormData(String supplierCode, String supplierName, String contactInfo, String address, String phoneNumber, String email, String taxId, String description) {
        Map<String, String> errors = new HashMap<>();

        // Validate supplierCode
        if (supplierCode == null || supplierCode.trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        }

        // Validate supplierName
        if (supplierName == null || supplierName.trim().isEmpty()) {
            errors.put("supplierName", "Supplier name cannot be empty.");
        } else if (supplierName.trim().length() > 100) {
            errors.put("supplierName", "Supplier name cannot exceed 100 characters.");
        } else if (!supplierName.trim().matches(".*\\S.*")) {
            errors.put("supplierName", "Supplier name cannot be just spaces.");
        }

        // Validate contactInfo
        if (contactInfo == null || contactInfo.trim().isEmpty()) {
            errors.put("contactInfo", "Contact Info is required.");
        } else if (contactInfo.trim().length() > 100) {
            errors.put("contactInfo", "Contact Info cannot exceed 100 characters.");
        }

        // Validate address
        if (address == null || address.trim().isEmpty()) {
            errors.put("address", "Address is required.");
        } else if (address.trim().length() > 200) {
            errors.put("address", "Address cannot exceed 200 characters.");
        }

        // Validate phoneNumber
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errors.put("phoneNumber", "Phone Number is required.");
        } else if (!phoneNumber.trim().matches("[0-9\\-\\s]{10,15}")) {
            errors.put("phoneNumber", "Please enter a valid phone number (10-15 digits, numbers, dash or space allowed).");
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.put("email", "Please enter a valid email address.");
        }

        // Validate taxId
        if (taxId == null || taxId.trim().isEmpty()) {
            errors.put("taxId", "Tax ID is required.");
        } else if (!taxId.trim().matches("[A-Za-z0-9]{1,20}")) {
            errors.put("taxId", "Please enter a valid Tax ID (letters and numbers only, max 20 characters).");
        }

        // Validate description 
        if (description != null && description.trim().length() > 200) {
            errors.put("description", "Description cannot exceed 200 characters.");
        }

        return errors;
    }
} 