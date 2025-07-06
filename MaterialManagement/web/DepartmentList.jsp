<%@ page import="java.util.List" %>
<%@ page import="entity.Department" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    Department editing = (Department) request.getAttribute("dept");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Department Management</title>
    <style>
        body {
            font-family: Arial;
            margin: 40px;
            background-color: #f8f8f8;
        }
        h1 {
            color: #d59f39;
        }
        .form-section, .table-section {
            background: #fff;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        input[type=text], input[type=email] {
            width: 100%;
            padding: 10px;
            margin: 8px 0 16px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        button {
            padding: 10px 18px;
            background-color: #d59f39;
            border: none;
            color: white;
            border-radius: 8px;
            cursor: pointer;
            margin-right: 10px;
        }
        a.button-link {
            display: inline-block;
            padding: 10px 18px;
            background-color: #ccc;
            color: black;
            text-decoration: none;
            border-radius: 8px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
        }
        th, td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }
        tr:hover { background-color: #f1f1f1; }
        .actions a {
            margin-right: 10px;
            text-decoration: none;
            color: #007bff;
        }
    </style>
</head>
<body>
    <h1>Department Management</h1>

    <div class="form-section">
        <h2><%= (editing != null ? "Edit Department" : "Add Department") %></h2>

        <form action="depairmentlist" method="post">
            <% if (editing != null) { %>
                <input type="hidden" name="id" value="<%= editing.getDepartmentId() %>"/>
            <% } %>
            <label>Name:</label>
            <input type="text" name="name" value="<%= editing != null ? editing.getDepartmentName() : "" %>" required/>

            <label>Code:</label>
            <input type="text" name="code" value="<%= editing != null ? editing.getDepartmentCode() : "" %>" required/>

            <label>Phone:</label>
            <input type="text" name="phone" value="<%= editing != null ? editing.getPhoneNumber() : "" %>" />

            <label>Email:</label>
            <input type="email" name="email" value="<%= editing != null ? editing.getEmail() : "" %>" />

            <label>Location:</label>
            <input type="text" name="location" value="<%= editing != null ? editing.getLocation() : "" %>" />

            <label>Description:</label>
            <input type="text" name="description" value="<%= editing != null ? editing.getDescription() : "" %>" />

            <button type="submit"><%= editing != null ? "Update" : "Add" %></button>
            <% if (editing != null) { %>
                <a class="button-link" href="depairmentlist">Cancel</a>
            <% } %>
        </form>
    </div>

    <div class="table-section">
        <h2>Department List</h2>
        <table>
            <thead>
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
                        <tr>
                            <td><%= d.getDepartmentId() %></td>
                            <td><%= d.getDepartmentName() %></td>
                            <td><%= d.getDepartmentCode() %></td>
                            <td><%= d.getPhoneNumber() %></td>
                            <td><%= d.getEmail() %></td>
                            <td><%= d.getLocation() %></td>
                            <td><%= d.getStatus() %></td>
                            <td class="actions">
                                <a href="depairmentlist?action=edit&id=<%= d.getDepartmentId() %>">Edit</a>
                                <a href="depairmentlist?action=delete&id=<%= d.getDepartmentId() %>"
                                   onclick="return confirm('Bạn có chắc muốn xoá phòng ban này không?');">Delete</a>
                            </td>
                        </tr>
                <% } } else { %>
                    <tr><td colspan="8">Không có phòng ban nào.</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>
