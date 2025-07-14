package controller;

import dal.PurchaseOrderDAO;
import dal.RolePermissionDAO;
import entity.PurchaseOrder;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "PurchaseOrderListServlet", urlPatterns = {"/PurchaseOrderList"})
public class PurchaseOrderListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderListServlet.class.getName());
    private final PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    
    // Hằng số cho phân trang
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final int MAX_ITEMS_PER_PAGE = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("PurchaseOrderListServlet.doGet() started");
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            LOGGER.info("Session ID: " + session.getId());
            LOGGER.info("User in session: " + (user != null ? user.getUsername() : "null"));

            if (user == null) {
                LOGGER.info("User is null, redirecting to Login.jsp");
                response.sendRedirect(request.getContextPath() + "/Login.jsp?returnUrl=" + request.getRequestURI());
                return;
            }

            boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_PURCHASE_ORDER_LIST");
            request.setAttribute("hasViewPurchaseOrderListPermission", hasPermission);
            if (!hasPermission) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            boolean hasSendToSupplierPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "SENT_TO_SUPPLIER");
            request.setAttribute("hasSendToSupplierPermission", hasSendToSupplierPermission);

            // Lấy và xử lý các tham số tìm kiếm
            String status = request.getParameter("status");
            String poCode = request.getParameter("poCode");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            // Xử lý phân trang
            int page = DEFAULT_PAGE;
            int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
            
            try {
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) page = DEFAULT_PAGE;
                }
                
                String itemsPerPageStr = request.getParameter("itemsPerPage");
                if (itemsPerPageStr != null && !itemsPerPageStr.isEmpty()) {
                    itemsPerPage = Integer.parseInt(itemsPerPageStr);
                    if (itemsPerPage < 1) itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
                    if (itemsPerPage > MAX_ITEMS_PER_PAGE) itemsPerPage = MAX_ITEMS_PER_PAGE;
                }
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid pagination parameters, using defaults: " + e.getMessage());
            }
            
            // Parse dates
            LocalDate startDate = null;
            LocalDate endDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateStr, formatter);
                } catch (Exception e) {
                    LOGGER.warning("Invalid start date format: " + startDateStr);
                }
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr, formatter);
                } catch (Exception e) {
                    LOGGER.warning("Invalid end date format: " + endDateStr);
                }
            }
            
            LOGGER.info("Parameters: status=" + status + ", poCode=" + poCode + 
                       ", startDate=" + startDate + ", endDate=" + endDate +
                       ", page=" + page + ", itemsPerPage=" + itemsPerPage);

            // Lấy dữ liệu
            List<PurchaseOrder> purchaseOrders = purchaseOrderDAO.getPurchaseOrders(page, itemsPerPage, status, poCode, startDate, endDate);
            int totalItems = purchaseOrderDAO.getPurchaseOrderCount(status, poCode, startDate, endDate);
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            LOGGER.info("Retrieved data: totalItems=" + totalItems + ", totalPages=" + totalPages + 
                       ", purchaseOrders size=" + (purchaseOrders != null ? purchaseOrders.size() : "null"));

            if (purchaseOrders == null) {
                LOGGER.severe("purchaseOrders is null from DAO");
                request.setAttribute("error", "An error occurred while loading data. Please try again later.");
            } else if (purchaseOrders.isEmpty()) {
                LOGGER.info("No purchase orders found");
            }

            // Set các thuộc tính cho JSP
            request.setAttribute("purchaseOrders", purchaseOrders);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("status", status);
            request.setAttribute("poCode", poCode);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);

            LOGGER.info("Forwarding to PurchaseOrderList.jsp");
            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            LOGGER.info("PurchaseOrderListServlet.doGet() completed successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in PurchaseOrderListServlet: " + e.getMessage(), e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            try {
                request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error forwarding to error page: " + ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        // Đã bỏ kiểm tra cứng quyền UPDATE_PURCHASE_ORDER_STATUS ở đây

        try {
            String action = request.getParameter("action");
            if ("updateStatus".equals(action)) {
                int poId = Integer.parseInt(request.getParameter("poId"));
                String status = request.getParameter("status");
                String approvalReason = request.getParameter("approvalReason");
                String rejectionReason = request.getParameter("rejectionReason");

                // Phân quyền động cho SENT_TO_SUPPLIER
                if ("sent_to_supplier".equals(status)) {
                    boolean hasSendToSupplierPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "SENT_TO_SUPPLIER");
                    if (!hasSendToSupplierPermission) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                        return;
                    }
                }

                boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, status, user.getUserId(), approvalReason, rejectionReason);

                if (success) {
                    if ("sent_to_supplier".equals(status)) {
                        // Gửi email cho các supplier trong đơn hàng
                        try {
                            dal.PurchaseOrderDAO poDAO = new dal.PurchaseOrderDAO();
                            dal.SupplierDAO supplierDAO = new dal.SupplierDAO();
                            entity.PurchaseOrder po = poDAO.getPurchaseOrderById(poId);
                            java.util.Set<Integer> supplierIds = new java.util.HashSet<>();
                            if (po != null && po.getDetails() != null) {
                                for (entity.PurchaseOrderDetail detail : po.getDetails()) {
                                    supplierIds.add(detail.getSupplierId());
                                }
                            }
                            for (Integer supplierId : supplierIds) {
                                entity.Supplier supplier = supplierDAO.getSupplierByID(supplierId);
                                if (supplier != null && supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
                                    String subject = "[Notification] New Purchase Order Sent To You";
                                    String content = "Dear Supplier,<br><br>You have a new purchase order (PO Code: " + po.getPoCode() + ") sent to you. Please log in to the system to view details.<br><br>Thank you.";
                                    try {
                                        utils.EmailUtils.sendEmail(supplier.getEmail(), subject, content);
                                    } catch (Exception e) {
                                        System.err.println("[MAIL] Failed to send email to supplier: " + supplier.getEmail());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[MAIL] Error when sending email to suppliers: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?message=Status updated successfully");
                    return;
                } else {
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?message=Error updating status");
                    return;
                }
            } else {
                doGet(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?message=Error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for listing and managing purchase orders";
    }
} 