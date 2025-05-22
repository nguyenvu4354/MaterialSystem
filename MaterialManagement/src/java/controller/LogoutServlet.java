package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hủy session hiện tại
        HttpSession session = request.getSession(false); // false để tránh tạo session mới nếu không có
        if (session != null) {
            session.invalidate();
        }

        // Chuyển hướng về trang đăng nhập hoặc trang chính
        response.sendRedirect("Login.jsp");
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý đăng xuất người dùng";
    }
}
