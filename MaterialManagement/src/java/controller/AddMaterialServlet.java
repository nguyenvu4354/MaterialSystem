package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Material;
import entity.Category;
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

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class AddMaterialServlet extends HttpServlet {

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
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền thêm mới vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        CategoryDAO cd = new CategoryDAO();
        UnitDAO ud = new UnitDAO();
        request.setAttribute("categories", cd.getAllCategories());
        request.setAttribute("units", ud.getAllUnits());

        MaterialDAO materialDAO = new MaterialDAO();
        int maxNum = materialDAO.getMaxMaterialNumber();
        String newMaterialCode = "MAT" + (maxNum + 1);
        request.setAttribute("materialCode", newMaterialCode);

        request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
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
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền thêm mới vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            request.setCharacterEncoding("UTF-8");

            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String priceStr = request.getParameter("price");
            String conditionPercentageStr = request.getParameter("conditionPercentage");
            String categoryIdStr = request.getParameter("categoryId");
            String unitIdStr = request.getParameter("unitId");
            String urlInput = request.getParameter("materialsUrl");

            System.out.println("🛠️ [AddMaterialServlet] Form Parameters:");
            System.out.println("materialCode: " + materialCode);
            System.out.println("materialName: " + materialName);
            System.out.println("materialStatus: " + materialStatus);
            System.out.println("price: " + priceStr);
            System.out.println("conditionPercentage: " + conditionPercentageStr);
            System.out.println("categoryId: " + categoryIdStr);
            System.out.println("unitId: " + unitIdStr);
            System.out.println("materialsUrl: " + urlInput);

            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                materialCode, materialName, materialStatus, priceStr, conditionPercentageStr, categoryIdStr, unitIdStr);

            if (!errors.isEmpty()) {
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setPrice(0);
                m.setCategory(new Category());
                m.setUnit(new Unit());

                request.setAttribute("errors", errors);
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("imageFile");
            String relativeFilePath;

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + "/";
                Files.createDirectories(Paths.get(buildUploadPath));
                filePart.write(buildUploadPath + fileName);
                System.out.println("✅ [AddMaterialServlet] Saved image to BUILD folder: " + buildUploadPath + fileName);

                Path projectRoot = Paths.get(buildUploadPath).getParent().getParent().getParent().getParent();
                Path sourceDir = projectRoot.resolve("web").resolve("images").resolve("material");
                Files.createDirectories(sourceDir);
                try {
                    Files.copy(
                            Paths.get(buildUploadPath + fileName),
                            sourceDir.resolve(fileName),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    System.out.println("✅ [AddMaterialServlet] Copied image to SOURCE folder: " + sourceDir.resolve(fileName));
                } catch (IOException e) {
                    System.out.println("❌ [AddMaterialServlet] Failed to copy to source: " + e.getMessage());
                }

                relativeFilePath = fileName;
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                relativeFilePath = urlInput.trim();
                System.out.println("📝 [AddMaterialServlet] Using URL input instead of new image: " + relativeFilePath);
            } else {
                relativeFilePath = "default.jpg";
                System.out.println("📎 [AddMaterialServlet] No image provided, using default.jpg");
            }

            int categoryId = Integer.parseInt(categoryIdStr);
            int unitId = Integer.parseInt(unitIdStr);
            int conditionPercentage = Integer.parseInt(conditionPercentageStr);
            double price = Double.parseDouble(priceStr);

            Material m = new Material();
            m.setMaterialCode(materialCode);
            m.setMaterialName(materialName);
            m.setMaterialsUrl(relativeFilePath);
            m.setMaterialStatus(materialStatus);
            m.setPrice(price);

            Category category = new Category();
            category.setCategory_id(categoryId);
            m.setCategory(category);

            Unit unit = new Unit();
            unit.setId(unitId);
            m.setUnit(unit);

            m.setDisable(false);
            m.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            m.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            System.out.println("📌 [AddMaterialServlet] Final Material Image URL: " + m.getMaterialsUrl());

            MaterialDAO md = new MaterialDAO();
            if (md.isMaterialCodeExists(materialCode)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "Mã vật tư đã tồn tại.");
                request.setAttribute("m", m);
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            System.out.println("🔄 [AddMaterialServlet] Adding material to database...");
            md.addMaterial(m);
            System.out.println("✅ [AddMaterialServlet] Material added successfully, redirecting to dashboardmaterial");
            response.sendRedirect("dashboardmaterial?success=Material added successfully");
        } catch (Exception ex) {
            System.out.println("❌ [AddMaterialServlet] Error: " + ex.getMessage());
            ex.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
            request.setAttribute("categories", new CategoryDAO().getAllCategories());
            request.setAttribute("units", new UnitDAO().getAllUnits());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        }
    }
}