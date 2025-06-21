package controller;

import dal.RepairRequestDAO;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RepairRequestServlet", urlPatterns = {"/repairrequest"})
public class RepairRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

         try {
            // Lấy user từ session
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loggedUser");
            if (user == null) {
                response.sendRedirect("Login.jsp");
                return;
            }
            int userId = user.getUserId(); // hoặc getId()

            // Lấy thông tin từ form
            String requestCode = request.getParameter("requestCode");
            String repairPhone = request.getParameter("repairPersonPhoneNumber");
            String repairEmail = request.getParameter("repairPersonEmail");
            String repairLocation = request.getParameter("repairLocation");
            String reason = request.getParameter("reason");
            Date estimatedReturnDate = Date.valueOf(request.getParameter("estimatedReturnDate"));

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

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

            // Chi tiết vật tư
            String[] materialIds = request.getParameterValues("materialId");
            String[] quantities = request.getParameterValues("quantity");
            String[] descriptions = request.getParameterValues("damageDescription");
            String[] repairCosts = request.getParameterValues("repairCost");

            List<RepairRequestDetail> detailList = new ArrayList<>();

            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
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

            new RepairRequestDAO().createRepairRequest(requestObj, detailList);

            response.sendRedirect("RepairRequestSuccess.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi gửi yêu cầu sửa chữa.");
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        }
    }
}
