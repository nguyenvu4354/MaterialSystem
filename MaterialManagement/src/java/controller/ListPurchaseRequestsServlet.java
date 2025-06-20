/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.PurchaseRequestDAO;
import entity.PurchaseRequest;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Admin
 */
@WebServlet(name="ListPurchaseRequestsServlet", urlPatterns={"/ListPurchaseRequestsServlet"})
public class ListPurchaseRequestsServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // User is logged in, now check for permissions
        if (user.getRoleId() != 2) {
            request.setAttribute("error", "You don't have permission to access this page. Only directors can view the purchase request list.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String sortOption = request.getParameter("sort");
            
            int pageSize = 10;
            
            int pageIndex = 1;
        
            
            
            try {
                pageIndex = Integer.parseInt(request.getParameter("page"));
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            } catch (NumberFormatException e) {
                
            }
            
            
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            List<PurchaseRequest> purchaseRequests = prd.searchPurchaseRequest(keyword, status, pageIndex, pageSize, sortOption);
            
            
            int totalRecords = prd.countPurchaseRequest(keyword, status);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            request.setAttribute("purchaseRequests", purchaseRequests);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            
           
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            
        } catch (Exception e) {
            
            e.printStackTrace();
            
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        doGet(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for handling purchase request list";
    }// </editor-fold>

}
