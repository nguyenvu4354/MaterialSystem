package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Category;
import entity.Material;
import entity.Unit;
import entity.User;
import utils.MaterialValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.Map;

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public class EditMaterialServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "images/material";
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "UPDATE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

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

        request.setAttribute("m", material);
        request.setAttribute("categories", new CategoryDAO().getAllCategories());
        request.setAttribute("units", new UnitDAO().getAllUnits());
        request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
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
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "UPDATE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            request.setCharacterEncoding("UTF-8");

            String materialId = request.getParameter("materialId");
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String conditionPercentage = request.getParameter("conditionPercentage");
            String priceStr = request.getParameter("price");
            String categoryId = request.getParameter("categoryId");
            String unitId = request.getParameter("unitId");
            String urlInput = request.getParameter("materialsUrl");

            System.out.println("🛠️ [EditMaterialServlet] Form Parameters:");
            System.out.println("materialId: " + materialId);
            System.out.println("materialCode: " + materialCode);
            System.out.println("materialName: " + materialName);
            System.out.println("materialStatus: " + materialStatus);
            System.out.println("conditionPercentage: " + conditionPercentage);
            System.out.println("price: " + priceStr);
            System.out.println("categoryId: " + categoryId);
            System.out.println("unitId: " + unitId);
            System.out.println("materialsUrl: " + urlInput);

            // Validate form data
            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                    materialCode, materialName, materialStatus, priceStr, conditionPercentage, categoryId, unitId);

            if (!errors.isEmpty()) {
                Material m = new Material();
                m.setMaterialId(Integer.parseInt(materialId));
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setPrice(0);
                m.setConditionPercentage(0);
                m.setCategory(new Category());
                m.setUnit(new Unit());

                request.setAttribute("errors", errors);
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
                return;
            }

            // Handle file upload
            Part filePart = request.getPart("materialImage");
            String imageUrl = null;

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + "/";
                Files.createDirectories(Paths.get(buildUploadPath));
                filePart.write(buildUploadPath + fileName);
                System.out.println("✅ [EditMaterialServlet] Saved image to BUILD folder: " + buildUploadPath + fileName);

                Path projectRoot = Paths.get(buildUploadPath).getParent().getParent().getParent().getParent();
                Path sourceDir = projectRoot.resolve("web").resolve("images").resolve("material");
                Files.createDirectories(sourceDir);
                try {
                    Files.copy(
                            Paths.get(buildUploadPath + fileName),
                            sourceDir.resolve(fileName),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    System.out.println("✅ [EditMaterialServlet] Copied image to SOURCE folder: " + sourceDir.resolve(fileName));
                } catch (IOException e) {
                    System.out.println("❌ [EditMaterialServlet] Failed to copy to source: " + e.getMessage());
                }

                imageUrl = fileName;
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                imageUrl = urlInput.trim();
                System.out.println("📝 [EditMaterialServlet] Using URL input instead of new image: " + imageUrl);
            }

            // Load existing material
            MaterialDAO materialDAO = new MaterialDAO();
            Material oldMaterial = materialDAO.getInformation(Integer.parseInt(materialId));
            if (oldMaterial == null) {
                request.setAttribute("error", "Vật tư không tồn tại.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // Create updated material object
            Material material = new Material();
            material.setMaterialId(Integer.parseInt(materialId));
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialStatus(materialStatus);
            material.setPrice(Double.parseDouble(priceStr));
            material.setConditionPercentage(Integer.parseInt(conditionPercentage));

            Category category = new Category();
            category.setCategory_id(Integer.parseInt(categoryId));
            material.setCategory(category);

            Unit unit = new Unit();
            unit.setId(Integer.parseInt(unitId));
            material.setUnit(unit);

            material.setMaterialsUrl(imageUrl != null ? imageUrl : oldMaterial.getMaterialsUrl());
            material.setDisable(oldMaterial.isDisable());
            material.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            System.out.println("📌 [EditMaterialServlet] Final Material Image URL: " + material.getMaterialsUrl());

            // Update material
            System.out.println("🔄 [EditMaterialServlet] Updating material in database...");
            materialDAO.updateMaterial(material);
            System.out.println("✅ [EditMaterialServlet] Material updated successfully, redirecting to dashboardmaterial");
            response.sendRedirect("dashboardmaterial?success=Material updated successfully");
        } catch (Exception e) {
            System.out.println("❌ [EditMaterialServlet] Error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        }
    }
}