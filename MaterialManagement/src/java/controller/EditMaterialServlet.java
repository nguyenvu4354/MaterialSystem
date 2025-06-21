package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.UnitDAO;
import entity.Category;
import entity.Material;
import entity.Unit;
import entity.User;
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

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class EditMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra session và quyền truy cập
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Kiểm tra quyền truy cập - chỉ cho phép Admin (role_id = 1)
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "Bạn không có quyền truy cập trang này. Chỉ Admin mới có thể chỉnh sửa vật tư.");
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

            // Lấy danh sách categories và units
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
        // Kiểm tra session và quyền truy cập
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Kiểm tra quyền truy cập - chỉ cho phép Admin (role_id = 1)
        if (user.getRoleId() != 1) {
            request.setAttribute("error", "Bạn không có quyền thực hiện hành động này. Chỉ Admin mới có thể chỉnh sửa vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");

            // Lấy thông tin từ form
            String materialId = request.getParameter("materialId");
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String conditionPercentage = request.getParameter("conditionPercentage");
            String price = request.getParameter("price");
            String categoryId = request.getParameter("categoryId");
            String unitId = request.getParameter("unitId");

            // Validate dữ liệu
            if (materialId == null || materialCode == null || materialName == null || 
                materialStatus == null || conditionPercentage == null || price == null || 
                categoryId == null || unitId == null || categoryId.trim().isEmpty() || unitId.trim().isEmpty()) {
                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc");
                doGet(request, response);
                return;
            }

            // Xử lý upload ảnh
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

            // Tạo đối tượng Material
            Material material = new Material();
            material.setMaterialId(Integer.parseInt(materialId));
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setMaterialsUrl(imageUrl);
            material.setMaterialStatus(materialStatus);
            material.setConditionPercentage(Integer.parseInt(conditionPercentage));
            material.setPrice(Double.parseDouble(price));
            
            // Set category
            Category category = new Category();
            category.setCategory_id(Integer.parseInt(categoryId));
            material.setCategory(category);
            
            // Set unit
            Unit unit = new Unit();
            unit.setId(Integer.parseInt(unitId));
            material.setUnit(unit);
            
            // Xử lý URL ảnh
            MaterialDAO materialDAO = new MaterialDAO();
            Material oldMaterial = materialDAO.getInformation(material.getMaterialId());
            
            if (imageUrl != null) {
                material.setMaterialsUrl(imageUrl);
            } else if (oldMaterial != null && oldMaterial.getMaterialsUrl() != null) {
                material.setMaterialsUrl(oldMaterial.getMaterialsUrl());
            }

            material.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            material.setDisable(oldMaterial.isDisable());

            // In log các giá trị nhận được từ request
            System.out.println("materialId: " + materialId);
            System.out.println("materialCode: " + materialCode);
            System.out.println("materialName: " + materialName);
            System.out.println("materialStatus: " + materialStatus);
            System.out.println("conditionPercentage: " + conditionPercentage);
            System.out.println("price: " + price);
            System.out.println("categoryId: " + categoryId);
            System.out.println("unitId: " + unitId);

            // In log đối tượng Material trước khi update
            System.out.println("Material update: materialId=" + material.getMaterialId()
                + ", materialCode=" + material.getMaterialCode()
                + ", materialName=" + material.getMaterialName()
                + ", status=" + material.getMaterialStatus()
                + ", condition=" + material.getConditionPercentage()
                + ", price=" + material.getPrice()
                + ", categoryId=" + material.getCategory().getCategory_id()
                + ", unitId=" + material.getUnit().getId()
                + ", disable=" + material.isDisable()
                + ", url=" + material.getMaterialsUrl()
                + ", updatedAt=" + material.getUpdatedAt()
            );

            // Gọi hàm update
            materialDAO.updateMaterial(material);

            // Chuyển hướng về dashboardmaterial
            response.sendRedirect("dashboardmaterial?success=Material updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            doGet(request, response);
        }
    }
}
