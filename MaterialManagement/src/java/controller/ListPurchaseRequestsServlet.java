/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.PurchaseRequestDAO;
import entity.PurchaseRequest;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // Lấy các tham số từ request
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String sortOption = request.getParameter("sort");
            
            // Mặc định pageIndex = 1 và pageSize = 10 nếu không có
            int pageIndex = 1;
            int pageSize = 10;
            
            try {
                pageIndex = Integer.parseInt(request.getParameter("page"));
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định nếu parse lỗi
            }
            
            // Khởi tạo DAO và lấy danh sách
            PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
            List<PurchaseRequest> purchaseRequests = prDAO.searchPurchaseRequest(keyword, status, pageIndex, pageSize, sortOption);
            
            // Lấy tổng số bản ghi để tính số trang
            int totalRecords = prDAO.getTotalRecords(keyword, status);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Set các attribute cho JSP
            request.setAttribute("purchaseRequests", purchaseRequests);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            
            // Forward đến trang JSP
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Log lỗi
            e.printStackTrace();
            // Chuyển hướng đến trang lỗi hoặc hiển thị thông báo lỗi
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
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
        return "Servlet xử lý danh sách yêu cầu mua hàng";
    }// </editor-fold>

}
