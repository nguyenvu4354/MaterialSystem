package controller;

import dal.MaterialDAO;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DeleteMaterialServlet", urlPatterns = {"/deletematerial"})
public class DeleteMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra session và quyền truy cập
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            // User is not logged in, save the requested URL and redirect to login
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            // Need a session to store the redirect URL
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Check access permissions - only allow Admin (role_id = 1)
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to access this page. Only Admins can delete materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check session and access rights
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            // User is not logged in, redirect to login
            response.sendRedirect("LoginServlet");
            return;
        }
        
        User user = (User) session.getAttribute("user");

        // Check access permissions - only allow Admin (role_id = 1)
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to perform this action. Only Admins can delete materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try{
           String idDelete = request.getParameter("materialId");
           int id = Integer.parseInt(idDelete);
           MaterialDAO md = new MaterialDAO();
           md.deleteMaterial(id);
            response.sendRedirect("dashboardmaterial");
       }catch(Exception e){
           e.printStackTrace();
       }
    }
}
