<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                        <a href="logout.jsp" class="btn btn-outline-dark btn-lg ms-4">
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
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Admin Dashboard</h1>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2>Danh sách người dùng</h2>
                        <button type="button" class="btn btn-primary" onclick="window.location.href='CreateUser.jsp'">Create User</button>
                    </div>

                    <!-- Filter Form -->
                    <form method="get" action="UserList" class="mb-3 d-flex flex-wrap gap-2 align-items-center">
                        <input type="text" name="username" class="form-control" placeholder="Search by username" value="${usernameFilter != null ? usernameFilter : ''}" style="max-width: 200px;"/>

                        <select name="status" class="form-select" style="max-width: 150px;">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                        </select>

                        <select name="roleId" class="form-select" style="max-width: 150px;">
                            <option value="">All Roles</option>
                            <option value="1" ${roleIdFilter == 1 ? 'selected' : ''}>Admin</option>
                            <option value="2" ${roleIdFilter == 2 ? 'selected' : ''}>User</option>
                        </select>

                        <button type="submit" class="btn btn-primary">Search</button>
                        <a href="UserList" class="btn btn-secondary">Clear</a>
                    </form>

                    <!-- User Table -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Tên đăng nhập</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>Vai trò</th>
                                    <th>Trạng thái</th>
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
                                        <td>
                                            <c:choose>
                                                <c:when test="${user.roleId == 1}">Admin</c:when>
                                                <c:when test="${user.roleId == 2}">User</c:when>
                                                <c:when test="${user.roleId == 3}">Guest</c:when>
                                                <c:otherwise>Unknown</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${user.status}</td>
                                        <td>
                                            <form method="post" action="UserList">
                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                <select name="roleId" class="form-select form-select-sm d-inline w-auto" onchange="autoSubmit(this.form)">
                                                    <option value="1" ${user.roleId == 1 ? "selected" : ""}>Admin</option>
                                                    <option value="2" ${user.roleId == 2 ? "selected" : ""}>User</option>
                                                    <option value="3" ${user.roleId == 3 ? "selected" : ""}>Guest</option>
                                                </select>
                                                <select name="status" class="form-select form-select-sm d-inline w-auto" onchange="autoSubmit(this.form)">
                                                    <option value="active" ${user.status == 'active' ? "selected" : ""}>Active</option>
                                                    <option value="inactive" ${user.status == 'inactive' ? "selected" : ""}>Inactive</option>
                                                </select>
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
                                        <a class="page-link" href="UserList?page=${currentPage - 1}
                                           <c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if>
                                           <c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if>
                                           <c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if>
                                               ">Previous</a>
                                        </li>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="UserList?page=${i}
                                           <c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if>
                                           <c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if>
                                           <c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if>
                                           ">${i}</a>
                                    </li>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="UserList?page=${currentPage + 1}
                                           <c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if>
                                           <c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if>
                                           <c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if>
                                               ">Next</a>
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
        <script>
                                                    function autoSubmit(form) {
                                                        form.submit();
                                                    }
        </script>
    </body>
</html>
