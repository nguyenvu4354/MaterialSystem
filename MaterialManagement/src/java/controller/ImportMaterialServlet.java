package controller;

import dal.ImportDAO;
import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import dal.RolePermissionDAO;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import utils.ImportValidator;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {
    private ImportDAO importDAO;
    private SupplierDAO supplierDAO;
    private MaterialDAO materialDAO;
    private InventoryDAO inventoryDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        importDAO = new ImportDAO();
        supplierDAO = new SupplierDAO();
        materialDAO = new MaterialDAO();
        inventoryDAO = new InventoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "IMPORT_MATERIAL");
        request.setAttribute("hasImportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        loadDataAndForward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "IMPORT_MATERIAL");
        request.setAttribute("hasImportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        try {
            switch (action) {
                case "add":
                    handleAddMaterial(request, response, session, user);
                    break;
                case "import":
                    handleImport(request, response, session, user);
                    break;
                case "remove":
                    handleRemoveMaterial(request, response, session);
                    break;
                case "update":
                    handleUpdateQuantity(request, response, session);
                    break;
                default:
                    request.setAttribute("error", "Invalid action.");
                    loadDataAndForward(request, response);
                    break;
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            loadDataAndForward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format provided.");
            loadDataAndForward(request, response);
        }
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        String unitPriceStr = request.getParameter("unitPrice");

        // Validate nghiệp vụ phía backend
        Map<String, String> errors = utils.ImportValidator.validateAddMaterial(materialIdStr, quantityStr, unitPriceStr, materialDAO);
        if (!errors.isEmpty()) {
            request.setAttribute("formErrors", errors);
            loadDataAndForward(request, response);
            return;
        }

        Integer tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == null || tempImportId == 0) {
            Import imports = new Import();
            imports.setImportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
            imports.setImportDate(LocalDateTime.now());
            imports.setImportedBy(user.getUserId());
            tempImportId = importDAO.createImport(imports);
            session.setAttribute("tempImportId", tempImportId);
        }

        ImportDetail detail = new ImportDetail();
        detail.setImportId(tempImportId);
        detail.setMaterialId(Integer.parseInt(materialIdStr));
        detail.setQuantity(Integer.parseInt(quantityStr));
        detail.setUnitPrice(Double.parseDouble(unitPriceStr));
        detail.setStatus("draft");
        // Tính lại total value phía backend nếu cần (quantity * unitPrice)
        // (Nếu ImportDetail có trường totalValue thì set ở đây)

        importDAO.createImportDetails(Collections.singletonList(detail));
        request.setAttribute("success", "Material added to import list successfully.");
        loadDataAndForward(request, response);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");

        if (tempImportId != null && tempImportId != 0) {
            importDAO.removeImportDetail(tempImportId, materialId, quantity);
            request.setAttribute("success", "Material removed from import list successfully.");
        } else {
            request.setAttribute("error", "No import list found to remove material from.");
        }
        loadDataAndForward(request, response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int newQuantity = Integer.parseInt(request.getParameter("newQuantity"));
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");

        if (newQuantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response);
            return;
        }

        if (tempImportId != null && tempImportId != 0) {
            ImportDetail detail = importDAO.getImportDetailByMaterial(tempImportId, materialId);
            if (detail != null) {
                importDAO.updateImportDetailQuantity(detail.getImportDetailId(), newQuantity);
                request.setAttribute("success", "Quantity updated successfully for material ID: " + materialId);
            } else {
                request.setAttribute("error", "Material not found in import list.");
            }
        } else {
            request.setAttribute("error", "No import list found to update.");
        }
        loadDataAndForward(request, response);
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException, SQLException {
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == null || tempImportId == 0) {
            request.setAttribute("error", "No materials selected for import.");
            loadDataAndForward(request, response);
            return;
        }

        List<ImportDetail> details = importDAO.getDraftImportDetails(tempImportId);
        if (details.isEmpty()) {
            request.setAttribute("error", "Import list is empty.");
            loadDataAndForward(request, response);
            return;
        }

        String supplierIdStr = request.getParameter("supplierId");
        String destination = request.getParameter("destination");
        String actualArrivalStr = request.getParameter("actualArrival");
        String note = request.getParameter("note");
        Map<String, String> formErrors = utils.ImportValidator.validateImportFormData(
            supplierIdStr, destination, actualArrivalStr, note, supplierDAO
        );
        if (!formErrors.isEmpty()) {
            request.setAttribute("formErrors", formErrors);
            loadDataAndForward(request, response);
            return;
        }

        Integer supplierId = Integer.parseInt(supplierIdStr);
        java.time.LocalDateTime actualArrival = java.time.LocalDateTime.parse(actualArrivalStr);

        Import imports = new Import();
        imports.setImportId(tempImportId);
        imports.setImportCode("IMP-" + UUID.randomUUID().toString().substring(0, 8));
        imports.setImportDate(java.time.LocalDateTime.now());
        imports.setImportedBy(user.getUserId());
        imports.setSupplierId(supplierId);
        imports.setDestination(destination);
        imports.setActualArrival(actualArrival);
        imports.setNote(note);

        double totalImportValue = utils.ImportValidator.calculateTotalImportValue(details);

        importDAO.updateImport(imports);
        importDAO.confirmImport(tempImportId);
        importDAO.updateInventoryByImportId(tempImportId, user.getUserId(), destination);
        session.setAttribute("tempImportId", 0);
        request.setAttribute("success", "Import completed successfully with code: " + imports.getImportCode() + ". Total value: $" + String.format("%.2f", totalImportValue));
        loadDataAndForward(request, response);
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            List<Material> allMaterials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
            // Remove department loading for destination
            // List<entity.Department> departments = departmentDAO.getDepartments();
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("materials", allMaterials);
            // request.setAttribute("departments", departments);

            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            if (tempImportId == null) {
                tempImportId = 0;
                session.setAttribute("tempImportId", 0);
            }

            List<ImportDetail> importDetails = (tempImportId != 0)
                    ? importDAO.getDraftImportDetails(tempImportId)
                    : new ArrayList<>();
            request.setAttribute("importDetails", importDetails);
            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material m : allMaterials) {
                materialMap.put(m.getMaterialId(), m);
            }
            
            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ImportDetail detail : importDetails) {
                stockMap.put(detail.getMaterialId(), inventoryDAO.getStockByMaterialId(detail.getMaterialId()));
            }

            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);

        } catch (SQLException e) {
            request.setAttribute("error", "Error loading page data: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/ImportMaterial.jsp").forward(request, response);
    }
}