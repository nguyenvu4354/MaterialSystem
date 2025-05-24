package controller;

import dal.MaterialDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entity.Material;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboardmaterial"})
public class DashboardMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get search parameters
            String searchTerm = request.getParameter("search");
            String status = request.getParameter("status");
            String showDisabled = request.getParameter("showDisabled"); // New parameter
            
            // Pagination parameters
            int page = 1;
            int pageSize = 10;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            }

            MaterialDAO materialDAO = new MaterialDAO();
            
            // Get total materials with filter
            int totalMaterials = materialDAO.getTotalMaterialsWithFilter(searchTerm, status);
            int totalPages = (int) Math.ceil((double) totalMaterials / pageSize);
            
            // Get filtered materials (including disabled ones)
            List<Material> materials = materialDAO.getAllMaterialsWithPagination(searchTerm, status, page, pageSize);
            
            // Set request attributes
            request.setAttribute("materials", materials);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("selectedStatus", status);
            
            // Forward to dashboard
            request.getRequestDispatcher("DashboardMaterial.jsp").forward(request, response);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
} 