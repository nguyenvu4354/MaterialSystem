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
import java.util.ArrayList;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboardmaterial"})
public class DashboardMaterialServlet extends HttpServlet {

    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int pageIndex = 1;
            int pageSize = 10;

            // Lấy trang hiện tại
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                pageIndex = Integer.parseInt(pageParam);
            }

            // Lấy từ khóa tìm kiếm
            String keyword = request.getParameter("keyword");
            if (keyword == null) {
                keyword = "";
            }
            String status = request.getParameter("status");
            if (status == null) {
                status = "";
            }
            String sortOption = request.getParameter("sortOption");
            if (sortOption == null) {
                sortOption = "";
            }
            MaterialDAO md = new MaterialDAO();

            List<Material> list;
            int totalMaterials;

            list = md.searchMaterials(keyword, status, pageIndex, pageSize,sortOption);
            totalMaterials = md.countMaterials(keyword, status);

            int totalPages = (int) Math.ceil((double) totalMaterials / pageSize);

            // Truyền dữ liệu ra JSP
            request.setAttribute("list", list);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.getRequestDispatcher("DashboardMaterial.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
