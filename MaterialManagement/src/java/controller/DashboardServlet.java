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

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 10 sản phẩm trên 1 trang
            int page = 1;
            int pageSize = 10;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            }

            MaterialDAO materialDAO = new MaterialDAO();
            
            // Tính thanh pagination
            int totalMaterials = materialDAO.getTotalMaterials();
            int totalPages = (int) Math.ceil((double) totalMaterials / pageSize);
            
            // Lấy trang dữ liệu từ dao
            List<Material> materials = materialDAO.getMaterialsWithPagination(page, pageSize);
            
            // link ảnh
            String contextPath = request.getContextPath();
            request.setAttribute("contextPath", contextPath);
            
            
            request.setAttribute("materials", materials);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            
            
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            // check lỗi 
        } catch (SQLException ex) {
            
            ex.printStackTrace();
            request.setAttribute("error", "Database error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (NumberFormatException ex) {
            
            response.sendRedirect("dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
} 