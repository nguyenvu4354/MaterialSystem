package controller;

import dal.MaterialDAO;
import dal.RolePermissionDAO;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DeleteMaterialServlet", urlPatterns = {"/deletematerial"})
public class DeleteMaterialServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "DELETE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền xóa vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("dashboardmaterial");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "DELETE_MATERIAL")) {
            request.setAttribute("error", "Bạn không có quyền xóa vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            String idDelete = request.getParameter("materialId");
            int id = Integer.parseInt(idDelete);
            MaterialDAO md = new MaterialDAO();
            md.deleteMaterial(id);
            response.sendRedirect("dashboardmaterial?success=Vật tư được xóa thành công");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}