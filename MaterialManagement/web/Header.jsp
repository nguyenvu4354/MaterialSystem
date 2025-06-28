<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <!-- Bootstrap CSS for styling -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <!-- Optional: Custom CSS for additional styling -->
    <link rel="stylesheet" href="css/custom.css">
</head>
<header>
    <div class="container py-2">
        <div class="row align-items-center">
            <div class="col-12 col-sm-4 text-center text-sm-start mb-3 mb-sm-0">
                <a href="home">
                    <img src="images/AdminLogo.png" alt="logo" class="img-fluid" style="max-width: 180px;">
                </a>
            </div>
            <div class="col-12 col-sm-8 d-flex flex-column flex-sm-row justify-content-sm-end align-items-center gap-3">
                <div class="text-center text-sm-end">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <span class="fs-6 text-muted">${sessionScope.user.fullName}</span><br>
                            <strong>${sessionScope.user.email}</strong>
                        </c:when>
                        <c:otherwise>
                            <span class="fs-6 text-muted">Guest</span><br>
                            <strong>guest@example.com</strong>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="logout" class="btn btn-outline-dark btn-sm">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="Login.jsp" class="btn btn-outline-primary btn-sm">Login</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <hr class="my-2">
    </div>

    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-light main-menu d-flex">
            <a class="navbar-brand d-lg-none" href="#">Menu</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header justify-content-center">
                    <button type="button" class="btn-close ms-auto" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body d-flex flex-column flex-lg-row align-items-lg-center justify-content-between">
                    <select class="filter-categories border-0 mb-0 me-5"
                            onchange="location.href = this.value;">
                        <option selected disabled>System Management</option>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_USER')}">
                            <option value="UserList">Manage Users</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT')}">
                            <option value="Departmet">Manage Department</option>
                        </c:if>
                            <c:if test="${sessionScope.userPermissions.contains('VIEW_UNIT')}">
                            <option value="Unit">Manage Unit</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_INVENTORY')}">
                            <option value="StaticInventory">Manage Inventory</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL')}">
                            <option value="dashboardmaterial">Manage Materials</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY')}">
                            <option value="Category">Manage Categories</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER')}">
                            <option value="Supplier">Manage Suppliers</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_UNIT')}">
                            <option value="manageUnits.jsp">Manage Units</option>
                        </c:if>
                    </select>

                    <select class="filter-categories border-0 mb-0 me-5"
                            onchange="location.href = this.value;">
                        <option selected disabled>Stock</option>
                        <c:if test="${sessionScope.userPermissions.contains('EXPORT_MATERIAL')}">
                            <option value="ExportMaterial">Export Stock</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('IMPORT_MATERIAL')}">
                            <option value="ImportMaterial">Import Stock</option>
                        </c:if>
                    </select>

                    <select class="filter-categories border-0 mb-0 me-5"
                            onchange="location.href = this.value;">
                        <option selected disabled>Request</option>
                        <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')}">
                            <option value="CreateExportRequest">Export Request</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')}">
                            <option value="CreatePurchaseRequest">Purchase Request</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                            <option value="repairrequest">Repair Request</option>
                        </c:if>
                    </select>

                    <select class="filter-categories border-0 mb-0 me-5"
                            onchange="location.href = this.value;">
                        <option selected disabled>Request List</option>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_EXPORT_REQUEST_LIST')}">
                            <option value="ExportRequestList">Export Request List</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_PURCHASE_REQUEST_LIST')}">
                            <option value="ListPurchaseRequests">Purchase Request List</option>
                        </c:if>
                        <c:if test="${sessionScope.userPermissions.contains('VIEW_REPAIR_REQUEST_LIST')}">
                            <option value="repairrequestlist">Repair Request List</option>
                        </c:if>
                    </select>

                    <ul class="navbar-nav d-flex flex-row flex-wrap gap-3 mb-3 mb-lg-0 menu-list list-unstyled">
                        <li class="nav-item">
                            <a href="home" class="nav-link active">Home</a>
                        </li>
                    </ul>

                    <div class="d-none d-lg-flex align-items-center gap-3 align-items-end">
                        <a href="profile" class="text-dark mx-2 mx-3">
                            <i class="fas fa-user fs-4"></i>
                        </a>
                        <a href="#" class="mx-3 d-lg-none" data-bs-toggle="offcanvas" data-bs-target="#offcanvasSearch" aria-controls="offcanvasSearch">
                            <iconify-icon icon="tabler:search" class="fs-4"></iconify-icon>
                        </a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>

<!-- Bootstrap JS and dependencies -->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js" integrity="sha384-7+zCNj/IqJ95wo16oMtfsKbZ9ccEh31eOz1HGyDuCQ6wgnyJNSYdrPa03rtR1zdB" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js" integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13" crossorigin="anonymous"></script>
<!-- Optional: Iconify for search icon -->
<script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>