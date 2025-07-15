<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Department Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
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
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .dashboard-title {
            color: #e2b77a;
            font-weight: bold;
            font-size: 2.2rem;
            margin-bottom: 24px;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>
            <!-- Page Content -->
            <div class="col-md-9 col-lg-10 content px-md-4">
                <c:if test="${empty sessionScope.user}">
                    <div class="alert alert-danger" role="alert">Vui lòng đăng nhập để truy cập trang này.</div>
                </c:if>
                <c:if test="${not empty sessionScope.user and sessionScope.user.roleId != 1 and !rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_LIST_DEPARTMENT')}">
                    <div class="alert alert-danger" role="alert">Bạn không có quyền xem danh sách phòng ban.</div>
                </c:if>
                <c:if test="${not empty sessionScope.user and (sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_LIST_DEPARTMENT'))}">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="dashboard-title mb-0">Department Management</h2>
                        <!-- Only show Add Department button for admin or users with CREATE_DEPARTMENT permission -->
                        <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'CREATE_DEPARTMENT')}">
                            <a href="adddepartment" class="btn flex-shrink-0" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 260px; font-size: 1.25rem; font-weight: 500; border-radius: 6px; padding: 0 32px; display: inline-flex; align-items: center; justify-content: center;">
                                <i class="fas fa-plus me-2"></i> Add Department
                            </a>
                        </c:if>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-4" style="flex-wrap: wrap;">
                        <form class="d-flex align-items-center gap-3 flex-shrink-0" action="depairmentlist" method="get" style="margin-bottom:0;">
                            <input class="form-control" type="search" name="search" placeholder="Search by Department Code" value="${searchKeyword}" aria-label="Search" style="min-width: 260px; max-width: 320px; height: 60px; border: 2px solid gray; border-radius: 6px; font-size: 1.1rem;">
                            <button class="btn" type="submit" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="depairmentlist" class="btn" style="background-color: #6c757d; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px;">Clear</a>
                        </form>
                    </div>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">${error}</div>
                    </c:if>
                    <c:if test="${not empty message}">
                        <div class="alert alert-success" role="alert">${message}</div>
                    </c:if>
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Code</th>
                                    <th>Phone</th>
                                    <th>Email</th>
                                    <th>Location</th>
                                    <th>Status</th>
                                    <!-- Only show Actions column for admin or users with UPDATE_DEPARTMENT or DELETE_DEPARTMENT permission -->
                                    <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                        <th>Actions</th>
                                    </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty departments}">
                                        <c:forEach var="d" items="${departments}">
                                            <tr>
                                                <td>${d.departmentId}</td>
                                                <td>${d.departmentName}</td>
                                                <td>${d.departmentCode}</td>
                                                <td>${d.phoneNumber}</td>
                                                <td>${d.email}</td>
                                                <td>${d.location}</td>
                                                <td>${d.status}</td>
                                                <!-- Only show action buttons for admin or users with appropriate permissions -->
                                                <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                    <td>
                                                        <!-- Edit button: only for admin or users with UPDATE_DEPARTMENT permission -->
                                                        <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT')}">
                                                            <a href="editdepartment?id=${d.departmentId}" class="btn btn-warning btn-sm me-1" title="Edit"><i class="fas fa-edit"></i></a>
                                                        </c:if>
                                                        <!-- Delete button: only for admin or users with DELETE_DEPARTMENT permission -->
                                                        <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                            <form action="deletedepartment" method="post" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xóa phòng ban này không?');">
                                                                <input type="hidden" name="id" value="${d.departmentId}" />
                                                                <button type="submit" class="btn btn-danger btn-sm" title="Delete"><i class="fas fa-trash"></i></button>
                                                            </form>
                                                        </c:if>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="text-center text-muted">Không có phòng ban nào.</td>
                                            <!-- Ensure empty Actions column if user has edit/delete permissions -->
                                            <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                <td></td>
                                            </c:if>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div> <!-- end content -->
        </div> <!-- end row -->
    </div> <!-- end container-fluid -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>