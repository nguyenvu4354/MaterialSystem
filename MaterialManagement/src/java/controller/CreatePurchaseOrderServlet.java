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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.EmailUtils;
import utils.PurchaseOrderValidator;

/**
 * Servlet for handling creation of purchase orders.
 */
@WebServlet(name = "CreatePurchaseOrderServlet", urlPatterns = {"/CreatePurchaseOrder"})
public class CreatePurchaseOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_ORDER");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase orders.");
            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            return;
        }

        try {
            // Generate proper PO code using DAO method
            PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
            String poCode = purchaseOrderDAO.generateNextPOCode();
            List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();

            request.setAttribute("poCode", poCode);
            request.setAttribute("purchaseRequests", purchaseRequests);
            request.setAttribute("materials", materials);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);

            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            if (purchaseRequestIdStr != null && !purchaseRequestIdStr.isEmpty()) {
                int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
                PurchaseRequestDetailDAO purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
                List<entity.PurchaseRequestDetail> purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, 1000);
                if (purchaseRequestDetailList == null) purchaseRequestDetailList = new ArrayList<>();
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
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        UserDAO userDAO = new UserDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_ORDER");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase orders.");
            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            return;
        }

        try {
            String poCode = request.getParameter("poCode");
            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            String note = request.getParameter("note");
            String[] materialIds = request.getParameterValues("materials[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");
            String[] suppliers = request.getParameterValues("suppliers[]");
            
           

            Map<String, String> formErrors = PurchaseOrderValidator.validatePurchaseOrderFormData(poCode, purchaseRequestIdStr, note);
            Map<String, String> detailErrors = PurchaseOrderValidator.validatePurchaseOrderDetails(materialIds, quantities, unitPrices, suppliers);
            formErrors.putAll(detailErrors);
            
           

            if (!formErrors.isEmpty()) {
                List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                List<Supplier> suppliersList = supplierDAO.getAllSuppliers();
                
                // Generate proper PO code using DAO method
                String newPoCode = purchaseOrderDAO.generateNextPOCode();
                
                // Preserve form data for retry
                request.setAttribute("poCode", newPoCode);
                request.setAttribute("purchaseRequests", purchaseRequests);
                request.setAttribute("materials", materials);
                request.setAttribute("suppliers", suppliersList);
                request.setAttribute("errors", formErrors);
                for (Map.Entry<String, String> entry : formErrors.entrySet()) {
                }
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                
                // Preserve submitted form data
                request.setAttribute("submittedPoCode", poCode);
                request.setAttribute("submittedPurchaseRequestId", purchaseRequestIdStr);
                request.setAttribute("submittedNote", note);
                request.setAttribute("submittedMaterialIds", materialIds);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedUnitPrices", unitPrices);
                request.setAttribute("submittedSuppliers", suppliers);

                // Load purchase request details if purchase request is selected
                if (purchaseRequestIdStr != null && !purchaseRequestIdStr.isEmpty()) {
                    try {
                        int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
                        PurchaseRequestDetailDAO purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
                        List<entity.PurchaseRequestDetail> purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, 1000);
                        if (purchaseRequestDetailList == null) purchaseRequestDetailList = new ArrayList<>();
                        request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
                        request.setAttribute("selectedPurchaseRequestId", purchaseRequestId);
                        
                        java.util.Map<Integer, String> materialImages = new java.util.HashMap<>();
                        java.util.Map<Integer, String> materialCategories = new java.util.HashMap<>();
                        java.util.Map<Integer, String> materialUnits = new java.util.HashMap<>();
                        
                        for (entity.PurchaseRequestDetail detail : purchaseRequestDetailList) {
                            Material material = materialDAO.getInformation(detail.getMaterialId());
                            if (material != null) {
                                // Get image
                                String fileName = (material.getMaterialsUrl() != null && !material.getMaterialsUrl().isEmpty())
                                    ? material.getMaterialsUrl()
                                    : "default.jpg";
                                materialImages.put(detail.getMaterialId(), fileName);
                                
                                // Get category name
                                String categoryName = material.getCategory() != null ? material.getCategory().getCategory_name() : "N/A";
                                materialCategories.put(detail.getMaterialId(), categoryName);
                                
                                // Get unit name
                                String unitName = material.getUnit() != null ? material.getUnit().getUnitName() : "N/A";
                                materialUnits.put(detail.getMaterialId(), unitName);
                            }
                        }
                        request.setAttribute("materialImages", materialImages);
                        request.setAttribute("materialCategories", materialCategories);
                        request.setAttribute("materialUnits", materialUnits);
                    } catch (NumberFormatException e) {
                        // Ignore if purchase request ID is invalid
                    }
                } else {
                    // If no purchase request selected, clear the material list
                    request.setAttribute("purchaseRequestDetailList", new ArrayList<>());
                    request.setAttribute("selectedPurchaseRequestId", null);
                }
                
                
                request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
                return;
            }

            // Parse purchase request ID after validation
            int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            if (purchaseRequest == null) {
                throw new Exception("Purchase request not found.");
            }
            if (!"APPROVED".equalsIgnoreCase(purchaseRequest.getStatus())) {
                throw new Exception("Purchase request must be approved to create purchase order.");
            }

            // Group materials by supplier
            Map<Integer, List<entity.PurchaseOrderDetail>> supplierGroups = new HashMap<>();
            
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                BigDecimal unitPrice = new BigDecimal(unitPrices[i]);
                int supplierId = Integer.parseInt(suppliers[i]);
                
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
                
                // Group by supplier
                supplierGroups.computeIfAbsent(supplierId, k -> new ArrayList<>()).add(detail);
            }

            // Create all purchase orders in a single transaction
            List<entity.PurchaseOrder> createdOrders = new ArrayList<>();
            
            // Prepare all purchase orders first
            List<entity.PurchaseOrder> purchaseOrdersToCreate = new ArrayList<>();
            for (Map.Entry<Integer, List<entity.PurchaseOrderDetail>> entry : supplierGroups.entrySet()) {
                int supplierId = entry.getKey();
                List<entity.PurchaseOrderDetail> supplierDetails = entry.getValue();
                
                // Create purchase order for this supplier (PO code will be auto-generated by DAO)
                entity.PurchaseOrder purchaseOrder = new entity.PurchaseOrder();
                purchaseOrder.setPoCode(null); // Let DAO generate the code
                purchaseOrder.setPurchaseRequestId(purchaseRequestId);
                purchaseOrder.setCreatedBy(currentUser.getUserId());
                purchaseOrder.setNote(note);
                purchaseOrder.setStatus("pending");
                
                purchaseOrdersToCreate.add(purchaseOrder);
            }
            
            // Create all purchase orders in batch
            boolean allSuccess = purchaseOrderDAO.createPurchaseOrdersBatch(purchaseOrdersToCreate, supplierGroups);
            
            if (allSuccess) {
                createdOrders.addAll(purchaseOrdersToCreate);
                
                // Send notifications for all created orders
                int poIndex = 0;
                for (Map.Entry<Integer, List<entity.PurchaseOrderDetail>> entry : supplierGroups.entrySet()) {
                    int supplierId = entry.getKey();
                    List<entity.PurchaseOrderDetail> supplierDetails = entry.getValue();
                    
                    // Get the corresponding purchase order by index
                    if (poIndex < createdOrders.size()) {
                        entity.PurchaseOrder purchaseOrder = createdOrders.get(poIndex);
                        
                        try {
                            sendPurchaseOrderNotification(purchaseOrder, supplierDetails, purchaseRequest, currentUser);
                        } catch (Exception e) {
                            System.err.println("Error sending purchase order notification: " + e.getMessage());
                            e.printStackTrace();
                        }
                        poIndex++;
                    }
                }
            } else {
                throw new Exception("Failed to create purchase orders");
            }

            if (!createdOrders.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/PurchaseOrderList");
                return;
            } else {
                request.setAttribute("error", "Failed to create purchase orders.");
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
            
            for (User u : allUsers) {
                if (u.getRoleId() == 2) {
                    directors.add(u);
                }
            }
            
            // Get supplier information from first detail (all details have same supplier)
            SupplierDAO supplierDAO = new SupplierDAO();
            Supplier supplier = null;
            if (!details.isEmpty()) {
                supplier = supplierDAO.getSupplierByID(details.get(0).getSupplierId());
            }
            String supplierName = (supplier != null) ? supplier.getSupplierName() : "N/A";
            
            String subject = "[Purchase Order] " + purchaseOrder.getPoCode() + " - " + supplierName;
            StringBuilder content = new StringBuilder();
            content.append("<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>");
            
            // Email container
            content.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>");
            
            // Header with golden brown theme
            content.append("<div style='background: linear-gradient(135deg, #E9B775 0%, #D4A574 100%); padding: 30px; text-align: center;'>");
            content.append("<h1 style='color: #000000; margin: 0; font-size: 28px; font-weight: bold;'>New Purchase Order</h1>");
            content.append("<p style='color: #000000; margin: 10px 0 0 0; font-size: 16px;'>A new purchase order has been submitted and requires your attention</p>");
            content.append("</div>");
            
            // Main content
            content.append("<div style='padding: 40px 30px;'>");
            
            // Request information section
            content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
            content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Request Information</h2>");
            
            content.append("<table style='width: 100%; border-collapse: collapse;'>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold; width: 40%;'>Request Code:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getPoCode()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Requested By:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getFullName()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Submitted:</td><td style='padding: 8px 0; color: #333333;'>").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Email:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getEmail() != null ? creator.getEmail() : "N/A").append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Phone:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getPhoneNumber() != null ? creator.getPhoneNumber() : "N/A").append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Purchase Order:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getPoCode()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Based on PR:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseRequest.getRequestCode()).append("</td></tr>");
            
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Supplier:</td><td style='padding: 8px 0; color: #333333;'>").append(supplierName).append("</td></tr>");
            
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Reason:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getNote() != null && !purchaseOrder.getNote().trim().isEmpty() ? purchaseOrder.getNote() : "No additional notes").append("</td></tr>");
            content.append("</table>");
            content.append("</div>");
            
            // Material details section
            content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
            content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Material Details</h2>");
            
            content.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #dee2e6;'>");
            content.append("<thead><tr style='background-color: #E9B775;'>");
            content.append("<th style='padding: 12px; text-align: left; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Material Name</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Quantity</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Category</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit Price</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Total Amount</th>");
            content.append("</tr></thead>");
            content.append("<tbody>");
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            MaterialDAO materialDAO = new MaterialDAO();
            
            for (entity.PurchaseOrderDetail detail : details) {
                BigDecimal lineTotal = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                totalAmount = totalAmount.add(lineTotal);
                
                // Get material information for category and unit
                Material material = materialDAO.getInformation(detail.getMaterialId());
                String categoryName = (material != null && material.getCategory() != null) ? material.getCategory().getCategory_name() : "N/A";
                String unitName = (material != null && material.getUnit() != null) ? material.getUnit().getUnitName() : "N/A";
                
                content.append("<tr style='background-color: #ffffff;'>");
                content.append("<td style='padding: 12px; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getMaterialName()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getQuantity()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(categoryName).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(unitName).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>$").append(detail.getUnitPrice()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>$").append(lineTotal).append("</td>");
                content.append("</tr>");
            }
            content.append("</tbody></table>");
            
            // Total amount
            content.append("<div style='text-align: right; margin-top: 20px; padding: 15px; background-color: #E9B775; border-radius: 5px;'>");
            content.append("<h3 style='color: #000000; margin: 0; font-size: 18px; font-weight: bold;'>Total Amount: $").append(totalAmount).append("</h3>");
            content.append("</div>");
            content.append("</div>");
            
            // Action button
            content.append("<div style='text-align: center; margin-top: 30px;'>");
            content.append("<a href='http://localhost:8080/MaterialManagement/PurchaseOrderList' style='display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>VIEW IN SYSTEM</a>");
            content.append("</div>");
            
            content.append("</div>");
            
            // Footer
            content.append("<div style='background-color: #E9B775; padding: 20px; text-align: center;'>");
            content.append("<p style='color: #000000; margin: 0; font-size: 14px;'>This is an automated notification from the Material Management System</p>");
            content.append("</div>");
            
            content.append("</div></body></html>");
            
            for (User director : directors) {
                if (director.getEmail() != null && !director.getEmail().trim().isEmpty()) {
                    try {
                        EmailUtils.sendEmail(director.getEmail(), subject, content.toString());
                    } catch (Exception e) {
                        System.err.println("Error sending email to director " + director.getEmail() + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error sending purchase order notification: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public String getServletInfo() {
        return "Servlet for handling creation of purchase orders";
    }
} 