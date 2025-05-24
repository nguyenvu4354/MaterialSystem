package controller;

import dal.MaterialDAO;
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

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
public class EditMaterialServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));
                
                if (materialDetails != null) {
                    request.setAttribute("details", materialDetails);
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
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String supplierIdStr = request.getParameter("supplierId");
            Integer supplierId = supplierIdStr != null && !supplierIdStr.trim().isEmpty() 
                              ? Integer.parseInt(supplierIdStr) 
                              : null;

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