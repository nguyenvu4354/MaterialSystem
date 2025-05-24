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
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
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
                        <button type="button" class="btn btn-primary" onclick="window.location.href = 'addMaterial'">Add New Material</button>
                    </div>

                    <!-- Filter Form -->
                    <div class="search-filter-container mb-4">
                        <form method="get" action="dashboard" class="d-flex flex-wrap gap-3 align-items-center bg-white p-4 rounded-3 shadow-sm">
                            <div class="flex-grow-1" style="max-width: 400px;">
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-end-0">
                                        <i class="fas fa-search text-muted"></i>
                                    </span>
                                    <input type="text" 
                                           name="search" 
                                           class="form-control border-start-0 ps-0" 
                                           placeholder="Search by name or code" 
                                           value="${param.search}"
                                           style="box-shadow: none;">
                                </div>
                            </div>

                            <div class="flex-shrink-0" style="min-width: 150px;">
                                <select name="status" class="form-select" style="min-width: 150px;">
                                    <option value="">All Status</option>
                                    <option value="NEW" ${param.status == 'NEW' ? 'selected' : ''}>New</option>
                                    <option value="USED" ${param.status == 'USED' ? 'selected' : ''}>Used</option>
                                    <option value="DAMAGED" ${param.status == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                                </select>
                            </div>

                            <div class="ms-auto d-flex gap-2">
                                <button type="submit" class="btn btn-primary px-4">
                                    <i class="fas fa-search me-2"></i>Search
                                </button>
                                <a href="dashboard" class="btn btn-light px-4">
                                    <i class="fas fa-redo-alt me-2"></i>Clear
                                </a>
                            </div>
                        </form>
                    </div>

                    <!-- Materials Table -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th>Image</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Price</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${materials}" var="material">
                                    <tr>
                                        <td style="width: 80px">
                                            <img src="${material.materialsUrl}" 
                                                 alt="${material.materialName}"
                                                 onerror="this.src='https://placehold.co/80x80?text=${material.materialCode}'"
                                                 class="img-fluid rounded"
                                                 style="width: 80px; height: 80px; object-fit: cover;">
                                        </td>
                                        <td>${material.materialCode}</td>
                                        <td class="text-start">${material.materialName}</td>
                                        <td class="text-end">
                                            <fmt:formatNumber value="${material.price}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                                        </td>
                                        <td>
                                            <span class="badge rounded-pill status-badge ${material.materialStatus == 'NEW' ? 'status-new' : material.materialStatus == 'USED' ? 'status-used' : 'status-damaged'}">
                                                ${material.materialStatus}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="viewmaterial?id=${material.materialId}" class="btn btn-sm action-button view-button" title="View">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="editmaterial?id=${material.materialId}" class="btn btn-sm action-button edit-button" title="Edit">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="deleteMaterial?id=${material.materialId}" 
                                                   class="btn btn-sm action-button delete-button"
                                                   onclick="return confirm('Are you sure you want to delete this material?')"
                                                   title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="d-flex justify-content-center mt-3">
                        <nav>
                            <ul class="pagination">
                                <!-- Previous -->
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="${baseUrl}?page=${currentPage - 1}${searchParam}${statusParam}">Previous</a>
                                    </li>
                                </c:if>

                                <!-- Page numbers -->
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${baseUrl}?page=${i}${searchParam}${statusParam}">${i}</a>
                                    </li>
                                </c:forEach>

                                <!-- Next -->
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="${baseUrl}?page=${currentPage + 1}${searchParam}${statusParam}">Next</a>
                                    </li>
                                </c:if>

                            </ul>
                        </nav>
                    </div>
                </div> <!-- end content -->
            </div> <!-- end row -->
        </div> <!-- end container-fluid -->

        <!-- Footer -->
        <footer class="footer py-4 bg-light mt-auto">
            <div class="container text-center">
                <span class="text-muted">&copy; 2025 Computer Accessories - All Rights Reserved.</span>
            </div>
        </footer>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
        <style>
            .search-filter-container .form-control:focus,
            .search-filter-container .form-select:focus {
                border-color: #dee2e6;
                box-shadow: none;
            }

            .search-filter-container .input-group-text {
                border-color: #dee2e6;
            }

            .search-filter-container .form-control,
            .search-filter-container .form-select {
                border-color: #dee2e6;
                padding: 0.6rem 1rem;
            }

            .search-filter-container .btn {
                padding: 0.6rem 1.5rem;
                font-weight: 500;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 0.5rem;
            }

            .search-filter-container .btn-light {
                background: #f8f9fa;
                border-color: #dee2e6;
            }

            .search-filter-container .btn-light:hover {
                background: #e9ecef;
            }

            /* Responsive adjustments */
            @media (max-width: 768px) {
                .search-filter-container form {
                    gap: 1rem !important;
                }

                .search-filter-container .flex-grow-1 {
                    max-width: 100% !important;
                }

                .search-filter-container .ms-auto {
                    width: 100%;
                }

                .search-filter-container .btn {
                    flex: 1;
                }
            }

            /* Status Badge Styles */
            .status-badge {
                padding: 8px 12px;
                font-weight: 500;
                font-size: 0.85rem;
                text-transform: capitalize;
            }

            .status-new {
                background-color: #00b4d8;
                color: white;
            }

            .status-used {
                background-color: #3a86ff;
                color: white;
            }

            .status-damaged {
                background-color: #ffd60a;
                color: #000;
            }

            /* Action Buttons Styles */
            .action-buttons {
                display: flex;
                gap: 8px;
                justify-content: center;
            }

            .action-button {
                width: 32px;
                height: 32px;
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 6px;
                transition: all 0.2s ease;
                border: none;
            }

            .action-button:hover {
                transform: translateY(-2px);
            }

            .view-button {
                background-color: #0dcaf0;
                color: white;
            }

            .view-button:hover {
                background-color: #0bacce;
                color: white;
            }

            .edit-button {
                background-color: #ffc107;
                color: #000;
            }

            .edit-button:hover {
                background-color: #e0a800;
                color: #000;
            }

            .delete-button {
                background-color: #dc3545;
                color: white;
            }

            .delete-button:hover {
                background-color: #bb2d3b;
                color: white;
            }

            /* Table Styles */
            .table {
                margin-bottom: 0;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                overflow: hidden;
            }

            .table th {
                background-color: #f8f9fa;
                border-bottom: 2px solid #dee2e6;
                font-weight: 600;
                color: #495057;
            }

            .table td {
                vertical-align: middle;
            }

            .table tbody tr:hover {
                background-color: #f8f9fa;
            }
        </style>
    </body>
</html> 