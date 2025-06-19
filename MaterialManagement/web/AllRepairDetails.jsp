<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Detailed list of repair materials</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2 class="mb-4 text-center">Detailed list of repair materials</h2>
    <c:if test="${not empty repairDetails}">
        <table class="table table-bordered table-striped">
            <thead class="table-dark">
            <tr>
                <th>Detail id</th>
                <th>Detail code</th>
                <th>Repair request code</th>
                <th>Material code</th>
                <th>Quantity</th>
                <th>Description of damage</th>
                <th>Repair costs</th>
                <th>Date created</th>
                <th>Update date</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="detail" items="${repairDetails}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td>${detail.detailId}</td>
                    <td>${detail.repairRequestId}</td>
                    <td>${detail.materialId}</td>
                    <td>${detail.quantity}</td>
                    <td>${detail.damageDescription}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty detail.repairCost}">
                                <fmt:formatNumber value="${detail.repairCost}" type="currency" currencySymbol="₫"/>
                            </c:when>
                            <c:otherwise>
                                not price
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${detail.createdAt}</td>
                    <td>${detail.updatedAt}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty repairDetails}">
        <div class="alert alert-info">No repair detail data available</div>
    </c:if>
</div>
</body>
</html>
