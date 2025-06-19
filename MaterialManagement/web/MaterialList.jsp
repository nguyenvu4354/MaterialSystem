<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Material List</title>
        <meta charset="UTF-8">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                background-color: #f0f2f5;
                margin: 0;
                padding: 0;
            }

            .container {
                max-width: 1280px;
                margin: 50px auto;
                background: #fff;
                border-radius: 16px;
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
                padding: 40px 50px;
            }

            h2 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 40px;
                font-size: 2.6rem;
                font-weight: 700;
                letter-spacing: 1px;
            }

            table {
                width: 100%;
                border-collapse: separate;
                border-spacing: 0;
                overflow: hidden;
                border-radius: 12px;
                background-color: #ffffff;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            }

            th, td {
                padding: 16px 14px;
                text-align: left;
            }

            th {
                background: linear-gradient(90deg, #3498db, #5dade2);
                color: #fff;
                font-size: 1.1rem;
                font-weight: 700;
                border: none;
            }

            tr:nth-child(even) {
                background-color: #f9fbfc;
            }

            tr:hover {
                background-color: #e8f4fd;
                transition: 0.2s ease-in-out;
            }

            td {
                font-size: 1rem;
                color: #34495e;
                border-bottom: 1px solid #e0e0e0;
            }

            td:first-child, th:first-child {
                border-radius: 8px 0 0 8px;
            }

            td:last-child, th:last-child {
                border-radius: 0 8px 8px 0;
            }

            @media (max-width: 768px) {
                .container {
                    padding: 20px;
                }

                th, td {
                    padding: 10px 8px;
                    font-size: 0.95rem;
                }

                h2 {
                    font-size: 1.6rem;
                }
            }

            .nav-buttons {
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                margin-bottom: 20px;
            }

            .btn-nav {
                padding: 10px 20px;
                background-color: #3498db;
                color: white;
                border: none;
                border-radius: 6px;
                font-size: 1rem;
                cursor: pointer;
                text-decoration: none;
                transition: background-color 0.2s;
            }

            .status-active {
                color: #27ae60;
                font-weight: bold;
            }

            .status-disabled {
                color: #e74c3c;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <!-- 🔍 Search Form -->
            <div style=" width: 900px; margin: 40px auto; padding: 0 15px;">>
                <div class="row mb-4 justify-content-center">
                    <div class="col-md-10">
                        <form action="searchbycode" method="get" class="input-group">
                            <input type="text" class="form-control" name="code" placeholder="Search by material code..."
                                   value="${code != null ? code : ''}" required>
                            <button 
                                style="padding: 10px 18px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-left: 10px;" type="submit">Search</button>
                            <a href="sortbymaterialname" class="btn btn-primary ms-2"
                               style="padding: 10px 18px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-left: 10px;">Sort by Name</a>
                        </form>
                    </div>
                </div> 
            </div>


            <!-- ✅ Bảng dữ liệu -->
            <c:choose>
                <c:when test="${empty productList}">
                    <div class="text-center text-danger fw-bold fs-5">
                        Not Found: <strong>${code}</strong>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="max-width: 1500px; margin: 40px auto; padding: 0 15px;">>
                        <div class="table-responsive">
                            <table class="table table-hover w-100 align-middle">
                                <thead class="text-center fs-6">
                                    <tr>
                                        <th style="width: 200px">Image</th>
                                        <th>Material Code</th>
                                        <th>Material Name</th>
                                        <th>Status</th>
                                        <th>Condition (%)</th>
                                        <th>Price</th>
                                        <th>Category</th>
                                        <th>Unit</th>
                                        <th>Created At</th>
                                        <th>Updated At</th>
                                        <th>Disable</th>
                                        <th style="width: 150px">Actions</th>
                                    </tr>
                                </thead>
                                <tbody class="text-center">
                                    <c:forEach var="product" items="${productList}">
                                        <tr>
                                            <td>
                                                <img src="${product.materialsUrl}" alt="${product.materialName}"
                                                     style="width: 120px; height: 80px; object-fit: cover; border-radius: 6px;">
                                            </td>
                                            <td>${product.materialCode}</td>
                                            <td>${product.materialName}</td>
                                            <td>${product.materialStatus}</td>
                                            <td>${product.conditionPercentage}</td>
                                            <td class="text-danger fw-bold">$${product.price != null ? product.price : 0}</td>
                                            <td>${product.category != null ? product.category.category_name : 'N/A'}</td>
                                            <td>${product.unit != null ? product.unit.unitName : 'N/A'}</td>
                                            <td>${product.createdAt}</td>
                                            <td>${product.updatedAt}</td>
                                            <td>${product.disable ? 'Yes' : 'No'}</td>
                                            <td>
                                                <a href="ProductDetail?id=${product.materialId}" class="btn btn-sm btn-primary">View Detail</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </c:otherwise>
            </c:choose>

            <!-- 🔙 Back button -->
            <div class="text-center mt-4">
                <a href="index.html" class="btn btn-outline-dark"
                   style="padding: 10px 18px; background-color: #3498db; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-left: 10px;">⬅ Back to Home</a>
            </div>
        </div>
    </body>
</html>