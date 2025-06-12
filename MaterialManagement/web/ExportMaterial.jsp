<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Xuất vật tư</title>
    <meta charset="UTF-8">
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
                alert("Số lượng phải lớn hơn 0.");
                input.value = 1;
            }
        }
    </script>
</head>
<body>
    <h1>Xuất vật tư</h1>
    
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>

    <h2>Thêm vật tư vào danh sách xuất</h2>
    <form action="ExportMaterial" method="post" class="add-material-form">
        <input type="hidden" name="action" value="add">
        <table>
            <thead>
                <tr>
                    <th>Tên vật tư</th>
                    <th>Số lượng</th>
                    <th>Tình trạng</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <select name="materialId" required>
                            <option value="">Chọn vật tư</option>
                            <c:forEach var="material" items="${materials}">
                                <option value="${material.materialId}">${material.materialName}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td><input type="number" name="quantity" min="1" required onchange="validateQuantity(this)"></td>
                    <td>
                        <select name="materialCondition" required>
                            <option value="new">Mới</option>
                            <option value="used">Đã sử dụng</option>
                            <option value="refurbished">Tân trang</option>
                        </select>
                    </td>
                </tr>
            </tbody>
        </table>
        <input type="submit" value="Thêm vào danh sách xuất">
    </form>

    <h2>Danh sách xuất hiện tại</h2>
    <c:if test="${not empty exportDetails}">
        <table>
            <tr>
                <th>Vật tư</th>
                <th>Số lượng</th>
                <th>Tình trạng</th>
                <th>Tồn kho khả dụng</th>
                <th>Thao tác</th>
            </tr>
            <c:forEach var="detail" items="${exportDetails}">
                <tr>
                    <td>${materialMap[detail.materialId].materialName}</td>
                    <td>
                        <form action="ExportMaterial" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="updateQuantity">
                            <input type="hidden" name="materialId" value="${detail.materialId}">
                            <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                            <input type="number" name="quantity" value="${detail.quantity}" min="1" required onchange="validateQuantity(this)">
                            <input type="submit" value="Cập nhật">
                        </form>
                    </td>
                    <td>${detail.materialCondition}</td>
                    <td>${stockMap[detail.materialId]}</td>
                    <td>
                        <form action="ExportMaterial" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="materialId" value="${detail.materialId}">
                            <input type="hidden" name="quantity" value="${detail.quantity}">
                            <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                            <input type="submit" value="Xóa">
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <h2>Xác nhận xuất kho</h2>
    <form action="ExportMaterial" method="post" class="confirm-form">
        <input type="hidden" name="action" value="export">
        <label>Người nhận:</label>
        <select name="recipientUserId" required>
            <option value="">Chọn người nhận</option>
            <c:forEach var="user" items="${users}">
                <option value="${user.userId}">${user.fullName} (ID: ${user.userId}, ${user.departmentName != null ? user.departmentName : 'Không có phòng ban'})</option>
            </c:forEach>
        </select><br>
        <label>Số lô:</label>
        <input type="text" name="batchNumber" maxlength="50"><br>
        <label>Ghi chú:</label>
        <textarea name="note"></textarea><br>
        <input type="submit" value="Xác nhận xuất kho">
    </form>
</body>
</html>