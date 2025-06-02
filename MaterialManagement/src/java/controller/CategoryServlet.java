package controller;

import dal.CategoryDAO;
import entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/CategoryServlet"})
public class CategoryServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        CategoryDAO dao = new CategoryDAO();
        String service = request.getParameter("service");
        if (service == null) service = "listCategory";

        try {
            switch (service) {
                case "updateCategory": {
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        // Display update form
                        String categoryIdRaw = request.getParameter("categoryID");
                        if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                            sendErrorResponse(response, "Missing category ID.");
                            return;
                        }
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw);
                            Category category = dao.getCategoryById(categoryId);
                            if (category == null) {
                                request.setAttribute("error", "No category found with ID: " + categoryId);
                                request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                return;
                            }
                            request.setAttribute("c", category);
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                        } catch (NumberFormatException e) {
                            sendErrorResponse(response, "Invalid category ID.");
                        }
                    } else {
                        // Process category update
                        try {
                            int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            String priority = request.getParameter("priority");
                            String code = request.getParameter("code");
                            String createdAtRaw = request.getParameter("createdAt");
                            String parentIDRaw = request.getParameter("parentID");

                            if (categoryName == null || categoryName.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category name and code cannot be empty.");
                                request.setAttribute("c", dao.getCategoryById(categoryId));
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            // Check for duplicate code (excluding the current category's code)
                            List<Category> existingCategories = dao.searchCategoriesByCode(code.trim());
                            for (Category existing : existingCategories) {
                                if (existing.getCategory_id() != categoryId && existing.getCode().equalsIgnoreCase(code.trim())) {
                                    request.setAttribute("error", "Category code '" + code + "' already exists. Please choose a different code.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Invalid date and time format.");
                                request.setAttribute("c", dao.getCategoryById(categoryId));
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (dao.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Parent category does not exist.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                                // Check for parent category loop
                                if (parentId == categoryId) {
                                    request.setAttribute("error", "A category cannot be its own parent category.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(categoryId, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            boolean updated = dao.updateCategory(category);
                            if (updated) {
                                System.out.println("CategoryServlet - Category updated successfully: " + categoryName);
                                response.sendRedirect("Category.jsp");
                            } else {
                                String daoError = dao.getLastError() != null ? dao.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to update category. Error details: " + daoError);
                                request.setAttribute("c", category);
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while updating category: " + e.getMessage());
                            request.setAttribute("c", dao.getCategoryById(Integer.parseInt(request.getParameter("categoryID"))));
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("UpdateCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "deleteCategory": {
                    String categoryIdRaw = request.getParameter("categoryID");
                    if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                        sendErrorResponse(response, "Missing category ID.");
                        return;
                    }
                    try {
                        int categoryId = Integer.parseInt(categoryIdRaw);
                        Category category = dao.getCategoryById(categoryId);
                        if (category == null) {
                            request.setAttribute("error", "No category found with ID: " + categoryId);
                            request.getRequestDispatcher("Category.jsp").forward(request, response);
                            return;
                        }
                        // Check if category has subcategories or related products
                        List<Category> childCategories = dao.getAllCategories().stream()
                                .filter(c -> c.getParent_id() != null && c.getParent_id() == categoryId)
                                .toList();
                        if (!childCategories.isEmpty()) {
                            request.setAttribute("error", "Cannot delete category due to dependent subcategories.");
                            request.getRequestDispatcher("Category.jsp").forward(request, response);
                            return;
                        }
                        // Assume no related products (additional check needed if Products table exists)
                        boolean deleted = dao.deleteCategory(categoryId);
                        if (deleted) {
                            System.out.println("CategoryServlet - Category deleted successfully: ID = " + categoryId);
                            response.sendRedirect("Category.jsp");
                        } else {
                            String daoError = dao.getLastError() != null ? dao.getLastError() : "No detailed error from CategoryDAO.";
                            request.setAttribute("error", "Unable to delete category. Error details: " + daoError);
                            request.getRequestDispatcher("Category.jsp").forward(request, response);
                        }
                    } catch (NumberFormatException e) {
                        sendErrorResponse(response, "Invalid category ID.");
                    } catch (Exception e) {
                        sendErrorResponse(response, "Error while deleting category: " + e.getMessage());
                    }
                    break;
                }

                case "addCategory": {
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        request.setAttribute("categories", dao.getAllCategories());
                        request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                    } else {
                        try {
                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            String priority = request.getParameter("priority");
                            String code = request.getParameter("code");
                            String createdAtRaw = request.getParameter("createdAt");
                            String parentIDRaw = request.getParameter("parentID");

                            if (categoryName == null || categoryName.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category name and code cannot be empty.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = dao.searchCategoriesByCode(code.trim());
                            if (!existingCategories.isEmpty()) {
                                request.setAttribute("error", "Category code '" + code + "' already exists. Please choose a different code.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Invalid date and time format.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (dao.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Parent category does not exist.");
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(0, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            System.out.println("CategoryServlet - Attempting to insert category: " + categoryName);
                            boolean inserted = dao.insertCategory(category);
                            if (inserted) {
                                System.out.println("CategoryServlet - Category inserted successfully: " + categoryName);
                                response.sendRedirect("Category.jsp");
                            } else {
                                String daoError = dao.getLastError() != null ? dao.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to add category. Error details: " + daoError);
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while adding category: " + e.getMessage());
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("InsertCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "listCategory": {
                    List<Category> categories = dao.getAllCategories();
                    request.setAttribute("data", categories);
                    request.getRequestDispatcher("Category.jsp").forward(request, response);
                    break;
                }

                default:
                    sendErrorResponse(response, "Unsupported service: " + service);
            }
        } catch (Exception e) {
            System.out.println("CategoryServlet - Error processing request: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Error processing request: " + e.getMessage());
        }
    }

    // Method to send error response in JSON format
    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"" + errorMessage + "\"}");
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Category Servlet for Category Management";
    }
}