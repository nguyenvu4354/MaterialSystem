package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.SupplierDAO;
import dal.UnitDAO;
import entity.Material;
import entity.Category;
import entity.Supplier;
import entity.Unit;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AddMaterialServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "material_images";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CategoryDAO cd = new CategoryDAO();
            SupplierDAO sd = new SupplierDAO();
            UnitDAO ud = new UnitDAO();

            request.setAttribute("categories", cd.getAllCategories());
            request.setAttribute("suppliers", sd.getAllSuppliers());
            request.setAttribute("units", ud.getAllUnits());

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
            // Tạo thư mục upload nếu chưa có
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lấy dữ liệu từ form
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");

            Part filePart = request.getPart("imageFile");
            String materialsUrl = null;
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + newFileName;
                filePart.write(filePath);
                materialsUrl = UPLOAD_DIRECTORY + "/" + newFileName;
            } else if (request.getParameter("materialsUrl") != null && !request.getParameter("materialsUrl").trim().isEmpty()) {
                materialsUrl = request.getParameter("materialsUrl").trim();
            } else {
                materialsUrl = "material_images/default.jpg";
            }

            // Xử lý giá
            String priceStr = request.getParameter("price");
            BigDecimal price;
            if (priceStr == null || priceStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Price is required.");
            }
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price must be greater than $0.");
                }
                price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format.");
            }

            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String supplierIdStr = request.getParameter("supplierId");
            if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn nhà cung cấp!");
                doGet(request, response);
                return;
            }
            Integer supplierId = Integer.parseInt(supplierIdStr);
            int unitId = Integer.parseInt(request.getParameter("unitId"));
            int conditionPercentage = Integer.parseInt(request.getParameter("conditionPercentage"));

            // Tạo đối tượng Material
            Material material = new Material();
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialStatus(materialStatus);
            material.setConditionPercentage(conditionPercentage);
            material.setPrice(price.doubleValue());
            material.setMaterialsUrl(materialsUrl);

            Category category = new Category();
            category.setCategory_id(categoryId);
            material.setCategory(category);

            
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            material.setSupplier(supplier);
          

            Unit unit = new Unit();
            unit.setId(unitId);
            material.setUnit(unit);

            MaterialDAO md = new MaterialDAO();
            md.addMaterial(material);
            response.sendRedirect("dashboardmaterial?success=Material added successfully");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        }
    }
}
