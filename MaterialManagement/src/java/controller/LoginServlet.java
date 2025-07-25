package controller;

import dal.UserDAO;
import dal.PermissionDAO;
import entity.User;
import entity.Permission;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        PermissionDAO permissionDAO = new PermissionDAO();
        User user = userDAO.login(username, password); 

        if (user != null) {
            if (user.getStatus() != null && user.getStatus().toString().equalsIgnoreCase("inactive")) {
                request.setAttribute("error", "Your account has been disabled!");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                List<Permission> permissions = permissionDAO.getPermissionsByRole(user.getRoleId());
                List<String> permissionNames = permissions.stream()
                    .map(Permission::getPermissionName)
                    .collect(Collectors.toList());
                session.setAttribute("userPermissions", permissionNames);
                
                String redirectURL = (String) session.getAttribute("redirectURL");
                if (redirectURL != null && !redirectURL.isEmpty()) {
                    session.removeAttribute("redirectURL");
                    response.sendRedirect(response.encodeRedirectURL(redirectURL));
                } else {
                    response.sendRedirect(response.encodeRedirectURL("home"));
                }
            }
        } else {
            request.setAttribute("error", "Invalid username or password!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }
}