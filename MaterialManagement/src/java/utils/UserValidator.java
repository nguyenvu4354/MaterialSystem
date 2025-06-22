package utils;

import dal.UserDAO;
import entity.User;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,15}$"; // Allow 1-15 digits, optional leading '+'

    public static Map<String, String> validateProfile(User user, UserDAO userDAO) {
        Map<String, String> errors = new HashMap<>();

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (user.getFullName().length() > 100) {
            errors.put("fullName", "Full name must not exceed 100 characters.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > 100) {
            errors.put("email", "Email must not exceed 100 characters.");
        } 

        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required.");
        } else if (!Pattern.matches(PHONE_REGEX, user.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number must be 1-15 digits, optionally starting with '+'.");
        } else if (user.getPhoneNumber().length() > 15) {
            errors.put("phoneNumber", "Phone number must not exceed 15 characters.");
        }

        if (user.getAddress() != null && user.getAddress().length() > 255) {
            errors.put("address", "Address must not exceed 255 characters.");
        }

        if (user.getDateOfBirth() != null) {
            if (user.getDateOfBirth().isAfter(LocalDate.now())) {
                errors.put("dateOfBirth", "Date of birth cannot be in the future.");
            }
        }

        if (user.getGender() != null) {
            try {
                User.Gender.valueOf(user.getGender().name().toLowerCase());
            } catch (IllegalArgumentException e) {
                errors.put("gender", "Invalid gender selected.");
            }
        }

        if (user.getDescription() != null && user.getDescription().length() > 1000) {
            errors.put("description", "Description must not exceed 1000 characters.");
        }

        return errors;
    }

    public static Map<String, String> validate(User user, UserDAO userDAO) {
        Map<String, String> errors = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.put("username", "Username is required");
        } else if (userDAO.isUsernameExist(user.getUsername())) {
            errors.put("username", "Username already exists");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.put("password", "Password is required");
        }

        if (user.getEmail() != null && !user.getEmail().matches("^\\S+@\\S+\\.\\S+$")) {
            errors.put("email", "Invalid email format");
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().matches("^\\d{8,15}$")) {
            errors.put("phoneNumber", "Invalid phone number");
        }

        if (user.getRoleId() == 0) {
            errors.put("roleId", "Role is required");
        }

        return errors;
    }
}