<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">
    <head>
        <title>Material Management - Professional Inventory System</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="format-detection" content="telephone=no">
        <meta name="apple-mobile-web-app-capable" content="yes">
        <meta name="author" content="">
        <meta name="keywords" content="inventory, material management, stock control, accessories">
        <meta name="description" content="A professional material management system for tracking and managing computer accessories and inventory.">
        <style>
            body {
                font-family: 'Inter', sans-serif;
                background-color: #faf4ee;
                color: #222;
                font-size: 16px;
                line-height: 1.6;
            }
            .top-bar {
                background: #DEAD6F;
                color: #fff;
                font-size: 16px;
                letter-spacing: 0.5px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.10);
            }
            .top-bar .contact-info span {
                margin-right: 18px;
            }
            .login-buttons .btn {
                border-radius: 20px;
                border: 2px solid #fff;
                color: #fff;
                background: transparent;
                transition: background 0.3s, color 0.3s;
            }
            .login-buttons .btn:hover {
                background: #fff;
                color: #DEAD6F;
            }
            .header-main {
                box-shadow: 0 6px 24px rgba(222,173,111,0.10);
                border-radius: 0 0 24px 24px;
                background: #fff;
                min-height: 80px;
            }
            .header-main .navbar-nav .nav-link {
                font-weight: 700;
                color: #DEAD6F;
                border-radius: 12px;
                font-size: 17px;
                transition: background 0.2s, color 0.2s;
                padding: 10px 22px !important;
                margin: 0 2px;
            }
            .header-main .navbar-nav .nav-link:hover, .header-main .navbar-nav .nav-link.active {
                background: #DEAD6F;
                color: #fff;
            }
            .header-main .btn-main {
                font-size: 18px;
                border-radius: 22px;
                padding: 12px 32px;
                font-weight: 700;
                box-shadow: 0 2px 8px rgba(222,173,111,0.10);
                transition: background 0.2s, color 0.2s, transform 0.2s;
            }
            .header-main .btn-main:hover {
                background: #cfa856;
                color: #fff !important;
                transform: scale(1.04);
            }
            .main-logo img {
                height: 54px;
                border-radius: 10px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.10);
            }
            .navbar-nav .nav-link {
                color: #DEAD6F;
                font-weight: 700;
                margin: 0 10px;
                border-radius: 8px;
                transition: background 0.2s, color 0.2s;
                font-size: 16px;
            }
            .navbar-nav .nav-link:hover, .navbar-nav .nav-link.active {
                background: #DEAD6F;
                color: #fff;
            }
            .offcanvas-body .btn {
                background: #DEAD6F;
                border: none;
                color: #fff;
                font-weight: 600;
                box-shadow: 0 2px 8px rgba(222,173,111,0.12);
                transition: background 0.2s, transform 0.2s;
            }
            .offcanvas-body .btn:hover {
                background: #cfa856;
                transform: translateY(-2px) scale(1.04);
            }
            .sidebar {
                padding: 24px 18px;
                border-radius: 18px;
                background: #fff;
                box-shadow: 0 4px 18px rgba(222,173,111,0.08);
                margin-top: 0;
                border: 1px solid #e0e0e0;
            }
            .sidebar h3 {
                color: #DEAD6F;
                font-weight: 800;
                border-bottom: 2px solid #f9f5f0;
                padding-bottom: 10px;
                margin-bottom: 24px;
                letter-spacing: 1px;
                font-size: 20px;
            }
            .sidebar-item {
                display: flex;
                align-items: center;
                padding: 13px 18px;
                margin-bottom: 14px;
                border-radius: 14px;
                background: #f9f5f0;
                box-shadow: 0 2px 8px rgba(222,173,111,0.06);
                transition: background 0.2s, color 0.2s, box-shadow 0.2s, transform 0.2s;
                text-decoration: none;
                font-weight: 600;
                color: #DEAD6F;
                font-size: 16px;
                gap: 10px;
            }
            .sidebar-item:hover {
                background: #DEAD6F;
                color: #fff;
                box-shadow: 0 4px 16px rgba(222,173,111,0.13);
                transform: translateX(6px) scale(1.04);
            }
            .sidebar-item:hover .category-icon {
                color: #fff;
                transform: scale(1.15);
            }
            .category-icon {
                font-size: 1.5rem;
                color: #DEAD6F;
                transition: color 0.2s, transform 0.2s;
                min-width: 28px;
                text-align: center;
            }
            .sidebar-item h5 {
                color: inherit;
                font-weight: 700;
                margin: 0;
                font-size: 17px;
                letter-spacing: 0.5px;
            }
            .card-container {
                display: flex;
                flex-wrap: wrap;
                gap: 28px;
                justify-content: flex-start;
            }
            .product-card {
                display: flex;
                flex-direction: column;
                width: 270px;
                border-radius: 20px;
                overflow: hidden;
                background: #fff;
                box-shadow: 0 4px 18px rgba(222,173,111,0.10);
                transition: box-shadow 0.3s, transform 0.3s;
                margin-bottom: 0;
                border: 1px solid #e0e0e0;
                position: relative;
            }
            .product-card:hover {
                box-shadow: 0 10px 32px rgba(222,173,111,0.18);
                transform: translateY(-8px) scale(1.04);
            }
            .product-card img {
                width: 100%;
                height: 180px;
                object-fit: cover;
                border-bottom: 1px solid #e0e0e0;
                border-radius: 20px 20px 0 0;
                box-shadow: 0 2px 8px rgba(222,173,111,0.06);
            }
            .card-content {
                padding: 20px 18px 18px 18px;
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                background: #fff;
            }
            .card-content h5 {
                margin: 0 0 10px 0;
                font-size: 1.15rem;
                font-weight: 700;
                color: #cfa856;
                min-height: 44px;
                letter-spacing: 0.5px;
                text-overflow: ellipsis;
                overflow: hidden;
                white-space: nowrap;
            }
            .card-content p {
                margin: 0 0 14px 0;
                font-size: 1.08rem;
                color: #DEAD6F;
                font-weight: 700;
            }
            .btn-main {
                background: #DEAD6F;
                color: #fff !important;
                border: none;
                border-radius: 24px;
                font-weight: 700;
                padding: 9px 28px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.10);
                font-size: 16px;
                transition: background 0.2s, color 0.2s, transform 0.2s, box-shadow 0.2s;
            }
            .btn-main:hover, .btn-main:focus {
                background: #cfa856;
                color: #fff !important;
                box-shadow: 0 4px 18px rgba(222,173,111,0.18);
                transform: scale(1.05);
            }
            .btn-outline-main {
                background: #fff;
                color: #DEAD6F !important;
                border: 2px solid #DEAD6F;
                border-radius: 24px;
                font-weight: 700;
                padding: 9px 28px;
                transition: background 0.2s, color 0.2s, border 0.2s;
                font-size: 16px;
            }
            .btn-outline-main:hover, .btn-outline-main:focus {
                background: #DEAD6F;
                color: #fff !important;
                border: 2px solid #cfa856;
            }
            .btn-action {
                background: #27ae60;
                color: #fff !important;
                border: none;
                border-radius: 20px;
                font-weight: 600;
                padding: 8px 20px;
                transition: background 0.2s, transform 0.2s;
                font-size: 16px;
            }
            .btn-action:hover, .btn-action:focus {
                background: #219150;
                color: #fff !important;
                transform: scale(1.05);
            }
            .btn-detail {
                background: #DEAD6F;
                color: #fff;
                border: none;
                border-radius: 18px;
                font-weight: 600;
                padding: 8px 20px;
                transition: background 0.2s, color 0.2s, transform 0.2s;
                box-shadow: 0 2px 8px rgba(222,173,111,0.10);
                display: inline-flex;
                align-items: center;
                gap: 6px;
                font-size: 15px;
            }
            .btn-detail:hover {
                background: #cfa856;
                color: #fff;
                transform: scale(1.05);
            }
            .btn-contact-info {
                border-radius: 24px;
                background: #23272f;
                color: #fff !important;
                border: none;
                font-weight: 600;
                padding: 8px 22px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.12);
                display: flex;
                align-items: center;
                gap: 8px;
                transition: background 0.2s, color 0.2s, transform 0.2s;
                font-size: 16px;
            }
            .btn-contact-info:hover {
                background: #DEAD6F;
                color: #fff !important;
                transform: scale(1.05);
            }
            .btn-login {
                border-radius: 50%;
                background: #23272f;
                color: #fff !important;
                border: none;
                font-weight: 700;
                width: 44px;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                box-shadow: 0 2px 12px rgba(222,173,111,0.13);
                transition: background 0.2s, color 0.2s, box-shadow 0.2s;
                padding: 0;
            }
            .btn-login:hover, .btn-login:focus {
                background: #DEAD6F;
                color: #fff !important;
                box-shadow: 0 4px 18px rgba(222,173,111,0.18);
            }
            .section-header h2 {
                color: #DEAD6F;
                font-weight: 800;
                border-bottom: 2px solid #f9f5f0;
                padding-bottom: 10px;
                margin-bottom: 20px;
                letter-spacing: 1px;
            }
            .pagination .page-link {
                color: #DEAD6F;
                border-radius: 8px;
                margin: 0 6px;
                font-weight: 600;
                transition: background 0.2s, color 0.2s;
                font-size: 16px;
                border: none;
            }
            .pagination .page-link:hover, .pagination .page-item.active .page-link {
                background: #DEAD6F;
                color: #fff;
            }
            footer {
                background: #fff;
                color: #222;
                border-radius: 18px;
                box-shadow: 0 4px 18px rgba(222,173,111,0.08);
            }
            .footer-menu h3 {
                color: #DEAD6F;
                font-weight: 800;
                margin-bottom: 18px;
                font-size: 18px;
            }
            .footer-menu a {
                color: #DEAD6F;
                font-weight: 600;
                transition: color 0.2s;
                font-size: 16px;
            }
            .footer-menu a:hover {
                color: #cfa856;
            }
            .footer-menu ul {
                padding-left: 0;
            }
            .footer-menu li {
                margin-bottom: 10px;
                font-size: 16px;
            }
            .footer-menu img {
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.12);
            }
            .social-links ul {
                gap: 10px;
            }
            .social-icon {
                font-size: 24px;
                color: #DEAD6F;
                transition: color 0.2s;
            }
            .social-icon:hover {
                color: #cfa856;
            }
            @media (max-width: 991px) {
                .sidebar {
                    margin-bottom: 32px;
                }
                .card-container {
                    justify-content: center;
                }
                .header-main .navbar-nav {
                    flex-direction: column !important;
                    gap: 0 !important;
                }
                .header-main .navbar-nav .nav-link {
                    margin-bottom: 8px;
                }
                .header-main .btn-main {
                    width: 100%;
                    margin-bottom: 8px;
                }
            }
            @media (max-width: 767px) {
                .header-main, .footer {
                    border-radius: 0;
                }
                .main-logo img {
                    height: 44px;
                }
                .product-card {
                    width: 100%;
                    min-width: 220px;
                }
            }
            .search-bar-center {
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
            }
            .search-icon {
                pointer-events: none;
            }
            .dropdown-menu .dropdown-item {
                font-size: 16px;
                font-weight: 500;
                border-radius: 8px;
                transition: background 0.2s, color 0.2s;
                padding: 10px 18px;
                color: #cfa856;
            }
            .dropdown-menu .dropdown-item:hover {
                background: #f9f5f0;
                color: #fff;
            }
            .dropdown-menu .dropdown-item i {
                color: #DEAD6F;
                min-width: 22px;
                text-align: center;
            }
            @media (max-width: 991px) {
                .search-bar-center {
                    position: static !important;
                    transform: none !important;
                    margin: 12px 0 0 0;
                    width: 100%;
                }
                .dropdown {
                    margin-left: 0 !important;
                }
            }
            .footer-mms {
                background: #23272f;
                color: #f1f5f9;
                border-radius: 16px 16px 0 0;
                box-shadow: 0 2px 12px rgba(222,173,111,0.08);
                font-size: 15px;
            }
            .footer-mms .footer-title {
                color: #DEAD6F;
                font-weight: 700;
                margin-bottom: 16px;
                font-size: 17px;
                display: flex;
                align-items: center;
            }
            .footer-mms .footer-desc {
                color: #e5e7eb;
                font-size: 15px;
            }
            .footer-mms .footer-list {
                list-style: none;
                padding-left: 0;
                margin-bottom: 0;
            }
            .footer-mms .footer-list li {
                margin-bottom: 10px;
                color: #e5e7eb;
                display: flex;
                align-items: center;
            }
            .footer-mms .footer-list a {
                color: #e5e7eb;
                text-decoration: none;
                transition: color 0.2s;
            }
            .footer-mms .footer-list a:hover {
                color: #f9f5f0;
            }
            .footer-mms .footer-list i {
                color: #f9f5f0;
                min-width: 20px;
                text-align: center;
            }
            @media (max-width: 991px) {
                .footer-mms .row > div {
                    margin-bottom: 24px;
                }
            }
            .btn-topbar {
                background: #23272f;
                color: #fff !important;
                border: none;
                border-radius: 50%;
                width: 44px;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: center;
                box-shadow: 0 2px 12px rgba(222,173,111,0.13);
                font-size: 20px;
                transition: background 0.2s, color 0.2s, box-shadow 0.2s;
                padding: 0;
            }
            .btn-topbar:hover, .btn-topbar:focus {
                background: #DEAD6F;
                color: #fff !important;
                box-shadow: 0 4px 18px rgba(222,173,111,0.18);
            }
        </style>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@9/swiper-bundle.min.css" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="top-bar py-2">
            <div class="container d-flex justify-content-center align-items-center position-relative">
                <form id="searchOverlay" action="search" method="get" class="search-overlay-form" style="max-width: 400px; width: 100%;">
                    <div class="input-group" style="border-radius: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); background: #fff;">
                        <span class="input-group-text bg-white border-0" style="border-radius: 24px 0 0 24px;">
                            <i class="fas fa-search" style="color: #DEAD6F;"></i>
                        </span>
                        <input class="form-control border-0" type="search" name="keyword" placeholder="Find material..." required style="border-radius: 0 24px 24px 0; height: 44px; background: #fff; color: #222;">
                        <button class="btn btn-main" type="submit" style="border-radius: 24px; height: 44px; min-width: 44px;">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <jsp:include page="Header.jsp" />
<!--        <section class="module-links py-4">
            <div class="container">
                <div class="row g-4 justify-content-center text-center">
                    <div class="col-6 col-md-3">
                        <a href="#" class="card-link-box d-block text-decoration-none shadow-sm p-4 rounded-3">
                            <i class="fas fa-boxes-stacked fa-2x mb-2 text-primary"></i>
                            <div class="fw-semibold text-dark">Inventory Management</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="card-link-box d-block text-decoration-none shadow-sm p-4 rounded-3">
                            <i class="fas fa-share-alt fa-2x mb-2 text-info"></i>
                            <div class="fw-semibold text-dark">Allocation Tracking</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="card-link-box d-block text-decoration-none shadow-sm p-4 rounded-3">
                            <i class="fas fa-tools fa-2x mb-2 text-warning"></i>
                            <div class="fw-semibold text-dark">Maintenance Logs</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="card-link-box d-block text-decoration-none shadow-sm p-4 rounded-3">
                            <i class="fas fa-chart-line fa-2x mb-2 text-success"></i>
                            <div class="fw-semibold text-dark">Reporting Tools</div>
                        </a>
                    </div>
                </div>
            </div>
        </section>-->

        <div class="container-fluid my-5">
            <div class="row">
                <div class="col-md-2 sidebar-col mt-5">
                    <div class="sidebar">
                        <h3 class="mb-4">Categories</h3>
                        <ul class="list-unstyled">
                            <c:forEach var="c" items="${categories}">
                                <li>
                                    <a href="filter?categoryId=${c.category_id}" class="sidebar-item" data-category-id="${c.category_id}">
                                        <h5>${c.category_name}</h5>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <div class="col-md-10 mb-1">
                    <div class="content">
                        <section id="clothing" class="my-5 overflow-hidden">
                            <div class="container pb-5">
                                <div class="section-header d-md-flex justify-content-between align-items-center mb-4">
                                    <h2 class="display-6 fw-semibold"><i class="fas fa-warehouse me-2"></i>Material Inventory</h2>
                                    <!-- Form tìm kiếm theo giá -->
                                    <form action="searchPrice" method="get" style="display: flex; margin-right: 200px">
                                        <div class="input-group" style="max-width: 300px;">
                                            <span class="input-group-text bg-white border-0" style="border-radius: 24px 0 0 24px;">
                                                <i class="fas fa-dollar-sign"></i>
                                            </span>
                                            <input type="number" name="minPrice" class="form-control" placeholder="Min Price" 
                                                   value="${minPrice}" min="0" step="0.01" style="border-radius: 0;">
                                            <input type="number" name="maxPrice" class="form-control" placeholder="Max Price" 
                                                   value="${maxPrice}" min="0" step="0.01" style="border-radius: 0 24px 24px 0;">
                                        </div>
                                        <!--                            <select name="sort" class="form-select" style="max-width: 200px; height: 44px;">
                                                                        <option value="code_asc" ${sortOption == 'code_asc' ? 'selected' : ''}>Code ↑</option>
                                                                        <option value="code_desc" ${sortOption == 'code_desc' ? 'selected' : ''}>Code ↓</option>
                                                                        <option value="name_asc" ${sortOption == 'name_asc' ? 'selected' : ''}>Name ↑</option>
                                                                        <option value="name_desc" ${sortOption == 'name_desc' ? 'selected' : ''}>Name ↓</option>
                                                                        <option value="price_asc" ${sortOption == 'price_asc' ? 'selected' : ''}>Price ↑</option>
                                                                        <option value="price_desc" ${sortOption == 'price_desc' ? 'selected' : ''}>Price ↓</option>
                                                                        <option value="condition_asc" ${sortOption == 'condition_asc' ? 'selected' : ''}>Condition ↑</option>
                                                                        <option value="condition_desc" ${sortOption == 'condition_desc' ? 'selected' : ''}>Condition ↓</option>
                                                                    </select>-->
                                        <button type="submit" class="btn btn-main" style="height: 44px;">Search</button>
                                        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-main" style="height: 44px;">Clear</a>
                                    </form>
                                </div>

                                <div class="card-container">
                                    <c:forEach var="product" items="${productList}">
                                        <div class="product-card">
                                            <img src="images/material/${product.materialsUrl}" alt="${product.materialName}" width="200">
                                            <div class="card-content">
                                                <h5>${product.materialName}</h5>
                                                <p style="color: #DEAD6F; font-weight: bold;">$${product.price}</p>
                                                <a href="ProductDetail?id=${product.materialId}" class="btn-detail">
                                                    <i class="fas fa-eye me-1"></i> View Detail
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>

                                    <!-- Pagination -->
                                    <nav aria-label="Page navigation" style="padding-left: 500px">
                                        <ul class="pagination justify-content-center mt-4" id="pagination">
                                            <li class="page-item active"><a class="page-link" href="#" data-page="1">1</a></li>
                                            <li class="page-item"><a class="page-link" href="#" data-page="2">2</a></li>
                                            <li class="page-item"><a class="page-link" href="#" data-page="3">3</a></li>
                                        </ul>
                                    </nav>
                                </div>
                            </div>
                        </section>
                    </div>
                </div>

            </div>
        </div>
        <footer id="footer" class="footer-mms mt-5">
            <div class="container py-4">
                <div class="row">
                    <div class="col-md-3 mb-4 mb-md-0">
                        <h5 class="footer-title"><i class="fas fa-warehouse me-2"></i>About System</h5>
                        <p class="footer-desc">
                            This Material Management System is for internal company use only. All activities are monitored and restricted to authorized personnel.
                        </p>
                    </div>
                    <div class="col-md-3 mb-4 mb-md-0">
                        <h5 class="footer-title"><i class="fas fa-phone-alt me-2"></i>Internal Contact</h5>
                        <ul class="footer-list">
                            <li><i class="fas fa-envelope me-2"></i>it-support@company.com</li>
                            <li><i class="fas fa-phone me-2"></i>Ext: 1234 (IT Dept.)</li>
                            <li><i class="fas fa-user-tie me-2"></i>Warehouse Manager: John Doe</li>
                        </ul>
                    </div>
                    <div class="col-md-3 mb-4 mb-md-0">
                        <h5 class="footer-title"><i class="fas fa-file-alt me-2"></i>Internal Policies</h5>
                        <ul class="footer-list">
                            <li><a href="#"><i class="fas fa-shield-alt me-2"></i>Data Security Policy</a></li>
                            <li><a href="#"><i class="fas fa-user-lock me-2"></i>System Usage Rules</a></li>
                            <li><a href="#"><i class="fas fa-headset me-2"></i>IT Support Policy</a></li>
                        </ul>
                    </div>
                    <div class="col-md-3">
                        <h5 class="footer-title"><i class="fas fa-question-circle me-2"></i>Guides</h5>
                        <ul class="footer-list">
                            <li><a href="#"><i class="fas fa-user-cog me-2"></i>User Manual</a></li>
                            <li><a href="#"><i class="fas fa-search me-2"></i>Check Inventory</a></li>
                            <li><a href="#"><i class="fas fa-life-ring me-2"></i>Contact IT Support</a></li>
                        </ul>
                    </div>
                </div>
                <hr class="my-3" style="border-top:1.5px solid #f9f5f0;opacity:0.15;">
                <div class="text-center small" style="color:#bbb;">© 2024 Material Management System (Internal Use Only). All rights reserved.</div>
            </div>
        </footer>
        <div class="modal fade" id="contactModal" tabindex="-1" aria-labelledby="contactModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="contactModalLabel"><i class="fas fa-user-shield me-2"></i>Internal Contact</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div><i class="fas fa-envelope me-2"></i> it-support@company.com</div>
                        <div><i class="fas fa-phone me-2"></i> Ext: 1234 (IT Dept.)</div>
                        <div><i class="fas fa-user-tie me-2"></i> Warehouse Manager: John Doe</div>
                    </div>
                </div>
            </div>
        </div>
        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/swiper@9/swiper-bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
        crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
            var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
            var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
                return new bootstrap.Popover(popoverTriggerEl);
            });
        </script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                var showBtn = document.getElementById('showSearchBtn');
                var overlay = document.getElementById('searchOverlay');
                var input = overlay.querySelector('input');
                showBtn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    overlay.classList.toggle('d-none');
                    if (!overlay.classList.contains('d-none')) {
                        setTimeout(function () {
                            input.focus();
                        }, 100);
                    }
                });
                document.addEventListener('click', function (e) {
                    if (!overlay.classList.contains('d-none') && !overlay.contains(e.target) && e.target !== showBtn) {
                        overlay.classList.add('d-none');
                    }
                });
                overlay.addEventListener('click', function (e) {
                    e.stopPropagation();
                });
            });
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const products = document.querySelectorAll('.product-card');
                const productsPerPage = 8;
                const pagination = document.getElementById('pagination');
                let currentPage = 1;
                const totalPages = Math.ceil(products.length / productsPerPage);
                function showPage(page) {
                    currentPage = page;
                    products.forEach((product, idx) => {
                        if (idx >= (page - 1) * productsPerPage && idx < page * productsPerPage) {
                            product.style.display = '';
                        } else {
                            product.style.display = 'none';
                        }
                    });
                    document.querySelectorAll('#pagination .page-item').forEach((li, i) => {
                        if (i === page - 1) {
                            li.classList.add('active');
                        } else {
                            li.classList.remove('active');
                        }
                    });
                }
                if (pagination) {
                    pagination.querySelectorAll('.page-link').forEach(link => {
                        link.addEventListener('click', function (e) {
                            e.preventDefault();
                            const page = parseInt(this.getAttribute('data-page'));
                            showPage(page);
                        });
                    });
                    showPage(1);
                }
            });
        </script>
        <script>
            // JavaScript đơn giản (chỉ giữ thanh tìm kiếm nếu cần)
            document.addEventListener('DOMContentLoaded', function () {
                var showBtn = document.getElementById('showSearchBtn');
                var overlay = document.getElementById('searchOverlay');
                var input = overlay.querySelector('input');
                showBtn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    overlay.classList.toggle('d-none');
                    if (!overlay.classList.contains('d-none')) {
                        setTimeout(function () {
                            input.focus();
                        }, 100);
                    }
                });
                document.addEventListener('click', function (e) {
                    if (!overlay.classList.contains('d-none') && !overlay.contains(e.target) && e.target !== showBtn) {
                        overlay.classList.add('d-none');
                    }
                });
                overlay.addEventListener('click', function (e) {
                    e.stopPropagation();
                });
            });
        </script>
    </body>
</html>