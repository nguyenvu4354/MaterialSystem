<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>

<%
User user = (User) session.getAttribute("user");
if (user != null) {
    PermissionDAO permissionDAO = new PermissionDAO();
    List<String> permissionNames = permissionDAO.getPermissionsByRole(user.getRoleId())
        .stream()
        .map(permission -> permission.getPermissionName())
        .collect(Collectors.toList());
    session.setAttribute("userPermissions", permissionNames);
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Internal Material Management</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
          integrity="sha512-..." crossorigin="anonymous" referrerpolicy="no-referrer" />
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</head>

<body>

<footer id="footer" class="bg-light border-top mt-5">
    <div class="container py-5">
        <div class="row">

            <div class="col-md-3 order-md-1">
                <h6 class="fw-bold mb-3">Quick Links</h6>
                <ul class="list-unstyled text-muted small">
                    <li><a href="home" class="text-decoration-none text-muted">Dashboard</a></li>
                    <li><a href="dashboardmaterial" class="text-decoration-none text-muted">Materials</a></li>
                    <li><a href="ImportMaterial" class="text-decoration-none text-muted">Import</a></li>
                    <li><a href="ExportMaterial" class="text-decoration-none text-muted">Export</a></li>
                    <li><a href="ExportRequestList" class="text-decoration-none text-muted">Requests</a></li>
                </ul>
            </div>

            <div class="col-md-3 order-md-2">
                <h6 class="fw-bold mb-3">Help Center</h6>
                <ul class="list-unstyled text-muted small">
                    <li><a href="#" class="text-decoration-none text-muted">User Guide</a></li>
                    <li><a href="#" class="text-decoration-none text-muted">Policies</a></li>
                    <li><a href="#" class="text-decoration-none text-muted">Return Materials</a></li>
                    <li><a href="#" class="text-decoration-none text-muted">Report Issue</a></li>
                    <li><a href="#" class="text-decoration-none text-muted">Technical Support</a></li>
                </ul>
            </div>

            <div class="col-md-3 order-md-3">
                <h6 class="fw-bold mb-3">Contact</h6>
                <ul class="list-unstyled text-muted small">
                    <li><strong>Material Dept.</strong></li>
                    <li>Email: support@materials.company.com</li>
                    <li>Phone: +1 234 567 890</li>
                    <li>Address: 123 Company Street, District 1, HCMC</li>
                </ul>
            </div>

            <div class="col-md-3 order-md-4 text-md-end text-center">
                <img src="images/AdminLogo.png" alt="Logo" width="180px" class="mb-3">
                <p class="text-muted small">Internal Materials Management System</p>
                <div class="d-flex justify-content-md-end justify-content-center gap-2">
                    <a href="#"><iconify-icon class="text-dark" icon="ri:facebook-fill"></iconify-icon></a>
                    <a href="#"><iconify-icon class="text-dark" icon="ri:linkedin-fill"></iconify-icon></a>
                    <a href="#"><iconify-icon class="text-dark" icon="ri:github-fill"></iconify-icon></a>
                </div>
            </div>

        </div>
        <hr class="mt-4">
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>
</body>
</html>
