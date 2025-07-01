package controller;

import dal.MaterialDAO;
import dal.PurchaseOrderDAO;
import dal.PurchaseRequestDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.PurchaseRequest;
import entity.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CreatePurchaseOrderServlet", urlPatterns = {"/CreatePurchaseOrder"})
public class CreatePurchaseOrderServlet extends HttpServlet {

    private PurchaseOrderDAO purchaseOrderDAO;
    private PurchaseRequestDAO purchaseRequestDAO;
    private MaterialDAO materialDAO;
    private SupplierDAO supplierDAO;

    @Override
    public void init() throws ServletException {
        try {
            purchaseOrderDAO = new PurchaseOrderDAO();
            purchaseRequestDAO = new PurchaseRequestDAO();
            materialDAO = new MaterialDAO();
            supplierDAO = new SupplierDAO();
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: Entering CreatePurchaseOrderServlet.doGet");
        HttpSession session = request.getSession();
        entity.User user = (entity.User) session.getAttribute("user");

        if (user == null) {
            System.out.println("DEBUG: User not logged in, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        // Check if user has permission to create purchase orders (role 2 = Director, role 3 = Manager)
        int roleId = user.getRoleId();
        if (roleId != 2 && roleId != 3) {
            System.out.println("DEBUG: Unauthorized access for role_id " + roleId + ", redirecting to HomePage.jsp");
            response.sendRedirect("HomePage.jsp");
            return;
        }

        try {
            String poCode = generatePOCode();
            System.out.println("DEBUG: Generated poCode: " + poCode);
            request.setAttribute("poCode", poCode);

            // Load approved purchase requests
            List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
            System.out.println("DEBUG: Loaded " + purchaseRequests.size() + " approved purchase requests");
            request.setAttribute("purchaseRequests", purchaseRequests);

            // Load materials
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            System.out.println("DEBUG: Loaded " + materials.size() + " materials");
            request.setAttribute("materials", materials);

            // Load suppliers
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            System.out.println("DEBUG: Loaded " + suppliers.size() + " suppliers");
            request.setAttribute("suppliers", suppliers);

            System.out.println("DEBUG: Forwarding to CreatePurchaseOrder.jsp");
            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("ERROR: Exception in doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("purchaseRequests", new ArrayList<PurchaseRequest>());
            request.setAttribute("materials", new ArrayList<Material>());
            request.setAttribute("suppliers", new ArrayList<Supplier>());
            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("DEBUG: Entering CreatePurchaseOrderServlet.doPost");
        HttpSession session = request.getSession();
        entity.User user = (entity.User) session.getAttribute("user");

        if (user == null) {
            System.out.println("DEBUG: User not logged in, redirecting to Login.jsp");
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        int roleId = user.getRoleId();
        if (roleId != 2 && roleId != 3) {
            System.out.println("DEBUG: Unauthorized access for role_id " + roleId + ", redirecting to HomePage.jsp");
            response.sendRedirect("HomePage.jsp");
            return;
        }

        try {
            String poCode = request.getParameter("poCode");
            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            String note = request.getParameter("note");

            System.out.println("DEBUG: poCode=" + poCode + ", purchaseRequestId=" + purchaseRequestIdStr + ", note=" + note);

            if (poCode == null || poCode.trim().isEmpty() ||
                purchaseRequestIdStr == null || purchaseRequestIdStr.trim().isEmpty()) {
                throw new Exception("Purchase order code and purchase request are required.");
            }

            int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
            
            // Validate purchase request exists and is approved
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            if (purchaseRequest == null) {
                throw new Exception("Purchase request not found.");
            }
            if (!"APPROVED".equals(purchaseRequest.getStatus())) {
                throw new Exception("Purchase request must be approved to create purchase order.");
            }

            String[] materialIds = request.getParameterValues("materials[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");
            String[] suppliers = request.getParameterValues("suppliers[]");

            System.out.println("DEBUG: Form parameters:");
            System.out.println("DEBUG: materialIds=" + (materialIds != null ? String.join(",", materialIds) : "null"));
            System.out.println("DEBUG: quantities=" + (quantities != null ? String.join(",", quantities) : "null"));
            System.out.println("DEBUG: unitPrices=" + (unitPrices != null ? String.join(",", unitPrices) : "null"));
            System.out.println("DEBUG: suppliers=" + (suppliers != null ? String.join(",", suppliers) : "null"));

            if (materialIds == null || quantities == null || unitPrices == null || suppliers == null ||
                materialIds.length == 0 || quantities.length == 0 || unitPrices.length == 0 || suppliers.length == 0) {
                throw new Exception("At least one material is required.");
            }

            // Create purchase order details
            List<entity.PurchaseOrderDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                BigDecimal unitPrice = new BigDecimal(unitPrices[i]);
                int supplierId = Integer.parseInt(suppliers[i]);

                System.out.println("DEBUG: Material " + i + ": ID=" + materialId + ", Quantity=" + quantity + ", UnitPrice=" + unitPrice + ", Supplier=" + supplierId);

                if (quantity <= 0) {
                    throw new Exception("Quantity must be positive.");
                }
                if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new Exception("Unit price must be positive.");
                }

                // Get material information
                Material material = materialDAO.getInformation(materialId);
                if (material == null) {
                    throw new Exception("Material with ID " + materialId + " not found.");
                }

                entity.PurchaseOrderDetail detail = new entity.PurchaseOrderDetail();
                detail.setMaterialName(material.getMaterialName());
                // Get categoryId from material's category object
                detail.setCategoryId(material.getCategory().getCategory_id());
                detail.setQuantity(quantity);
                detail.setUnitPrice(unitPrice);
                detail.setSupplierId(supplierId);
                details.add(detail);
            }

            // Create purchase order
            entity.PurchaseOrder purchaseOrder = new entity.PurchaseOrder();
            purchaseOrder.setPoCode(poCode);
            purchaseOrder.setPurchaseRequestId(purchaseRequestId);
            purchaseOrder.setCreatedBy(user.getUserId());
            purchaseOrder.setStatus("pending");
            purchaseOrder.setNote(note);

            System.out.println("DEBUG: Purchase Order details:");
            System.out.println("DEBUG: poCode=" + purchaseOrder.getPoCode());
            System.out.println("DEBUG: purchaseRequestId=" + purchaseOrder.getPurchaseRequestId());
            System.out.println("DEBUG: createdBy=" + purchaseOrder.getCreatedBy());
            System.out.println("DEBUG: status=" + purchaseOrder.getStatus());
            System.out.println("DEBUG: note=" + purchaseOrder.getNote());
            System.out.println("DEBUG: Number of details=" + details.size());

            boolean success = purchaseOrderDAO.createPurchaseOrder(purchaseOrder, details);
            System.out.println("DEBUG: Purchase order creation " + (success ? "successful" : "failed"));

            if (success) {
                response.sendRedirect(request.getContextPath() + "/purchaseorderlist?message=Purchase order created successfully");
            } else {
                throw new Exception("Failed to create purchase order.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Exception in doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.setAttribute("poCode", request.getParameter("poCode"));
            request.setAttribute("purchaseRequestId", request.getParameter("purchaseRequestId"));
            request.setAttribute("note", request.getParameter("note"));
            request.setAttribute("materialIds", request.getParameterValues("materials[]"));
            request.setAttribute("quantities", request.getParameterValues("quantities[]"));
            request.setAttribute("unitPrices", request.getParameterValues("unitPrices[]"));
            request.setAttribute("suppliers", request.getParameterValues("suppliers[]"));
            try {
                List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
                System.out.println("DEBUG: Loaded " + purchaseRequests.size() + " approved purchase requests in error case");
                request.setAttribute("purchaseRequests", purchaseRequests);
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                System.out.println("DEBUG: Loaded " + materials.size() + " materials in error case");
                request.setAttribute("materials", materials);
                List<Supplier> suppliers = supplierDAO.getAllSuppliers();
                System.out.println("DEBUG: Loaded " + suppliers.size() + " suppliers in error case");
                request.setAttribute("suppliers", suppliers);
            } catch (Exception ex) {
                System.out.println("ERROR: Failed to load data in error case: " + ex.getMessage());
                ex.printStackTrace();
                request.setAttribute("purchaseRequests", new ArrayList<PurchaseRequest>());
                request.setAttribute("materials", new ArrayList<Material>());
                request.setAttribute("suppliers", new ArrayList<Supplier>());
            }
            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
        }
    }

    private String generatePOCode() {
        String prefix = "PO";
        String year = String.valueOf(LocalDate.now().getYear());
        String sql = "SELECT po_code FROM Purchase_Orders WHERE po_code LIKE ? ORDER BY po_code DESC LIMIT 1";
        String likePattern = prefix + "-" + year + "-%";
        try (java.sql.Connection conn = purchaseOrderDAO.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                if (rs.next()) {
                    String lastCode = rs.getString("po_code");
                    String[] parts = lastCode.split("-");
                    if (parts.length == 3) {
                        try {
                            nextSeq = Integer.parseInt(parts[2]) + 1;
                        } catch (NumberFormatException ignore) {}
                    }
                }
                return prefix + "-" + year + "-" + String.format("%03d", nextSeq);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "-" + year + "-001";
        }
    }
} 