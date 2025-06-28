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
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/Category"}) // Sửa từ /CategoryServlet thành /Category
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 5;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
        System.out.println("CategoryServlet - Initialized with CategoryDAO and RolePermissionDAO");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String service = request.getParameter("service");
        if (service == null) service = "listCategory";

        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            System.out.println("CategoryServlet - No user in session");
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/Category.jsp").forward(request, response);
            return;
        }

        int roleId = currentUser.getRoleId();
        boolean isAdmin = roleId == 1;

        System.out.println("CategoryServlet - Service: " + service + ", RoleId: " + roleId + ", IsAdmin: " + isAdmin);

        try {
            switch (service) {
                case "updateCategory": {
                    if (!isAdmin && !rolePermissionDAO.hasPermission(roleId, "UPDATE_CATEGORY")) {
                        System.out.println("CategoryServlet - User lacks UPDATE_CATEGORY permission");
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
                            String createdAtRaw = request.getParameter("createdAt");
                            String parentIDRaw = request.getParameter("parentID");

                            if (categoryName == null || categoryName.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category name and code cannot be empty.");
                                request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = categoryDAO.searchCategoriesByCode(code.trim());
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

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Invalid date and time format.");
                                request.setAttribute("c", categoryDAO.getCategoryById(categoryId));
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

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
                                System.out.println("CategoryServlet - Category updated successfully: " + categoryName);
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
                        System.out.println("CategoryServlet - User lacks DELETE_CATEGORY permission");
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
                            System.out.println("CategoryServlet - Category deleted successfully: ID = " + categoryId);
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
                        System.out.println("CategoryServlet - User lacks CREATE_CATEGORY permission");
                        request.setAttribute("error", "You do not have permission to create categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String submit = request.getParameter("submit");
                    if (submit == null) {
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
                            String createdAtRaw = request.getParameter("createdAt");
                            String parentIDRaw = request.getParameter("parentID");

                            if (categoryName == null || categoryName.trim().isEmpty() || code == null || code.trim().isEmpty()) {
                                request.setAttribute("error", "Category name and code cannot be empty.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = categoryDAO.searchCategoriesByCode(code.trim());
                            if (!existingCategories.isEmpty()) {
                                request.setAttribute("error", "Category code '" + code + "' already exists. Please choose a different code.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Invalid date and time format.");
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

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
                                System.out.println("CategoryServlet - Category inserted successfully: " + categoryName);
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
                        System.out.println("CategoryServlet - User lacks VIEW_LIST_CATEGORY permission");
                        request.setAttribute("error", "You do not have permission to view the category list.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String code = request.getParameter("code");
                    String keyword = request.getParameter("categoryName");
                    String priority = request.getParameter("priority");
                    String status = request.getParameter("status");
                    String sortBy = request.getParameter("sortBy");
                    String pageStr = request.getParameter("page");
                    int currentPage = 1;
                    try {
                        if (pageStr != null && !pageStr.trim().isEmpty()) {
                            currentPage = Integer.parseInt(pageStr);
                            if (currentPage < 1) currentPage = 1;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("CategoryServlet - Invalid page number: " + pageStr);
                    }

                    System.out.println("CategoryServlet - Parameters: code=" + code + ", categoryName=" + keyword +
                            ", priority=" + priority + ", status=" + status + ", sortBy=" + sortBy + ", page=" + currentPage);

                    List<Category> list;
                    int totalCategories;

                    try {
                        if (code != null && !code.trim().isEmpty()) {
                            list = categoryDAO.searchCategoriesByCode(code.trim());
                            totalCategories = list.size();
                            request.setAttribute("code", code);
                        } else if (keyword != null && !keyword.trim().isEmpty() ||
                                priority != null && !priority.trim().isEmpty() ||
                                status != null && !status.trim().isEmpty()) {
                            boolean hasPriority = priority != null && !priority.trim().isEmpty();
                            boolean hasName = keyword != null && !keyword.trim().isEmpty();
                            boolean hasStatus = status != null && !status.trim().isEmpty();
                            if (hasPriority && hasName && hasStatus) {
                                list = categoryDAO.searchCategoriesByPriorityNameAndStatus(priority.trim(), keyword.trim(), status.trim());
                            } else if (hasPriority && hasName) {
                                list = categoryDAO.searchCategoriesByPriorityAndName(priority.trim(), keyword.trim());
                            } else if (hasPriority && hasStatus) {
                                list = categoryDAO.searchCategoriesByPriorityAndStatus(priority.trim(), status.trim());
                            } else if (hasName && hasStatus) {
                                list = categoryDAO.searchCategoriesByNameAndStatus(keyword.trim(), status.trim());
                            } else if (hasPriority) {
                                list = categoryDAO.searchCategoriesByPriority(priority.trim());
                            } else if (hasStatus) {
                                list = categoryDAO.searchCategoriesByStatusSorted(status.trim(), sortBy != null && sortBy.endsWith("_desc") ? "desc" : "asc");
                            } else {
                                list = categoryDAO.searchCategoriesByNameSorted(keyword.trim(), sortBy != null && sortBy.endsWith("_desc") ? "desc" : "asc");
                            }
                            totalCategories = list.size();
                            request.setAttribute("categoryName", keyword);
                            request.setAttribute("priority", priority);
                            request.setAttribute("status", status);
                        } else {
                            list = categoryDAO.getAllCategories();
                            totalCategories = list.size();
                        }

                        System.out.println("CategoryServlet - Total categories fetched: " + totalCategories);

                        if (sortBy != null && !sortBy.isEmpty()) {
                            switch (sortBy) {
                                case "name_asc":
                                    Collections.sort(list, Comparator.comparing(Category::getCategory_name));
                                    break;
                                case "name_desc":
                                    Collections.sort(list, Comparator.comparing(Category::getCategory_name).reversed());
                                    break;
                                case "status_asc":
                                    Collections.sort(list, Comparator.comparing(Category::getStatus));
                                    break;
                                case "status_desc":
                                    Collections.sort(list, Comparator.comparing(Category::getStatus).reversed());
                                    break;
                                case "code_asc":
                                    Collections.sort(list, Comparator.comparing(Category::getCode));
                                    break;
                                case "code_desc":
                                    Collections.sort(list, Comparator.comparing(Category::getCode).reversed());
                                    break;
                            }
                            request.setAttribute("sortBy", sortBy);
                        }

                        int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);
                        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

                        int start = (currentPage - 1) * PAGE_SIZE;
                        int end = Math.min(start + PAGE_SIZE, list.size());
                        if (start < list.size()) {
                            list = list.subList(start, end);
                        } else {
                            list = Collections.emptyList();
                        }

                        System.out.println("CategoryServlet - Paginated list size: " + list.size());

                        request.setAttribute("data", list);
                        request.setAttribute("currentPage", currentPage);
                        request.setAttribute("totalPages", totalPages);
                        request.setAttribute("code", code);
                        request.setAttribute("categoryName", keyword);
                        request.setAttribute("priority", priority);
                        request.setAttribute("status", status);
                        request.setAttribute("sortBy", sortBy);
                        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.out.println("CategoryServlet - Error fetching categories: " + e.getMessage());
                        e.printStackTrace();
                        request.setAttribute("error", "Error loading categories: " + e.getMessage());
                        request.setAttribute("data", Collections.emptyList());
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                    }
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
        return "Category Servlet for Category Management with Permission Checks";
    }
}