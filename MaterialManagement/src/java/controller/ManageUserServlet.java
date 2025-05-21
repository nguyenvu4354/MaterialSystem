package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;

@WebServlet(name = "ManageUserServlet", value = "/create-user")

@MultipartConfig
public class ManageUserServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
            return;
        }

        // Forward tới CreateUser.jsp nằm ngoài root folder, ví dụ trong WebContent/ hoặc webapp/
        RequestDispatcher dispatcher = request.getRequestDispatcher("/CreateUser.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User admin = (User) session.getAttribute("user");
        if (admin.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
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
            int roleId = Integer.parseInt(request.getParameter("roleId"));

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setAddress(address);
            newUser.setRoleId(roleId);
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                newUser.setDateOfBirth(LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ISO_LOCAL_DATE));
            }
            if (gender != null && !gender.isEmpty()) {
                newUser.setGender(User.Gender.valueOf(gender.toLowerCase()));
            }
            newUser.setDescription(description);

            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + "images/profiles/";
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String filePath = uploadPath + fileName;
                filePart.write(filePath);
                newUser.setUserPicture(fileName);
            }

            boolean created = userDAO.createUser(newUser);
            if (created) {
                request.setAttribute("message", "Tạo người dùng mới thành công!");
            } else {
                request.setAttribute("error", "Tạo người dùng mới thất bại. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo người dùng: " + e.getMessage());
        }

        // Vẫn chuyển về trang CreateUser.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/CreateUser.jsp");
        dispatcher.forward(request, response);
    }
}
