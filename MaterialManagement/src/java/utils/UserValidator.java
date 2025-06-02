package utils;

import dal.UserDAO;
import entity.User;

import java.util.HashMap;
import java.util.Map;

public class UserValidator {

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
