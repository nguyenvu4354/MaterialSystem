package controller;

import dal.RoleDAO;
import dal.PermissionDAO;
import dal.RolePermissionDAO;
import entity.Role;
import entity.Permission;
import entity.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RolePermissionServlet", urlPatterns = {"/RolePermission"})
public class RolePermissionServlet extends HttpServlet {

    private RoleDAO roleDAO = new RoleDAO();
    private PermissionDAO permissionDAO = new PermissionDAO();
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || currentUser.getRoleId() != 1) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        try {
            List<Role> roles = roleDAO.getAllRoles();
            List<Permission> permissions = permissionDAO.getAllPermissions();
            
            Map<Integer, Map<Integer, Boolean>> rolePermissionMap = new HashMap<>();
            for (Role role : roles) {
                List<Permission> assignedPermissions = permissionDAO.getPermissionsByRole(role.getRoleId());
                Map<Integer, Boolean> permMap = new HashMap<>();
                for (Permission perm : permissions) {
                    permMap.put(perm.getPermissionId(), 
                        assignedPermissions.stream().anyMatch(p -> p.getPermissionId() == perm.getPermissionId()));
                }
                rolePermissionMap.put(role.getRoleId(), permMap);
            }

            request.setAttribute("roles", roles);
            request.setAttribute("permissions", permissions);
            request.setAttribute("rolePermissionMap", rolePermissionMap);

            request.getRequestDispatcher("RolePermission.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            System.out.println("❌ Error in doGet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("System error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || currentUser.getRoleId() != 1) {
            response.sendRedirect("Login.jsp");
            return;
        }

        try {
            String action = request.getParameter("action");
            if (!"update".equals(action)) {
                request.setAttribute("errorMessage", "Invalid action!");
                doGet(request, response);
                return;
            }

            List<Role> roles = roleDAO.getAllRoles();
            List<Permission> permissions = permissionDAO.getAllPermissions();

            for (Role role : roles) {
                for (Permission perm : permissions) {
                    String paramName = "permission_" + role.getRoleId() + "_" + perm.getPermissionId();
                    boolean isChecked = request.getParameter(paramName) != null;
                    boolean hasPermission = rolePermissionDAO.hasPermission(role.getRoleId(), perm.getPermissionName());

                    if (isChecked && !hasPermission) {
                        rolePermissionDAO.assignPermissionToRole(role.getRoleId(), perm.getPermissionId());
                    } else if (!isChecked && hasPermission) {
                        rolePermissionDAO.removePermissionFromRole(role.getRoleId(), perm.getPermissionId());
                    }
                }
            }

            request.setAttribute("successMessage", "Permissions updated successfully!");
            doGet(request, response); // Refresh the page with updated data
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            System.out.println("❌ Error in doPost: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("System error", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing role permissions";
    }
}