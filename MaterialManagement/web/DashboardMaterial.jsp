<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Material Management Dashboard - Computer Accessories</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .table-responsive {
            margin: 20px 0;
        }
        .search-box {
            margin-bottom: 20px;
        }
        .pagination {
            justify-content: center;
            margin-top: 20px;
        }
        .btn-action {
            width: 50px;
            height: 32px;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 2px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .custom-search {
            max-width: 400px;
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
        .status-new { 
            background-color: #d1e7dd; 
            color: #0f5132; 
        }
        .status-used { 
            background-color: #fff3cd; 
            color: #664d03; 
        }
        .status-damaged { 
            background-color: #f8d7da; 
            color: #842029; 
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
            background-color: #28a745; 
        }
        .condition-warning { 
            background-color: #ffc107; 
        }
        .condition-bad { 
            background-color: #dc3545; 
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="HeaderAdmin.jsp" />

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
                    <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="bi bi-box"></i> Material List</h2>
                    <c:if test="${!readonly}">
                        <a href="${pageContext.request.contextPath}/addmaterial" class="btn btn-primary">
                            <i class="fas fa-plus me-1"></i> Add New Material
                        </a>
                    </c:if>
                </div>

                <!-- Search and Filter Section -->
                <div class="row search-box">
                    <div class="col-md-8">
                        <form method="get" action="dashboardmaterial" class="d-flex gap-2 align-items-center">
                            <input type="text" name="keyword" class="form-control" 
                                   placeholder="Search by Name" 
                                   value="${keyword != null ? keyword : ''}" 
                                   style="width: 200px; height: 50px; border: 2px solid gray"/>
                            <select name="status" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">All Status</option>
                                <option value="NEW" ${status == 'NEW' ? 'selected' : ''}>New</option>
                                <option value="USED" ${status == 'USED' ? 'selected' : ''}>Used</option>
                                <option value="DAMAGED" ${status == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                            </select>
                            <select name="sortOption" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">Sort By</option>
                                <option value="name_asc" ${sortOption == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="name_desc" ${sortOption == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="code_asc" ${sortOption == 'code_asc' ? 'selected' : ''}>Code (A-Z)</option>
                                <option value="code_desc" ${sortOption == 'code_desc' ? 'selected' : ''}>Code (Z-A)</option>
                                <option value="condition_asc" ${sortOption == 'condition_asc' ? 'selected' : ''}>Condition (Low-High)</option>
                                <option value="condition_desc" ${sortOption == 'condition_desc' ? 'selected' : ''}>Condition (High-Low)</option>
                            </select>
                            <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="dashboardmaterial" class="btn btn-secondary" style="width: 150px; height: 50px">Clear</a>
                        </form>
                    </div>
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Material Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">Image</th>
                                <th scope="col" style="width: 120px">Code</th>
                                <th scope="col" style="width: 200px">Name</th>
                                <th scope="col" style="width: 100px">Status</th>
                                <th scope="col" style="width: 120px">Price</th>
                                <th scope="col" style="width: 150px">Condition</th>
                                <th scope="col" style="width: 150px">Category</th>
                                <th scope="col" style="width: 150px">Created At</th>
                                <th scope="col" style="width: 150px">Updated At</th>
                                <th scope="col">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty list}">
                                    <c:forEach items="${list}" var="material">
                                        <tr>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(material.materialsUrl, 'http://') || fn:startsWith(material.materialsUrl, 'https://')}">
                                                        <img src="${material.materialsUrl}" alt="${material.materialCode}" class="material-image">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="images/material/${material.materialsUrl}" alt="${material.materialCode}" class="material-image">
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${material.materialCode}</td>
                                            <td>${material.materialName}</td>
                                            <td>
                                                <span class="status-badge ${material.materialStatus == 'NEW' ? 'status-new' : material.materialStatus == 'USED' ? 'status-used' : 'status-damaged'}">
                                                    ${material.materialStatus}
                                                </span>
                                            </td>
                                            <td><fmt:formatNumber value="${material.price}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="3"/></td>
                                            <td>
                                                <div class="condition-bar">
                                                    <div class="condition-fill ${material.conditionPercentage >= 70 ? 'condition-good' : material.conditionPercentage >= 40 ? 'condition-warning' : 'condition-bad'}" 
                                                         style="width: ${material.conditionPercentage}%;"></div>
                                                </div>
                                                <small class="text-muted">${material.conditionPercentage}%</small>
                                            </td>
                                            <td>${material.category != null ? material.category.category_name : 'N/A'}</td>
                                            <td><fmt:formatDate value="${material.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                            <td><fmt:formatDate value="${material.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                            <td>
                                                <div class="d-flex justify-content-center">
                                                    <a href="${pageContext.request.contextPath}/viewmaterial?materialId=${material.materialId}" 
                                                       class="btn btn-info btn-action" 
                                                       title="View Details">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <c:if test="${!readonly}">
                                                        <a href="editmaterial?materialId=${material.materialId}" 
                                                           class="btn btn-warning btn-action" 
                                                           title="Edit Material">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <form method="post" action="deletematerial" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa vật tư này?');">
                                                            <input type="hidden" name="materialId" value="${material.materialId}" />
                                                            <button type="submit" class="btn btn-danger btn-action" title="Delete">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="10" class="text-center text-muted">No materials found.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav>
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="dashboardmaterial?page=${currentPage - 1}&keyword=${keyword}&status=${status}&sortOption=${sortOption}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${i}&keyword=${keyword}&status=${status}&sortOption=${sortOption}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="dashboardmaterial?page=${currentPage + 1}&keyword=${keyword}&status=${status}&sortOption=${sortOption}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div> <!-- end content -->
        </div> <!-- end row -->
    </div> <!-- end container-fluid -->

    <!-- Footer -->
    <jsp:include page="Footer.jsp" />

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
</body>
</html>