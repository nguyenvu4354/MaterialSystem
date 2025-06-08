<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Export Materials</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
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
        .add-material-form {
            margin-bottom: 20px;
        }
        .confirm-form label {
            display: inline-block;
            width: 120px;
            margin: 5px 0;
        }
        .confirm-form input, .confirm-form select, .confirm-form textarea {
            margin: 5px 0;
        }
    </style>
</head>
<body>
    <h1>Export Materials</h1>
    
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>

    <h2>Add Material to Export</h2>
    <form action="ExportMaterial" method="post" class="add-material-form">
        <input type="hidden" name="action" value="add">
        <table>
            <thead>
                <tr>
                    <th>Material Name</th>
                    <th>Quantity</th>
                    <th>Condition</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <select name="materialId" required>
                            <option value="">Select Material</option>
                            <c:forEach var="material" items="${materials}">
                                <option value="${material.materialId}">${material.materialName}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td><input type="number" name="quantity" min="0" required></td>
                    <td>
                        <select name="materialCondition" required>
                            <option value="new">New</option>
                            <option value="used">Used</option>
                            <option value="refurbished">Refurbished</option>
                        </select>
                    </td>
                </tr>
            </tbody>
        </table>
        <input type="submit" value="Add to Export List">
    </form>

    <h2>Current Export List</h2>
    <c:if test="${not empty exportDetails}">
        <table>
            <tr>
                <th>Material</th>
                <th>Quantity</th>
                <th>Condition</th>
                <th>Stock Available</th>
                <th>Action</th>
            </tr>
            <c:forEach var="detail" items="${exportDetails}">
                <tr>
                    <td>${materialMap[detail.materialId].materialName}</td>
                    <td>${detail.quantity}</td>
                    <td>${detail.materialCondition}</td>
                    <td>${stockMap[detail.materialId]}</td>
                    <td>
                        <form action="ExportMaterial" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="materialId" value="${detail.materialId}">
                            <input type="hidden" name="quantity" value="${detail.quantity}">
                            <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                            <input type="submit" value="Remove">
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h2>Confirm Export</h2>
    <form action="ExportMaterial" method="post" class="confirm-form">
        <input type="hidden" name="action" value="export">
        <label>Department:</label>
        <select name="departmentId">
            <option value="">Select Department</option>
            <c:forEach var="department" items="${departments}">
                <option value="${department.departmentId}">${department.departmentName}</option>
            </c:forEach>
        </select><br>
        <label>Destination:</label>
        <input type="text" name="destination"><br>
        <label>Batch Number:</label>
        <input type="text" name="batchNumber" maxlength="50"><br>
        <label>Expiry Date:</label>
        <input type="date" name="expiryDate"><br>
        <label>Note:</label>
        <textarea name="note"></textarea><br>
        <input type="submit" value="Confirm Export">
    </form>
</body>
</html>