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

// Dinh nghia servlet xu ly duong dan /addmaterial
@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // Gioi han kich thuoc file (1MB)
        maxFileSize = 1024 * 1024 * 10, // Kich thuoc toi da cua file (10MB)
        maxRequestSize = 1024 * 1024 * 15 // Kich thuoc toi da cua request (15MB)
)
public class AddMaterialServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "material_images"; // Thu muc luu anh

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lay danh sach danh muc va nha cung cap
            CategoryDAO categoryDAO = new CategoryDAO();
            SupplierDAO supplierDAO = new SupplierDAO();
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("suppliers", supplierDAO.getAllSuppliers());

            // Chuyen huong toi trang AddMaterial.jsp
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
            // Tao thu muc luu anh neu chua ton tai
            String applicationPath = request.getServletContext().getRealPath(""); // luu file up load
            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Lay du lieu tu form
            String materialCode  = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            Part filePart = request.getPart("imageFile");
            String materialsUrl = request.getParameter("materialUrl");
            // Xu ly upload file anh
            if (filePart != null && filePart.getSize() > 0) {
                // Lay ten file goc tu phan upload (vd: image.png)
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // Lay phan duoi mo rong cua file (vd: .png, .jpg)
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));

                // Tao mot ten file moi duy nhat bang UUID (vd: 2a5f6b28-c2d6-4a23-8d7b-3cb914e8cf8e.png)
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                // Tao duong dan day du tren server de luu file (vd: /duong_dan_project/material_images/ten_file_moi.png)
                String filePath = uploadPath + File.separator + newFileName;

                // Ghi file tu part vao duong dan tren server
                filePart.write(filePath);

                // Luu duong dan tuong doi cua anh de su dung trong database hoac giao dien web
                materialsUrl = "material_images/" + newFileName;
            }

            
            // Xu ly gia tien
            String priceStr = request.getParameter("price"); // Lay chuoi gia tien tu form
            BigDecimal price;

             // Kiem tra neu chuoi gia la null hoac trong => nem loi
            if (priceStr == null || priceStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Price is required");
            }

            try {
                // Chuyen chuoi thanh doi tuong BigDecimal de tinh toan chinh xac (tranh sai so nhu float, double)
                price = new BigDecimal(priceStr);

                // Kiem tra neu gia <= 0 thi nem loi
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price must be greater than $0");
                }

                // Lam tron gia den 2 chu so thap phan (vd: 123.456 -> 123.46)
                price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
            } catch (NumberFormatException e) {
                // Neu nguoi dung nhap khong dung dinh dang so (vd: "abc") thi nem loi
                throw new IllegalArgumentException("Invalid price format. Please enter a valid number");
            }

            // Lay cac thong tin con lai
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String supplierIdStr = request.getParameter("supplierId");
            // Xử lý ID nhà cung cấp (supplierId)
            Integer supplierId = (supplierIdStr != null && !supplierIdStr.trim().isEmpty()) ? Integer.parseInt(supplierIdStr) : null;
            int conditionPercentage = Integer.parseInt(request.getParameter("conditionPercentage"));

            // Tao doi tuong vat tu
            Material material = new Material();
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialsUrl(materialsUrl);
            material.setMaterialStatus(materialStatus);
            material.setPrice(price);
            material.setQuantity(quantity);
            material.setCategoryId(categoryId);
            material.setSupplierId(supplierId);
            material.setConditionPercentage(conditionPercentage);
            material.setCreatedAt(LocalDateTime.now());
            material.setUpdatedAt(LocalDateTime.now());
            material.setDisable(false);

            // Luu vao database
            MaterialDAO materialDAO = new MaterialDAO();
            boolean success = materialDAO.createMaterial(material);

            // Neu thanh cong thi chuyen huong ve dashboard
            if (success) {
                response.sendRedirect("dashboardmaterial?success=Material added successfully");
            } else {
                // Neu that bai thi quay ve form va hien loi
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
                // Neu loi thi lay lai du lieu danh muc va nha cung cap
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
