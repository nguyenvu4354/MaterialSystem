package controller;

import dal.CategoryDAO;
import entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/CategoryServlet"})
public class CategoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        CategoryDAO dao = new CategoryDAO(); // Khai báo một lần duy nhất

        String service = request.getParameter("service");
        if (service == null) service = "listCategory";

        try {
            switch (service) {

                case "updateCategory": {
                    String submit = request.getParameter("submit");

                    if (submit == null) {
                        int categoryID = Integer.parseInt(request.getParameter("categoryID"));
                        Category c = dao.getCategoryById(categoryID);
                        if (c == null) {
                            request.setAttribute("error", "Danh mục không tồn tại.");
                            request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                            return;
                        }
                        request.setAttribute("c", c);
                        request.setAttribute("categories", dao.getAllCategories());
                        request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                    } else {
                        try {
                            int id = Integer.parseInt(request.getParameter("categoryID"));
                            String name = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            String status = request.getParameter("status");
                            int priority = Integer.parseInt(request.getParameter("priority"));
                            String createdAtRaw = request.getParameter("createdAt");
                            String parentIDRaw = request.getParameter("parentID");

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = Timestamp.valueOf(outputFormat.format(date));
                            } catch (Exception e) {
                                request.setAttribute("error", "Định dạng ngày giờ không hợp lệ.");
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                            }

                            Category category = new Category(id, name, parentId, createdAt, disable, status, description, priority);
                            boolean updated = dao.updateCategory(category);
                            if (updated) {
                                response.sendRedirect("jsp/Category.jsp");
                            } else {
                                request.setAttribute("error", "Update failure.");
                                request.setAttribute("c", category);
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Lỗi cập nhật: " + e.getMessage());
                            request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "deleteCategory": {
                    int categoryID = Integer.parseInt(request.getParameter("categoryID"));
                    dao.deleteCategory(categoryID);
                    response.sendRedirect("jsp/Category.jsp");
                    break;
                }

                case "addCategory": {
                    String submit = request.getParameter("submit");

                    if (submit == null) {
                        request.setAttribute("categories", dao.getAllCategories());
                        request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                    } else {
                        try {
                            String category_name = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            int disable = Integer.parseInt(request.getParameter("disable"));
                            int priority = Integer.parseInt(request.getParameter("priority"));

                            String createdAtRaw = request.getParameter("createdAt");
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = inputFormat.parse(createdAtRaw);
                            Timestamp created_at = Timestamp.valueOf(outputFormat.format(date));

                            String parentIDRaw = request.getParameter("parentID");
                            Integer parent_id = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parent_id = Integer.valueOf(parentIDRaw);
                                if (dao.getCategoryById(parent_id) == null) {
                                    request.setAttribute("error", "Danh mục cha không tồn tại.");
                                    request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category c = new Category(0, category_name, parent_id, created_at, disable, status, description, priority);
                            boolean inserted = dao.insertCategory(c);

                            if (inserted) {
                                response.sendRedirect("jsp/Category.jsp");
                            } else {
                                request.setAttribute("error", "Không thể thêm danh mục.");
                                request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                            }

                        } catch (Exception e) {
                            request.setAttribute("error", "Lỗi thêm danh mục: " + e.getMessage());
                            request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "listCategory": {
                    String keyword = request.getParameter("categoryName");
                    String categoryIdRaw = request.getParameter("categoryId");
                    String status = request.getParameter("status");
                    String pageRaw = request.getParameter("page");
                    String sortBy = request.getParameter("sortBy"); // Tham số sortBy: name_asc, name_desc, status_asc, status_desc
                    List<Category> list = new ArrayList<>();

                    final int PAGE_SIZE = 5;
                    int currentPage = 1;

                    if (pageRaw != null && !pageRaw.trim().isEmpty()) {
                        try {
                            currentPage = Integer.parseInt(pageRaw);
                            if (currentPage < 1) currentPage = 1;
                        } catch (NumberFormatException e) {
                            currentPage = 1;
                        }
                    }

                    boolean hasId = categoryIdRaw != null && !categoryIdRaw.trim().isEmpty();
                    boolean hasName = keyword != null && !keyword.trim().isEmpty();
                    boolean hasStatus = status != null && !status.trim().isEmpty();

                    // Xử lý sắp xếp
                    if (hasId && hasName && hasStatus) {
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw.trim());
                            list = dao.searchCategoriesByIdNameAndStatus(categoryId, keyword.trim(), status.trim());
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID không hợp lệ.");
                            list = new ArrayList<>();
                        }
                    } else if (hasId && hasName) {
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw.trim());
                            list = dao.searchCategoriesByIdAndName(categoryId, keyword.trim());
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID không hợp lệ.");
                            list = new ArrayList<>();
                        }
                    } else if (hasId && hasStatus) {
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw.trim());
                            list = dao.searchCategoriesByIdAndStatus(categoryId, status.trim());
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID không hợp lệ.");
                            list = new ArrayList<>();
                        }
                    } else if (hasName && hasStatus) {
                        list = dao.searchCategoriesByNameAndStatus(keyword.trim(), status.trim());
                    } else if (hasId) {
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw.trim());
                            list = dao.searchCategoriesById(categoryId);
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID không hợp lệ.");
                            list = new ArrayList<>();
                        }
                    } else if (hasStatus) {
                        list = dao.searchCategoriesByStatusSorted(status.trim(), sortBy != null && sortBy.endsWith("_desc") ? "desc" : "asc");
                    } else if (hasName) {
                        list = dao.searchCategoriesByNameSorted(keyword.trim(), sortBy != null && sortBy.endsWith("_desc") ? "desc" : "asc");
                    } else {
                        if ("name_asc".equalsIgnoreCase(sortBy)) {
                            list = dao.getAllCategoriesSortedByName("asc");
                        } else if ("name_desc".equalsIgnoreCase(sortBy)) {
                            list = dao.getAllCategoriesSortedByName("desc");
                        } else if ("status_asc".equalsIgnoreCase(sortBy)) {
                            list = dao.getAllCategoriesSortedByStatus("asc");
                        } else if ("status_desc".equalsIgnoreCase(sortBy)) {
                            list = dao.getAllCategoriesSortedByStatus("desc");
                        } else {
                            list = dao.getAllCategories();
                        }
                    }

                    int totalCategories = list.size();
                    int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);
                    if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int end = Math.min(start + PAGE_SIZE, totalCategories);
                    if (start < totalCategories) {
                        list = list.subList(start, end);
                    } else {
                        list = new ArrayList<>();
                    }

                    request.setAttribute("data", list);
                    request.setAttribute("currentPage", currentPage);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("categoryName", keyword);
                    request.setAttribute("categoryId", categoryIdRaw);
                    request.setAttribute("status", status);
                    request.setAttribute("sortBy", sortBy);
                    request.setAttribute("pageTitle", "Category Manager");
                    request.setAttribute("tableTitle", "List of Categories");
                    request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                    break;
                }

                default:
                    response.getWriter().println("❌ Service not supported.");
            }

        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý request: " + e.getMessage(), e);
        }
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