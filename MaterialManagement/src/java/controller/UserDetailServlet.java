package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "UserDetailServlet", urlPatterns = {"/UserDetail"})
public class UserDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null) {
            response.sendRedirect("UserList");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);

            if (user != null) {
                request.setAttribute("user", user);
                request.getRequestDispatcher("UserDetail.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Không tìm thấy người dùng.");
                request.getRequestDispatcher("UserList.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi lấy thông tin chi tiết: " + e.getMessage());
            e.printStackTrace();
            request.getRequestDispatcher("UserList.jsp").forward(request, response);
        }
    }
}
