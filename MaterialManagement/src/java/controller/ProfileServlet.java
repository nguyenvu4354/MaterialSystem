package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import utils.UserValidator;

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

        // Lấy thông báo nếu có từ session sau redirect
        String message = (String) session.getAttribute("message");
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);

        RequestDispatcher dispatcher = request.getRequestDispatcher("Profile.jsp");
        dispatcher.forward(request, response);
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

            // Cập nhật thông tin user
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                user.setDateOfBirth(LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
                user.setDateOfBirth(null);
            }
            if (gender != null && !gender.isEmpty()) {
                user.setGender(User.Gender.valueOf(gender.toLowerCase()));
            } else {
                user.setGender(null);
            }
            user.setDescription(description);

            Map<String, String> errors = new HashMap<>();
            if (userDAO.isEmailExist(email, user.getUserId())) {
                errors.put("email", "This email is already in use.");
            }

            errors.putAll(UserValidator.validateProfile(user, userDAO));

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("user", user);
                RequestDispatcher dispatcher = request.getRequestDispatcher("Profile.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Xử lý ảnh đại diện nếu có
            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

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

            // Lưu thông tin cập nhật
            boolean updated = userDAO.updateUser(user);
            if (updated) {
                user = userDAO.getUserById(user.getUserId());
                session.setAttribute("user", user);
                session.setAttribute("message", "Profile updated successfully!");
                response.sendRedirect("profile");
                return;
            } else {
                request.setAttribute("error", "Failed to update profile. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while updating the profile: " + e.getMessage());
        }

        request.setAttribute("user", user);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Profile.jsp");
        dispatcher.forward(request, response);
    }
}
