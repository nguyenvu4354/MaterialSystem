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
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
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
        .ui-autocomplete {
            max-height: 200px;
            overflow-y: auto;
            overflow-x: hidden;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            background-color: #fff;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
            z-index: 1000;
        }
        .ui-menu-item {
            padding: 8px 12px;
            font-size: 1rem;
            cursor: pointer;
        }
        .ui-menu-item:hover {
            background-color: #f8f9fa;
        }
        .ui-menu-item div {
            display: flex;
            justify-content: space-between;
        }
        .ui-menu-item .code {
            color: #6c757d;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>
            <div class="col-md-9 col-lg-10 content px-md-4">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_LIST_MATERIAL')}">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="bi bi-box"></i> Material List</h2>
                        <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'CREATE_MATERIAL')}">
                            <a href="${pageContext.request.contextPath}/addmaterial" class="btn btn-primary">
                                <i class="fas fa-plus me-1"></i> Add New Material
                            </a>
                        </c:if>
                    </div>
                    <div class="row search-box">
                        <div class="col-md-12">
                            <form id="searchForm" method="get" action="dashboardmaterial" class="d-flex gap-2 align-items-center">
                                <div class="position-relative">
                                    <input type="text" id="materialSearch" name="materialSearch" class="form-control" placeholder="Tên hoặc mã vật tư..." autocomplete="off" style="width: 220px; height: 50px; border: 2px solid gray" value="${param.materialSearch != null ? param.materialSearch : ''}" />
                                    <input type="hidden" id="materialId" name="materialId" value="" />
                                </div>
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
                                </select>
                                <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                    <i class="fas fa-search me-2"></i> Search
                                </button>
                                <a href="dashboardmaterial" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px">Clear</a>
                            </form>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col">Image</th>
                                    <th scope="col" style="width: 120px">Code</th>
                                    <th scope="col" style="width: 200px">Name</th>
                                    <th scope="col" style="width: 100px">Status</th>
                                    <th scope="col" style="width: 150px">Category</th>
                                    <th scope="col" style="width: 150px">Created At</th>
                                    <th scope="col" style="width: 150px">Updated At</th>
                                    <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_DETAIL_MATERIAL') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_MATERIAL') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_MATERIAL')}">
                                        <th scope="col">Actions</th>
                                    </c:if>
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
                                                <td>${material.materialCode != null ? material.materialCode : 'N/A'}</td>
                                                <td>${material.materialName != null ? material.materialName : 'N/A'}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${material.materialStatus == 'new'}">
                                                            New
                                                        </c:when>
                                                        <c:when test="${material.materialStatus == 'used'}">
                                                            Used
                                                        </c:when>
                                                        <c:otherwise>
                                                            Damaged
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${material.category != null ? material.category.category_name : 'N/A'}</td>
                                                <td><fmt:formatDate value="${material.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                                <td><fmt:formatDate value="${material.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                                <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_DETAIL_MATERIAL') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_MATERIAL') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_MATERIAL')}">
                                                    <td>
                                                        <div class="d-flex justify-content-center">
                                                            <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_DETAIL_MATERIAL')}">
                                                                <a href="${pageContext.request.contextPath}/viewmaterial?materialId=${material.materialId}" 
                                                                   class="btn btn-info btn-action" 
                                                                   title="View Details">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                            </c:if>
                                                            <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_MATERIAL')}">
                                                                <a href="${pageContext.request.contextPath}/editmaterial?materialId=${material.materialId}" 
                                                                   class="btn btn-warning btn-action" 
                                                                   title="Edit Material">
                                                                    <i class="fas fa-edit"></i>
                                                                </a>
                                                            </c:if>
                                                            <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_MATERIAL')}">
                                                                <form method="post" action="${pageContext.request.contextPath}/deletematerial" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this material?');">
                                                                    <input type="hidden" name="materialId" value="${material.materialId}" />
                                                                    <button type="submit" class="btn btn-danger btn-action" title="Delete Material">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="8">No materials found.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <nav>
                            <ul class="pagination">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${currentPage - 1}&materialSearch=${materialSearch}&status=${status}&sortOption=${sortOption}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="dashboardmaterial?page=${i}&materialSearch=${materialSearch}&status=${status}&sortOption=${sortOption}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="dashboardmaterial?page=${currentPage + 1}&materialSearch=${materialSearch}&status=${status}&sortOption=${sortOption}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:if>
            </div>
        </div>
    </div>
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
        document.addEventListener('DOMContentLoaded', function() {
            const materials = [
                <c:forEach var="material" items="${materials}" varStatus="loop">
                    {
                        label: "${fn:escapeXml(material.materialName)}",
                        value: "${fn:escapeXml(material.materialName)}",
                        id: "${material.materialId}",
                        code: "${fn:escapeXml(material.materialCode)}"
                    }${loop.last ? '' : ','}
                </c:forEach>
            ];
            $("#materialSearch").autocomplete({
                source: function(request, response) {
                    const term = request.term.toLowerCase();
                    const matches = materials.filter(material =>
                        material.label.toLowerCase().includes(term) ||
                        material.code.toLowerCase().includes(term)
                    );
                    response(matches);
                },
                select: function(event, ui) {
                    document.getElementById('materialId').value = ui.item.id;
                    document.getElementById('materialSearch').value = ui.item.value;
                    document.getElementById('materialSearch').classList.remove('is-invalid');
                    document.getElementById('searchForm').submit();
                },
                change: function(event, ui) {
                    if (!ui.item) {
                        const inputValue = document.getElementById('materialSearch').value.toLowerCase().trim();
                        const selectedMaterial = materials.find(material =>
                            material.label.toLowerCase() === inputValue ||
                            material.code.toLowerCase() === inputValue
                        );
                        if (selectedMaterial) {
                            document.getElementById('materialId').value = selectedMaterial.id;
                            document.getElementById('materialSearch').value = selectedMaterial.label;
                            document.getElementById('materialSearch').classList.remove('is-invalid');
                        } else {
                            document.getElementById('materialId').value = '';
                            document.getElementById('materialSearch').classList.add('is-invalid');
                        }
                        document.getElementById('searchForm').submit();
                    }
                },
                minLength: 0,
                open: function() {
                    $(this).autocomplete('widget').css('z-index', 1000);
                },
                create: function() {
                    $(this).data('ui-autocomplete')._renderItem = function(ul, item) {
                        return $('<li>')
                            .append('<div>' + item.label + '<span class="code">' + item.code + '</span></div>')
                            .appendTo(ul);
                    };
                }
            }).focus(function() {
                $(this).autocomplete("search", $(this).val());
            });
        });
    </script>
</body>
</html>