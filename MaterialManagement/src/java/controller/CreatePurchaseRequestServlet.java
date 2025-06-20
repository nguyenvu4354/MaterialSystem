/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.PurchaseRequestDetailDAO;
import dal.UserDAO;
import entity.Category;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequestServlet", "/create-request"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        
        List<User> users = userDAO.getAllUsers(); 
        List<Category> categories = categoryDAO.getAllCategories();

        
        String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                + "-" + (int) (Math.random() * 1000);
        String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

        
        request.setAttribute("users", users);
        request.setAttribute("categories", categories);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("requestDate", requestDate);

        
        request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Lấy dữ liệu từ form
        String estimatedPriceStr = request.getParameter("estimatedPrice");
        String reason = request.getParameter("reason");

        String[] materialNames = request.getParameterValues("materialName");
        String[] categoryIds = request.getParameterValues("categoryId");
        String[] quantities = request.getParameterValues("quantity");
        String[] notes = request.getParameterValues("note");

        // Validation cơ bản
        if (reason == null || reason.trim().isEmpty()) {
            request.setAttribute("error", "Lý do yêu cầu không được để trống.");
            doGet(request, response);
            return;
        }

        if (estimatedPriceStr == null || estimatedPriceStr.trim().isEmpty()) {
            request.setAttribute("error", "Giá dự kiến không được để trống.");
            doGet(request, response);
            return;
        }

        // Parse và validate estimatedPrice
        double estimatedPrice;
        try {
            estimatedPrice = Double.parseDouble(estimatedPriceStr);
            if (estimatedPrice < 0) {
                request.setAttribute("error", "Giá dự kiến phải lớn hơn hoặc bằng 0.");
                doGet(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Giá dự kiến không hợp lệ.");
            doGet(request, response);
            return;
        }

        // Validate và tạo danh sách chi tiết
        List<PurchaseRequestDetail> purchaseRequestDetails = new ArrayList<>();
        
        if (materialNames != null && categoryIds != null && quantities != null) {
            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                String categoryIdStr = categoryIds[i];
                String quantityStr = quantities[i];
                
                // Bỏ qua nếu material name rỗng
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                
                // Validate categoryId
                int categoryId;
                try {
                    categoryId = Integer.parseInt(categoryIdStr);
                    if (categoryId <= 0) {
                        request.setAttribute("error", "Danh mục không hợp lệ.");
                        doGet(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Danh mục không hợp lệ.");
                    doGet(request, response);
                    return;
                }
                
                // Validate quantity
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) {
                        request.setAttribute("error", "Số lượng phải lớn hơn 0.");
                        doGet(request, response);
                        return;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Số lượng không hợp lệ.");
                    doGet(request, response);
                    return;
                }
                
                // Tạo detail object
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setMaterialName(materialName.trim());
                detail.setCategoryId(categoryId);
                detail.setQuantity(quantity);
                
                // Xử lý notes
                String note = (notes != null && notes.length > i) ? notes[i] : null;
                detail.setNotes(note != null && !note.trim().isEmpty() ? note.trim() : null);
                
                purchaseRequestDetails.add(detail);
            }
        }

        // Kiểm tra có ít nhất một vật tư
        if (purchaseRequestDetails.isEmpty()) {
            request.setAttribute("error", "Bạn cần thêm ít nhất một vật tư.");
            doGet(request, response);
            return;
        }

        try {
            // Tạo mã yêu cầu
            String requestCode = "PR-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date())
                    + "-" + (int) (Math.random() * 1000);

            // Tạo PurchaseRequest object
            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setRequestCode(requestCode);
            purchaseRequest.setUserId(user.getUserId());
            purchaseRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            purchaseRequest.setStatus("PENDING"); // Sử dụng trạng thái chuẩn
            purchaseRequest.setEstimatedPrice(estimatedPrice);
            purchaseRequest.setReason(reason.trim());

            // Sử dụng phương thức tạo yêu cầu với chi tiết trong một transaction
            PurchaseRequestDAO prDAO = new PurchaseRequestDAO();
            boolean success = prDAO.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);

            if (success) {
                // Redirect đến trang danh sách với thông báo thành công
                response.sendRedirect("ListPurchaseRequestsServlet?success=created");
            } else {
                request.setAttribute("error", "Không thể tạo yêu cầu mua hàng. Vui lòng thử lại.");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi khi xử lý yêu cầu: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Handles creation and saving of purchase requests.";
    }// </editor-fold>

}
