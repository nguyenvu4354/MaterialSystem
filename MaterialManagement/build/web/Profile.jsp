<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <title>Hồ sơ người dùng</title>
</head>
<body>
<h2>Hồ sơ của: <%= user.getUsername() %></h2>

<% String message = (String) request.getAttribute("message"); %>
<% String error = (String) request.getAttribute("error"); %>

<% if (message != null) { %>
    <p style="color: green;"><%= message %></p>
<% } %>
<% if (error != null) { %>
    <p style="color: red;"><%= error %></p>
<% } %>

<form action="profile" method="post" enctype="multipart/form-data">
    <label>Họ và tên: <input type="text" name="fullName" value="<%= user.getFullName() != null ? user.getFullName() : "" %>"/></label><br/><br/>
    <label>Email: <input type="email" name="email" value="<%= user.getEmail() != null ? user.getEmail() : "" %>"/></label><br/><br/>
    <label>Số điện thoại: <input type="text" name="phoneNumber" value="<%= user.getPhoneNumber() != null ? user.getPhoneNumber() : "" %>"/></label><br/><br/>
    <label>Địa chỉ: <input type="text" name="address" value="<%= user.getAddress() != null ? user.getAddress() : "" %>"/></label><br/><br/>
    <label>Ngày sinh: <input type="date" name="dateOfBirth" value="<%= user.getDateOfBirth() != null ? user.getDateOfBirth() : "" %>"/></label><br/><br/>
    <label>Giới tính:
        <select name="gender">
            <option value="">Chọn</option>
            <option value="male" <%= "male".equalsIgnoreCase(user.getGender() != null ? user.getGender().toString() : "") ? "selected" : "" %>>Nam</option>
            <option value="female" <%= "female".equalsIgnoreCase(user.getGender() != null ? user.getGender().toString() : "") ? "selected" : "" %>>Nữ</option>
            <option value="other" <%= "other".equalsIgnoreCase(user.getGender() != null ? user.getGender().toString() : "") ? "selected" : "" %>>Khác</option>
        </select>
    </label><br/><br/>
    <label>Mô tả:<br/>
        <textarea name="description" rows="4" cols="40"><%= user.getDescription() != null ? user.getDescription() : "" %></textarea>
    </label><br/><br/>
    <label>Ảnh đại diện:
        <input type="file" name="userPicture" accept="image/*"/>
    </label><br/><br/>
    <% if (user.getUserPicture() != null && !user.getUserPicture().isEmpty()) { %>
        <img src="images/profiles/<%= user.getUserPicture() %>" alt="Ảnh đại diện" width="150"/>
    <% } else { %>
        <p>Chưa có ảnh đại diện.</p>
    <% } %><br/><br/>
    <button type="submit">Cập nhật hồ sơ</button>
</form>

</body>
</html>
