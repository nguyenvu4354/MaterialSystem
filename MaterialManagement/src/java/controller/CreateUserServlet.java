package controller;

import dal.DepartmentDAO;
import dal.UserDAO;
import entity.Department;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import utils.UserValidator;
import utils.EmailUtils;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CreateUserServlet", value = "/CreateUser")
@MultipartConfig
public class CreateUserServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);

        request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            String gender = request.getParameter("gender");
            String description = request.getParameter("description");
            String roleIdStr = request.getParameter("roleId");
            String departmentIdStr = request.getParameter("departmentId"); 
            int roleId = 0;
            int departmentId = 0;

            try {
                roleId = Integer.parseInt(roleIdStr);
            } catch (NumberFormatException ignored) {
            }
            try {
                departmentId = Integer.parseInt(departmentIdStr);
            } catch (NumberFormatException ignored) {
            }

            User newUser = new User();
            newUser.setUsername(username);
            String hashedPassword = hashPasswordWithMD5(password);
            newUser.setPassword(hashedPassword);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setAddress(address);
            newUser.setRoleId(roleId);
            newUser.setDepartmentId(departmentId); // Set departmentId
            newUser.setDescription(description);

            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                newUser.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            }

            if (gender != null && !gender.isEmpty()) {
                if ("male".equalsIgnoreCase(gender) || "female".equalsIgnoreCase(gender) || "other".equalsIgnoreCase(gender)) {
                    newUser.setGender(User.Gender.valueOf(gender.toLowerCase()));
                }
            }

            Map<String, String> errors = UserValidator.validate(newUser, userDAO);

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("enteredUser", newUser);
                request.setAttribute("enteredDateOfBirth", dateOfBirthStr);
                request.setAttribute("enteredGender", gender);
                request.setAttribute("enteredRoleId", roleIdStr);
                request.setAttribute("enteredDepartmentId", departmentIdStr);
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + "images/profiles/";
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                filePart.write(uploadPath + fileName);
                newUser.setUserPicture(fileName);
            }

            boolean created = userDAO.createUser(newUser);
            if (created) {
                try {
                    String subject = "Your account has been created!";
                    String content = "Hello " + newUser.getFullName() + ",\n\n"
                            + "Your account has been successfully created.\n\n"
                            + "Username: " + newUser.getUsername() + "\n"
                            + "Password: " + password + "\n\n"
                            + "Please log in and change your password after your first login to ensure security.\n\n"
                            + "Best regards,\nSupport Team.";
                    EmailUtils.sendEmail(newUser.getEmail(), subject, content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                session.setAttribute("successMessage", "User created successfully!");
                response.sendRedirect(request.getContextPath() + "/UserList");
            } else {
                request.setAttribute("error", "Failed to create user. Please try again.");
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error occurred: " + e.getMessage());
            List<Department> departments = departmentDAO.getAllDepartments();
            request.setAttribute("departments", departments);
            request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
        }
    }

    private String hashPasswordWithMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}