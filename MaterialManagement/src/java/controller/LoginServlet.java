package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form login
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Gọi DAO để kiểm tra đăng nhập
        UserDAO userDAO = new UserDAO(); // UserDAO kế thừa DBConnect
        User user = userDAO.login(username, password);

        if (user != null) {
            // Đăng nhập thành công
            HttpSession session = request.getSession();
            session.setAttribute("user", user); // Lưu thông tin user vào session
            response.sendRedirect("HomePage.jsp");  // Chuyển hướng tới trang chính
        } else {
            // Đăng nhập thất bại
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Điều hướng người dùng về trang login nếu truy cập bằng GET
        response.sendRedirect("Login.jsp");
    }

    @Override
    public String getServletInfo() {
        return "Login Servlet xử lý đăng nhập người dùng";
    }
}
