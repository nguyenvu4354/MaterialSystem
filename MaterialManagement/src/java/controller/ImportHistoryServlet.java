/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.ImportDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import entity.Import;
import entity.Material;
import entity.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ImportHistoryServlet", urlPatterns = {"/ImportHistory"})
public class ImportHistoryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

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
            out.println("<title>Servlet ImportHistoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ImportHistoryServlet at " + request.getContextPath() + "</h1>");
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
        // 1. Lấy filter
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String importCode = request.getParameter("importCode");
        String materialName = request.getParameter("materialName");
        String supplierName = request.getParameter("supplierName");
        int page = 1;
        int pageSize = 10; // Default page size
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) {
                page = 1;
            }
        } catch (Exception e) {
            page = 1;
        }
        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
            if (pageSize < 1) {
                pageSize = 10;
            }
        } catch (Exception e) {
            pageSize = 10;
        }

        // 2. Khởi tạo DAO
        ImportDAO importDAO = new ImportDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        SupplierDAO supplierDAO = new SupplierDAO();

        // 3. Lấy danh sách phiếu nhập, filter nâng cao, phân trang
        List<Import> importList = importDAO.getImportHistoryAdvanced(fromDate, toDate, importCode, materialName, supplierName, page, pageSize);
        int totalRecords = importDAO.countImportHistoryAdvanced(fromDate, toDate, importCode, materialName, supplierName);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // 4. Lấy danh sách vật tư, nhà cung cấp cho autocomplete
        List<Material> materialList = materialDAO.getAllMaterials();
        List<Supplier> supplierList = supplierDAO.getAllSuppliers();

        // 5. Đẩy dữ liệu lên request
        request.setAttribute("importList", importList);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("importCode", importCode);
        request.setAttribute("materialName", materialName);
        request.setAttribute("supplierName", supplierName);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize); // Add pageSize to request
        request.setAttribute("materialList", materialList);
        request.setAttribute("supplierList", supplierList);

        // 6. Forward về JSP
        request.getRequestDispatcher("ImportHistory.jsp").forward(request, response);
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
