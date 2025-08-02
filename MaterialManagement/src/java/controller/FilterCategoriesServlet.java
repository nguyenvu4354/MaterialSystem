package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import entity.Category;
import entity.Material;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "FilterCategoriesServlet", urlPatterns = {"/filter"})
public class FilterCategoriesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryId = Integer.parseInt(request.getParameter("categoryId"));

        MaterialDAO materialDAO = new MaterialDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        List<Material> materials = materialDAO.searchMaterialsByCategoriesID(categoryId);

        List<Category> categories = categoryDAO.getAllCategories();

        // Redirect to StaticInventory (category filter can be added later if needed)
        response.sendRedirect("StaticInventory");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Không xử lý POST, hoặc có thể redirect về HomePage
        response.sendRedirect("HomePage.jsp");
    }

    @Override
    public String getServletInfo() {
        return "Filter product by category and keep category list displayed";
    }
}
