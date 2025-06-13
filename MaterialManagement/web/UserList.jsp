<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard - Computer Accessories</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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
    </style>
</head>
<body>
    <%@ include file="HeaderAdmin.jsp" %>


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
                    <h2 class="text-primary fw-bold display-6 border-bottom pb-2">👤 User List</h2>
                    <button type="button" class="btn btn-primary" onclick="window.location.href = 'create-user'">Create User</button>
                </div>

                <!-- Filter Form -->
                <form method="get" action="UserList" class="mb-3 d-flex flex-wrap gap-2 align-items-center">
                    <input type="text" name="username" class="form-control" placeholder="Search by username" value="${usernameFilter != null ? usernameFilter : ''}" style="max-width: 200px;"/>
                    <select name="status" class="form-select" style="max-width: 150px;">
                        <option value="">All Status</option>
                        <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Active</option>
                        <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Inactive</option>
                    </select>
                    <select name="roleId" class="form-select" style="max-width: 150px;">
                        <option value="">All Roles</option>
                        <option value="1" ${roleIdFilter == 1 ? 'selected' : ''}>Warehouse Manager</option>
                        <option value="2" ${roleIdFilter == 2 ? 'selected' : ''}>Warehouse Staff</option>
                        <option value="3" ${roleIdFilter == 3 ? 'selected' : ''}>Director</option>
                        <option value="4" ${roleIdFilter == 4 ? 'selected' : ''}>Employee</option>
                    </select>
                    <select name="departmentId" class="form-select" style="max-width: 150px;">
                        <option value="">All Departments</option>
                        <c:forEach var="dept" items="${departmentList}">
                            <option value="${dept.departmentId}" ${departmentIdFilter == dept.departmentId ? 'selected' : ''}>${dept.departmentName}</option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn btn-primary">Search</button>
                    <a href="UserList" class="btn btn-secondary">Clear</a>
                </form>

                <!-- Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success">${message}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- User Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Department</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${userList}">
                                <tr>
                                    <td>${user.userId}</td>
                                    <td>${user.username}</td>
                                    <td>${user.fullName}</td>
                                    <td>${user.email}</td>
                                    <td>${user.departmentName != null ? user.departmentName : '-'}</td>
                                    <td>${user.roleName}</td>
                                    <td>${user.status}</td>
                                    <td>
                                        <form method="post" action="UserList" style="display:inline;">
                                            <input type="hidden" name="userId" value="${user.userId}"/>
                                            <select name="roleId" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                <option value="1" ${user.roleId == 1 ? 'selected' : ''} ${user.roleId == 1 ? 'disabled' : ''}>Warehouse Manager</option>
                                                <option value="2" ${user.roleId == 2 ? 'selected' : ''}>Warehouse Staff</option>
                                                <option value="3" ${user.roleId == 3 ? 'selected' : ''}>Director</option>
                                                <option value="4" ${user.roleId == 4 ? 'selected' : ''}>Employee</option>
                                            </select>
                                            <select name="status" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                <option value="active" ${user.status == 'active' ? 'selected' : ''}>Active</option>
                                                <option value="inactive" ${user.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                            </select>
                                            <a href="UserDetail?userId=${user.userId}" class="btn btn-outline-primary btn-sm mt-2 d-inline-flex align-items-center gap-1">
                                                <i class="bi bi-eye"></i> View
                                            </a>
                                        </form>
                                        <form method="post" action="UserList" style="display:inline;">
                                            <input type="hidden" name="userId" value="${user.userId}"/>
                                            <input type="hidden" name="action" value="delete"/>
                                            <button type="submit" class="btn btn-danger btn-sm mt-2" onclick="return confirm('Are you sure you want to delete this user?');">Delete</button>
                                        </form>
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
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="UserList?page=${currentPage - 1}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">Previous</a>
                                </li>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="UserList?page=${i}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">${i}</a>
                                </li>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <a class="page-link" href="UserList?page=${currentPage + 1}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">Next</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer py-4 bg-light mt-auto">
        <div class="container text-center">
            <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
</body>
</html>