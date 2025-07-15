package utils;

import dal.UserDAO;
import entity.User;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,15}$";
    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 100;
    private static final int FULLNAME_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PHONE_MAX_LENGTH = 15;
    private static final int ADDRESS_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    public static Map<String, String> validateProfile(User user, UserDAO userDAO) {
        Map<String, String> errors = new HashMap<>();

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (user.getFullName().length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            errors.put("email", "Email must not exceed " + EMAIL_MAX_LENGTH + " characters.");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            errors.put("phoneNumber", "Phone number is required.");
        } else if (!Pattern.matches(PHONE_REGEX, user.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number must be 1-15 digits, optionally starting with '+'.");
        } else if (user.getPhoneNumber().length() > PHONE_MAX_LENGTH) {
            errors.put("phoneNumber", "Phone number must not exceed " + PHONE_MAX_LENGTH + " characters.");
        }

        if (user.getAddress() != null && user.getAddress().length() > ADDRESS_MAX_LENGTH) {
            errors.put("address", "Address must not exceed " + ADDRESS_MAX_LENGTH + " characters.");
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

        if (user.getDescription() != null && user.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            errors.put("description", "Description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters.");
        }

        return errors;
    }

    public static Map<String, String> validate(User user, UserDAO userDAO) {
        Map<String, String> errors = new HashMap<>();

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.put("username", "Username is required.");
        } else if (user.getUsername().length() > USERNAME_MAX_LENGTH) {
            errors.put("username", "Username must not exceed " + USERNAME_MAX_LENGTH + " characters.");
        } else if (userDAO.isUsernameExist(user.getUsername())) {
            errors.put("username", "Username already exists.");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.put("password", "Password is required.");
        } else if (user.getPassword().length() < PASSWORD_MIN_LENGTH) {
            errors.put("password", "Password must be at least " + PASSWORD_MIN_LENGTH + " characters long.");
        } else if (user.getPassword().length() > PASSWORD_MAX_LENGTH) {
            errors.put("password", "Password must not exceed " + PASSWORD_MAX_LENGTH + " characters.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is required.");
        } else if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            errors.put("email", "Invalid email format.");
        } else if (user.getEmail().length() > EMAIL_MAX_LENGTH) {
            errors.put("email", "Email must not exceed " + EMAIL_MAX_LENGTH + " characters.");
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            if (!Pattern.matches(PHONE_REGEX, user.getPhoneNumber())) {
                errors.put("phoneNumber", "Phone number must be 1-15 digits, optionally starting with '+'.");
            } else if (user.getPhoneNumber().length() > PHONE_MAX_LENGTH) {
                errors.put("phoneNumber", "Phone number must not exceed " + PHONE_MAX_LENGTH + " characters.");
            }
        }

        if (user.getRoleId() == 0) {
            errors.put("roleId", "Role is required.");
        }

        return errors;
    }
}