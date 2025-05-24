<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Admin Dashboard - Computer Accessories</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- Bootstrap & Custom CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            .table-responsive {
                margin: 20px 0;
            }
            .material-active {
                background-color: rgba(25, 135, 84, 0.1) !important;
            }
            .material-disabled {
                background-color: rgba(220, 53, 69, 0.1) !important;
            }
            .search-box {
                margin-bottom: 20px;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .material-image {
                width: 48px;
                height: 48px;
                object-fit: cover;
                border-radius: 4px;
            }
            .status-badge {
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 0.85em;
            }
            .status-new { background-color: #d1e7dd; color: #0f5132; }
            .status-used { background-color: #fff3cd; color: #664d03; }
            .status-damaged { background-color: #f8d7da; color: #842029; }
            .btn-action {
                width: 32px;
                height: 32px;
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 2px;
            }
        </style>
    </head>
    <body>
        <!-- Header -->
        <header>
            <div class="container py-2">
                <div class="row py-4 pb-0 pb-sm-4 align-items-center">
                    <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                        <a href="HomePage.jsp">
                            <img src="images/logo.png" alt="logo" class="img-fluid" width="300px">
                        </a>
                    </div>
                    <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                        <div class="text-end d-none d-xl-block">
                            <span class="fs-6 text-muted">Admin</span>
                            <h5 class="mb-0">admin@accessories.com</h5>
                        </div>
                        <a href="logout" class="btn btn-outline-dark btn-lg ms-4">
                            Logout
                        </a>
                    </div>
                </div>
            </div>
        </header>

        <!-- Main content -->
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>

                <!-- Page Content -->
                <div class="col-md-9 col-lg-10 px-md-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2">📦 Material List</h2>
                        <a href="creatematerial" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Add New Material
                        </a>
                    </div>

                    <!-- Search and Filter Section -->
                    <div class="row search-box">
                        <div class="col-md-8">
                            <form action="dashboardmaterial" method="GET" class="d-flex gap-2">
                                <input type="text" 
                                       name="search" 
                                       value="${searchTerm}" 
                                       class="form-control" 
                                       placeholder="Search materials...">
                                <select name="status" class="form-select" style="width: auto;">
                                    <option value="">All Status</option>
                                    <option value="NEW" ${selectedStatus == 'NEW' ? 'selected' : ''}>New</option>
                                    <option value="USED" ${selectedStatus == 'USED' ? 'selected' : ''}>Used</option>
                                    <option value="DAMAGED" ${selectedStatus == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                                </select>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search"></i> Search
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Materials Table -->
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Status</th>
                                    <th>Quantity</th>
                                    <th>Price</th>
                                    <th>Condition</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${materials}" var="material">
                                    <tr class="${material.disable ? 'material-disabled' : 'material-active'}">
                                        <td>
                                            <img src="${material.materialsUrl}" alt="${material.materialCode}" class="material-image">
                                        </td>
                                        <td>${material.materialCode}</td>
                                        <td>${material.materialName}</td>
                                        <td>
                                            <span class="status-badge ${material.materialStatus == 'NEW' ? 'status-new' : material.materialStatus == 'USED' ? 'status-used' : 'status-damaged'}">
                                                ${material.materialStatus}
                                            </span>
                                        </td>
                                        <td>${material.quantity}</td>
                                        <td>
                                            <fmt:formatNumber value="${material.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </td>
                                        <td>${material.conditionPercentage}%</td>
                                        <td>
                                            <div class="d-flex">
                                                <a href="viewmaterial?id=${material.materialId}" 
                                                   class="btn btn-info btn-action" 
                                                   title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="editmaterial?id=${material.materialId}" 
                                                   class="btn btn-warning btn-action"
                                                   title="Edit Material">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav>
                            <ul class="pagination">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${currentPage - 1}&search=${searchTerm}&status=${selectedStatus}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="dashboardmaterial?page=${i}&search=${searchTerm}&status=${selectedStatus}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${currentPage + 1}&search=${searchTerm}&status=${selectedStatus}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <footer class="footer py-4 bg-light mt-auto">
            <div class="container text-center">
                <span class="text-muted">&copy; 2025 Computer Accessories - All Rights Reserved.</span>
            </div>
        </footer>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
    </body>
</html> 