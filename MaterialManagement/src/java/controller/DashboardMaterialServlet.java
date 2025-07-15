package controller;

import dal.MaterialDAO;
import dal.RolePermissionDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entity.Material;
import entity.User;
import jakarta.servlet.http.HttpSession;
import dal.CategoryDAO;
import entity.Category;

@WebServlet(name = "DashboardMaterialServlet", urlPatterns = {"/dashboardmaterial"})
public class DashboardMaterialServlet extends HttpServlet {
    private MaterialDAO materialDAO;
    private RolePermissionDAO rolePermissionDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        materialDAO = new MaterialDAO();
        rolePermissionDAO = new RolePermissionDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            if (user == null || (user.getRoleId() != 1 && user.getRoleId() != 2 && user.getRoleId() != 3 && user.getRoleId() != 4)) {
                request.setAttribute("error", "Vui lòng đăng nhập để truy cập trang này.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            // Kiểm tra quyền VIEW_LIST_MATERIAL
            int roleId = user.getRoleId();
            if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_MATERIAL")) {
                request.setAttribute("error", "Bạn không có quyền xem danh sách vật tư.");
                request.getRequestDispatcher("DashboardMaterial.jsp").forward(request, response);
                return;
            }

            boolean readonly = user.getRoleId() != 1;
            request.setAttribute("readonly", readonly);

            int pageIndex = 1;
            int pageSize = 10;

            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    pageIndex = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    pageIndex = 1;
                }
            }

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

            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            String categoryIdParam = request.getParameter("categoryId");
            Integer categoryId = null;
            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                try {
                    categoryId = Integer.parseInt(categoryIdParam);
                } catch (NumberFormatException e) {
                    categoryId = null;
                }
            }

            String categoryName = request.getParameter("categoryName");
            List<Material> list;
            int totalMaterials;
            int totalPages;
            if (categoryName != null && !categoryName.trim().isEmpty() && (categoryId == null)) {
                // Nhập tên nhưng không chọn gợi ý, trả về rỗng
                list = java.util.Collections.emptyList();
                totalMaterials = 0;
                totalPages = 1;
            } else if (categoryId != null) {
                list = materialDAO.searchMaterialsByCategoriesID(categoryId);
                totalMaterials = list.size();
                totalPages = 1;
            } else {
                list = materialDAO.searchMaterials(keyword, status, pageIndex, pageSize, sortOption);
                totalMaterials = materialDAO.countMaterials(keyword, status);
                totalPages = (int) Math.ceil((double) totalMaterials / pageSize);
            }

            request.setAttribute("list", list);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);

            request.getRequestDispatcher("DashboardMaterial.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}