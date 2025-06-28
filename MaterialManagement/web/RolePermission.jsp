<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Role Permission Management</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2, h3, h4 { color: #333; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { padding: 8px; text-align: left; border: 1px solid #ddd; }
        th { background-color: #f2f2f2; }
        .error { color: red; }
        .success { color: green; }
        select, input[type="submit"] { padding: 5px; margin: 5px; }
    </style>
</head>
<body>
    <h2>Role Permission Management</h2>

    <c:if test="${not empty errorMessage}">
        <p class="error">${errorMessage}</p>
    </c:if>
    <c:if test="${not empty successMessage}">
        <p class="success">${successMessage}</p>
    </c:if>

    <form action="RolePermission" method="get">
        <label>Select Role:
            <select name="roleId" onchange="this.form.submit()">
                <option value="">Select a role</option>
                <c:forEach var="role" items="${roles}">
                    <option value="${role.roleId}" ${role.roleId == selectedRoleId ? 'selected' : ''}>
                        ${role.roleName}
                    </option>
                </c:forEach>
            </select>
        </label>
    </form>

    <c:if test="${not empty selectedRoleId}">
        <h3>Permissions for Role: ${selectedRoleName} (ID: ${selectedRoleId})</h3>
        
        <h4>Assigned Permissions</h4>
        <table>
            <tr><th>Permission Name</th><th>Description</th><th>Action</th></tr>
            <c:forEach var="perm" items="${assignedPermissions}">
                <tr>
                    <td>${perm.permissionName}</td>
                    <td>${perm.description}</td>
                    <td>
                        <form action="RolePermission" method="post">
                            <input type="hidden" name="roleId" value="${selectedRoleId}">
                            <input type="hidden" name="permissionId" value="${perm.permissionId}">
                            <input type="hidden" name="action" value="remove">
                            <input type="submit" value="Remove">
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <h4>Available Permissions</h4>
        <form action="RolePermission" method="post">
            <input type="hidden" name="roleId" value="${selectedRoleId}">
            <input type="hidden" name="action" value="assign">
            <label>Select Permission:
                <select name="permissionId">
                    <option value="">Select a permission</option>
                    <c:forEach var="perm" items="${availablePermissions}">
                        <c:if test="${not empty perm}">
                            <option value="${perm.permissionId}">${perm.permissionName}</option>
                        </c:if>
                    </c:forEach>
                </select>
            </label>
            <input type="submit" value="Assign">
        </form>
    </c:if>
    <c:if test="${empty selectedRoleId && empty errorMessage}">
        <p>Please select a role to manage permissions.</p>
    </c:if>
</body>
</html>