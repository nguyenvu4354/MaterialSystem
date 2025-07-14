package controller;

import dal.MaterialDAO;
import dal.PurchaseOrderDAO;
import dal.PurchaseRequestDAO;
import dal.SupplierDAO;
import dal.PurchaseRequestDetailDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import entity.Material;
import entity.PurchaseRequest;
import entity.Supplier;
import entity.User;
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
import utils.EmailUtils;

@WebServlet(name = "CreatePurchaseOrderServlet", urlPatterns = {"/CreatePurchaseOrder"})
public class CreatePurchaseOrderServlet extends HttpServlet {

    private PurchaseOrderDAO purchaseOrderDAO;
    private PurchaseRequestDAO purchaseRequestDAO;
    private MaterialDAO materialDAO;
    private SupplierDAO supplierDAO;
    private PurchaseRequestDetailDAO purchaseRequestDetailDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
            purchaseOrderDAO = new PurchaseOrderDAO();
            purchaseRequestDAO = new PurchaseRequestDAO();
            materialDAO = new MaterialDAO();
            supplierDAO = new SupplierDAO();
        purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        entity.User user = (entity.User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_PURCHASE_ORDER");
        request.setAttribute("hasCreatePurchaseOrderPermission", hasPermission);
        if (!hasPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        try {
            String poCode = generatePOCode();
            request.setAttribute("poCode", poCode);
            List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
            request.setAttribute("purchaseRequests", purchaseRequests);
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            request.setAttribute("materials", materials);
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            request.setAttribute("suppliers", suppliers);
            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            if (purchaseRequestIdStr != null && !purchaseRequestIdStr.isEmpty()) {
                int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
                List<entity.PurchaseRequestDetail> purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, 1000);
                if (purchaseRequestDetailList == null) purchaseRequestDetailList = new java.util.ArrayList<>();
                request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
                request.setAttribute("selectedPurchaseRequestId", purchaseRequestId);
                java.util.Map<Integer, String> materialImages = new java.util.HashMap<>();
                for (entity.PurchaseRequestDetail detail : purchaseRequestDetailList) {
                    Material material = materialDAO.getInformation(detail.getMaterialId());
                    String fileName = (material != null && material.getMaterialsUrl() != null && !material.getMaterialsUrl().isEmpty())
                        ? material.getMaterialsUrl()
                        : "default.jpg";
                    materialImages.put(detail.getMaterialId(), fileName);
                }
                request.setAttribute("materialImages", materialImages);
            }
            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("purchaseRequests", new java.util.ArrayList<PurchaseRequest>());
            request.setAttribute("materials", new java.util.ArrayList<Material>());
            request.setAttribute("suppliers", new java.util.ArrayList<Supplier>());
            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        entity.User user = (entity.User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_PURCHASE_ORDER");
        request.setAttribute("hasCreatePurchaseOrderPermission", hasPermission);
        if (!hasPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        try {
            String poCode = request.getParameter("poCode");
            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            String note = request.getParameter("note");
            if (poCode == null || poCode.trim().isEmpty() ||
                purchaseRequestIdStr == null || purchaseRequestIdStr.trim().isEmpty()) {
                throw new Exception("Purchase order code and purchase request are required.");
            }
            int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            if (purchaseRequest == null) {
                throw new Exception("Purchase request not found.");
            }
            if (!"APPROVED".equalsIgnoreCase(purchaseRequest.getStatus())) {
                throw new Exception("Purchase request must be approved to create purchase order.");
            }
            String[] materialIds = request.getParameterValues("materials[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");
            String[] suppliers = request.getParameterValues("suppliers[]");
            if (materialIds == null || quantities == null || unitPrices == null || suppliers == null ||
                materialIds.length == 0 || quantities.length == 0 || unitPrices.length == 0 || suppliers.length == 0) {
                throw new Exception("At least one material is required.");
            }
            java.util.List<entity.PurchaseOrderDetail> details = new java.util.ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                java.math.BigDecimal unitPrice = new java.math.BigDecimal(unitPrices[i]);
                int supplierId = Integer.parseInt(suppliers[i]);
                if (quantity <= 0) {
                    throw new Exception("Quantity must be positive.");
                }
                if (unitPrice.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    throw new Exception("Unit price must be positive.");
                }
                Material material = materialDAO.getInformation(materialId);
                if (material == null) {
                    throw new Exception("Material with ID " + materialId + " not found.");
                }
                entity.PurchaseOrderDetail detail = new entity.PurchaseOrderDetail();
                detail.setMaterialId(materialId);
                detail.setMaterialName(material.getMaterialName());
                detail.setCategoryId(material.getCategory().getCategory_id());
                detail.setQuantity(quantity);
                detail.setUnitPrice(unitPrice);
                detail.setSupplierId(supplierId);
                details.add(detail);
            }
            entity.PurchaseOrder purchaseOrder = new entity.PurchaseOrder();
            purchaseOrder.setPoCode(poCode);
            purchaseOrder.setPurchaseRequestId(purchaseRequestId);
            purchaseOrder.setCreatedBy(user.getUserId());
            purchaseOrder.setNote(note);
            // Đảm bảo luôn set status là 'pending' khi tạo mới
            purchaseOrder.setStatus("pending");
            boolean success = purchaseOrderDAO.createPurchaseOrder(purchaseOrder, details);
            if (success) {
                // Gửi email thông báo cho giám đốc
                try {
                    sendPurchaseOrderNotification(purchaseOrder, details, purchaseRequest, user);
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi gửi email thông báo purchase order: " + e.getMessage());
                    e.printStackTrace();
                }
                request.setAttribute("success", "Purchase order created successfully!");
                doGet(request, response);
                return;
            } else {
                request.setAttribute("error", "Failed to create purchase order.");
                doGet(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    private void sendPurchaseOrderNotification(entity.PurchaseOrder purchaseOrder, 
                                            List<entity.PurchaseOrderDetail> details, 
                                            PurchaseRequest purchaseRequest, 
                                            User creator) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> allUsers = userDAO.getAllUsers();
            List<User> directors = new ArrayList<>();
            
            // Lấy danh sách giám đốc (roleId = 2)
            for (User u : allUsers) {
                if (u.getRoleId() == 2) {
                    directors.add(u);
                }
            }
            
            // Tạo nội dung email
            String subject = "[Thông báo] Purchase Order mới đã được tạo";
            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>Purchase Order mới đã được tạo</h2>");
            content.append("<p><strong>Mã Purchase Order:</strong> ").append(purchaseOrder.getPoCode()).append("</p>");
            content.append("<p><strong>Người tạo:</strong> ").append(creator.getFullName()).append(" (ID: ").append(creator.getUserId()).append(")</p>");
            content.append("<p><strong>Mã Purchase Request:</strong> ").append(purchaseRequest.getRequestCode()).append("</p>");
            content.append("<p><strong>Ghi chú:</strong> ").append(purchaseOrder.getNote() != null ? purchaseOrder.getNote() : "Không có").append("</p>");
            content.append("<p><strong>Thời gian tạo:</strong> ").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</p>");
            
            content.append("<h3>Chi tiết vật tư:</h3>");
            content.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            content.append("<tr><th>Vật tư</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr>");
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (entity.PurchaseOrderDetail detail : details) {
                BigDecimal lineTotal = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                totalAmount = totalAmount.add(lineTotal);
                content.append("<tr>");
                content.append("<td>").append(detail.getMaterialName()).append("</td>");
                content.append("<td>").append(detail.getQuantity()).append("</td>");
                content.append("<td>").append(detail.getUnitPrice()).append(" $</td>");
                content.append("<td>").append(lineTotal).append(" $</td>");
                content.append("</tr>");
            }
            content.append("</table>");
            content.append("<p><strong>Tổng tiền:</strong> ").append(totalAmount).append(" $</p>");
            content.append("<p>Vui lòng đăng nhập hệ thống để xem chi tiết và xử lý purchase order.</p>");
            content.append("</body></html>");
            
            // Chỉ gửi email cho giám đốc
            for (User director : directors) {
                if (director.getEmail() != null && !director.getEmail().trim().isEmpty()) {
                    try {
                        EmailUtils.sendEmail(director.getEmail(), subject, content.toString());
                        System.out.println("✅ Đã gửi email thông báo purchase order cho giám đốc: " + director.getEmail());
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi gửi email cho giám đốc " + director.getEmail() + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email thông báo purchase order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generatePOCode() {
        String prefix = "PO";
        String year = String.valueOf(LocalDate.now().getYear());
        String sql = "SELECT po_code FROM Purchase_Orders WHERE po_code LIKE ? ORDER BY po_code DESC LIMIT 1";
        String likePattern = prefix + "-" + year + "-%";
        try (java.sql.Connection conn = new entity.DBContext().getConnection();
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