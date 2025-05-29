package controller;

import dal.CategoryDAO;
import entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
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
                        // Hiển thị form cập nhật
                        String categoryIdRaw = request.getParameter("categoryID");
                        if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                            request.setAttribute("error", "Thiếu ID danh mục.");
                            request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
                            return;
                        }
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw);
                            Category category = dao.getCategoryById(categoryId);
                            if (category == null) {
                                request.setAttribute("error", "Không tìm thấy danh mục với ID: " + categoryId);
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                return;
                            }
                            request.setAttribute("c", category);
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "ID danh mục không hợp lệ.");
                            request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
                        }
                    } else {
                        // Xử lý cập nhật danh mục
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
                                request.setAttribute("error", "Tên danh mục và mã không được để trống.");
                                request.setAttribute("c", dao.getCategoryById(categoryId));
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            // Kiểm tra trùng lặp code (trừ code của chính danh mục đang cập nhật)
                            List<Category> existingCategories = dao.searchCategoriesByCode(code.trim());
                            for (Category existing : existingCategories) {
                                if (existing.getCategory_id() != categoryId && existing.getCode().equalsIgnoreCase(code.trim())) {
                                    request.setAttribute("error", "Mã danh mục '" + code + "' đã tồn tại. Vui lòng chọn mã khác.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Định dạng ngày giờ không hợp lệ.");
                                request.setAttribute("c", dao.getCategoryById(categoryId));
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (dao.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Danh mục cha không tồn tại.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                                // Kiểm tra vòng lặp danh mục cha
                                if (parentId == categoryId) {
                                    request.setAttribute("error", "Danh mục không thể là danh mục cha của chính nó.");
                                    request.setAttribute("c", dao.getCategoryById(categoryId));
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(categoryId, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            boolean updated = dao.updateCategory(category);
                            if (updated) {
                                System.out.println("CategoryServlet - Category updated successfully: " + categoryName);
                                response.sendRedirect("jsp/Category.jsp");
                            } else {
                                String daoError = dao.getLastError() != null ? dao.getLastError() : "Không có lỗi chi tiết từ CategoryDAO.";
                                request.setAttribute("error", "Không thể cập nhật danh mục. Chi tiết lỗi: " + daoError);
                                request.setAttribute("c", category);
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Lỗi khi cập nhật danh mục: " + e.getMessage());
                            request.setAttribute("c", dao.getCategoryById(Integer.parseInt(request.getParameter("categoryID"))));
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("jsp/UpdateCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "deleteCategory": {
                    String categoryIdRaw = request.getParameter("categoryID");
                    if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                        request.setAttribute("error", "Thiếu ID danh mục.");
                        request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
                        return;
                    }
                    try {
                        int categoryId = Integer.parseInt(categoryIdRaw);
                        Category category = dao.getCategoryById(categoryId);
                        if (category == null) {
                            request.setAttribute("error", "Không tìm thấy danh mục với ID: " + categoryId);
                            request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                            return;
                        }
                        // Kiểm tra danh mục có danh mục con hoặc sản phẩm liên quan không
                        List<Category> childCategories = dao.getAllCategories().stream()
                                .filter(c -> c.getParent_id() != null && c.getParent_id() == categoryId)
                                .toList();
                        if (!childCategories.isEmpty()) {
                            request.setAttribute("error", "Không thể xóa danh mục vì có danh mục con phụ thuộc.");
                            request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                            return;
                        }
                        // Giả sử không có sản phẩm liên quan (cần kiểm tra thêm nếu có bảng Products)
                        boolean deleted = dao.deleteCategory(categoryId);
                        if (deleted) {
                            System.out.println("CategoryServlet - Category deleted successfully: ID = " + categoryId);
                            response.sendRedirect("jsp/Category.jsp");
                        } else {
                            String daoError = dao.getLastError() != null ? dao.getLastError() : "Không có lỗi chi tiết từ CategoryDAO.";
                            request.setAttribute("error", "Không thể xóa danh mục. Chi tiết lỗi: " + daoError);
                            request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("error", "ID danh mục không hợp lệ.");
                        request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
                    } catch (Exception e) {
                        request.setAttribute("error", "Lỗi khi xóa danh mục: " + e.getMessage());
                        request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
                    }
                    break;
                }

                case "addCategory": {
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        request.setAttribute("categories", dao.getAllCategories());
                        request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
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
                                request.setAttribute("error", "Tên danh mục và mã không được để trống.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            List<Category> existingCategories = dao.searchCategoriesByCode(code.trim());
                            if (!existingCategories.isEmpty()) {
                                request.setAttribute("error", "Mã danh mục '" + code + "' đã tồn tại. Vui lòng chọn mã khác.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Timestamp createdAt;
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = inputFormat.parse(createdAtRaw);
                                createdAt = new Timestamp(date.getTime());
                            } catch (Exception e) {
                                request.setAttribute("error", "Định dạng ngày giờ không hợp lệ.");
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                parentId = Integer.valueOf(parentIDRaw);
                                if (dao.getCategoryById(parentId) == null) {
                                    request.setAttribute("error", "Danh mục cha không tồn tại.");
                                    request.setAttribute("categories", dao.getAllCategories());
                                    request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                                    return;
                                }
                            }

                            Category category = new Category(0, categoryName, parentId, createdAt, disable, status, description, priority, code);
                            System.out.println("CategoryServlet - Attempting to insert category: " + categoryName);
                            boolean inserted = dao.insertCategory(category);
                            if (inserted) {
                                System.out.println("CategoryServlet - Category inserted successfully: " + categoryName);
                                response.sendRedirect("jsp/Category.jsp");
                            } else {
                                String daoError = dao.getLastError() != null ? dao.getLastError() : "Không có lỗi chi tiết từ CategoryDAO.";
                                request.setAttribute("error", "Không thể thêm danh mục. Chi tiết lỗi: " + daoError);
                                request.setAttribute("categories", dao.getAllCategories());
                                request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Lỗi khi thêm danh mục: " + e.getMessage());
                            request.setAttribute("categories", dao.getAllCategories());
                            request.getRequestDispatcher("jsp/InsertCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "listCategory": {
                    List<Category> categories = dao.getAllCategories();
                    request.setAttribute("data", categories);
                    request.getRequestDispatcher("jsp/Category.jsp").forward(request, response);
                    break;
                }

                default:
                    request.setAttribute("error", "Dịch vụ không được hỗ trợ: " + service);
                    request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("CategoryServlet - Lỗi xử lý request: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Lỗi xử lý request: " + e.getMessage());
            request.getRequestDispatcher("jsp/error.jsp").forward(request, response);
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