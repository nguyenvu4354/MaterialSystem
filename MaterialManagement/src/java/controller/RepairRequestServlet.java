package controller;

import dal.MaterialDAO;
import dal.RepairRequestDAO;
import dal.UserDAO;
import entity.Material;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách vật tư từ database
        MaterialDAO materialDAO = new MaterialDAO();
        List<Material> materialList = materialDAO.getAllProducts();

        // Truyền danh sách vật tư sang JSP
        request.setAttribute("materialList", materialList);

        // Forward đến trang tạo yêu cầu sửa chữa
        request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy user từ session
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("Login.jsp");
                return;
            }
            int userId = user.getUserId();

            // Sinh tự động requestCode
            String requestCode = generateRequestCode();

            // Lấy thông tin từ form
            String repairPhone = request.getParameter("repairPersonPhoneNumber");
            String repairEmail = request.getParameter("repairPersonEmail");
            String repairLocation = request.getParameter("repairLocation");
            String reason = request.getParameter("reason");
            Date estimatedReturnDate = Date.valueOf(request.getParameter("estimatedReturnDate"));
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // Tạo yêu cầu sửa chữa
            RepairRequest requestObj = new RepairRequest();
            requestObj.setRequestCode(requestCode);
            requestObj.setUserId(userId);
            requestObj.setRepairPersonPhoneNumber(repairPhone);
            requestObj.setRepairPersonEmail(repairEmail);
            requestObj.setRepairLocation(repairLocation);
            requestObj.setReason(reason);
            requestObj.setEstimatedReturnDate(estimatedReturnDate);
            requestObj.setRequestDate(now);
            requestObj.setStatus("Pending");
            requestObj.setCreatedAt(now);
            requestObj.setUpdatedAt(now);
            requestObj.setDisable(false);

            // Lấy danh sách chi tiết vật tư
            String[] materialNames = request.getParameterValues("materialName");
            String[] quantities = request.getParameterValues("quantity");
            String[] descriptions = request.getParameterValues("damageDescription");
            String[] repairCosts = request.getParameterValues("repairCost");

            List<RepairRequestDetail> detailList = new ArrayList<>();
            MaterialDAO materialDAO = new MaterialDAO(); // thêm bên ngoài vòng for

            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i].trim();
                int materialId = materialDAO.getMaterialIdByName(materialName);

                if (materialId == -1) {
                    request.setAttribute("errorMessage", "Không tìm thấy vật tư có tên: " + materialName);
                    request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                    return;
                }

                int quantity = Integer.parseInt(quantities[i]);
                String description = descriptions[i];

                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setDamageDescription(description);

                Double repairCost = null;
                if (repairCosts[i] != null && !repairCosts[i].isEmpty()) {
                    repairCost = Double.parseDouble(repairCosts[i]);
                }
                detail.setRepairCost(repairCost);
                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);

                detailList.add(detail);
            }

            // Lưu yêu cầu vào DB
            boolean success = new RepairRequestDAO().createRepairRequest(requestObj, detailList);

            // Nếu thành công thì gửi email cho giám đốc
            if (success) {
                UserDAO userDAO = new UserDAO();
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
                    content.append("Địa điểm sửa chữa: ").append(repairLocation).append("\n");
                    content.append("Ngày dự kiến hoàn trả: ").append(estimatedReturnDate).append("\n");
                    content.append("Thời gian gửi yêu cầu: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now)).append("\n\n");
                    content.append("Vui lòng đăng nhập hệ thống để xem chi tiết và xử lý yêu cầu.");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                        try {
                            EmailUtils.sendEmail(user.getEmail(), subject, content.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Chuyển đến trang thông báo thành công
            response.sendRedirect("RepairRequestSuccess.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error sending repair request.");
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        }
    }

    // Phương thức sinh requestCode tự động
    private String generateRequestCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String timestamp = sdf.format(new java.util.Date());
        int random = (int) (Math.random() * 9000) + 1000;
        return "RR-" + timestamp + "-" + random;
    }
}
