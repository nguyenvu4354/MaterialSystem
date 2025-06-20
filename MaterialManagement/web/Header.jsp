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
                <a href="logout" class="btn btn-outline-dark btn-sm">Logout</a>
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
                    <select class="form-select w-auto mb-3 mb-lg-0 me-lg-4">
                        <option selected disabled>Admin Options</option>
                        <option>Manage Products</option>
                        <option>Manage Users</option>
                        <option>Manage Orders</option>
                        <option>Reports</option>
                    </select>

                    <ul class="navbar-nav d-flex flex-row flex-wrap gap-3 mb-3 mb-lg-0">
                        <li class="nav-item">
                            <a href="adminDashboard.jsp" class="nav-link active">Dashboard</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" id="adminPages" data-bs-toggle="dropdown">Request</a>
                            <ul class="dropdown-menu">
                                <li><a href="manageProducts.jsp" class="dropdown-item">Export Request</a></li>
                                <li><a href="manageUsers.jsp" class="dropdown-item">Purchase Request</a></li>
                                <li><a href="manageOrders.jsp" class="dropdown-item">Repair Request</a></li>
                            </ul>
                        </li>
                        <li class="nav-item">
                            <a href="profile" class="nav-link">Profile</a>
                        </li>
                        <li class="nav-item">
                            <a href="support.jsp" class="nav-link">Support</a>
                        </li>
                    </ul>

                    <div class="d-none d-lg-flex align-items-center">
                        <a href="Profile.jsp" class="text-dark mx-2">
                            <iconify-icon icon="healthicons:person" class="fs-4"></iconify-icon>
                        </a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>
