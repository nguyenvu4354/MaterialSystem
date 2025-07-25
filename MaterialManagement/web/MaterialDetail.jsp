<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Material Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <style>
    body {
        background-color: #fff5e6;
        font-family: 'Inter', sans-serif;
    }
    .product-card {
        border-radius: 16px;
        background-color: #fff;
        box-shadow: 0 2px 12px rgba(0,0,0,0.07);
        padding: 32px 28px;
        margin-bottom: 32px;
        border: 1px solid #e0e0e0;
        transition: box-shadow 0.2s;
    }
    .product-card:hover {
        box-shadow: 0 4px 18px rgba(244,161,97,0.13);
    }
    .product-image {
        width: 100%;
        max-width: 320px;
        height: auto;
        max-height: 320px;
        object-fit: cover;
        background: #fff;
        border-radius: 12px;
        padding: 8px;
        border: 1px solid #e0e0e0;
        display: block;
        margin: 0 auto;
    }
    .product-info p {
        font-size: 1.08rem;
        margin-bottom: 13px;
        color: #222;
    }
    .product-info strong {
        color: #4a4a4a;
    }
    .badge-status {
        padding: 8px 22px;
        border-radius: 12px;
        font-size: 1rem;
        font-weight: 600;
        border: 1.5px solid #bdbdbd;
        display: inline-block;
        min-width: 70px;
        text-align: center;
    }
    .badge-status.new {
        background: #198754;
        color: #fff;
        border-color: #198754;
    }
    .badge-status.used {
        background: #fde047;
        color: #000;
        border-color: #facc15;
    }
    .badge-status.damaged {
        background: #dc2626;
        color: #fff;
        border-color: #b91c1c;
    }
    .back-btn, .category-btn {
        border-radius: 30px;
        padding: 10px 30px;
        font-weight: 500;
        background: #fff;
        color: #f4a261;
        border: 2px solid #f4a261;
        transition: all 0.2s;
        margin-right: 10px;
    }
    .back-btn:hover, .category-btn:hover {
        background-color: #f4a261;
        color: #fff;
    }
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
    .icon {
        width: 1.3em;
        margin-right: 6px;
        opacity: 0.8;
    }
    .action-btn-row {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 18px;
        margin-top: 18px;
    }
    .action-btn-row .btn, .action-btn-row .category-btn, .action-btn-row .back-btn {
        min-width: 150px;
        max-width: 220px;
        font-size: 1.08rem;
        padding: 12px 0;
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        font-weight: 500;
        transition: all 0.2s;
        box-shadow: 0 1px 4px rgba(244,161,97,0.07);
    }
    .action-btn-row .btn-primary, .action-btn-row .btn-warning, .action-btn-row .btn-success, .action-btn-row .btn-danger, .action-btn-row .btn-info, .action-btn-row .btn-secondary, .action-btn-row .btn-outline-primary, .action-btn-row .btn-outline-dark {
        color: #fff;
    }
    .action-btn-row .btn-outline-primary, .action-btn-row .btn-outline-dark, .action-btn-row .category-btn, .action-btn-row .back-btn {
        background: #fff;
        color: #f4a261;
        border: 2px solid #f4a261;
    }
    .action-btn-row .btn-outline-primary:hover, .action-btn-row .btn-outline-dark:hover, .action-btn-row .category-btn:hover, .action-btn-row .back-btn:hover {
        background: #f4a261;
        color: #fff;
    }
    @media (max-width: 600px) {
        .action-btn-row .btn, .action-btn-row .category-btn, .action-btn-row .back-btn {
            min-width: 110px;
            font-size: 0.98rem;
            padding: 10px 0;
        }
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
                                    <img src="images/material/${product.materialsUrl}" alt="${product.materialName}" class="product-image img-fluid">
                                </c:when>
                                <c:otherwise>
                                    <img src="images/material/default-material.png" alt="No Image" class="product-image img-fluid">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-7 product-info">
                            <h3 class="fw-bold mb-3" style="color:#1976d2;">${product.materialName}</h3>
                            <p><img src="https://img.icons8.com/ios-filled/20/1976d2/identification-documents.png" class="icon"/> <strong>Code:</strong> ${product.materialCode}</p>
                            <p><img src="https://img.icons8.com/ios-filled/20/f4a261/price-tag.png" class="icon"/> <strong>Status:</strong> 
                                <span class="badge-status ${product.materialStatus == 'new' ? 'new' : product.materialStatus == 'used' ? 'used' : 'damaged'}">
                                    ${product.materialStatus}
                                </span>
                            </p>
                            <p><img src="https://img.icons8.com/ios-filled/20/1976d2/scale.png" class="icon"/> <strong>Unit:</strong> 
                                <c:choose>
                                    <c:when test="${not empty product.unit}">
                                        ${product.unit.unitName}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p><img src="https://img.icons8.com/ios-filled/20/f4a261/folder-invoices--v1.png" class="icon"/> <strong>Category:</strong> 
                                <c:choose>
                                    <c:when test="${not empty product.category}">
                                        ${product.category.category_name}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p><img src="https://img.icons8.com/ios-filled/20/1976d2/calendar.png" class="icon"/> <strong>Created At:</strong> ${product.createdAt}</p>
                            <p><img src="https://img.icons8.com/ios-filled/20/1976d2/refresh.png" class="icon"/> <strong>Updated At:</strong> ${product.updatedAt}</p>
                            <p><img src="https://img.icons8.com/ios-filled/20/f44336/cancel.png" class="icon"/> <strong>Disabled:</strong> ${product.disable ? 'Yes' : 'No'}</p>
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

    <div class="action-btn-row">
        <c:if test="${not empty product.category}">
            <a href="filter?categoryId=${product.category.category_id}" class="btn category-btn" title="View all in this category">📂 <span class="d-none d-md-inline">${product.category.category_name}</span></a>
        </c:if>
        <a href="home" class="btn back-btn" title="Back to Home">🏠 <span class="d-none d-md-inline">HOME</span></a>
        <c:if test="${product.disable}">
            <span class="badge bg-danger ms-2" style="font-size:1rem;vertical-align:middle;">Disabled</span>
        </c:if>
        <c:if test="${!product.disable}">
            <c:choose>
                <c:when test="${roleId == 1}">
                    <a href="editmaterial?materialId=${product.materialId}" class="btn btn-primary">✏️ <span class="d-none d-md-inline">Edit</span></a>
                    <a href="ExportMaterial?id=${product.materialId}" class="btn btn-warning">📦 <span class="d-none d-md-inline">Export</span></a>
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            More
                        </button>
                        <ul class="dropdown-menu">
                            <li>
                                <form action="deletematerial" method="post" style="display:inline;">
                                    <input type="hidden" name="materialId" value="${product.materialId}" />
                                    <button type="submit" class="dropdown-item" onclick="return confirm('Are you sure you want to delete this material?');">🗑️ Delete</button>
                                </form>
                            </li>
                            <li><a class="dropdown-item" href="ImportMaterial">➕ Import</a></li>
                            <li><a class="dropdown-item" href="StaticInventory">📊 Statistics</a></li>
                            <li><a class="dropdown-item" href="UserList">👤 User Management</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:when test="${roleId == 2}">
                    <a href="StaticInventory" class="btn btn-info">📊 <span class="d-none d-md-inline">Statistics</span></a>
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            More
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="ExportRequestList">✔️ Approve Requests</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:when test="${roleId == 3}">
                    <a href="editmaterial?materialId=${product.materialId}" class="btn btn-primary">✏️ <span class="d-none d-md-inline">Edit</span></a>
                    <a href="ExportMaterial?id=${product.materialId}" class="btn btn-warning">📦 <span class="d-none d-md-inline">Export</span></a>
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            More
                        </button>
                        <ul class="dropdown-menu">
                            <li>
                                <form action="deletematerial" method="post" style="display:inline;">
                                    <input type="hidden" name="materialId" value="${product.materialId}" />
                                    <button type="submit" class="dropdown-item" onclick="return confirm('Are you sure you want to delete this material?');">🗑️ Delete</button>
                                </form>
                            </li>
                            <li><a class="dropdown-item" href="ImportMaterial">➕ Import</a></li>
                            <li><a class="dropdown-item" href="RepairRequestList">🛠️ Request Repair</a></li>
                            <li><a class="dropdown-item" href="StaticInventory">📊 Statistics</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:when test="${roleId == 4}">
                    <a href="ExportRequestList" class="btn btn-outline-primary">📤 <span class="d-none d-md-inline">Request Export</span></a>
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            More
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="ListPurchaseRequests">🛒 Request Purchase</a></li>
                            <li><a class="dropdown-item" href="ViewRequests">📋 My Requests</a></li>
                        </ul>
                    </div>
                </c:when>
            </c:choose>
        </c:if>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>