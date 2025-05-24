package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.MaterialDetails;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import entity.Category;

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
public class EditMaterialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                CategoryDAO categoryDAO = new CategoryDAO();

                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));
                List<Category> categories = categoryDAO.getAllCategories();

                if (materialDetails != null) {
                    // Get suppliers
                    SupplierDAO supplierDAO = new SupplierDAO();

                    request.setAttribute("details", materialDetails);
                    request.setAttribute("categories", categories);
                    request.setAttribute("suppliers", supplierDAO.getAllSuppliers());

                    request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
                } else {
                    response.sendRedirect("dashboardmaterial");
                }
            } else {
                response.sendRedirect("dashboardmaterial");
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy tham số paramater
            int materialId = Integer.parseInt(request.getParameter("materialId"));
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialsUrl = request.getParameter("materialsUrl");

            // Xử lý file ảnh
            if (materialsUrl == null || materialsUrl.trim().isEmpty()) {
                materialsUrl = "https://placehold.co/200x200?text=" + materialCode;
            }

            String materialStatus = request.getParameter("materialStatus");
            int conditionPercentage = Integer.parseInt(request.getParameter("conditionPercentage"));
            String priceStr = request.getParameter("price");
            BigDecimal price;
            if (priceStr == null || priceStr.trim().isEmpty()) {
                price = BigDecimal.ZERO; // hoặc throw lỗi tùy logic bạn muốn
            } else {
                price = new BigDecimal(priceStr);
            }
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String supplierIdStr = request.getParameter("supplierId");
            Integer supplierId = supplierIdStr != null && !supplierIdStr.trim().isEmpty()
                    ? Integer.parseInt(supplierIdStr)
                    : null;

            // Xử lý disable parameter
            String disableStr = request.getParameter("disable");
            boolean disable = disableStr != null && disableStr.equals("true");

            // Tạo object
            Material material = new Material();
            material.setMaterialId(materialId);
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialsUrl(materialsUrl);
            material.setMaterialStatus(materialStatus);
            material.setConditionPercentage(conditionPercentage);
            material.setPrice(price);
            material.setQuantity(quantity);
            material.setCategoryId(categoryId);
            material.setSupplierId(supplierId);
            material.setDisable(disable);

            // Cập nhật material
            MaterialDAO materialDAO = new MaterialDAO();
            boolean success = materialDAO.updateMaterial(material);

            if (success) {
                // nếu thành công
                response.sendRedirect("dashboardmaterial?success=Material updated successfully");
            } else {
                // nếu thất bại
                request.setAttribute("error", "Failed to update material");
                MaterialDetails materialDetails = materialDAO.getMaterialById(materialId);
                request.setAttribute("details", materialDetails);
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            String errorMessage = "Error occurred: " + ex.getMessage();
            request.setAttribute("error", errorMessage);

            try {
                // lấy thông tin
                int materialId = Integer.parseInt(request.getParameter("materialId"));
                MaterialDAO materialDAO = new MaterialDAO();
                MaterialDetails materialDetails = materialDAO.getMaterialById(materialId);
                request.setAttribute("details", materialDetails);
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        }
    }
}
