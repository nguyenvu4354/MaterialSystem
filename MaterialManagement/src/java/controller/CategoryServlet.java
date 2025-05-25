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
        CategoryDAO dao = new CategoryDAO();
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
                        // Xử lý cập nhật dữ liệu
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
                    response.sendRedirect("CategoryServlet?service=listCategory");
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
                    List<Category> list = new ArrayList<>();

                    if (categoryIdRaw != null && !categoryIdRaw.trim().isEmpty()) {
                        try {
                            int id = Integer.parseInt(categoryIdRaw);
                            list = dao.searchCategoriesById(id);
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID không hợp lệ.");
                        }
                    } else if (status != null && !status.isEmpty()) {
                        list = dao.searchCategoriesByStatus(status);
                    } else if (keyword != null && !keyword.trim().isEmpty()) {
                        list = dao.searchCategoriesByName(keyword);
                    } else {
                        list = dao.getAllCategories();
                    }

                    request.setAttribute("data", list);
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
