<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Danh sách yêu cầu sửa chữa</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .request-box {
            border: 1px solid #ccc;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .request-box ul { padding-left: 20px; }
        .request-box b { color: #333; }
    </style>
</head>
<body>
    <h2>📋 Danh sách Yêu Cầu Sửa Chữa</h2>

    <c:if test="${not empty error}">
        <p style="color:red">${error}</p>
    </c:if>

    <c:if test="${empty requests}">
        <p>Không có yêu cầu nào được tìm thấy.</p>
    </c:if>

    <c:forEach var="req" items="${requests}">
        <div class="request-box">
            <b>Mã yêu cầu:</b> ${req.requestCode} <br>
            <b>Người gửi (ID):</b> ${req.userId} <br>
            <b>Trạng thái:</b> 
            <c:choose>
                <c:when test="${req.status == 'pending'}">Đang chờ</c:when>
                <c:when test="${req.status == 'approved'}">Đã phê duyệt</c:when>
                <c:when test="${req.status == 'completed'}">Đã hoàn tất</c:when>
                <c:otherwise>${req.status}</c:otherwise>
            </c:choose>
            <br>
            <b>Ngày yêu cầu:</b> ${req.requestDate} <br>
            <b>Lý do:</b> ${req.reason} <br>

            <b>Chi tiết vật tư:</b>
            <ul>
                <c:forEach var="detail" items="${detailMap[req.repairRequestId]}">
                    <li>
                        <b>Vật tư ID:</b> ${detail.materialId} |
                        <b>Số lượng:</b> ${detail.quantity} |
                        <b>Mô tả hư hỏng:</b> ${detail.damageDescription} |
                        <b>Chi phí:</b> ${detail.repairCost}
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:forEach>
</body>
</html>
