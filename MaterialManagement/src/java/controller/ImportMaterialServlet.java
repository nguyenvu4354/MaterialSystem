package controller;

import dal.ImportDAO;
import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Material;
import entity.Supplier;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {

    private ImportDAO importDAO;
    private SupplierDAO supplierDAO;
    private MaterialDAO materialDAO;
    private InventoryDAO inventoryDAO;

    @Override
    public void init() throws ServletException {
        importDAO = new ImportDAO();
        supplierDAO = new SupplierDAO();
        materialDAO = new MaterialDAO();
        inventoryDAO = new InventoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            // Lấy danh sách nhà cung cấp
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            
            // Lấy danh sách vật tư
            List<Material> materials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
            
            // Lấy ID phiếu nhập tạm thời từ session
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            
            // Lấy chi tiết phiếu nhập
            List<ImportDetail> importDetails = new ArrayList<>();
            if (tempImportId != null && tempImportId != 0) {
                importDetails = importDAO.getDraftImportDetails(tempImportId);
            }

            // Tạo map để dễ dàng truy xuất thông tin vật tư
            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material material : materials) {
                materialMap.put(material.getMaterialId(), material);
            }

            // Lấy thông tin tồn kho cho từng vật tư
            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ImportDetail detail : importDetails) {
                int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
                stockMap.put(detail.getMaterialId(), stock);
            }

            // Set các thuộc tính cho request
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("materials", materials);
            request.setAttribute("importDetails", importDetails);
            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);

            // Khởi tạo tempImportId nếu chưa có
            if (tempImportId == null) {
                session.setAttribute("tempImportId", 0);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi lấy dữ liệu: " + e.getMessage());
        }
        request.getRequestDispatcher("/ImportMaterial.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                handleAddMaterial(request, response, session, user);
            } else if ("import".equals(action)) {
                handleImport(request, response, session, user);
            } else if ("remove".equals(action)) {
                handleRemoveMaterial(request, response, session);
            } else if ("updateQuantity".equals(action)) {
                handleUpdateQuantity(request, response, session);
            } else {
                request.setAttribute("error", "Invalid action.");
                doGet(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input format: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        String condition = request.getParameter("materialCondition");
        String unitPriceStr = request.getParameter("unitPrice");
        boolean isDamaged = Boolean.parseBoolean(request.getParameter("isDamaged"));
        List<ImportDetail> detailsToAdd = new ArrayList<>();
        List<Material> materials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
        Map<Integer, Material> materialMap = new HashMap<>();
        for (Material material : materials) {
            materialMap.put(material.getMaterialId(), material);
        }

        if (materialIdStr == null || materialIdStr.isEmpty() ||
                quantityStr == null || quantityStr.isEmpty() ||
                condition == null || condition.isEmpty() ||
                unitPriceStr == null || unitPriceStr.isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            loadDataAndForward(request, response);
            return;
        }

        try {
            int materialId = Integer.parseInt(materialIdStr);
            int quantity = Integer.parseInt(quantityStr);
            double unitPrice = Double.parseDouble(unitPriceStr);

            if (quantity <= 0) {
                request.setAttribute("error", "Quantity must be greater than 0.");
                loadDataAndForward(request, response);
                return;
            }

            if (!materialMap.containsKey(materialId)) {
                request.setAttribute("error", "Invalid material selected.");
                loadDataAndForward(request, response);
                return;
            }

            ImportDetail detail = new ImportDetail();
            detail.setMaterialId(materialId);
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice);
            detail.setMaterialCondition(condition);
            detail.setStatus("draft");
            detail.setCreatedAt(LocalDateTime.now());
            detailsToAdd.add(detail);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for material ID, quantity, or unit price.");
            loadDataAndForward(request, response);
            return;
        } catch (Exception e) {
            request.setAttribute("error", "Invalid data format.");
            loadDataAndForward(request, response);
            return;
        }

        int tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == 0) {
            Import imports = new Import();
            imports.setImportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
            imports.setImportDate(LocalDateTime.now());
            imports.setImportedBy(user.getUserId());
            imports.setCreatedAt(LocalDateTime.now());
            imports.setUpdatedAt(LocalDateTime.now());
            imports.setIsDamaged(isDamaged);
            tempImportId = importDAO.createImport(imports);
            session.setAttribute("tempImportId", tempImportId);
        }

        for (ImportDetail detail : detailsToAdd) {
            detail.setImportId(tempImportId);
        }
        importDAO.createImportDetails(detailsToAdd);
        request.setAttribute("success", "Material added to import list.");
        loadDataAndForward(request, response);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String materialCondition = request.getParameter("materialCondition");
        int tempImportId = (Integer) session.getAttribute("tempImportId");

        importDAO.removeImportDetail(tempImportId, materialId, quantity, materialCondition);
        request.setAttribute("success", "Material removed from import list.");
        loadDataAndForward(request, response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String materialCondition = request.getParameter("materialCondition");
        int tempImportId = (Integer) session.getAttribute("tempImportId");

        if (quantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response);
            return;
        }

        ImportDetail existingDetail = importDAO.getImportDetailByMaterialAndCondition(tempImportId, materialId, materialCondition);
        if (existingDetail == null) {
            request.setAttribute("error", "Import detail not found for material ID: " + materialId);
            loadDataAndForward(request, response);
            return;
        }

        importDAO.updateImportDetailQuantity(existingDetail.getImportDetailId(), quantity);
        request.setAttribute("success", "Quantity updated for material ID: " + materialId);
        loadDataAndForward(request, response);
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        int tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == 0) {
            request.setAttribute("error", "No materials selected for import.");
            loadDataAndForward(request, response);
            return;
        }

        List<ImportDetail> details = importDAO.getDraftImportDetails(tempImportId);
        if (details.isEmpty()) {
            request.setAttribute("error", "No materials in the import cart.");
            loadDataAndForward(request, response);
            return;
        }

        Integer supplierId = request.getParameter("supplierId") != null && !request.getParameter("supplierId").isEmpty()
                ? Integer.parseInt(request.getParameter("supplierId")) : null;
        String destination = request.getParameter("destination");
        String batchNumber = request.getParameter("batchNumber");
        String actualArrivalStr = request.getParameter("actualArrival");
        String note = request.getParameter("note");
        boolean isDamaged = Boolean.parseBoolean(request.getParameter("isDamaged"));

        LocalDateTime actualArrival = null;
        if (actualArrivalStr != null && !actualArrivalStr.isEmpty()) {
            try {
                actualArrival = LocalDateTime.parse(actualArrivalStr);
            } catch (Exception e) {
                request.setAttribute("error", "Invalid actual arrival date format.");
                loadDataAndForward(request, response);
                return;
            }
        }

        Import imports = new Import();
        imports.setImportId(tempImportId);
        imports.setImportCode("IMP-" + UUID.randomUUID().toString().substring(0, 8));
        imports.setImportDate(LocalDateTime.now());
        imports.setImportedBy(user.getUserId());
        imports.setSupplierId(supplierId);
        imports.setDestination(destination);
        imports.setBatchNumber(batchNumber);
        imports.setIsDamaged(isDamaged);
        imports.setActualArrival(actualArrival);
        imports.setNote(note);
        imports.setUpdatedAt(LocalDateTime.now());

        importDAO.updateImport(imports);
        importDAO.confirmImport(tempImportId);
        importDAO.updateInventoryByImportId(tempImportId, user.getUserId());

        session.setAttribute("tempImportId", 0);
        request.setAttribute("success", "Import successful with code: " + imports.getImportCode());
        loadDataAndForward(request, response);
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        List<Material> materials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
        Integer tempImportId = (Integer) request.getSession().getAttribute("tempImportId");
        List<ImportDetail> importDetails = (tempImportId != null && tempImportId != 0)
                ? importDAO.getDraftImportDetails(tempImportId)
                : new ArrayList<>();

        Map<Integer, Material> materialMap = new HashMap<>();
        for (Material material : materials) {
            materialMap.put(material.getMaterialId(), material);
        }

        Map<Integer, Integer> stockMap = new HashMap<>();
        for (ImportDetail detail : importDetails) {
            int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
            stockMap.put(detail.getMaterialId(), stock);
        }

        request.setAttribute("suppliers", suppliers);
        request.setAttribute("materials", materials);
        request.setAttribute("importDetails", importDetails);
        request.setAttribute("materialMap", materialMap);
        request.setAttribute("stockMap", stockMap);
        request.getRequestDispatcher("/ImportMaterial.jsp").forward(request, response);
    }
}