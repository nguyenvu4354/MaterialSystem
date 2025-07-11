/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.RepairRequestDAO;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nhat Anh
 */
@WebServlet(name = "RepairRequestListServlet", urlPatterns = {"/repairrequestlist"})
public class RepairRequestListServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet RepairRequestListServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RepairRequestListServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String status = request.getParameter("status");
        String search = request.getParameter("search");

        RepairRequestDAO dao = new RepairRequestDAO();
        List<RepairRequest> list = new ArrayList<>();

        try {
            if (search != null && !search.trim().isEmpty()
                    && status != null && !status.equalsIgnoreCase("all")) {
                // Có cả tìm kiếm và lọc status
                List<RepairRequest> searchList = dao.searchByRequestCode(search);
                for (RepairRequest r : searchList) {
                    if (r.getStatus().equalsIgnoreCase(status)) {
                        list.add(r);
                    }
                }
            } else if (search != null && !search.trim().isEmpty()) {
                // Chỉ tìm kiếm
                list = dao.searchByRequestCode(search);
            } else if (status != null && !status.equalsIgnoreCase("all")) {
                // Chỉ lọc theo trạng thái
                list = dao.filterByStatus(status);
            } else {
                // Không có gì, lấy tất cả
                list = dao.getAllRepairRequests();
            }

            request.setAttribute("repairRequests", list);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("searchKeyword", search);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi truy xuất danh sách yêu cầu!");
        }

        request.getRequestDispatcher("RepairRequestList.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
