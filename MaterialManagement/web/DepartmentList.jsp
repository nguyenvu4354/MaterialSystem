<%@ page import="java.util.List" %>
<%@ page import="entity.Department" %>
<%@ page import="entity.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    String message = (String) request.getAttribute("message");
    User user = (User) session.getAttribute("user");
%>
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
                <% if (user == null) { %>
                <div class="alert alert-danger" role="alert">Vui lòng đăng nhập để truy cập trang này.</div>
                <% } else if (user.getRoleId() != 1) { %>
                <div class="alert alert-danger" role="alert">Bạn không có quyền truy cập trang quản lý phòng ban.</div>
                <% } else { %>
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h2 class="dashboard-title mb-0">Department Management</h2>
                    <a href="DepartmentForm.jsp" class="btn flex-shrink-0" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 260px; font-size: 1.25rem; font-weight: 500; border-radius: 6px; padding: 0 32px; display: inline-flex; align-items: center; justify-content: center;">
                        <i class="fas fa-plus me-2"></i> Add Department
                    </a>
                </div>
                <div class="d-flex align-items-center gap-3 mb-4" style="flex-wrap: wrap;">
                    <form class="d-flex align-items-center gap-3 flex-shrink-0" action="depairmentlist" method="get" style="margin-bottom:0;">
                        <input class="form-control" type="search" name="search" placeholder="Search by Department Code" value="${searchKeyword != null ? searchKeyword : ''}" aria-label="Search" style="min-width: 260px; max-width: 320px; height: 60px; border: 2px solid gray; border-radius: 6px; font-size: 1.1rem;">
                        <button class="btn" type="submit" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px;">
                            <i class="fas fa-search me-2"></i> Search
                        </button>
                        <a href="depairmentlist" class="btn" style="background-color: #6c757d; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px;">Clear</a>
                    </form>
                </div>
                <% if (message != null) { %>
                <div class="alert alert-success" role="alert"><%= message %></div>
                <% } %>
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
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (departments != null && !departments.isEmpty()) {
                                for (Department d : departments) { %>
                                <tr id="row-<%= d.getDepartmentId() %>">
                                    <td><%= d.getDepartmentId() %></td>
                                    <td class="name"><%= d.getDepartmentName() %></td>
                                    <td class="code"><%= d.getDepartmentCode() %></td>
                                    <td class="phone"><%= d.getPhoneNumber() %></td>
                                    <td class="email"><%= d.getEmail() %></td>
                                    <td class="location"><%= d.getLocation() %></td>
                                    <td class="status"><%= d.getStatus() %></td>
                                    <td class="actions">
                                        <a href="javascript:void(0)" onclick="editRow(<%= d.getDepartmentId() %>)" class="btn btn-warning btn-sm me-1" title="Edit"><i class="fas fa-edit"></i></a>
                                        <form action="depairmentlist" method="get" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xoá phòng ban này không?');">
                                            <input type="hidden" name="action" value="delete" />
                                            <input type="hidden" name="id" value="<%= d.getDepartmentId() %>" />
                                            <button type="submit" class="btn btn-danger btn-sm" title="Delete"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                                <tr id="edit-row-<%= d.getDepartmentId() %>" style="display:none;">
                                    <form class="edit-form" action="depairmentlist" method="post">
                                        <input type="hidden" name="id" value="<%= d.getDepartmentId() %>"/>
                                        <td><%= d.getDepartmentId() %></td>
                                        <td><input type="text" name="name" value="<%= d.getDepartmentName() %>" class="form-control" required/></td>
                                        <td><input type="text" name="code" value="<%= d.getDepartmentCode() %>" class="form-control" required/></td>
                                        <td><input type="text" name="phone" value="<%= d.getPhoneNumber() %>" class="form-control" /></td>
                                        <td><input type="email" name="email" value="<%= d.getEmail() %>" class="form-control" /></td>
                                        <td><input type="text" name="location" value="<%= d.getLocation() %>" class="form-control" /></td>
                                        <td><%= d.getStatus() %></td>
                                        <td>
                                            <button type="submit" class="btn btn-success btn-sm me-1">Save</button>
                                            <button type="button" class="btn btn-secondary btn-sm" onclick="cancelEdit(<%= d.getDepartmentId() %>)">Cancel</button>
                                        </td>
                                    </form>
                                </tr>
                            <% } } else { %>
                            <tr><td colspan="8" class="text-center text-muted">Không có phòng ban nào.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div> <!-- end content -->
        </div> <!-- end row -->
    </div> <!-- end container-fluid -->
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function editRow(id) {
            document.getElementById('row-' + id).style.display = 'none';
            document.getElementById('edit-row-' + id).style.display = 'table-row';
        }

        function cancelEdit(id) {
            document.getElementById('row-' + id).style.display = 'table-row';
            document.getElementById('edit-row-' + id).style.display = 'none';
        }
    </script>
</body>
</html>