<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Material Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
    body {
        background-color: #fff5e6;
        font-family: 'Inter', sans-serif;
    }

    .product-card {
        border-radius: 20px;
        background-color: #ffffff;
        box-shadow: 0 4px 18px rgba(244,161,97,0.10);
        padding: 30px;
        transition: transform 0.3s ease-in-out;
    }

    .product-card:hover {
        transform: translateY(-3px);
    }

    .product-image {
        width: 100%;
        height: auto;
        max-height: 320px;
        object-fit: contain;
        background-color: #fff;
        border-radius: 15px;
        padding: 10px;
        border: 1px solid #e0e0e0;
    }

    .product-info p {
        font-size: 1.05rem;
        margin-bottom: 12px;
        color: #2a2a2a;
    }

    .product-info strong {
        color: #4a4a4a;
    }

    .back-btn {
        border-radius: 30px;
        padding: 10px 30px;
        font-weight: 500;
        background: #fff;
        color: #f4a261;
        border: 2px solid #f4a261;
        transition: all 0.2s ease;
    }

    .back-btn:hover {
        background-color: #f4a261;
        color: #fff;
    }

    .status-new { color: #27ae60; }
    .status-used { color: #ffc107; }
    .status-damaged { color: #dc3545; }
    .condition-text { font-style: italic; color: #6c757d; }

    h2 {
        color: #f4a261;
        font-weight: 800;
        letter-spacing: 1px;
        margin-bottom: 20px;
    }

    .alert {
        border-radius: 15px;
        box-shadow: 0 2px 8px rgba(244,161,97,0.10);
    }
</style>
</head>
<body>
<div class="container my-5">
    <h2 class="text-center fw-bold mb-5">🔍 Material Detail</h2>

    <c:if test="${not empty product}">
        <div class="row justify-content-center">
            <div class="col-md-10 col-lg-9">
                <div class="product-card">
                    <div class="row g-4 align-items-center">
                        <div class="col-md-5">
                            <c:choose>
                                <c:when test="${not empty product.materialsUrl}">
                                    <img src="images/material1/${product.materialsUrl}" alt="${product.materialName}" class="product-image img-fluid">
                                </c:when>
                                <c:otherwise>
                                    <img src="images/default.jpg" alt="No Image" class="product-image img-fluid">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-7 product-info">
                            <h3 class="fw-bold mb-3 text-primary">${product.materialName}</h3>
                            <p><strong>🆔 Code:</strong> ${product.materialCode}</p>
                            <p><strong>💲 Price:</strong> 
                                $<fmt:formatNumber value="${product.price != null ? product.price : 0}" type="number" />
                            </p>
                            <p><strong>🏷️ Status:</strong> 
                                <span class="status-${product.materialStatus == 'new' ? 'new' : product.materialStatus == 'used' ? 'used' : 'damaged'}">
                                    ${product.materialStatus}
                                </span>
                            </p>
                            <p class="condition-text"><strong>🔢 Condition:</strong> ${product.conditionPercentage}%</p>

                            <p><strong>📏 Unit:</strong> 
                                <c:choose>
                                    <c:when test="${not empty product.unit}">
                                        ${product.unit.unitName}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>

                         

                            <p><strong>📂 Category:</strong> 
                                <c:choose>
                                    <c:when test="${not empty product.category}">
                                        ${product.category.category_name}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>

                            <p><strong>📅 Created At:</strong> ${product.createdAt}</p>
                            <p><strong>🔄 Updated At:</strong> ${product.updatedAt}</p>
                            <p><strong>🚫 Disabled:</strong> ${product.disable ? 'Yes' : 'No'}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <c:if test="${empty product}">
        <div class="alert alert-danger text-center mt-5">
            ❌ No product found for the given ID.
        </div>
    </c:if>

    <div class="text-center mt-4">
        <a href="view" class="btn btn-outline-secondary back-btn me-2">⬅ Back to Material List</a>
        <a href="home" class="btn btn-outline-secondary back-btn">🏠 Back to Home</a>
    </div>
</div>
</body>
</html>