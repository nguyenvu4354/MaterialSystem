/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.PurchaseRequestDAO;
import dal.PurchaseRequestDetailDAO;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author Admin
 */
@WebServlet(name="ListPurchaseDetailServlet", urlPatterns={"/PurchaseRequestDetailServlet"})

public class PurchaseRequestDetailServlet extends HttpServlet {
   
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
            out.println("<title>Servlet PurchaseRequestDetailServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PurchaseRequestDetailServlet at " + request.getContextPath () + "</h1>");
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
        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            // Lấy thông tin chi tiết yêu cầu
            PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();
            List<PurchaseRequestDetail> purchaseRequestDetailList = prdd.getPurchaseRequestDetailByPurchaseRequestId(purchaseRequestId);
            
            // Lấy thông tin yêu cầu
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            PurchaseRequest purchaseRequest = prd.getPurchaseRequestById(purchaseRequestId);

            request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
            request.setAttribute("purchaseRequest", purchaseRequest);
            
            request.getRequestDispatcher("PurchaseRequestDetail.jsp").forward(request, response);
        } catch(Exception ex) {
            System.out.println("Error in PurchaseRequestDetailServlet: " + ex.getMessage());
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request");
        }
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
        try {
            String action = request.getParameter("action");
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            boolean success = false;
            
            if ("approve".equals(action)) {
               
                success = prd.updatePurchaseRequestStatus(purchaseRequestId, "approved", null);
            } else if ("reject".equals(action)) {
                
                success = prd.updatePurchaseRequestStatus(purchaseRequestId, "rejected", null);
            }
            
            if (success) {
                response.sendRedirect("PurchaseRequestDetailServlet?id=" + purchaseRequestId);
            } else {
                response.sendRedirect("PurchaseRequestDetailServlet?id=" + purchaseRequestId + "&error=action_failed");
            }
            
        } catch(Exception ex) {
            System.out.println("Error in PurchaseRequestDetailServlet POST: " + ex.getMessage());
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request");
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet xử lý chi tiết yêu cầu mua hàng";
    }// </editor-fold>

}
