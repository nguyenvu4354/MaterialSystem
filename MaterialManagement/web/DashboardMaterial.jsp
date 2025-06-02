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
            body {
                background: #fff !important;
            }
            /* Tùy chỉnh các lớp CSS cho bảng, trạng thái, button, ... */
            .table-responsive {
                margin: 20px 0;
            }
            .material-active {
                background-color: #fff !important; /* Màu nền trắng cho vật tư active */
            }
            .material-disabled {
                background-color: rgba(220, 53, 69, 0.1) !important; /* Màu nền đỏ nhạt cho vật tư đã disabled */
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
                object-fit: cover; /* Giữ tỉ lệ ảnh */
                border-radius: 4px;
            }
            .status-badge {
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 0.85em;
            }
            .status-new { background-color: #d1e7dd; color: #0f5132; } /* Màu xanh cho trạng thái New */
            .status-used { background-color: #fff3cd; color: #664d03; } /* Màu vàng cho trạng thái Used */
            .status-damaged { background-color: #f8d7da; color: #842029; } /* Màu đỏ cho trạng thái Damaged */
            .btn-action {
                width: 32px;
                height: 32px;
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 2px;
            }
            .material-card {
                transition: transform 0.2s;
            }
            .material-card:hover {
                transform: translateY(-5px);
            }
            .condition-bar {
                height: 5px;
                background-color: #e9ecef;
                border-radius: 3px;
                margin-top: 5px;
            }
            .condition-fill {
                height: 100%;
                border-radius: 3px;
                transition: width 0.3s ease;
            }
            .condition-good {
                background-color: #28a745; /* Màu xanh cho tình trạng tốt */
            }
            .condition-warning {
                background-color: #ffc107; /* Màu vàng cho tình trạng cảnh báo */
            }
            .condition-bad {
                background-color: #dc3545; /* Màu đỏ cho tình trạng xấu */
            }
            .text-primary {
                color: #DEAD6F !important;
            }
            .btn-primary {
                background-color: #DEAD6F !important;
                border-color: #DEAD6F !important;
                color: #fff !important;
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
        <div style="height: 90px;"></div>

        <!-- Nội dung chính -->
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar menu -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>

                <!-- Nội dung trang chính -->
                <div class="col-md-9 col-lg-10 px-md-4">
                    <!-- Tiêu đề và nút thêm vật tư mới -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2">📦 Material List</h2>
                        <a href="addmaterial" class="btn" style="background:#DEAD6F; color:#fff; border:none; border-radius:0.5rem; padding:1.2rem 3rem; font-size:1.25rem; min-width:320px;">Add New Material</a>
                    </div>

                    <!-- Filter Form a-->
                    <form method="get" action="dashboardmaterial" class="mb-3 d-flex flex-wrap gap-2 align-items-center">
                        <input type="text" name="search" class="form-control" placeholder="Search by name" value="${searchTerm}"
                            style="height:56px; font-size:1.25rem; border-radius:0.25rem; padding:1.25rem 0 1.25rem 1.25rem; max-width:350px; border:1px solid rgba(65,64,62,0.20); color:#908F8D; text-transform:capitalize; letter-spacing:0.02125rem;"/>
                        <select name="status" class="form-select" style="max-width: 150px;">
                            <option value="">All Status</option>
                            <option value="NEW" ${selectedStatus == 'NEW' ? 'selected' : ''}>New</option>
                            <option value="USED" ${selectedStatus == 'USED' ? 'selected' : ''}>Used</option>
                            <option value="DAMAGED" ${selectedStatus == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                        </select>
                        <select name="sortBy" class="form-select" style="max-width: 150px;">
                            <option value="">Sort By</option>
                            <option value="name_asc" ${sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                            <option value="name_desc" ${sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                            <option value="price_asc" ${sortBy == 'price_asc' ? 'selected' : ''}>Price (Low-High)</option>
                            <option value="price_desc" ${sortBy == 'price_desc' ? 'selected' : ''}>Price (High-Low)</option>
                            <option value="quantity_asc" ${sortBy == 'quantity_asc' ? 'selected' : ''}>Quantity (Low-High)</option>
                            <option value="quantity_desc" ${sortBy == 'quantity_desc' ? 'selected' : ''}>Quantity (High-Low)</option>
                            <option value="condition_asc" ${sortBy == 'condition_asc' ? 'selected' : ''}>Condition (Low-High)</option>
                            <option value="condition_desc" ${sortBy == 'condition_desc' ? 'selected' : ''}>Condition (High-Low)</option>
                        </select>
                        <button type="submit" class="btn" style="background:#DEAD6F; color:#fff; border:none; border-radius:0.5rem; padding:1.2rem 3rem; font-size:1.25rem;">Search</button>
                        <a href="dashboardmaterial" class="btn btn-secondary">Clear</a>
                    </form>

                    <!-- Bảng danh sách vật tư -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
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
                                <!-- Duyệt từng vật tư trong danh sách materials -->
                                <c:forEach items="${materials}" var="material">
                                    <tr class="${material.disable ? 'material-disabled' : 'material-active'}">
                                        <!-- Ảnh đại diện vật tư -->
                                        <td>
                                            <img src="${material.materialsUrl}" alt="${material.materialCode}" class="material-image">
                                        </td>

                                        <!-- Mã vật tư -->
                                        <td>${material.materialCode}</td>

                                        <!-- Tên vật tư -->
                                        <td>${material.materialName}</td>

                                        <!-- Trạng thái vật tư với màu sắc theo trạng thái -->
                                        <td>
                                            <span class="badge rounded-pill px-3 py-2 ${material.materialStatus == 'NEW' ? 'bg-success-subtle text-success' : material.materialStatus == 'USED' ? 'bg-warning-subtle text-warning' : 'bg-danger-subtle text-danger'}">
                                                ${material.materialStatus}
                                            </span>
                                        </td>

                                        <!-- Số lượng vật tư -->
                                        <td>${material.quantity}</td>

                                        <!-- Giá vật tư, định dạng tiền tệ -->
                                        <td>
                                            <fmt:formatNumber value="${material.price}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="2"/>
                                        </td>

                                        <!-- Thanh thể hiện % tình trạng vật tư -->
                                        <td>
                                            <div class="progress" style="height: 8px;">
                                                <c:choose>
                                                    <c:when test="${material.conditionPercentage >= 70}">
                                                        <div class="progress-bar bg-success" role="progressbar" style="width: ${material.conditionPercentage}%;" aria-valuenow="${material.conditionPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                                    </c:when>
                                                    <c:when test="${material.conditionPercentage >= 40}">
                                                        <div class="progress-bar bg-warning" role="progressbar" style="width: ${material.conditionPercentage}%;" aria-valuenow="${material.conditionPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="progress-bar bg-danger" role="progressbar" style="width: ${material.conditionPercentage}%;" aria-valuenow="${material.conditionPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>

                                        <!-- Các hành động với vật tư: xem chi tiết, chỉnh sửa -->
                                        <td>
                                            <div class="d-flex justify-content-center gap-1">
                                                <a href="viewmaterial?id=${material.materialId}" class="btn" style="background:#DEAD6F; color:#fff; border:none; border-radius:0.5rem; padding:0.7rem 2rem; font-size:1rem;">View</a>
                                                <a href="editmaterial?id=${material.materialId}" class="btn" style="background:#6c757d; color:#fff; border:none; border-radius:0.5rem; padding:0.7rem 2rem; font-size:1rem;">Edit</a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <!-- Nếu không có vật tư nào thì hiển thị dòng thông báo -->
                                <c:if test="${empty materials}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted">No materials found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <!-- Phân trang -->
                    <nav aria-label="Page navigation example">
                        <ul class="pagination">
                            <!-- Nút trang trước -->
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="dashboardmaterial?page=${currentPage - 1}&search=${searchTerm}&status=${selectedStatus}&sortBy=${sortBy}" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>

                            <!-- Vòng lặp các trang -->
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${i}&search=${searchTerm}&status=${selectedStatus}&sortBy=${sortBy}">${i}</a>
                                </li>
                            </c:forEach>

                            <!-- Nút trang tiếp theo -->
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="dashboardmaterial?page=${currentPage + 1}&search=${searchTerm}&status=${selectedStatus}&sortBy=${sortBy}" aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS Bundle (Popper + Bootstrap JS) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
