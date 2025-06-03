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
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">

        <style>
            body {
                background-color: #f8f9fa;
                padding: 20px;
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
                width: 50px;
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
            .content {
                padding-left: 20px;
                font-family: 'Roboto', sans-serif;
            }
            .custom-search {
                max-width: 400px;
            }
            .btn-gold {
                background: #e2b77a;
                color: #fff;
                border: none;
                border-radius: 10px;
                padding: 12px 36px;
                font-size: 1.1rem;
                font-weight: 500;
                transition: background 0.2s;
            }
            .btn-gold:hover {
                background: #cfa45e;
                color: #fff;
            }
            .btn-grey {
                background: #7b868e;
                color: #fff;
                border: none;
                border-radius: 10px;
                padding: 12px 24px;
                font-size: 1.1rem;
                font-weight: 500;
            }
            .dashboard-title {
                color: #e2b77a;
                font-size: 2.5rem;
                font-weight: 700;
                display: flex;
                align-items: center;
                gap: 10px;
            }
        </style>
    </head>
    <body>
        <!-- Header -->
        <header>
            <div class="container-fluid py-2">
                <div class="row align-items-center">
                    <div class="col-6 d-flex align-items-center">
                        <img src="images/logo.png" alt="logo" style="height:60px;">
                        <span style="font-size:2.2rem; font-weight:600; color:#b88c4a; margin-left:12px;">Material Management</span>
                    </div>
                    <div class="col-6 d-flex justify-content-end align-items-center">
                        <div class="text-end me-4">
                            <span style="font-size:1rem; color:#888;">Admin</span><br>
                            <span style="font-size:1.1rem; color:#333;">admin@accessories.com</span>
                        </div>
                        <a href="logout" class="btn btn-outline-dark" style="font-size:1.3rem; border-radius:10px; padding:10px 36px; margin-left:10px;">LOGOUT</a>
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
                <div class="col-md-9 col-lg-10 content px-md-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="dashboard-title">
                            <i class="fas fa-boxes" style="color:#b88c4a;"></i> Material List
                        </div>
                        <a href="MaterialServlet?action=edit" class="btn btn-gold">Add New Material</a>
                    </div>

                    <!-- Search and Filter Section -->
                    <div class="row search-box">
                        <div class="col-md-8 d-flex gap-2">
                            <form action="MaterialServlet" method="GET" class="d-flex gap-2 w-100">
                                <input type="hidden" name="action" value="list" />
                                <input type="text" name="keyword" class="form-control" 
                                       placeholder="Search by name..." 
                                       value="${keyword != null ? keyword : ''}" 
                                       style="width: 200px; height: 50px; border: 2px solid gray" />
                                <input type="text" name="code" class="form-control" 
                                       placeholder="Search by code..." 
                                       value="${code != null ? code : ''}" 
                                       style="width: 200px; height: 50px; border: 2px solid gray" />
                                <select name="sortBy" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                    <option value="">Sort By</option>
                                    <option value="name_asc" ${sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                    <option value="name_desc" ${sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                    <option value="code_asc" ${sortBy == 'code_asc' ? 'selected' : ''}>Code (A-Z)</option>
                                    <option value="code_desc" ${sortBy == 'code_desc' ? 'selected' : ''}>Code (Z-A)</option>
                                </select>
                                <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                    <i class="fas fa-search me-2"></i> Search
                                </button>
                                <a href="MaterialServlet?action=list" class="btn btn-secondary" style="width: 150px; height: 50px">Clear</a>
                            </form>
                        </div>
                    </div>

                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <!-- Material Table -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col" style="width: 150px">Material Name</th>
                                    <th scope="col" style="width: 150px">Code</th>
                                    <th scope="col" style="width: 150px">Category</th>
                                    <th scope="col" style="width: 150px">Supplier</th>
                                    <th scope="col" style="width: 150px">Quantity</th>
                                    <th scope="col" style="width: 150px">Unit Price</th>
                                    <th scope="col" style="width: 200px">Description</th>
                                    <th scope="col">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="m" items="${materialList}">
                                    <tr>
                                        <td>${m.materialId}</td>
                                        <td>${m.materialName}</td>
                                        <td>${m.code}</td>
                                        <td>${m.categoryName}</td>
                                        <td>${m.supplierName}</td>
                                        <td>${m.quantity}</td>
                                        <td>${m.unitPrice}</td>
                                        <td>${m.description}</td>
                                        <td>
                                            <div class="d-flex justify-content-center">
                                                <a href="MaterialServlet?action=edit&id=${m.materialId}" 
                                                   class="btn btn-warning btn-action" 
                                                   title="Edit">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="MaterialServlet?action=delete&id=${m.materialId}" 
                                                   class="btn btn-danger btn-action" 
                                                   onclick="return confirm('Are you sure you want to delete this material?');" 
                                                   title="Delete">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty materialList}">
                                    <tr>
                                        <td colspan="9" class="text-center text-muted">No materials found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
