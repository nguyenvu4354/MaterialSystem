<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Import Materials</title>
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
    <script>
        function validateQuantity(input) {
            if (input.value <= 0) {
                alert("Quantity must be greater than 0.");
                input.value = 1;
            }
        }
        function validateUnitPrice(input) {
            if (input.value < 0) {
                alert("Unit price cannot be negative.");
                input.value = 0;
            }
        }
    </script>
</head>
<body>
    <h1>Import Materials</h1>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>

    <h2>Add Material to Import</h2>
    <form action="ImportMaterial" method="post" class="add-material-form">
        <input type="hidden" name="action" value="add">
        <table>
            <thead>
                <tr>
                    <th>Material</th>
                    <th>Quantity</th>
                    <th>Unit Price</th>
                    <th>Condition</th>
                    <th>Expiry Date</th>
                    <th>Is Damaged</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <select name="materialId" required>
                            <option value="">Select Material</option>
                            <c:forEach var="material" items="${materials}">
                                <option value="${material.materialId}">${material.materialName} (${material.unit.name}) - ${material.category.categoryName}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td><input type="number" name="quantity" min="1" required onchange="validateQuantity(this)"></td>
                    <td><input type="number" name="unitPrice" min="0" step="0.01" required onchange="validateUnitPrice(this)"></td>
                    <td>
                        <select name="materialCondition" required>
                            <option value="new">New</option>
                            <option value="used">Used</option>
                            <option value="refurbished">Refurbished</option>
                        </select>
                    </td>
                    <td><input type="date" name="expiryDate"></td>
                    <td><input type="checkbox" name="isDamaged" value="true"></td>
                </tr>
            </tbody>
        </table>
        <input type="submit" value="Add to Import">
    </form>

    <h2>Current Import List</h2>
    <c:if test="${not empty importDetails}">
        <table>
            <thead>
                <th>Material</th>
                <th>Unit</th>
                <th>Category</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Condition</th>
                <th>Expiry Date</th>
                <th>Stock Available</th>
                <th>Actions</th>
            </thead>
            <tbody>
                <c:forEach var="detail" items="${importDetails}">
                    <tr>
                        <td>${materialMap[detail.materialId].materialName}</td>
                        <td>${materialMap[detail.materialId].unit.name}</td>
                        <td>${materialMap[detail.materialId].category.categoryName}</td>
                        <td>
                            <form action="ImportMaterial" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateQuantity">
                                <input type="hidden" name="materialId" value="1">
                                <input type="hidden" name="materialCondition" value="${detail.quantity}">
                                <input type="number" name="quantity" value="1" min="1" required onchange="validateQuantity(this)">
                                <input type="submit" value="Update">
                            </form>
                        </td>
                        <td>${detail.unitPrice}</td>
                        <td>${detail.materialCondition}</td>
                        <td>${detail.expiryDate}</td>
                        <td>${stockMap[detail.materialId]}</td>
                        <td>
                            <form action="ImportMaterial" method="post" style="true">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="materialId" value="${detail.materialId}">
                                    <input type="hidden" name="quantity" value="${detail.quantity}">
                                    <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                    <input type="submit" value="Remove">
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

    <h2>Confirm Import</h2>
    <form action="ImportMaterial" method="post" class="confirm-form">
        <input type="hidden" name="action" value="import">
        <label>Supplier:</label>
        <select name="supplierId">
            <option value="">Select Supplier</option>
            <c:forEach var="supplier" items="${suppliers}">
                <option value="${supplier.supplierId}">${supplier.supplierName}</option>
            </c:forEach>
        </select><br>
        <label>Destination:</label>
        <input type="text" name="destination"><br>
        <label>Batch Number:</label>
        input<input type="text" name="batchNumber" maxlength="50"><br>
        <label>Actual Arrival:</label>
        <input type="datetime-local" name="actualArrival"><br>
        <label>Is Damaged:</label>
        <input type="checkbox" name="isDamaged" value="true"><br>
        <label>Note:</label>
        <textarea name="note"></textarea><br>
        <input type="submit" value="Confirm Import">
    </form>
</body>
</html>