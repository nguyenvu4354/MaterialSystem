package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import utils.UserValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
@MultipartConfig
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String message = (String) session.getAttribute("message");
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        request.getRequestDispatcher("Profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            request.setCharacterEncoding("UTF-8");

            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            String gender = request.getParameter("gender");
            String description = request.getParameter("description");

            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            user.setDescription(description);

            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                try {
                    user.setDateOfBirth(LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (Exception e) {
                    user.setDateOfBirth(null);
                }
            } else {
                user.setDateOfBirth(null);
            }

            if (gender != null && !gender.isEmpty()) {
                try {
                    user.setGender(User.Gender.valueOf(gender.toLowerCase()));
                } catch (IllegalArgumentException e) {
                    user.setGender(null);
                }
            } else {
                user.setGender(null);
            }

            Map<String, String> errors = new HashMap<>();
            if (userDAO.isEmailExist(email, user.getUserId())) {
                errors.put("email", "This email is already in use.");
            }

            String userPicture = user.getUserPicture();
            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                if (filePart.getSize() > 2 * 1024 * 1024) {
                    errors.put("userPicture", "Image file size must not exceed 2MB.");
                } else {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    userPicture = fileName;

                    String buildPath = getServletContext().getRealPath("/");
                    Path projectRoot = Paths.get(buildPath).getParent().getParent();
                    Path uploadDir = projectRoot.resolve("web").resolve("images").resolve("profiles");

                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    Path savePath = uploadDir.resolve(fileName);
                    filePart.write(savePath.toString());
                    user.setUserPicture(fileName);
                }
            }
            errors.putAll(UserValidator.validateForProfile(user, fullName, address, description, dateOfBirthStr, gender));
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("user", user);
                request.getRequestDispatcher("Profile.jsp").forward(request, response);
                return;
            }

            boolean updated = userDAO.updateUser(user);
            if (updated) {
                user = userDAO.getUserById(user.getUserId()); 
                session.setAttribute("user", user);
                session.setAttribute("message", "Profile updated successfully!");
                response.sendRedirect("profile");
            } else {
                request.setAttribute("error", "Failed to update profile. Please try again.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("Profile.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.setAttribute("user", user);
            request.getRequestDispatcher("Profile.jsp").forward(request, response);
        }
    }
}
