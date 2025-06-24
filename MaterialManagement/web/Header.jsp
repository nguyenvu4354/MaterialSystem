<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
    <div class="container py-2">
        <div class="row align-items-center">
            <div class="col-12 col-sm-4 text-center text-sm-start mb-3 mb-sm-0">
                <a href="HomePage.jsp">
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
        <nav class="navbar navbar-expand-lg navbar-light">
            <a class="navbar-brand d-lg-none" href="#">Menu</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar">
                <div class="offcanvas-header">
                    <button type="button" class="btn-close ms-auto" data-bs-dismiss="offcanvas"></button>
                </div>
                <div class="offcanvas-body d-flex flex-column flex-lg-row align-items-lg-center justify-content-between">
                    <select class="filter-categories border-0 mb-0 me-5"
                            onchange="location.href = this.value;">
                        <option selected disabled>System Management</option>
                        <option value="UserList">Manage Users</option>
                        <option value="manageDepartments.jsp">Manage Departments</option>
                        <option value="dashboardmaterial">Manage Materials</option>
                        <option value="Category.jsp">Manage Categories</option>
                        <option value="manageSuppliers.jsp">Manage Suppliers</option>
                        <option value="manageUnits.jsp">Manage Units</option>
                    </select>

                    <ul class="navbar-nav d-flex flex-row flex-wrap gap-3 mb-3 mb-lg-0">
                        <li class="nav-item">
                            <a href="HomePage.jsp" class="nav-link active">Home</a>
                        </li>
                        <c:choose>

                            <c:when test="${not empty sessionScope.user and sessionScope.user.roleId == 3}">
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" id="stockPages" data-bs-toggle="dropdown">Stock</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="ExportMaterial" class="dropdown-item">Export Stock</a></li>
                                        <li><a href="ImportMaterial" class="dropdown-item">Import Stock</a></li>
                                    </ul>
                                </li>
                            </c:when>

                            <c:when test="${not empty sessionScope.user and sessionScope.user.roleId == 4}">
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" id="adminPages" data-bs-toggle="dropdown">Request</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="CreateExportRequest" class="dropdown-item">Export Request</a></li>
                                        <li><a href="CreatePurchaseRequest" class="dropdown-item">Purchase Request</a></li>
                                        <li><a href="CreateRepairRequest.jsp" class="dropdown-item">Repair Request</a></li>

                                        <li><a href="ExportRequestList" class="dropdown-item">Export Request List</a></li>
                                        <li><a href="ListPurchaseRequestsServlet" class="dropdown-item">Purchase Request List</a></li>
                                        <li><a href="repairrequestlist" class="dropdown-item">Repair Request List</a></li>

                                    </ul>
                                </li>
                            </c:when>
                        </c:choose>

                    </ul>

                    <div class="d-none d-lg-flex align-items-center gap-3">
                        
                        <c:if test="${not empty sessionScope.user and (sessionScope.user.roleId == 2 or sessionScope.user.roleId == 3 or sessionScope.user.roleId == 4)}">
                            <div class="dropdown">
                                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" id="requestDropdownBtn" data-bs-toggle="dropdown" aria-expanded="false">
                                    Request List
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="requestDropdownBtn">
                                    <li><a href="ExportRequestList" class="dropdown-item">Export Request List</a></li>
                                    <li><a href="ListPurchaseRequestsServlet" class="dropdown-item">Purchase Request List</a></li>
                                    <li><a href="repairRequestList.jsp" class="dropdown-item">Repair Request List</a></li>
                                </ul>
                            </div>
                        </c:if>
                        <a href="profile" class="text-dark mx-2">
                            <iconify-icon icon="healthicons:person" class="fs-4"></iconify-icon>
                        </a>
                    </div>

                </div>
            </div>
        </nav>
    </div>
</header>
