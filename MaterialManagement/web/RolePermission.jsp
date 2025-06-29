<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Role Permission Management</title>
        <link rel="stylesheet" href="css/style.css">
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            h2 {
                color: #333;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
            }
            th, td {
                padding: 8px;
                text-align: center;
                border: 1px solid #ddd;
            }
            th {
                background-color: #f2f2f2;
            }
            .error {
                color: red;
            }
            .success {
                color: green;
            }
            input[type="checkbox"] {
                cursor: pointer;
            }
            input[type="submit"] {
                padding: 10px 20px;
                margin-top: 10px;
                background-color: #4CAF50;
                color: white;
                border: none;
                cursor: pointer;
            }
            input[type="submit"]:hover {
                background-color: #45a049;
            }
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

        <form action="RolePermission" method="post">
            <input type="hidden" name="action" value="update">
            <table>
                <tr>
                    <th>Permission \ Role</th>
                        <c:forEach var="role" items="${roles}">
                        <th>${role.roleName}</th>
                        </c:forEach>
                </tr>
                <c:forEach var="perm" items="${permissions}">
                    <c:if test="${perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5 || rolePermissionMap[1][perm.permissionId]}">
                        <tr>
                            <td>${perm.permissionName} (${perm.description})</td>
                            <c:forEach var="role" items="${roles}">
                                <td>
                                    <c:if test="${role.roleId == 1 || (perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5)}">
                                        <input type="checkbox" 
                                               name="permission_${role.roleId}_${perm.permissionId}" 
                                               ${rolePermissionMap[role.roleId][perm.permissionId] ? 'checked' : ''}>
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>
            <input type="submit" value="Save Changes">
        </form>
    </body>
</html>