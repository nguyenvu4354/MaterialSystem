<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Category Management Dashboard - Computer Accessories</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
        .pagination .page-item.active .page-link {
            background-color: #DEAD6F;
            border-color: #DEAD6F;
            color: #fff;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

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
                    <h2 class="fw-bold display-6 border-bottom pb-2" style="color: #DEAD6F;">
                        <i class="bi bi-list-ul"></i> Category List
                    </h2>
                    <!-- Show Create Category button only if user has CREATE_CATEGORY permission -->
                    <c:if test="${sessionScope.user != null && rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'CREATE_CATEGORY')}">
                        <a href="${pageContext.request.contextPath}/Category?service=addCategory" class="btn" style="width: 180px; height: 50px; background-color: #DEAD6F; color: white;">
                            <i class="fas fa-plus me-1"></i> Create Category
                        </a>
                    </c:if>
                </div>

                <!-- Search and Filter Section -->
                <div class="row search-box">
                    <div class="col-md-8">
                        <form method="get" action="${pageContext.request.contextPath}/Category" class="d-flex gap-2 align-items-center">
                            <input type="hidden" name="service" value="listCategory"/>
                            <input type="text" name="code" class="form-control" 
                                   placeholder="Search by Code" 
                                   value="${code != null ? code : ''}" 
                                   style="width: 200px; height: 50px; border: 2px solid gray"/>
                            <!-- Đã loại bỏ trường search by name (categoryName) -->
                            <select name="priority" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">All Priorities</option>
                                <option value="high" ${priority == 'high' ? 'selected' : ''}>High</option>
                                <option value="medium" ${priority == 'medium' ? 'selected' : ''}>Medium</option>
                                <option value="low" ${priority == 'low' ? 'selected' : ''}>Low</option>
                            </select>
                            <select name="status" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">All Status</option>
                                <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                                <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                            <select name="sortBy" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">Sort By</option>
                                <option value="name_asc" ${sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="name_desc" ${sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="status_asc" ${sortBy == 'status_asc' ? 'selected' : ''}>Status (A-Z)</option>
                                <option value="status_desc" ${sortBy == 'status_desc' ? 'selected' : ''}>Status (Z-A)</option>
                                <option value="code_asc" ${sortBy == 'code_asc' ? 'selected' : ''}>Code (A-Z)</option>
                                <option value="code_desc" ${sortBy == 'code_desc' ? 'selected' : ''}>Code (Z-A)</option>
                            </select>
                            <button type="submit" class="btn d-flex align-items-center justify-content-center" style="width: 150px; height: 50px; background-color: #DEAD6F; color: white;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="${pageContext.request.contextPath}/Category?service=listCategory" class="btn btn-secondary" style="width: 150px; height: 50px">Clear</a>
                        </form>
                    </div>
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Category Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col" style="width: 150px">Code</th>
                                <th scope="col" style="width: 300px">Name</th>
                                <th scope="col" style="width: 150px">Parent ID</th>
                                <th scope="col" style="width: 300px">Created Date</th>
                                <th scope="col" style="width: 150px">Status</th>
                                <th scope="col" style="width: 500px">Description</th>
                                <th scope="col" style="width: 150px">Priority</th>
                                <!-- Show Actions column only if user has UPDATE_CATEGORY or DELETE_CATEGORY permission -->
                                <c:if test="${sessionScope.user != null && (rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_CATEGORY') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_CATEGORY'))}">
                                    <th scope="col" style="width: 150px">Actions</th>
                                </c:if>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty data}">
                                    <c:forEach var="cat" items="${data}">
                                        <tr>
                                            <td>${cat.category_id}</td>
                                            <td>${cat.code}</td>
                                            <td>${cat.category_name}</td>
                                            <td>${cat.parent_id != null ? cat.parent_id : 'None'}</td>
                                            <td><fmt:formatDate value="${cat.created_at}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                            <td>${cat.status}</td>
                                            <td>${cat.description}</td>
                                            <td>${cat.priority}</td>
                                            <!-- Show Edit/Delete buttons based on permissions -->
                                            <c:if test="${sessionScope.user != null && (rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_CATEGORY') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_CATEGORY'))}">
                                                <td class="d-flex justify-content-center">
                                                    <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_CATEGORY')}">
                                                        <a href="${pageContext.request.contextPath}/Category?service=updateCategory&category_id=${cat.category_id}" 
                                                           class="btn btn-warning btn-sm btn-action">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                    </c:if>
                                                    <c:if test="${rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_CATEGORY')}">
                                                        <a href="${pageContext.request.contextPath}/Category?service=deleteCategory&category_id=${cat.category_id}" 
                                                           class="btn btn-danger btn-sm btn-action" 
                                                           onclick="return confirm('Are you sure you want to delete this category?');">
                                                            <i class="fas fa-trash"></i>
                                                        </a>
                                                    </c:if>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="${sessionScope.user != null && (rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_CATEGORY') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_CATEGORY')) ? 9 : 8}" class="text-center text-muted">No categories found.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/Category?service=listCategory&page=${currentPage - 1}&code=${code}&priority=${priority}&status=${status}&sortBy=${sortBy}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/Category?service=listCategory&page=${i}&code=${code}&priority=${priority}&status=${status}&sortBy=${sortBy}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/Category?service=listCategory&page=${currentPage + 1}&code=${code}&priority=${priority}&status=${status}&sortBy=${sortBy}">Next</a>
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
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
</body>
</html>