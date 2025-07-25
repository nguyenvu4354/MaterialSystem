package controller;

import dal.MaterialDAO;
import dal.RepairRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
import entity.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.EmailUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RepairRequestServlet", urlPatterns = {"/repairrequest"})
public class RepairRequestServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_REPAIR_REQUEST")) {
            request.setAttribute("error", "You do not have permission to access the repair request creation page.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        MaterialDAO materialDAO = new MaterialDAO();
        List<Material> materialList = materialDAO.getAllProducts();

        SupplierDAO supplierDAO = new SupplierDAO();
        List<Supplier> supplierList = supplierDAO.getAllSuppliers();

        request.setAttribute("materialList", materialList);
        request.setAttribute("supplierList", supplierList);

        // Forward đến trang tạo yêu cầu sửa chữa
        request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("Login.jsp");
                return;
            }

            if (!rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_REPAIR_REQUEST")) {
                request.setAttribute("error", "You do not have permission to create a repair request.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            int userId = user.getUserId();

            String requestCode = generateRequestCode();

            String supplierIdStr = request.getParameter("supplierId");
            String reason = request.getParameter("reason");
            Date estimatedReturnDate = Date.valueOf(request.getParameter("estimatedReturnDate"));
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // Kiểm tra supplierId
            if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Please select a provider.");
                request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                return;
            }
            int supplierId;
            try {
                supplierId = Integer.parseInt(supplierIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid vendor ID.");
                request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                return;
            }

            RepairRequest requestObj = new RepairRequest();
            requestObj.setRequestCode(requestCode);
            requestObj.setUserId(userId);
            requestObj.setRepairPersonPhoneNumber(null); 
            requestObj.setRepairPersonEmail(null); 
            requestObj.setRepairLocation(null); 
            requestObj.setReason(reason);
            requestObj.setEstimatedReturnDate(estimatedReturnDate);
            requestObj.setRequestDate(now);
            requestObj.setStatus("Pending");
            requestObj.setCreatedAt(now);
            requestObj.setUpdatedAt(now);
            requestObj.setDisable(false);

            String[] materialNames = request.getParameterValues("materialName");
            String[] quantities = request.getParameterValues("quantity");
            String[] descriptions = request.getParameterValues("damageDescription");

            if (materialNames == null || quantities == null || descriptions == null) {
                request.setAttribute("errorMessage", "Invalid material data.");
                request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                return;
            }

            List<RepairRequestDetail> detailList = new ArrayList<>();
            MaterialDAO materialDAO = new MaterialDAO();

            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i].trim();
                int materialId = materialDAO.getMaterialIdByName(materialName);

                if (materialId == -1) {
                    request.setAttribute("errorMessage", "No material found with name: " + materialName);
                    request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                    return;
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(quantities[i]);
                    if (quantity <= 0) {
                        request.setAttribute("errorMessage", "Quantity must be greater than 0.");
                        request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid quantity for material: " + materialName);
                    request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                    return;
                }

                String description = descriptions[i];
                if (description == null || description.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Damage description is required for the material: " + materialName);
                    request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                    return;
                }

                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setDamageDescription(description);
                detail.setSupplierId(supplierId); 
                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);

                detailList.add(detail);
            }

            boolean success = new RepairRequestDAO().createRepairRequest(requestObj, detailList);
            System.out.println("[DEBUG] [doPost] Kết quả lưu yêu cầu vào DB: " + success);

            if (success) {
                UserDAO userDAO = new UserDAO();
                SupplierDAO supplierDAO = new SupplierDAO();
                Supplier supplier = supplierDAO.getSupplierByID(supplierId);
                List<User> allUsers = userDAO.getAllUsers();
                List<User> managers = new ArrayList<>();
                for (User u : allUsers) {
                    if (u.getRoleId() == 2) {
                        managers.add(u);
                    }
                }

                if (!managers.isEmpty()) {
                    String subject = "[Thông báo] Có yêu cầu sửa chữa mới";

                    StringBuilder content = new StringBuilder();
                    content.append("Xin chào Giám đốc,\n\n");
                    content.append("Một yêu cầu sửa chữa mới đã được tạo trên hệ thống.\n\n");
                    content.append("Mã yêu cầu: ").append(requestCode).append("\n");
                    content.append("Người tạo: ").append(user.getFullName()).append(" (ID: ").append(user.getUserId()).append(")\n");
                    content.append("Lý do: ").append(reason).append("\n");
                    content.append("Nhà cung cấp: ").append(supplier != null ? supplier.getSupplierName() : "N/A").append("\n");
                    content.append("Ngày dự kiến hoàn trả: ").append(estimatedReturnDate).append("\n");
                    content.append("Thời gian gửi yêu cầu: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now)).append("\n\n");
                    content.append("Vui lòng đăng nhập hệ thống để xem chi tiết và xử lý yêu cầu.");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                            } catch (Exception e) {
                                System.out.println("[DEBUG] [MAIL] Lỗi gửi mail cho manager: " + manager.getEmail());
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("[DEBUG] [MAIL] Manager không có email hợp lệ: " + manager.getFullName());
                        }
                    }

                    if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                        try {
                            System.out.println("[DEBUG] [MAIL] Sending email to user: " + user.getEmail());
                            System.out.println("[DEBUG] [MAIL] Subject: " + subject);
                            System.out.println("[DEBUG] [MAIL] Content: " + content.toString());
                            EmailUtils.sendEmail(user.getEmail(), subject, content.toString());
                        } catch (Exception e) {
                            System.out.println("[DEBUG] [MAIL] Lỗi gửi mail cho user: " + user.getEmail());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("[DEBUG] [MAIL] User không có email hợp lệ: " + user.getFullName());
                    }
                }
            }

            response.sendRedirect("RepairRequestSuccess.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi gửi yêu cầu sửa chữa: " + e.getMessage());
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        }
    }

    private String generateRequestCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String timestamp = sdf.format(new java.util.Date());
        int random = (int) (Math.random() * 9000) + 1000;
        return "RR-" + timestamp + "-" + random;
    }
}
