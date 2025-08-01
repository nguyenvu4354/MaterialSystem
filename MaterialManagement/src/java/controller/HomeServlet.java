/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.ExportRequestDAO;
import dal.InventoryDAO;
import dal.RoleDAO;
import dal.PurchaseRequestDAO;
import dal.RepairRequestDAO;
import dal.RequestDAO;
import dal.UserDAO;
import entity.Category;
import entity.Material;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/**
 *
 * @author Nhat Anh
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

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
            out.println("<title>Servlet HomeServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        // Kiểm tra đăng nhập - nếu chưa đăng nhập thì redirect về trang login
        if (user == null) {
            response.sendRedirect("Login");
            return;
        }
        
        MaterialDAO dao = new MaterialDAO();
        int page = 1;
        int pageSize = 8;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int materialCount = dao.countMaterials(null, null);
        int totalPages = (materialCount + pageSize - 1) / pageSize;
        if (page > totalPages && totalPages > 0) page = totalPages;
        List<Material> productList = dao.searchMaterials(null, null, page, pageSize, null);
        request.setAttribute("productList", productList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categoryTree = categoryDAO.getCategoryTree(3, 10); // tối đa 3 cấp, mỗi node 10 con
        request.setAttribute("categories", categoryTree);

        // Dashboard tổng quan cho tất cả role
        request.setAttribute("materialCount", materialCount);

        // Số yêu cầu xuất kho chờ duyệt
        ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
        int pendingExportRequestCount = exportRequestDAO.getTotalCount("pending", null, null);
        request.setAttribute("pendingExportRequestCount", pendingExportRequestCount);

        // Tổng số người dùng (chỉ cho admin)
        UserDAO userDAO = new UserDAO();
        int totalUserCount = userDAO.getUserCountByFilter(null, null, null, null);
        request.setAttribute("totalUserCount", totalUserCount);

        // Tổng số quyền (chỉ cho admin)
        dal.PermissionDAO permissionDAO = new dal.PermissionDAO();
        int totalPermissionCount = permissionDAO.getAllPermissions().size();
        request.setAttribute("totalPermissionCount", totalPermissionCount);

        // Số yêu cầu mua vật tư chờ duyệt
        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        int pendingPurchaseRequestCount = purchaseRequestDAO.countPurchaseRequest(null, "pending", null, null);
        request.setAttribute("pendingPurchaseRequestCount", pendingPurchaseRequestCount);

        // Số yêu cầu sửa chữa chờ duyệt
        RepairRequestDAO repairRequestDAO = new RepairRequestDAO();
        int pendingRepairRequestCount = 0;
        try {
            pendingRepairRequestCount = repairRequestDAO.getTotalRepairRequestCount(null, "pending", null, null);
        } catch (Exception e) {
            pendingRepairRequestCount = 0;
        }
        request.setAttribute("pendingRepairRequestCount", pendingRepairRequestCount);

        // Số yêu cầu của user hiện tại
        int myPurchaseRequestCount = 0;
        int myRepairRequestCount = 0;
        if (user != null) {
            RequestDAO requestDAO = new RequestDAO();
            myPurchaseRequestCount = requestDAO.getPurchaseRequestCountByUser(user.getUserId(), null, null, null, null, null);
            myRepairRequestCount = requestDAO.getRepairRequestCountByUser(user.getUserId(), null, null, null, null, null);
        }
        request.setAttribute("myPurchaseRequestCount", myPurchaseRequestCount);
        request.setAttribute("myRepairRequestCount", myRepairRequestCount);

        // Thống kê tồn kho
        InventoryDAO inventoryDAO = new InventoryDAO();
        int totalStock = 0, lowStockCount = 0, outOfStockCount = 0;
        try {
            Map<String, Integer> stats = inventoryDAO.getInventoryStatistics();
            totalStock = stats.getOrDefault("totalStock", 0);
            lowStockCount = stats.getOrDefault("lowStockCount", 0);
            outOfStockCount = stats.getOrDefault("outOfStockCount", 0);
        } catch (SQLException e) {
            // ignore
        }
        request.setAttribute("totalStock", totalStock);
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("outOfStockCount", outOfStockCount);

        // Lấy roleName nếu chưa có
        if (user != null && (user.getRoleName() == null || user.getRoleName().isEmpty())) {
            RoleDAO roleDAO = new RoleDAO();
            entity.Role role = roleDAO.getRoleById(user.getRoleId());
            if (role != null) {
                user.setRoleName(role.getRoleName());
                session.setAttribute("user", user);
            }
        }

        // TODO: Lấy thêm các số liệu dashboard khác nếu cần (yêu cầu mua, sửa chữa, ...)

        request.getRequestDispatcher("HomePage.jsp").forward(request, response);
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
