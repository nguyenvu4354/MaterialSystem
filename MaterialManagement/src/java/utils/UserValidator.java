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
    private static final int USERNAME_MAX_LENGTH = 10;
    private static final int FULLNAME_MAX_LENGTH = 25;
    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int PHONE_MAX_LENGTH = 15;
    private static final int ADDRESS_MAX_LENGTH = 25;
    private static final int DESCRIPTION_MAX_LENGTH = 25;

    public static Map<String, String> validateForCreateUser(User user, UserDAO userDAO, String fullName, String address, String description,
                                                           String dateOfBirth, String gender, String departmentId, String userPicture) {
        Map<String, String> errors = new HashMap<>();

        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            if (user.getUsername().trim().isEmpty()) {
                errors.put("username", "Username is required.");
            } else if (user.getUsername().length() > USERNAME_MAX_LENGTH) {
                errors.put("username", "Username must not exceed " + USERNAME_MAX_LENGTH + " characters.");
            } else if (!user.getUsername().matches("^[a-z0-9_.-]+$")) {
                errors.put("username", "Username must not contain uppercase letters.");
            } else if (userDAO.isUsernameExist(user.getUsername())) {
                errors.put("username", "Username already exists.");
            }
        }

        if (user.getPassword() != null && user.getPassword().trim().isEmpty()) {
            errors.put("password", "Password is required.");
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

        if (user.getRoleId() != 0 && user.getRoleId() < 0) {
            errors.put("roleId", "Role is required.");
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        } else if (fullName.matches(".*\\d.*")) {
            errors.put("fullName", "Full name must not contain numbers.");
        }

        if (address == null || address.trim().isEmpty()) {
            errors.put("address", "Address is required.");
        } else if (address.length() > ADDRESS_MAX_LENGTH) {
            errors.put("address", "Address must not exceed " + ADDRESS_MAX_LENGTH + " characters.");
        }

        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            errors.put("dateOfBirth", "Date of birth is required.");
        } else {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirth);
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                }
            } catch (Exception e) {
                errors.put("dateOfBirth", "Invalid date of birth format.");
            }
        }

        if (gender == null || gender.trim().isEmpty()) {
            errors.put("gender", "Gender is required.");
        } else if (!"male".equalsIgnoreCase(gender) && !"female".equalsIgnoreCase(gender) && !"other".equalsIgnoreCase(gender)) {
            errors.put("gender", "Invalid gender.");
        }

        if (departmentId != null && departmentId.trim().isEmpty()) {
            errors.put("departmentId", "Department is required.");
        }

        if (userPicture != null && userPicture.trim().isEmpty()) {
            errors.put("userPicture", "User picture is required.");
        }

        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            errors.put("description", "Description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters.");
        }

        return errors;
    }

    public static Map<String, String> validateForProfile(User user, String fullName, String address, String description,
                                                        String dateOfBirth, String gender) {
        Map<String, String> errors = new HashMap<>();

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

        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Full name is required.");
        } else if (fullName.length() > FULLNAME_MAX_LENGTH) {
            errors.put("fullName", "Full name must not exceed " + FULLNAME_MAX_LENGTH + " characters.");
        } else if (fullName.matches(".*\\d.*")) {
            errors.put("fullName", "Full name must not contain numbers.");
        }

        if (address == null || address.trim().isEmpty()) {
            errors.put("address", "Address is required.");
        } else if (address.length() > ADDRESS_MAX_LENGTH) {
            errors.put("address", "Address must not exceed " + ADDRESS_MAX_LENGTH + " characters.");
        }

        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            errors.put("dateOfBirth", "Date of birth is required.");
        } else {
            try {
                LocalDate dob = LocalDate.parse(dateOfBirth);
                if (dob.isAfter(LocalDate.now())) {
                    errors.put("dateOfBirth", "Date of birth cannot be in the future.");
                }
            } catch (Exception e) {
                errors.put("dateOfBirth", "Invalid date of birth format.");
            }
        }

        if (gender == null || gender.trim().isEmpty()) {
            errors.put("gender", "Gender is required.");
        } else if (!"male".equalsIgnoreCase(gender) && !"female".equalsIgnoreCase(gender) && !"other".equalsIgnoreCase(gender)) {
            errors.put("gender", "Invalid gender.");
        }

        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            errors.put("description", "Description must not exceed " + DESCRIPTION_MAX_LENGTH + " characters.");
        }

        return errors;
    }
}