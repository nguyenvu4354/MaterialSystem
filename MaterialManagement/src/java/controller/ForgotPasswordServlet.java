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
import utils.EmailUtils;

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
        String email = request.getParameter("email");
        UserDAO userDAO = new UserDAO();
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
        // Send email to admin
        String adminEmail = "admin@company.com"; // Change to your admin email
        String subject = "Password Reset Request";
        String content = "User with email: " + email + " has requested a password reset. Please assist them in resetting their password.";
        try {
            EmailUtils.sendEmail(adminEmail, subject, content);
            request.setAttribute("message", "Your request has been sent to the admin. Please wait for the admin to reset your password.");
        } catch (Exception e) {
            request.setAttribute("message", "Failed to send email to admin. Please try again later.");
        }
        request.setAttribute("step", "done");
        request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
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
