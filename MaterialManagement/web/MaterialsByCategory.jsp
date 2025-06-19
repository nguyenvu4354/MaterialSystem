<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.time.ZoneId" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Material List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f9fa;
            padding: 20px;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #3498db;
        }

        .table img {
            width: 160px;
            height: 120px;
            object-fit: cover;
            border-radius: 6px;
        }

        .alert {
            max-width: 600px;
            margin: 40px auto;
        }

        .back-button {
            text-align: right;
            margin-bottom: 20px;
        }

        .status-new { color: #28a745; }
        .status-used { color: #ffc107; }
        .status-damaged { color: #dc3545; }
        .condition-text { font-style: italic; color: #6c757d; }
    </style>
</head>
<body>

<div class="back-button">
    <a href="index.html" class="btn btn-primary">🔙 Back to Home</a>
</div>

<h2>List of Materials by Category</h2>

<c:choose>
    <c:when test="${empty materials}">
        <div class="alert alert-warning text-center" role="alert">
            Not found material.
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-bordered table-hover align-middle shadow-sm">
                <thead class="table-primary text-center align-middle">
                    <tr>
                        <th style="width: 50px;">ID</th>
                        <th style="width: 160px;">Image</th>
                        <th style="width: 180px;">Material Name</th>
                        <th style="width: 100px;">Code</th>
                        <th style="width: 90px;">Status</th>
                        <th style="width: 80px;">Condition (%)</th>
                        <th style="width: 80px;">Price ($)</th>
                        <th style="width: 70px;">Unit</th>
                        <th style="width: 70px;">Category</th>
                        <th style="width: 120px;">Created At</th>
                        <th style="width: 120px;">Updated At</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="material" items="${materials}" varStatus="loop">
                        <tr>
                            <td class="text-center">${loop.count}</td>
                            <td class="text-center">
                                <img src="${not empty material.materialsUrl ? material.materialsUrl : 'https://via.placeholder.com/160x120?text=No+Image'}"
                                     alt="Image ${material.materialName}" style="border-radius: 8px; box-shadow: 0 0 4px rgba(0,0,0,0.1);">
                            </td>
                            <td>${material.materialName}</td>
                            <td class="text-center">${material.materialCode}</td>
                            <td class="text-center">
                                <span class="status-${material.materialStatus == 'new' ? 'new' : material.materialStatus == 'used' ? 'used' : 'damaged'}">
                                    ${material.materialStatus}
                                </span>
                            </td>
                            <td class="text-end">${material.conditionPercentage} %</td>
                            <td class="text-end">$<fmt:formatNumber value="${material.price != null ? material.price : 0}" type="number" pattern="#,##0.00" /></td>
                            <td class="text-center">${material.unit != null ? material.unit.unitName : 'N/A'}</td>
                            <td class="text-center">${material.category != null ? material.category.category_name : 'N/A'}</td>
                            <td class="text-center">
                              ${material.createdAt}
                            </td>
                            <td class="text-center">
                              ${material.updatedAt}
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>