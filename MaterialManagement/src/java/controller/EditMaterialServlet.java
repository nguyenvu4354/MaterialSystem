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

            String materialId = request.getParameter("materialId");
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String conditionPercentage = request.getParameter("conditionPercentage");
            String price = request.getParameter("price");
            String categoryId = request.getParameter("categoryId");
            String unitId = request.getParameter("unitId");

            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                materialCode, materialName, materialStatus, price, conditionPercentage, categoryId, unitId);
            
            if (materialId == null || materialId.trim().isEmpty()) {
                errors.put("materialId", "Material ID cannot be empty.");
            } else {
                try {
                    int id = Integer.parseInt(materialId);
                    if (id <= 0) {
                        errors.put("materialId", "Invalid Material ID.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("materialId", "Invalid Material ID.");
                }
            }

            if (!errors.isEmpty()) {
                String firstError = errors.values().iterator().next();
                request.setAttribute("error", firstError);
                doGet(request, response);
                return;
            }

            String imageUrl = null;
            Part filePart = request.getPart("materialImage");
            String urlInput = request.getParameter("materialsUrl");

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
                String uploadPath = getServletContext().getRealPath("/material_images");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                filePart.write(uploadPath + File.separator + fileName);
                imageUrl = "material_images/" + fileName;
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                imageUrl = urlInput.trim();
            }

            int materialIdInt = Integer.parseInt(materialId);
            int conditionPercentageInt = Integer.parseInt(conditionPercentage);
            double priceDouble = Double.parseDouble(price);
            int categoryIdInt = Integer.parseInt(categoryId);
            int unitIdInt = Integer.parseInt(unitId);

            Material material = new Material();
            material.setMaterialId(materialIdInt);
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialsUrl(imageUrl);
            material.setMaterialStatus(materialStatus);
            material.setConditionPercentage(conditionPercentageInt);
            material.setPrice(priceDouble);
            
            Category category = new Category();
            category.setCategory_id(categoryIdInt);
            material.setCategory(category);
            
            Unit unit = new Unit();
            unit.setId(unitIdInt);
            material.setUnit(unit);
            
            MaterialDAO materialDAO = new MaterialDAO();
            Material oldMaterial = materialDAO.getInformation(material.getMaterialId());
            
            if (imageUrl != null) {
                material.setMaterialsUrl(imageUrl);
            } else if (oldMaterial != null && oldMaterial.getMaterialsUrl() != null) {
                material.setMaterialsUrl(oldMaterial.getMaterialsUrl());
            }

            material.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            material.setDisable(oldMaterial.isDisable());

            materialDAO.updateMaterial(material);

            response.sendRedirect("dashboardmaterial?success=Material updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            doGet(request, response);
        }
    }
}
