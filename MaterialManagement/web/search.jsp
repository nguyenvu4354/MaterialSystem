<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Search Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #fff3e0;
            font-family: 'Segoe UI', sans-serif;
        }
        .container {
            max-width: 1200px;
            margin: 40px auto;
            background: #fff;
            border-radius: 16px;
            padding: 40px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        h2 {
            text-align: center;
            color: #d59f39;
            margin-bottom: 30px;
            font-size: 2.2rem;
            font-weight: bold;
        }
        .search-box {
            max-width: 600px;
            margin: auto;
        }
        .input-group {
            display: flex;
            gap: 10px;
        }
        .form-control {
            border-radius: 30px;
            border: 1px solid #ddd;
            padding: 10px 20px;
            font-size: 1rem;
        }
        .btn-primary {
            background-color: #d59f39;
            border: none;
            border-radius: 30px;
            padding: 10px 25px;
            font-weight: 600;
        }
        .btn-outline-primary {
            color: #d59f39;
            border: 1px solid #d59f39;
            border-radius: 18px;
            padding: 5px 14px;
            font-size: 0.85rem;
            font-weight: 600;
        }
        .btn-outline-dark {
            background-color: #d59f39;
            color: white;
            border: none;
            border-radius: 24px;
            padding: 10px 24px;
            font-weight: 600;
        }
        table img {
            width: 120px;
            height: 80px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #e0e0e0;
        }
        table {
            border-radius: 12px;
            overflow: hidden;
            background-color: #fff;
        }
        .table-light {
            background-color: #d59f39;
            color: white;
            font-weight: bold;
            font-size: 0.9rem;
        }
        td, th {
            font-size: 0.9rem;
            padding: 12px;
            text-align: center;
            vertical-align: middle;
        }
        tr:nth-child(even) {
            background-color: #fffaf0;
        }
        .alert-info {
            border-radius: 8px;
            background-color: #fef6e4;
            color: #444;
            font-weight: 500;
        }
        @media (max-width: 768px) {
            .container {
                padding: 20px;
            }
            h2 {
                font-size: 1.5rem;
            }
            .search-box {
                max-width: 100%;
            }
            .input-group {
                flex-direction: column;
                gap: 10px;
            }
            .form-control, .btn-primary {
                width: 100%;
            }
            table, thead, tbody, th, td, tr {
                font-size: 0.85rem;
            }
        }
    </style>
</head>
<body>
<div class="container my-5">
    <h2 class="text-center">🔎 Search Materials</h2>

    <c:if test="${not empty keyword}">
        <h5 class="text-center text-muted mb-4">
            Result for search: <strong>${keyword}</strong>
        </h5>
    </c:if>

    <form action="search" method="get" class="search-box mb-4">
        <div class="input-group">
            <input type="text" name="keyword" class="form-control" placeholder="Enter product name..." required>
            <button class="btn btn-primary" type="submit">Search</button>
        </div>
    </form>

    <c:if test="${not empty results}">
        <div class="table-responsive">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table table-light">
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
                            <td><img src="images/material/${product.materialsUrl}" alt="${product.materialName}"></td>
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

    <c:if test="${empty results}">
        <div class="alert alert-info text-center">
            No products found.
        </div>
    </c:if>

    <div class="text-center mt-4">
        <a href="home" class="btn btn-outline-dark">⬅ Back to Home</a>
    </div>
</div>
</body>
</html>
