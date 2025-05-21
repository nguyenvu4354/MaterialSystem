<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%
    // Check admin session
    User admin = (User) session.getAttribute("user");
    if (admin == null || admin.getRoleId() != 1) {
        response.sendRedirect("/Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Waggy - Tạo người dùng mới</title>
    <meta charset="UTF-8">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>Tạo người dùng mới</h2>

    <% String message = (String) request.getAttribute("message"); %>
    <% String error = (String) request.getAttribute("error"); %>
    <% if (message != null) { %>
        <div class="alert alert-success"><%= message %></div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <form action="${pageContext.request.contextPath}/admin/create-user" method="post" enctype="multipart/form-data">
        <div class="mb-3">
            <label for="username" class="form-label">Tên đăng nhập</label>
            <input type="text" class="form-control" id="username" name="username" required maxlength="50">
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Mật khẩu</label>
            <input type="password" class="form-control" id="password" name="password" required maxlength="255">
        </div>
        <div class="mb-3">
            <label for="fullName" class="form-label">Họ và tên</label>
            <input type="text" class="form-control" id="fullName" name="fullName" maxlength="100">
        </div>
        <div class="mb-3">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email" name="email" maxlength="100">
        </div>
        <div class="mb-3">
            <label for="phoneNumber" class="form-label">Số điện thoại</label>
            <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" maxlength="20">
        </div>
        <div class="mb-3">
            <label for="address" class="form-label">Địa chỉ</label>
            <input type="text" class="form-control" id="address" name="address" maxlength="255">
        </div>
        <div class="mb-3">
            <label for="dateOfBirth" class="form-label">Ngày sinh</label>
            <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth">
        </div>
        <div class="mb-3">
            <label for="gender" class="form-label">Giới tính</label>
            <select class="form-select" id="gender" name="gender">
                <option value="">Chọn giới tính</option>
                <option value="male">Nam</option>
                <option value="female">Nữ</option>
                <option value="other">Khác</option>
            </select>
        </div>
        <div class="mb-3">
            <label for="roleId" class="form-label">Vai trò</label>
            <select class="form-select" id="roleId" name="roleId" required>
                <option value="1">Admin</option>
                <option value="2">User</option>
                <!-- Thêm role khác nếu có -->
            </select>
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" id="description" name="description" rows="3"></textarea>
        </div>
        <div class="mb-3">
            <label for="userPicture" class="form-label">Ảnh đại diện</label>
            <input class="form-control" type="file" id="userPicture" name="userPicture" accept="image/*">
        </div>
        <button type="submit" class="btn btn-primary">Tạo mới</button>
    </form>
</div>
</body>
</html>
