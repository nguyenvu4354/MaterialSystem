package controller;

import dal.RoleDAO;
import dal.PermissionDAO;
import dal.RolePermissionDAO;
import entity.Role;
import entity.Permission;
import entity.User;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        try {
            List<Role> roles = roleDAO.getAllRoles();
            request.setAttribute("roles", roles);

            String roleIdParam = request.getParameter("roleId");
            if (roleIdParam != null && !roleIdParam.isEmpty()) {
                try {
                    int selectedRoleId = Integer.parseInt(roleIdParam);
                    Role selectedRole = roleDAO.getRoleById(selectedRoleId);
                    List<Permission> assignedPermissions = permissionDAO.getPermissionsByRole(selectedRoleId);
                    List<Permission> allPermissions = permissionDAO.getAllPermissions();
                    List<Permission> availablePermissions = allPermissions.stream()
                            .filter(p -> assignedPermissions.stream().noneMatch(ap -> ap.getPermissionId() == p.getPermissionId()))
                            .collect(Collectors.toList());

                    request.setAttribute("selectedRoleId", selectedRoleId);
                    request.setAttribute("selectedRoleName", selectedRole != null ? selectedRole.getRoleName() : "");
                    request.setAttribute("assignedPermissions", assignedPermissions);
                    request.setAttribute("availablePermissions", availablePermissions);
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid role ID!");
                    System.out.println("❌ Invalid roleId format: " + e.getMessage());
                }
            }

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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        try {
            String action = request.getParameter("action");
            String roleIdParam = request.getParameter("roleId");
            String permissionIdParam = request.getParameter("permissionId");

            if (roleIdParam == null || permissionIdParam == null || action == null) {
                request.setAttribute("errorMessage", "Missing required parameters!");
                response.sendRedirect("RolePermission?roleId=" + roleIdParam);
                return;
            }

            int selectedRoleId = Integer.parseInt(roleIdParam);
            int permissionId = Integer.parseInt(permissionIdParam);

            if ("assign".equals(action)) {
                rolePermissionDAO.assignPermissionToRole(selectedRoleId, permissionId);
                request.setAttribute("successMessage", "Permission assigned successfully!");
            } else if ("remove".equals(action)) {
                rolePermissionDAO.removePermissionFromRole(selectedRoleId, permissionId);
                request.setAttribute("successMessage", "Permission removed successfully!");
            } else {
                request.setAttribute("errorMessage", "Invalid action: " + action);
                System.out.println("❌ Invalid action: " + action);
            }

            response.sendRedirect("RolePermission?roleId=" + selectedRoleId);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid parameter format!");
            System.out.println("❌ Invalid parameter format: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Invalid parameter format", e);
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