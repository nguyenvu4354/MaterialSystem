/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import dal.UserDAO;

/**
 *
 * @author Admin
 */
@WebServlet(name="ForgotPasswordServlet", urlPatterns={"/ForgotPassword"})
public class ForgotPasswordServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ForgotPasswordSevlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordSevlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Show email input form
        request.setAttribute("step", "email");
        request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String step = request.getParameter("step");
        String email = request.getParameter("email");
        if (step == null) step = "email";
        UserDAO userDAO = new UserDAO();
        if (step.equals("email")) {
            // Step 1: Receive email, check existence
            if (email == null || email.isEmpty()) {
                request.setAttribute("message", "Please enter your email.");
                request.setAttribute("step", "email");
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
                return;
            }
            if (!userDAO.isEmailExists(email)) {
                request.setAttribute("message", "Email does not exist in the system.");
                request.setAttribute("step", "email");
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
                return;
            }
            // Valid email, move to new password input step
            request.setAttribute("step", "reset");
            request.setAttribute("email", email);
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
        } else if (step.equals("reset") || (request.getParameter("password") != null && request.getParameter("confirm") != null)) {
            // Step 2: Receive new password
            String password = request.getParameter("password");
            String confirm = request.getParameter("confirm");
            if (password == null || confirm == null || password.isEmpty() || confirm.isEmpty()) {
                request.setAttribute("message", "Please enter the new password.");
                request.setAttribute("step", "reset");
                request.setAttribute("email", email);
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
                return;
            }
            if (!password.equals(confirm)) {
                request.setAttribute("message", "Password confirmation does not match.");
                request.setAttribute("step", "reset");
                request.setAttribute("email", email);
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
                return;
            }
            // Hash with MD5 and update DB
            String md5Password = md5(password);
            boolean updated = userDAO.updatePasswordByEmail(email, md5Password);
            if (updated) {
                request.setAttribute("step", "done");
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "Failed to reset password. Please try again or contact support.");
                request.setAttribute("step", "reset");
                request.setAttribute("email", email);
                request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            }
        } else {
            // Default: return to email input step
            request.setAttribute("step", "email");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
        }
    }

    // MD5 hash function
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
