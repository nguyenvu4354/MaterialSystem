package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.SupplierDAO;
import entity.Material;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import entity.Supplier;

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class AddMaterialServlet extends HttpServlet {
    
    private static final String UPLOAD_DIRECTORY = "material_images";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get categories and suppliers for dropdowns
            CategoryDAO categoryDAO = new CategoryDAO();
            SupplierDAO supplierDAO = new SupplierDAO();
            
            List<Category> categories = categoryDAO.getAllCategories();
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            
            request.setAttribute("categories", categories);
            request.setAttribute("suppliers", suppliers);
            
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get the upload directory path
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

            // Create the upload directory if it doesn't exist
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Get form parameters
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            
            // Handle file upload and image URL
            Part filePart = request.getPart("imageFile");
            String materialsUrl = request.getParameter("materialsUrl");
            
            if (filePart != null && filePart.getSize() > 0) {
                // If file is uploaded, it takes precedence
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + newFileName;
                
                // Save the file
                filePart.write(filePath);
                
                // Set the URL for the database
                materialsUrl = "material_images/" + newFileName;
            } else if (materialsUrl == null || materialsUrl.trim().isEmpty()) {
                // If no file and no URL provided, use default placeholder
                materialsUrl = "https://placehold.co/200x200?text=" + materialCode;
            }
            // If URL is provided and no file uploaded, use the URL as is
            
            // Handle price with better error handling
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
                // Ensure price has at most 2 decimal places
                price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format. Please enter a valid number");
            }
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            
            // Handle optional supplier
            String supplierIdStr = request.getParameter("supplierId");
            Integer supplierId = null;
            if (supplierIdStr != null && !supplierIdStr.trim().isEmpty()) {
                supplierId = Integer.parseInt(supplierIdStr);
            }

            // Create Material object
            Material material = new Material();
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialsUrl(materialsUrl);
            material.setMaterialStatus(materialStatus);
            material.setPrice(price);
            material.setQuantity(quantity);
            material.setCategoryId(categoryId);
            material.setSupplierId(supplierId);
            material.setCreatedAt(LocalDateTime.now());
            material.setUpdatedAt(LocalDateTime.now());
            material.setDisable(false);

            // Save to database
            MaterialDAO materialDAO = new MaterialDAO();
            boolean success = materialDAO.createMaterial(material);

            if (success) {
                response.sendRedirect("dashboardmaterial?success=Material added successfully");
            } else {
                // If failed, get categories and suppliers again and show error
                CategoryDAO categoryDAO = new CategoryDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
                request.setAttribute("error", "Failed to add material");
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                // Get categories and suppliers for form
                CategoryDAO categoryDAO = new CategoryDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        }
    }
} 