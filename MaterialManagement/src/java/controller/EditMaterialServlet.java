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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import entity.Category;

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class EditMaterialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "material_images";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                CategoryDAO categoryDAO = new CategoryDAO();
                SupplierDAO supplierDAO = new SupplierDAO();

                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));
                List<Category> categories = categoryDAO.getAllCategories();

                if (materialDetails != null) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("doPost của EditMaterialServlet được gọi");
        MaterialDAO materialDAO = new MaterialDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        int materialId = -1;

        try {
            // up load file
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

           
            materialId = Integer.parseInt(request.getParameter("materialId"));
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            int conditionPercentage = Integer.parseInt(request.getParameter("conditionPercentage"));

            // Xử lý tiền
            BigDecimal price;
            try {
                String priceStr = request.getParameter("price");
                if (priceStr == null || priceStr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Price is required");
                }
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price must be greater than $0");
                }
                
                price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format. Please enter a valid number");
            }

            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));

            String supplierIdStr = request.getParameter("supplierId");
            Integer supplierId = supplierIdStr != null && !supplierIdStr.trim().isEmpty()
                    ? Integer.parseInt(supplierIdStr)
                    : null;

            String disableStr = request.getParameter("disable");
            boolean disable = disableStr != null && disableStr.equals("true");

           
            Part filePart = request.getPart("imageFile");
            String materialsUrl = request.getParameter("materialsUrl");

            
            MaterialDetails existingMaterial = materialDAO.getMaterialById(materialId);
            String existingUrl = existingMaterial.getMaterial().getMaterialsUrl();

            // Kiểm tra file up load
            if (filePart != null && filePart.getSize() > 0) {
                // Process file upload
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + newFileName;

               
                filePart.write(filePath);

                // tiền tố
                materialsUrl = "material_images/" + newFileName;
            } 
            else if (materialsUrl != null && !materialsUrl.trim().isEmpty()) {
                
                materialsUrl = materialsUrl.trim();
            } 
            else {
                materialsUrl = existingUrl;
            }

            
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

            boolean success = materialDAO.updateMaterial(material);

            if (success) {
                
                String contextPath = request.getContextPath();
               

                response.sendRedirect(contextPath + "/dashboardmaterial?success=Material updated successfully");
                return;
            } else {
                request.setAttribute("error", "Failed to update material");
                request.setAttribute("details", materialDAO.getMaterialById(materialId));
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                request.setAttribute("error", "Error occurred: " + ex.getMessage());
                request.setAttribute("details", materialDAO.getMaterialById(materialId));
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        }
    }
}
