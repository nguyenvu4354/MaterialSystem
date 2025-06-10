<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Computer Accessories</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #fff; }
        .material-active { background-color: #fff; }
        .material-disabled { background-color: rgba(220, 53, 69, 0.1); }
        .search-box { margin-bottom: 20px; }
        .pagination { justify-content: center; margin-top: 20px; }
        .material-image { width: 48px; height: 48px; object-fit: cover; border-radius: 4px; }
        .status-badge { padding: 5px 10px; border-radius: 15px; font-size: 0.85em; }
        .status-new { background-color: #d1e7dd; color: #0f5132; }
        .status-used { background-color: #fff3cd; color: #664d03; }
        .status-damaged { background-color: #f8d7da; color: #842029; }
        .custom-btn {
            background-color: #d6a354;
            color: #fff;
            border: none;
            border-radius: 0px;
            font-weight: 600;
            padding: 10px 24px;
            transition: background 0.2s;
        }
        .custom-btn:hover {
            background-color: #b8860b;
            color: #fff;
        }
        .form-control, .form-select {
            border-radius: 0px;
            border: 1px solid #e0c48c;
            padding: 10px 16px;
            font-size: 1rem;
        }
        .material-title {
            color: #d6a354 !important;
            font-weight: bold;
            display: flex;
            align-items: center;
        }
        .btn-action { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; margin: 0 2px; }
        .condition-bar { height: 5px; background-color: #e9ecef; border-radius: 3px; margin-top: 5px; }
        .condition-fill { height: 100%; border-radius: 3px; transition: width 0.3s ease; }
        .condition-good { background-color: #28a745; }
        .condition-warning { background-color: #ffc107; }
        .condition-bad { background-color: #dc3545; }
    </style>
</head>
<body>
<header class="container py-2">
    <div class="row py-4 align-items-center">
        <div class="col-sm-4 col-lg-3">
            <a href="HomePage.jsp" class="text-decoration-none">
                <h2 class="text-primary"><i class="fas fa-dollar-sign"></i> Material Management</h2>
            </a>
        </div>
        <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
            <div class="text-end">
                <span class="fs-6 text-muted">Admin</span>
                <h5 class="mb-0">admin@accessories.com</h5>
            </div>
            <a href="logout" class="btn btn-outline-dark btn-lg ms-4">Logout</a>
        </div>
    </div>
</header>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3 col-lg-2 bg-light p-0">
            <jsp:include page="Sidebar.jsp" />
        </div>
        <div class="col-md-9 col-lg-10 px-md-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="material-title display-6 border-bottom pb-2">
                    <i class="fas fa-box me-2"></i> Material List
                </h2>
                <a href="addmaterial" class="btn custom-btn">
                    <i class="fas fa-plus"></i> Add New Material
                </a>
            </div>
            <form action="dashboardmaterial" method="GET" class="row g-2 align-items-end mb-4">
                <div class="col-md-4">
                    <label for="keyword" class="form-label fw-bold">Search</label>
                    <input type="text" id="keyword" name="keyword" value="${keyword}" class="form-control" placeholder="Search materials...">
                </div>
                <div class="col-md-3">
                    <label for="status" class="form-label fw-bold">Status</label>
                    <select id="status" name="status" class="form-select">
                        <option value="">All Status</option>
                        <option value="NEW" ${status == 'NEW' ? 'selected' : ''}>New</option>
                        <option value="USED" ${status == 'USED' ? 'selected' : ''}>Used</option>
                        <option value="DAMAGED" ${status == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label for="sortOption" class="form-label fw-bold">Sort By</label>
                    <select id="sortOption" name="sortOption" class="form-select">
                        <option value="">Default</option>
                        <option value="name_asc" ${sortOption == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                        <option value="name_desc" ${sortOption == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                        <option value="code_asc" ${sortOption == 'code_asc' ? 'selected' : ''}>Code (A-Z)</option>
                        <option value="code_desc" ${sortOption == 'code_desc' ? 'selected' : ''}>Code (Z-A)</option>
                        <option value="condition_asc" ${sortOption == 'condition_asc' ? 'selected' : ''}>Condition (Low-High)</option>
                        <option value="condition_desc" ${sortOption == 'condition_desc' ? 'selected' : ''}>Condition (High-Low)</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid">
                    <button type="submit" class="btn custom-btn btn-lg rounded-pill">
                        <i class="fas fa-search"></i> Search
                    </button>
                </div>
            </form>
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Image</th>
                            <th>Code</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th>Price</th>
                            <th>Condition</th>
                            <th>Created At</th>
                            <th>Updated At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${list}" var="material">
                            <tr>
                                <td><img src="${material.materialsUrl}" alt="${material.materialCode}" class="material-image"></td>
                                <td>${material.materialCode}</td>
                                <td>${material.materialName}</td>
                                <td><span class="status-badge ${material.materialStatus == 'NEW' ? 'status-new' : material.materialStatus == 'USED' ? 'status-used' : 'status-damaged'}">${material.materialStatus}</span></td>
                                <td><fmt:formatNumber value="${material.price}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="3"/></td>
                                <td>
                                    <div class="condition-bar">
                                        <div class="condition-fill ${material.conditionPercentage >= 70 ? 'condition-good' : material.conditionPercentage >= 40 ? 'condition-warning' : 'condition-bad'}" style="width: ${material.conditionPercentage}%;"></div>
                                    </div>
                                </td>
                                <td><fmt:formatDate value="${material.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td><fmt:formatDate value="${material.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>
                                    <div class="d-flex">
                                        <a href="${pageContext.request.contextPath}/viewmaterial?materialId=${material.materialId}" class="btn btn-info btn-action" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <a href="editmaterial?materialId=${material.materialId}" class="btn btn-warning btn-action" title="Edit Material">
                                            <i class="fas fa-pen"></i>
                                        </a>
                                        <form method="post" action="deletematerial" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa vật tư này?');">
                                            <input type="hidden" name="materialId" value="${material.materialId}" />
                                            <button type="submit" class="btn btn-danger btn-action" title="Delete">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty list}">
                            <tr><td colspan="7" class="text-center text-muted">No materials found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <nav aria-label="Page navigation">
                <ul class="pagination">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="dashboardmaterial?page=${currentPage - 1}&search=${searchTerm}&status=${selectedStatus}&sortBy=${sortBy}" aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                            <a class="page-link" href="dashboardmaterial?page=${i}&search=${searchTerm}&status=${selectedStatus}&sortBy=${sortBy}">${i}</a>
                        </li>
                    </c:forEach>
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
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>