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

// Dinh nghia servlet cho duong dan /editmaterial va ho tro upload file
@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // neu file vuot qua 1MB se luu vao o dia
        maxFileSize = 1024 * 1024 * 10, // kich thuoc toi da cua 1 file: 10MB
        maxRequestSize = 1024 * 1024 * 15 // kich thuoc toi da cua toan bo request: 15MB
)
public class EditMaterialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "material_images"; // thu muc luu file anh

    // Xu ly GET: hien thi form sua vat tu
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id"); // lay id tu URL
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));
                if (materialDetails != null) {
                    // Lay danh sach danh muc va nha cung cap de hien thi
                    CategoryDAO categoryDAO = new CategoryDAO();
                    SupplierDAO supplierDAO = new SupplierDAO();
                    request.setAttribute("details", materialDetails);
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
                    request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
                } else {
                    response.sendRedirect("dashboardmaterial"); // neu khong tim thay vat tu
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

    // Xu ly POST: luu thong tin sau khi sua vat tu
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MaterialDAO materialDAO = new MaterialDAO();
        int materialId = -1;
        try {
            // Tao duong dan luu file anh
            String applicationPath = request.getServletContext().getRealPath("");
// Lay duong dan goc cua web app tren server (VD: C:/project/build/web/)

            String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;
// Noi de luu thu muc chua anh, ghep duong dan goc voi "material_images"

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
// Neu thu muc "material_images" chua ton tai thi tao moi

// Lay du lieu tu form gui len
            materialId = Integer.parseInt(request.getParameter("materialId"));
// Lay id cua vat tu dang chinh sua

            String materialCode = request.getParameter("materialCode");
// Ma vat tu

            String materialName = request.getParameter("materialName");
// Ten vat tu

            String materialStatus = request.getParameter("materialStatus");
// Trang thai (con/dang dung, hong, dang sua,...)

            int conditionPercentage = Integer.parseInt(request.getParameter("conditionPercentage"));
// Phan tram tinh trang su dung (0 - 100)

// Xu ly truong gia tien
            String priceStr = request.getParameter("price"); // Gia dang text
            BigDecimal price;
            if (priceStr == null || priceStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Price is required");
            }
            // Bao loi neu nguoi dung khong nhap gia

            try {
                price = new BigDecimal(priceStr); // Chuyen text sang so BigDecimal

                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price must be greater than $0");
                }
                // Neu <= 0 thi bao loi

                price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
                // Lam tron den 2 so thap phan (VD: 15.456 -> 15.46)
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format. Please enter a valid number");
                // Neu nguoi dung nhap sai format (chu cai, ky tu dac biet,...) thi bao loi
            }

            int quantity = Integer.parseInt(request.getParameter("quantity"));
// So luong vat tu

            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
// ID danh muc cua vat tu

            String supplierIdStr = request.getParameter("supplierId");
// Lay supplier id tu form (co the bo trong)

            Integer supplierId = (supplierIdStr != null && !supplierIdStr.trim().isEmpty())
                    ? Integer.parseInt(supplierIdStr) : null;
// Neu co thi chuyen sang int, khong thi giu null

            Part filePart = request.getPart("imageFile");
// File anh moi (neu nguoi dung upload)

            String materialsUrl = request.getParameter("materialsUrl");
// Duong dan URL cua anh hien tai (neu co)

// Lay thong tin cu cua vat tu bang ID
            MaterialDetails existingMaterial = materialDAO.getMaterialById(materialId);
            String existingUrl = existingMaterial.getMaterial().getMaterialsUrl();
// Lay URL cu cua anh trong DB

// Xu ly upload file anh moi
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Lay ten file goc

                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                // Lay phan duoi file (vi du: ".jpg", ".png")

                String newFileName = UUID.randomUUID().toString() + fileExtension;
                // Tao ten file moi ngau nhien (tranh trung ten file)

                String filePath = uploadPath + File.separator + newFileName;
                // Duong dan day du de luu file tren server

                filePart.write(filePath);
                // Ghi file vao o dia

                materialsUrl = "material_images/" + newFileName;
                // Cap nhat URL cua anh moi vao DB
            } else if (materialsUrl != null && !materialsUrl.trim().isEmpty()) {
                materialsUrl = materialsUrl.trim();
                // Neu khong upload file moi thi giu URL cu dang co tren form
            } else {
                materialsUrl = existingUrl;
                // Neu khong co du lieu URL cu thi giu URL cu tu DB
            }

            // Tao doi tuong Material moi de cap nhat
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
            material.setDisable(existingMaterial.getMaterial().isDisable());

            // Cap nhat vao database
            boolean success = materialDAO.updateMaterial(material);
            if (success) {
                String contextPath = request.getContextPath();
                response.sendRedirect(contextPath + "/dashboardmaterial?success=Material updated successfully");
            } else {
                // Neu cap nhat that bai, hien thi lai form
                CategoryDAO categoryDAO = new CategoryDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
                request.setAttribute("error", "Failed to update material");
                request.setAttribute("details", materialDAO.getMaterialById(materialId));
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                // Neu co loi, tai lai form voi du lieu cu
                CategoryDAO categoryDAO = new CategoryDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("suppliers", supplierDAO.getAllSuppliers());
                request.setAttribute("details", materialDAO.getMaterialById(materialId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        }
    }
}
