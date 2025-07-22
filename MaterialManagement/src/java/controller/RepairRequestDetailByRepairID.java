/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.RepairRequestDAO;
import dal.RepairRequestDetailDAO;
import entity.RepairRequest;
import entity.RepairRequestDetail;
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
 * @author Nhat Anh
 */
@WebServlet(name = "RepairRequestDetailByRepairID", urlPatterns = {"/repairrequestdetailbyID"})
public class RepairRequestDetailByRepairID extends HttpServlet {

    private static final int PAGE_SIZE = 5; // Number of records per page

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
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RepairRequestDetailByRepairID</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RepairRequestDetailByRepairID at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));

            // Get current page from request, default to 1 if not provided
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) {
                        currentPage = 1;
                    }
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }

            RepairRequestDetailDAO dao = new RepairRequestDetailDAO();
            // Get all details to calculate total records
            List<RepairRequestDetail> allDetails = dao.getRepairRequestDetailsByRequestId(requestId);

            // Calculate pagination parameters
            int totalRecords = allDetails.size();
            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            if (totalPages < 1) {
                totalPages = 1;
            }
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            // Get paginated details
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalRecords);
            List<RepairRequestDetail> details = allDetails.subList(start, end);

            // Get RepairRequest information
            RepairRequestDAO repairRequestDAO = new RepairRequestDAO();
            RepairRequest repairRequest = repairRequestDAO.getRepairRequestById(requestId);

            // Get user information from session
            HttpSession session = request.getSession();
            entity.User user = (entity.User) session.getAttribute("user");

            int roleId = user != null ? user.getRoleId() : 0;

            // Set attributes for JSP
            request.setAttribute("details", details);
            request.setAttribute("requestId", requestId);
            request.setAttribute("roleId", roleId);
            request.setAttribute("status", repairRequest != null ? repairRequest.getStatus() : "N/A");
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);

            request.getRequestDispatcher("RepairRequestDetailByID.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load repair request details.");
            request.getRequestDispatcher("RepairRequestDetailByID.jsp").forward(request, response);
        }
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
        return "Handles repair request details with pagination";
    }
}