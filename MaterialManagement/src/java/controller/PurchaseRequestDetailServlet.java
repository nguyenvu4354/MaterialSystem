/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.PurchaseRequestDAO;
import dal.PurchaseRequestDetailDAO;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // User is logged in, now check for permissions
        if (user.getRoleId() != 2) {
            request.setAttribute("error", "You don't have permission to access this page. Only directors can view purchase request details.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("id"));
            
            // Lấy thông tin yêu cầu
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            PurchaseRequest purchaseRequest = prd.getPurchaseRequestById(purchaseRequestId);
            
            if (purchaseRequest == null) {
                request.setAttribute("error", "Purchase request not found.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // Xử lý phân trang
            int pageSize = 10;
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }
            
            // Lấy thông tin chi tiết yêu cầu với phân trang
            PurchaseRequestDetailDAO prdd = new PurchaseRequestDetailDAO();
            
            // Lấy tổng số chi tiết
            int totalItems = prdd.count(purchaseRequestId);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }
            
            List<PurchaseRequestDetail> purchaseRequestDetailList;
            
            if (totalItems > pageSize) {
                // Sử dụng phân trang
                purchaseRequestDetailList = prdd.paginationOfDetails(
                    purchaseRequestId, currentPage, pageSize);
                
                request.setAttribute("currentPage", currentPage);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("totalItems", totalItems);
                request.setAttribute("showPagination", true);
            } else {
                // Không cần phân trang
                purchaseRequestDetailList = prdd.getPurchaseRequestDetailByPurchaseRequestId(purchaseRequestId);
                request.setAttribute("showPagination", false);
            }

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // User is logged in, now check for permissions
        if (user.getRoleId() != 2) {
            request.setAttribute("error", "You don't have permission to perform this action. Only directors can approve/reject purchase requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
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
