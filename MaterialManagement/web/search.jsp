<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Search Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .search-box {
            max-width: 600px;
            margin: auto;
        }
        table img {
            width: 150px;
            height: 100px;
            object-fit: cover;
            border-radius: 6px;
        }
    </style>
</head>
<body>
<div class="container my-5">
    <h2 class="text-center fw-bold mb-3">🔎 Search Materials</h2>

    <!-- Hiển thị từ khóa tìm kiếm -->
    <c:if test="${not empty keyword}">
        <h5 class="text-center text-muted mb-4">
            Result for search: <strong>${keyword}</strong>
        </h5>
    </c:if>

    <!-- Search Form -->
    <form action="search" method="get" class="search-box mb-4">
        <div class="input-group">
            <input type="text" name="keyword" class="form-control" placeholder="Enter product name..." required>
            <button class="btn btn-primary" type="submit">Search</button>
        </div>
    </form>

    <!-- Kết quả tìm kiếm dưới dạng bảng -->
    <c:if test="${not empty results}">
        <div class="table-responsive">
            <table class="table table-hover table-bordered align-middle bg-white shadow-sm">
                <thead class="table-light">
                    <tr class="text-center">
                        <th>ID</th>
                        <th>Image</th>
                        <th>Material Name</th>
                        <th>Price</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${results}" varStatus="loop">
                        <tr class="text-center">
                            <td>${loop.index + 1}</td>
                            <td><img src="${product.materialsUrl}" alt="${product.materialName}" width="150px" height="100px"></td>
                            <td>${product.materialName}</td>
                            <td>$${product.price}</td>
                            <td>
                                <a href="ProductDetail?id=${product.materialId}" class="btn btn-sm btn-outline-primary">
                                    View Detail
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- Không có kết quả -->
    <c:if test="${empty results}">
        <div class="alert alert-info text-center">
            No products found.
        </div>
    </c:if>

    <!-- Nút quay lại trang chủ -->
    <div class="text-center mt-4">
        <a href="index.html" class="btn btn-outline-dark">⬅ Back to Home</a>
    </div>
</div>
</body>
</html>
