package controller;

import dal.DepartmentDAO;
import dal.UserDAO;
import entity.Department;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import utils.UserValidator;
import utils.EmailUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            Integer departmentId = null;

            try {
                roleId = Integer.parseInt(roleIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Role ID không hợp lệ.");
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                return;
            }
            try {
                if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                    departmentId = Integer.parseInt(departmentIdStr);
                }
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
            newUser.setDepartmentId(departmentId);
            newUser.setDescription(description);
            newUser.setVerificationToken(UUID.randomUUID().toString());
            newUser.setVerificationStatus("pending");
            newUser.setVerificationExpiry(LocalDateTime.now().plusHours(24));
            newUser.setStatus(User.Status.inactive); 

            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                try {
                    newUser.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
                } catch (Exception e) {
                    request.setAttribute("error", "Ngày sinh không hợp lệ.");
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                    return;
                }
            }

            if (gender != null && !gender.isEmpty()) {
                try {
                    if ("male".equalsIgnoreCase(gender) || "female".equalsIgnoreCase(gender) || "other".equalsIgnoreCase(gender)) {
                        newUser.setGender(User.Gender.valueOf(gender.toLowerCase()));
                    } else {
                        throw new IllegalArgumentException("Giới tính không hợp lệ.");
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Giới tính không hợp lệ.");
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                    return;
                }
            }

            Map<String, String> errors = new HashMap<>();
            
            if (userDAO.isEmailExist(email, 0)) {
                errors.put("email", "Email này đã được sử dụng.");
            }

            // Validate dữ liệu người dùng
            errors.putAll(UserValidator.validate(newUser, userDAO));

            // Xử lý file ảnh
            Part filePart = request.getPart("userPicture");
            if (filePart != null && filePart.getSize() > 0) {
                if (filePart.getSize() > 2 * 1024 * 1024) { // Giới hạn 2MB
                    errors.put("userPicture", "Kích thước file ảnh không được vượt quá 2MB.");
                } else {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                    String buildPath = getServletContext().getRealPath("/");
                    Path projectRoot = Paths.get(buildPath).getParent().getParent();
                    Path uploadDir = projectRoot.resolve("web").resolve("images").resolve("profiles");

                    try {
                        if (!Files.exists(uploadDir)) {
                            Files.createDirectories(uploadDir);
                        }
                        Path savePath = uploadDir.resolve(fileName);
                        filePart.write(savePath.toString());
                        newUser.setUserPicture(fileName);
                    } catch (IOException e) {
                        errors.put("userPicture", "Lỗi khi lưu file ảnh: " + e.getMessage());
                    }
                }
            }

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

            boolean created = userDAO.createUser(newUser);
            if (created) {
                try {
                    String verificationLink = "http://localhost:8080/MaterialManagement/VerifyUser?token=" + newUser.getVerificationToken();
                    String subject = "Xác thực tài khoản của bạn";
                    String content = "<html><body>" +
                                    "<p>Hello " + newUser.getFullName() + ",</p>" +
                                    "<p>Your account has been successfully created. Please verify your email by clicking the link below:</p>" +
                                    "<p><a href=\"" + verificationLink + "\">Verify Your Account</a></p>" +
                                    "<p>Your login credentials:</p>" +
                                    "<p><strong>Username:</strong> " + newUser.getUsername() + "</p>" +
                                    "<p><strong>Password:</strong> " + password + "</p>" +
                                    "<p>This link will expire in 24 hours.</p>" +
                                    "<p>Best regards,<br>The Support Team</p>" +
                                    "</body></html>";

                    EmailUtils.sendEmail(newUser.getEmail(), subject, content);
                    session.setAttribute("successMessage", "Tạo tài khoản thành công! Email xác thực đã được gửi.");
                    response.sendRedirect(request.getContextPath() + "/UserList");
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi gửi email xác thực: " + e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("error", "Tạo tài khoản thành công, nhưng không thể gửi email xác thực: " + e.getMessage());
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Không thể tạo tài khoản. Vui lòng thử lại.");
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/CreateUser.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi hệ thống khi tạo tài khoản: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
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
            throw new RuntimeException("Lỗi khi mã hóa mật khẩu", e);
        }
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}