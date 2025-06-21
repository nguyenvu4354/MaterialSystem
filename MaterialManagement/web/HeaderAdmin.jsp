<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
    <div class="container py-2">
        <div class="row py-4 pb-0 pb-sm-4 align-items-center">
            <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                <div class="main-logo">
                    <a href="HomePage.jsp">
                        <img src="images/AdminLogo.png" alt="logo" class="img-fluid" width="300px">
                    </a>
                </div>
            </div>
            <div class="col-sm-8 col-lg-9 d-flex justify-content-end gap-5 align-items-center mt-4 mt-sm-0 justify-content-center justify-content-sm-end">
                <div class="support-box text-end d-none d-xl-block">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <span class="fs-6 secondary-font text-muted">${sessionScope.user.fullName}</span>
                            <h5 class="mb-0">${sessionScope.user.email}</h5>
                        </c:when>
                        <c:otherwise>
                            <span class="fs-6 secondary-font text-muted">Guest</span>
                            <h5 class="mb-0">guest@example.com</h5>
                        </c:otherwise>
                    </c:choose>
                </div>
                <a href="logout" class="btn btn-outline-dark btn-lg ms-4">
                    Logout
                </a>
            </div>
        </div>
    </div>

    <div class="container-fluid">
        <hr class="m-0">
    </div>

    <div class="container">
        <nav class="main-menu d-flex navbar navbar-expand-lg">
            <div class="d-flex d-lg-none align-items-end mt-3">
                <ul class="d-flex justify-content-end list-unstyled m-0">
                    <li>
                        <a href="adminAccount.jsp" class="mx-3">
                            <iconify-icon icon="healthicons:person" class="fs-4"></iconify-icon>
                        </a>
                    </li>
                    <li>
                        <a href="#" class="mx-3" data-bs-toggle="offcanvas" data-bs-target="#offcanvasSearch" aria-controls="offcanvasSearch">
                            <iconify-icon icon="tabler:search" class="fs-4"></iconify-icon>
                        </a>
                    </li>
                </ul>
            </div>

            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header justify-content-center">
                    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>

                <div class="offcanvas-body justify-content-between">
                    <select class="filter-categories border-0 mb-0 me-5">
                        <option>Admin Options</option>
                        <option>Manage Products</option>
                        <option>Manage Users</option>
                        <option>Manage Orders</option>
                        <option>Reports</option>
                    </select>

                    <ul class="navbar-nav menu-list list-unstyled d-flex gap-md-3 mb-0">
                        <li class="nav-item">
                            <a href="HomePage.jsp" class="nav-link active">Home</a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" role="button" id="adminPages" data-bs-toggle="dropdown" aria-expanded="false">Management</a>
                            <ul class="dropdown-menu" aria-labelledby="adminPages">
                                <li><a href="manageProducts.jsp" class="dropdown-item">Products</a></li>
                                <li><a href="manageUsers.jsp" class="dropdown-item">Users</a></li>
                                <li><a href="manageOrders.jsp" class="dropdown-item">Orders</a></li>
                                <li><a href="manageCategories.jsp" class="dropdown-item">Categories</a></li>
                                <li><a href="reports.jsp" class="dropdown-item">Reports</a></li>
                                <li><a href="settings.jsp" class="dropdown-item">Settings</a></li>
                            </ul>
                        </li>
                    </ul>

                    <div class="d-none d-lg-flex align-items-end">
                        <ul class="d-flex justify-content-end list-unstyled m-0">
                            <li>
                                <a href="profile" class="mx-3">
                                    <iconify-icon icon="healthicons:person" class="fs-4"></iconify-icon>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>