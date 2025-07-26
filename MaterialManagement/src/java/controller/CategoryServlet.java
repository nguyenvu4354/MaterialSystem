package controller;

import dal.CategoryDAO;
import dal.RolePermissionDAO;
import entity.Category;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/Category"})
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 5;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String service = request.getParameter("service");
        if (service == null) service = "listCategory";
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/Category.jsp").forward(request, response);
            return;
        }
        int roleId = currentUser.getRoleId();
        boolean isAdmin = roleId == 1;
        try {
            switch (service) {
                case "updateCategory": {
                    if (!isAdmin && !rolePermissionDAO.hasPermission(roleId, "UPDATE_CATEGORY")) {
                        request.setAttribute("error", "You do not have permission to update categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        String categoryIdRaw = request.getParameter("category_id");
                        if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                            sendErrorResponse(response, "Missing category ID.");
                            return;
                        }
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw);
                            Category category = categoryDAO.getCategoryById(categoryId);
                            if (category == null) {
                                request.setAttribute("error", "No category found with ID: " + categoryId);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }
                            request.setAttribute("c", category);
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                        } catch (NumberFormatException e) {
                            sendErrorResponse(response, "Invalid category ID.");
                        }
                    } else {
                        try {
                            int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            String priority = request.getParameter("priority");
                            String code = request.getParameter("code");
                            String parentIDRaw = request.getParameter("parentID");

                            if (code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category code cannot be empty.");
                                request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            if (categoryDAO.isCategoryNameExists(categoryName.trim(), categoryId)) {
                                request.setAttribute("error", "Category name '" + categoryName + "' already exists. Please choose a different name.");
                                request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = categoryDAO.searchCategories(code.trim(), null, null, null, null);
                            for (Category existing : existingCategories) {
                                if (existing.getCategory_id() != categoryId && existing.getCode().equalsIgnoreCase(code.trim())) {
                                    request.setAttribute("error", "Category code '" + code + "' already exists. Please choose a different code.");
                                    request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                    request.setAttribute("categories", categoryDAO.getAllCategories());
                                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                    request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (categoryDAO.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Parent category does not exist.");
                                    request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                    request.setAttribute("categories", categoryDAO.getAllCategories());
                                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                    request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                                if (parentId == categoryId) {
                                    request.setAttribute("error", "A category cannot be its own parent category.");
                                    request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                    request.setAttribute("categories", categoryDAO.getAllCategories());
                                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                    request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(categoryId, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            boolean updated = categoryDAO.updateCategory(category);
                            if (updated) {
                                response.sendRedirect("Category?service=listCategory");
                            } else {
                                String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to update category. Error details: " + daoError);
                                request.setAttribute("c", category);
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while updating category: " + e.getMessage());
                            request.setAttribute("c", categoryDAO.getCategoryById(Integer.parseInt(request.getParameter("categoryID"))));
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "deleteCategory": {
                    if (!isAdmin && !rolePermissionDAO.hasPermission(roleId, "DELETE_CATEGORY")) {
                        request.setAttribute("error", "You do not have permission to delete categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String categoryIdRaw = request.getParameter("category_id");
                    if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                        sendErrorResponse(response, "Missing category ID.");
                        return;
                    }
                    try {
                        int categoryId = Integer.parseInt(categoryIdRaw);
                        Category category = categoryDAO.getCategoryById(categoryId);
                        if (category == null) {
                            request.setAttribute("error", "No category found with ID: " + categoryId);
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                            return;
                        }
                        List<Category> childCategories = categoryDAO.getAllCategories().stream()
                                .filter(c -> c.getParent_id() != null && c.getParent_id() == categoryId)
                                .toList();
                        if (!childCategories.isEmpty()) {
                            request.setAttribute("error", "Cannot delete category due to dependent subcategories.");
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                            return;
                        }
                        boolean deleted = categoryDAO.deleteCategory(categoryId);
                        if (deleted) {
                            response.sendRedirect("Category?service=listCategory");
                        } else {
                            String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                            request.setAttribute("error", "Unable to delete category. Error details: " + daoError);
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        }
                    } catch (NumberFormatException e) {
                        sendErrorResponse(response, "Invalid category ID.");
                    } catch (Exception e) {
                        sendErrorResponse(response, "Error while deleting category: " + e.getMessage());
                    }
                    break;
                }

                case "addCategory": {
                    if (!isAdmin && !rolePermissionDAO.hasPermission(roleId, "CREATE_CATEGORY")) {
                        request.setAttribute("error", "You do not have permission to create categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        int maxNum = categoryDAO.getMaxCategoryNumber();
                        String newCategoryCode = "CAT" + (maxNum + 1);
                        request.setAttribute("categoryCode", newCategoryCode);
                        request.setAttribute("categories", categoryDAO.getAllCategories());
                        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                        request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                    } else {
                        try {
                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            String priority = request.getParameter("priority");
                            String code = request.getParameter("code");
                            String parentIDRaw = request.getParameter("parentID");

                            if (categoryName == null || categoryName.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category name and code cannot be empty.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            if (categoryDAO.isCategoryNameExists(categoryName.trim(), null)) {
                                request.setAttribute("error", "Category name '" + categoryName + "' already exists. Please choose a different name.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.setAttribute("enteredCategoryCode", code);
                                request.setAttribute("enteredCategoryName", categoryName);
                                request.setAttribute("enteredDescription", description);
                                request.setAttribute("enteredStatus", status);
                                request.setAttribute("enteredPriority", priority);
                                request.setAttribute("enteredParentID", parentIDRaw);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = categoryDAO.searchCategories(code.trim(), null, null, null, null);
                            if (!existingCategories.isEmpty()) {
                                request.setAttribute("error", "Category code '" + code + "' already exists. Please choose a different code.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Timestamp createdAt = new Timestamp(System.currentTimeMillis());

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (categoryDAO.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Parent category does not exist.");
                                    request.setAttribute("categories", categoryDAO.getAllCategories());
                                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                    request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(0, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            boolean inserted = categoryDAO.insertCategory(category);
                            if (inserted) {
                                response.sendRedirect("Category?service=listCategory");
                            } else {
                                String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to add category. Error details: " + daoError);
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while adding category: " + e.getMessage());
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "listCategory": {
                    if (!isAdmin && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_CATEGORY")) {
                        request.setAttribute("error", "You do not have permission to view the category list.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String name = request.getParameter("name");
                    String priority = request.getParameter("priority");
                    String status = request.getParameter("status");
                    String sortBy = request.getParameter("sortBy");
                    int currentPage = 1;
                    String pageStr = request.getParameter("page");
                    try {
                        if (pageStr != null && !pageStr.trim().isEmpty()) {
                            currentPage = Integer.parseInt(pageStr);
                            if (currentPage < 1) currentPage = 1;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("CategoryServlet - Invalid page number: " + pageStr);
                    }

                    String sortCol = null;
                    String sortOrder = null;
                    if (sortBy != null && !sortBy.isEmpty()) {
                        if (sortBy.endsWith("_asc")) {
                            sortCol = sortBy.replace("_asc", "");
                            sortOrder = "asc";
                        } else if (sortBy.endsWith("_desc")) {
                            sortCol = sortBy.replace("_desc", "");
                            sortOrder = "desc";
                        }
                    }

                    List<Category> filteredList = categoryDAO.searchCategories(name, priority, status, sortCol, sortOrder);
                    int totalCategories = filteredList.size();
                    int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);
                    if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
                    int start = (currentPage - 1) * PAGE_SIZE;
                    int end = Math.min(start + PAGE_SIZE, filteredList.size());
                    List<Category> paginatedList = (start < filteredList.size()) ? filteredList.subList(start, end) : Collections.emptyList();

                    request.setAttribute("data", paginatedList);
                    request.setAttribute("currentPage", currentPage);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("name", name);
                    request.setAttribute("priority", priority);
                    request.setAttribute("status", status);
                    request.setAttribute("sortBy", sortBy);
                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                    request.setAttribute("categoryDAO", categoryDAO);
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/Category.jsp").forward(request, response);
                    break;
                }

                default:
                    sendErrorResponse(response, "Unsupported service: " + service);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/Category.jsp").forward(request, response);
        }
    }

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
        return "Category management servlet";
    }
}