package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.UnitDAO;
import entity.Material;
import entity.Category;
import entity.Unit;
import entity.User;
import utils.MaterialValidator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class AddMaterialServlet extends HttpServlet {

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
            request.setAttribute("error", "You do not have permission to access this page. Only Admins can add materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            CategoryDAO cd = new CategoryDAO();
            UnitDAO ud = new UnitDAO();
            request.setAttribute("categories", cd.getAllCategories());
            request.setAttribute("units", ud.getAllUnits());

            String code = "MTL-" + new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date()) + "-" + (int)(Math.random()*900+100);
            request.setAttribute("materialCode", code);

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to perform this action. Only Admins can add materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String priceStr = request.getParameter("price");
            String conditionPercentageStr = request.getParameter("conditionPercentage");
            String categoryIdStr = request.getParameter("categoryId");
            String unitIdStr = request.getParameter("unitId");

            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                materialCode, materialName, materialStatus, priceStr, conditionPercentageStr, categoryIdStr, unitIdStr);

            if (!errors.isEmpty()) {
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setConditionPercentage(0);
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

                // Lưu vào thư mục web (như ProfileServlet)
                String buildPath = getServletContext().getRealPath("/"); 
                Path projectRoot = Paths.get(buildPath).getParent().getParent(); 
                Path uploadDir = projectRoot.resolve("web").resolve("images").resolve("material");

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path savePath = uploadDir.resolve(fileName);
                filePart.write(savePath.toString());
                relativeFilePath = fileName;
            } else if (request.getParameter("materialsUrl") != null && !request.getParameter("materialsUrl").trim().isEmpty()) {
                relativeFilePath = request.getParameter("materialsUrl").trim();
            } else {
                relativeFilePath = "default.jpg";
            }

            BigDecimal priceBD = new BigDecimal(priceStr).setScale(2, BigDecimal.ROUND_HALF_UP);
            int categoryId = Integer.parseInt(categoryIdStr);
            int unitId = Integer.parseInt(unitIdStr);
            int conditionPercentage = Integer.parseInt(conditionPercentageStr);

            Material m = new Material();
            m.setMaterialCode(materialCode);
            m.setMaterialName(materialName);
            m.setMaterialsUrl(relativeFilePath);
            m.setMaterialStatus(materialStatus);
            m.setConditionPercentage(conditionPercentage);
            m.setPrice(priceBD.doubleValue());

            Category category = new Category();
            category.setCategory_id(categoryId);
            m.setCategory(category);

            Unit unit = new Unit();
            unit.setId(unitId);
            m.setUnit(unit);

            m.setDisable(false);
            m.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            m.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            MaterialDAO md = new MaterialDAO();
            if (md.isMaterialCodeExists(materialCode)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "Material code already exists, please enter a different code!");
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            md.addMaterial(m);
            response.sendRedirect("dashboardmaterial?success=Material added successfully");

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        }
    }
}