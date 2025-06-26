package controller;

import dal.CategoryDAO; 
import dal.DepartmentDAO;
import dal.RequestDAO;
import entity.Category; 
import entity.ExportRequest;
import entity.PurchaseRequest;
import entity.RepairRequest;
import entity.User;
import entity.Material;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ViewRequestDetailsServlet", urlPatterns = {"/ViewRequestDetails"})
public class ViewRequestDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");
            Object requestObj = null;

            RequestDAO requestDAO = new RequestDAO();

            switch (type.toLowerCase()) {
                case "export":
                    requestObj = requestDAO.getExportRequestById(requestId);
                    break;
                case "purchase":
                    requestObj = requestDAO.getPurchaseRequestById(requestId);
                    break;
                case "repair":
                    requestObj = requestDAO.getRepairRequestById(requestId);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request type");
                    return;
            }

            if (requestObj == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }

            // Check access permissions
            int requestUserId = getRequestUserId(requestObj);
            boolean hasAccess = requestUserId == user.getUserId() || "Director".equalsIgnoreCase(user.getRoleName());
            if (!hasAccess) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            // Lấy danh sách vật tư từ DepartmentDAO
            DepartmentDAO departmentDAO = new DepartmentDAO();
            List<Material> materials = departmentDAO.getMaterials();
            request.setAttribute("materials", materials);

            // Lấy danh sách danh mục từ CategoryDAO
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("request", requestObj);
            request.setAttribute("requestType", type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
            request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
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
        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            String type = request.getParameter("type");
            Object requestObj = null;

            // Khởi tạo RequestDAO
            RequestDAO requestDAO = new RequestDAO();

            // Fetch the request based on type
            switch (type.toLowerCase()) {
                case "export":
                    requestObj = requestDAO.getExportRequestById(requestId);
                    break;
                case "purchase":
                    requestObj = requestDAO.getPurchaseRequestById(requestId);
                    break;
                case "repair":
                    requestObj = requestDAO.getRepairRequestById(requestId);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request type");
                    return;
            }

            if (requestObj == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }

            int requestUserId = getRequestUserId(requestObj);
            if (requestUserId != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the request owner can cancel it");
                return;
            }

            if ("cancel".equals(action)) {
                String status = getRequestStatus(requestObj);
                if (!"pending".equals(status)) {
                    request.setAttribute("message", "Error: Only pending requests can be cancelled.");
                    request.setAttribute("request", requestObj);
                    request.setAttribute("requestType", type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
                    DepartmentDAO departmentDAO = new DepartmentDAO();
                    List<Material> materials = departmentDAO.getMaterials();
                    request.setAttribute("materials", materials);
                    CategoryDAO categoryDAO = new CategoryDAO();
                    List<Category> categories = categoryDAO.getAllCategories();
                    request.setAttribute("categories", categories);
                    request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
                    return;
                }

                boolean cancelled = false;
                switch (type.toLowerCase()) {
                    case "export":
                        cancelled = requestDAO.cancelExportRequest(requestId, user.getUserId());
                        break;
                    case "purchase":
                        cancelled = requestDAO.cancelPurchaseRequest(requestId, user.getUserId());
                        break;
                    case "repair":
                        cancelled = requestDAO.cancelRepairRequest(requestId, user.getUserId());
                        break;
                }

                if (cancelled) {
                    response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + requestId + "&message=Request+cancelled+successfully");
                } else {
                    request.setAttribute("message", "Error: Failed to cancel request.");
                    request.setAttribute("request", requestObj);
                    request.setAttribute("requestType", type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
                    // Lấy lại danh sách vật tư và danh mục
                    DepartmentDAO departmentDAO = new DepartmentDAO();
                    List<Material> materials = departmentDAO.getMaterials();
                    request.setAttribute("materials", materials);
                    CategoryDAO categoryDAO = new CategoryDAO();
                    List<Category> categories = categoryDAO.getAllCategories();
                    request.setAttribute("categories", categories);
                    request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private int getRequestUserId(Object request) {
        if (request instanceof ExportRequest) {
            return ((ExportRequest) request).getUserId();
        } else if (request instanceof PurchaseRequest) {
            return ((PurchaseRequest) request).getUserId();
        } else if (request instanceof RepairRequest) {
            return ((RepairRequest) request).getUserId();
        }
        return 0;
    }

    private String getRequestStatus(Object request) {
        if (request instanceof ExportRequest) {
            return ((ExportRequest) request).getStatus();
        } else if (request instanceof PurchaseRequest) {
            return ((PurchaseRequest) request).getStatus();
        } else if (request instanceof RepairRequest) {
            return ((RepairRequest) request).getStatus();
        }
        return "";
    }
}