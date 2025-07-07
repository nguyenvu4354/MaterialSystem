<%@ page import="java.util.List" %>
<%@ page import="entity.Department" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Department Management</title>
        <style>
            body {
                font-family: 'Roboto', Arial, sans-serif;
                margin: 40px;
                background-color: #f8f8f8;
            }
            h1 {
                color: #d59f39;
            }
            .table-section {
                background: #fff;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 30px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            button, .add-button {
                padding: 10px 18px;
                background-color: #d59f39;
                border: none;
                color: white;
                border-radius: 8px;
                cursor: pointer;
                margin-bottom: 20px;
                text-decoration: none;
                display: inline-block;
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
            tr:hover {
                background-color: #f1f1f1;
            }
            .actions a {
                margin-right: 10px;
                text-decoration: none;
                color: #007bff;
            }
            input[type=text], input[type=email] {
                width: 100%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 6px;
            }
            .edit-form button {
                padding: 8px 12px;
                margin-right: 5px;
            }
            .message {
                color: green;
                margin-bottom: 20px;
            }
            .form-add {
                display: flex;
                gap: 500px;
            }
            .form-search {
                display: flex;
                justify-content: center;
            }
            .form-searchdetail {
                display: flex;
                gap: 10px;
            }
            .col-auto input {
                width: 250px;
                height: 20px;
            }
            .col-auto button {
                width: 100px;
                height: 40px;
            }
        </style>
    </head>
    <body>
        <h1>Department Management</h1>



        <div class="table-section">
            <div class="form-add">
                <div>
                    <a href="DepartmentForm.jsp" class="add-button">Add Department</a>

                </div>
                <div class="form-search">
                    <form method="get" action="depairmentlist" class="form-searchdetail">
                        <div class="col-auto">
                            <input type="text" name="search" class="form-control" placeholder="Search by Department Code"
                                   value="${searchKeyword != null ? searchKeyword : ''}">
                        </div>
                        <div class="col-auto">
                            <button type="submit" class="btn-filter">Filter</button>
                        </div>
                    </form>
                </div>


            </div>
            <% if (message != null) { %>
            <div class="message"><%= message %></div>
            <% } %>
            <table>
                <thead style="background-color: #f9f5f0">
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
                            <a href="javascript:void(0)" onclick="editRow(<%= d.getDepartmentId() %>)" style="color: #DEAD6F">Edit</a>
                            <a href="depairmentlist?action=delete&id=<%= d.getDepartmentId() %>"
                               onclick="return confirm('Bạn có chắc muốn xoá phòng ban này không?');" style="color: #DEAD6F">Delete</a>
                        </td>
                    </tr>
                    <tr id="edit-row-<%= d.getDepartmentId() %>" style="display:none;">
                <form class="edit-form" action="depairmentlist" method="post">
                    <input type="hidden" name="id" value="<%= d.getDepartmentId() %>"/>
                    <td><%= d.getDepartmentId() %></td>
                    <td><input type="text" name="name" value="<%= d.getDepartmentName() %>" required/></td>
                    <td><input type="text" name="code" value="<%= d.getDepartmentCode() %>" required/></td>
                    <td><input type="text" name="phone" value="<%= d.getPhoneNumber() %>" /></td>
                    <td><input type="email" name="email" value="<%= d.getEmail() %>" /></td>
                    <td><input type="text" name="location" value="<%= d.getLocation() %>" /></td>
                    <td><%= d.getStatus() %></td>
                    <td>
                        <button type="submit">Save</button>
                        <button type="button" onclick="cancelEdit(<%= d.getDepartmentId() %>)">Cancel</button>
                    </td>
                </form>
                </tr>
                <% } } else { %>
                <tr><td colspan="8">Không có phòng ban nào.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>

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