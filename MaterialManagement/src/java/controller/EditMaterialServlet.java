package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.UnitDAO;
import entity.Category;
import entity.Material;
import entity.Unit;
import entity.User;
import utils.MaterialValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public class EditMaterialServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "images/material";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to access this page. Only Admins can edit materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            String materialId = request.getParameter("materialId");
            if (materialId == null || materialId.trim().isEmpty()) {
                response.sendRedirect("dashboardmaterial");
                return;
            }

            MaterialDAO materialDAO = new MaterialDAO();
            Material material = materialDAO.getInformation(Integer.parseInt(materialId));
            
            if (material == null) {
                response.sendRedirect("dashboardmaterial");
                return;
            }

            CategoryDAO categoryDAO = new CategoryDAO();
            UnitDAO unitDAO = new UnitDAO();
            
            List<Category> categories = categoryDAO.getAllCategories();
            List<Unit> units = unitDAO.getAllUnits();

            request.setAttribute("m", material);
            request.setAttribute("categories", categories);
            request.setAttribute("units", units);
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboardmaterial");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        User user = (User) session.getAttribute("user");

        if (user.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to perform this action. Only Admins can edit materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            
            String realPath = request.getServletContext().getRealPath("/");
            String uploadPath = realPath + UPLOAD_DIRECTORY + File.separator;

            System.out.println("Debug - Real path: " + realPath);
            System.out.println("Debug - Upload path: " + uploadPath);

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println("Debug - Created upload directory: " + uploadPath);
            } else {
                System.out.println("Debug - Upload directory already exists: " + uploadPath);
            }

            String materialId = request.getParameter("materialId");
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String conditionPercentage = request.getParameter("conditionPercentage");
            String priceStr = request.getParameter("price");
            String categoryId = request.getParameter("categoryId");
            String unitId = request.getParameter("unitId");

            System.out.println("Debug - Form parameters:");
            System.out.println("  materialId: " + materialId);
            System.out.println("  materialCode: " + materialCode);
            System.out.println("  materialName: " + materialName);
            System.out.println("  materialStatus: " + materialStatus);
            System.out.println("  conditionPercentage: " + conditionPercentage);
            System.out.println("  price: " + priceStr);
            System.out.println("  categoryId: " + categoryId);
            System.out.println("  unitId: " + unitId);

            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                materialCode, materialName, materialStatus, priceStr, conditionPercentage, categoryId, unitId);
            System.out.println(errors);
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                Material m = new Material();
                try { m.setMaterialId(Integer.parseInt(materialId)); } catch (Exception ex) { m.setMaterialId(0); }
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setConditionPercentage(0);
                m.setPrice(0);
                Category category = new Category();
                category.setCategory_id(0);
                m.setCategory(category);
                Unit unit = new Unit();
                unit.setId(0);
                m.setUnit(unit);
                request.setAttribute("m", m);
                CategoryDAO categoryDAO = new CategoryDAO();
                UnitDAO unitDAO = new UnitDAO();
                request.setAttribute("categories", categoryDAO.getAllCategories());
                request.setAttribute("units", unitDAO.getAllUnits());
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
                return;
            }

            String imageUrl = null;
            Part filePart = request.getPart("materialImage");
            String urlInput = request.getParameter("materialsUrl");

            System.out.println("Debug - File part: " + (filePart != null ? "exists" : "null"));
            System.out.println("Debug - File size: " + (filePart != null ? filePart.getSize() : "N/A"));
            System.out.println("Debug - URL input: " + urlInput);

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                filePart.write(uploadPath + File.separator + fileName);
                imageUrl = fileName;
                System.out.println("Debug - Image URL set to: " + imageUrl);
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                imageUrl = urlInput.trim();
                System.out.println("Debug - Using URL input: " + imageUrl);
            } else {
                imageUrl = "default.jpg";
                System.out.println("Debug - No new image provided, using default.jpg");
            }

            int materialIdInt = Integer.parseInt(materialId);
            int conditionPercentageInt = Integer.parseInt(conditionPercentage);
            int categoryIdInt = Integer.parseInt(categoryId);
            int unitIdInt = Integer.parseInt(unitId);

            Material material = new Material();
            material.setMaterialId(materialIdInt);
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialStatus(materialStatus);
            material.setConditionPercentage(conditionPercentageInt);
            material.setPrice(Double.parseDouble(priceStr));
            
            Category category = new Category();
            category.setCategory_id(categoryIdInt);
            material.setCategory(category);
            
            Unit unit = new Unit();
            unit.setId(unitIdInt);
            material.setUnit(unit);
            
            MaterialDAO materialDAO = new MaterialDAO();
            Material oldMaterial = materialDAO.getInformation(material.getMaterialId());
            
            // Xử lý URL ảnh: ưu tiên ảnh mới upload, sau đó là URL nhập tay, cuối cùng giữ ảnh cũ
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                material.setMaterialsUrl(imageUrl);
                System.out.println("Debug - Final image URL set to: " + imageUrl);
            } else if (oldMaterial != null && oldMaterial.getMaterialsUrl() != null) {
                material.setMaterialsUrl(oldMaterial.getMaterialsUrl());
                System.out.println("Debug - Keeping old image URL: " + oldMaterial.getMaterialsUrl());
            } else {
                System.out.println("Debug - No image URL available");
            }

            material.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            material.setDisable(oldMaterial.isDisable());

            System.out.println("Debug - About to update material with ID: " + material.getMaterialId());
            System.out.println("Debug - Material image URL: " + material.getMaterialsUrl());
            materialDAO.updateMaterial(material);
            
            response.sendRedirect("dashboardmaterial?success=Material updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            doGet(request, response);
        }
    }
}
