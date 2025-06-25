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
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

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

            // Sinh mã code tự động
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
            String realPath = request.getServletContext().getRealPath("/");
            String uploadPath = realPath + UPLOAD_DIRECTORY + File.separator;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

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
                request.setAttribute("errors", errors);
                // Gửi lại dữ liệu đã nhập để giữ lại trên form nếu cần
                Material m = new Material();
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
                // Truyền lại materialCode về JSP để không bị trống
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("imageFile");
            String relativeFilePath = null;
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                filePart.write(uploadPath + newFileName);
                relativeFilePath = UPLOAD_DIRECTORY + "/" + newFileName;
            } else if (request.getParameter("materialsUrl") != null && !request.getParameter("materialsUrl").trim().isEmpty()) {
                relativeFilePath = request.getParameter("materialsUrl").trim();
            } else {
                relativeFilePath = UPLOAD_DIRECTORY + "/default.jpg";
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
                CategoryDAO cd = new CategoryDAO();
                UnitDAO ud = new UnitDAO();
                request.setAttribute("categories", cd.getAllCategories());
                request.setAttribute("units", ud.getAllUnits());
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
