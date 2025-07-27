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
import java.io.PrintWriter;

@WebServlet(name="ChangePasswordServlet", urlPatterns={"/ChangePassword"})
public class ChangePasswordServlet extends HttpServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChangePasswordServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChangePasswordServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        request.getRequestDispatcher("ChangePassword.jsp").forward(request, response);
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
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String error = null;
        String message = null;
        UserDAO userDAO = new UserDAO();
        if (oldPassword == null || newPassword == null || confirmPassword == null
                || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            error = "All fields are required.";
        } else if (!userDAO.md5(oldPassword).equals(user.getPassword())) {
            error = "Current password is incorrect.";
        } else if (!newPassword.equals(confirmPassword)) {
            error = "New password and confirmation do not match.";
        } else if (newPassword.length() < 6) {
            error = "New password must be at least 6 characters.";
        } else if (!newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$")) {
            error = "Password must contain letters, numbers, and special characters.";
        } else if (userDAO.md5(newPassword).equals(user.getPassword())) {
            error = "New password must be different from the old password.";
        } else {
            user.setPassword(userDAO.md5(newPassword));
            boolean updated = userDAO.updateUser(user);
            if (updated) {
                session.setAttribute("user", user);
                message = "Password changed successfully.";
            } else {
                error = "Failed to change password. Please try again.";
            }
        }
        request.setAttribute("error", error);
        request.setAttribute("message", message);
        request.getRequestDispatcher("ChangePassword.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
 