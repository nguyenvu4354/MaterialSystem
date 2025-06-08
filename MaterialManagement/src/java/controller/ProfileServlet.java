package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

            // Update user object
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

            // Handle image upload
            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("/") + "images" + File.separator + "profiles" + File.separator;
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String filePath = uploadPath + fileName;
                filePart.write(filePath);
                user.setUserPicture(fileName);
            }

            // Update user in database
            boolean updated = userDAO.updateUser(user);
            if (updated) {
                user = userDAO.getUserById(user.getUserId());
                session.setAttribute("user", user);
                request.setAttribute("message", "✔️ Cập nhật hồ sơ thành công!");
            } else {
                request.setAttribute("error", "❌ Không thể cập nhật hồ sơ. Vui lòng thử lại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "❌ Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        request.setAttribute("user", user);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Profile.jsp");
        dispatcher.forward(request, response);
    }
}