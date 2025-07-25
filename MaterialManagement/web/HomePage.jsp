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
            /* Sidebar */
            .sidebar {
                background: #fff;
                border: 1.5px solid #DEAD6F;
                border-radius: 16px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.07);
                padding: 0;
                max-height: 400px;
                overflow-y: auto;
            }
            .sidebar .sidebar-header {
                background: #DEAD6F;
                color: #fff;
                padding: 10px 18px;
                border-radius: 14px 14px 0 0;
                font-weight: 800;
                font-size: 20px;
                letter-spacing: 1px;
                position: relative;
                margin-bottom: 0;
            }
            .sidebar .sidebar-header::after {
                content: '▲';
                position: absolute;
                right: 10px;
                top: 50%;
                transform: translateY(-50%);
            }
            .sidebar-item {
                background: #fff;
                color: #DEAD6F;
                border: 1px solid #DEAD6F;
                border-radius: 12px;
                margin: 10px 10px 0;
                padding: 12px 18px;
                font-weight: 700;
                transition: background 0.2s, color 0.2s;
                font-size: 16px;
                text-align: left;
                display: block;
            }
            .sidebar-item.active {
                background: #DEAD6F;
                color: #fff !important;
                border: 1.5px solid #cfa856;
            }
            .sidebar-item:hover {
                background: #cfa856;
                color: #fff !important;
                border: 1.5px solid #cfa856;
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
                display: inline;
            }
            .card-container {
                display: flex;
                flex-wrap: wrap;
                gap: 28px;
                justify-content: flex-start;
            }
            /* Card */
            .product-card {
                width: 270px;
                min-height: 420px;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                background: #fff;
                border: 1.5px solid #DEAD6F;
                border-radius: 18px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.07);
                transition: box-shadow 0.2s, transform 0.2s;
            }
            .product-card:hover {
                box-shadow: 0 6px 24px rgba(222,173,111,0.13);
                transform: scale(1.03);
            }
            .product-card img {
                width: 100%;
                height: 180px;
                object-fit: cover;
                border-bottom: 1px solid #f9f5f0;
                border-radius: 20px 20px 0 0;
                box-shadow: 0 2px 8px rgba(222,173,111,0.06);
            }
            .card-content {
                flex: 1 1 auto;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                min-height: 180px;
            }
            .card-content h5 {
                min-height: 48px;
                max-height: 48px;
                overflow: hidden;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                color: #DEAD6F;
                font-weight: 700;
            }
            .card-content .info-label {
                color: #6c757d;
                font-size: 14px;
            }
            .card-content .status-new {
                color: #DEAD6F;
                font-weight: 700;
            }
            .card-content .status-used {
                color: #cfa856;
                font-weight: 700;
            }
            .card-content .status-damaged {
                color: #e74c3c;
                font-weight: 700;
            }
            .card-content .stock-zero {
                color: #e74c3c;
                font-weight: 700;
            }
            .card-content b {
                color: #DEAD6F;
            }
            .card-content p {
                margin: 0 0 14px 0;
                font-size: 1.08rem;
                color: #DEAD6F;
                font-weight: 700;
            }
            /* Button */
            .btn-main {
                background: #DEAD6F;
                color: #fff !important;
                border-radius: 24px;
                font-weight: 700;
                transition: background 0.2s, color 0.2s;
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
                background: #DEAD6F;
                color: #fff !important;
                border: none;
                border-radius: 20px;
                font-weight: 600;
                padding: 8px 20px;
                transition: background 0.2s, transform 0.2s;
                font-size: 16px;
            }
            .btn-action:hover, .btn-action:focus {
                background: #cfa856;
                color: #fff !important;
                transform: scale(1.05);
            }
            /* Nút View Detail */
            .btn-detail {
                background: #DEAD6F;
                color: #fff !important;
                border: none;
                border-radius: 18px;
                font-weight: 600;
                padding: 8px 20px;
                transition: background 0.2s, color 0.2s, transform 0.2s;
                box-shadow: 0 2px 8px rgba(222,173,111,0.07);
                display: inline-flex;
                align-items: center;
                gap: 6px;
                font-size: 15px;
            }
            .btn-detail:hover {
                background: #cfa856;
                color: #fff !important;
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
                color: #f9f0;
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
            .dashboard-card {
                min-height: 170px;
                height: 100%;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                width: 100%;
                background: #fff;
                border: 2px solid #DEAD6F;
                border-radius: 10px;
                box-shadow: 0 2px 8px rgba(222,173,111,0.08);
                margin-bottom: 16px;
                transition: box-shadow 0.2s, transform 0.2s;
            }
            .dashboard-card:hover {
                box-shadow: 0 6px 24px rgba(222,173,111,0.18);
                transform: scale(1.03);
            }
            .dashboard-card .card-title {
                font-size: 1.2rem;
                font-weight: 700;
                margin-bottom: 8px;
                color: #DEAD6F;
            }
            .dashboard-card .card-text {
                font-size: 2rem;
                font-weight: 700;
                color: #DEAD6F;
            }
            .dashboard-row-scroll {
                display: flex;
                flex-wrap: nowrap;
                overflow-x: auto;
                gap: 24px;
                padding-bottom: 8px;
                margin-left: -8px;
                margin-right: -8px;
            }
            .dashboard-row-scroll .dashboard-card {
                min-width: 240px;
                max-width: 260px;
                flex: 0 0 auto;
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
            </div>
        </div>
        <jsp:include page="Header.jsp" />

        <!-- DASHBOARD OVERVIEW (clickable cards) -->
        <div class="container my-4">
            <div class="dashboard-row-scroll mb-4">
                <!-- Nhóm 1: Quản lý vật tư/kho (mọi role đều thấy) -->
                <a href="dashboardmaterial" style="text-decoration:none;">
                    <div class="dashboard-card text-center">
                        <i class="fas fa-boxes fa-2x mb-2" style="color:#DEAD6F;"></i>
                        <h5 class="card-title">Total Materials</h5>
                        <p class="card-text">${materialCount}</p>
                    </div>
                </a>
                <a href="StaticInventory" style="text-decoration:none;">
                    <div class="dashboard-card text-center">
                        <i class="fas fa-warehouse fa-2x mb-2" style="color:#DEAD6F;"></i>
                        <h5 class="card-title">Total Inventory</h5>
                        <p class="card-text">${totalStock}</p>
                    </div>
                </a>
                <a href="StaticInventory?stockFilter=low" style="text-decoration:none;">
                    <div class="dashboard-card text-center">
                        <i class="fas fa-exclamation-triangle fa-2x mb-2" style="color:#DEAD6F;"></i>
                        <h5 class="card-title">Low Stock Materials</h5>
                        <p class="card-text">${lowStockCount}</p>
                    </div>
                </a>
                <a href="StaticInventory?stockFilter=zero" style="text-decoration:none;">
                    <div class="dashboard-card text-center">
                        <i class="fas fa-times-circle fa-2x mb-2" style="color:#DEAD6F;"></i>
                        <h5 class="card-title">Out of Stock</h5>
                        <p class="card-text">${outOfStockCount}</p>
                    </div>
                </a>

                <!-- Nhóm 2: Quản lý yêu cầu (Admin, Warehouse, Director) -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 2 || sessionScope.user.roleId == 3}">
                    <a href="ExportRequestList" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-file-signature fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">Pending Export Requests</h5>
                            <p class="card-text">${pendingExportRequestCount}</p>
                        </div>
                    </a>
                    <a href="ListPurchaseRequests" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-shopping-cart fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">Pending Purchase Requests</h5>
                            <p class="card-text">${pendingPurchaseRequestCount}</p>
                        </div>
                    </a>
                    <a href="repairrequestlist" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-tools fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">Pending Repair Requests</h5>
                            <p class="card-text">${pendingRepairRequestCount}</p>
                        </div>
                    </a>
                </c:if>

                <!-- Nhóm 2: My Requests (Employee) -->
                <c:if test="${sessionScope.user.roleId == 4}">
                    <a href="ListPurchaseRequests" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-user-check fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">My Requests</h5>
                            <p class="card-text fs-5 fw-bold">
                                Purchase: ${myPurchaseRequestCount}<br/>
                                Repair: ${myRepairRequestCount}
                            </p>
                        </div>
                    </a>
                </c:if>

                <!-- Nhóm 3: Quản trị hệ thống (Admin) -->
                <c:if test="${sessionScope.user.roleId == 1}">
                    <a href="UserList" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-users fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">Total Users</h5>
                            <p class="card-text">${totalUserCount}</p>
                        </div>
                    </a>
                    <a href="RolePermission" style="text-decoration:none;">
                        <div class="dashboard-card text-center">
                            <i class="fas fa-key fa-2x mb-2" style="color:#DEAD6F;"></i>
                            <h5 class="card-title">Total Permissions</h5>
                            <p class="card-text">${totalPermissionCount}</p>
                        </div>
                    </a>
                </c:if>
            </div>
        </div>

        <!-- NOTIFICATIONS (if any) -->
        <div class="container my-3">
            <c:if test="${not empty notifications}">
                <div class="alert alert-info" style="background:#fffbe9; border:1.5px solid #DEAD6F; color:#DEAD6F;">
                    <i class="fas fa-info-circle"></i>
                    <c:forEach var="noti" items="${notifications}">
                        <div>${noti.message}</div>
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <div class="container-fluid my-5">
            <div class="row">
                <div class="col-md-2 sidebar-col mt-5">
                    <div class="sidebar">
                        <div class="sidebar-header">Categories</div>
                        <ul class="list-unstyled">
                            <c:forEach var="c" items="${categories}">
                                <li>
                                    <a href="filter?categoryId=${c.category_id}" class="sidebar-item <c:if test='${param.categoryId == c.category_id}'>active</c:if>" data-category-id="${c.category_id}" >
                                        <h5 style="display:inline;">${c.category_name}</h5>
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
                                    <h2 class="display-6 fw-semibold"><i class="fas fa-warehouse me-2"></i>Material List</h2>
                                </div>
                                <div class="card-container">
                                    <c:forEach var="product" items="${productList}">
                                        <div class="product-card">
                                            <img src="images/material/${product.materialsUrl}" alt="${product.materialName}" width="200">
                                            <div class="card-content">
                                                <h5>${product.materialName}</h5>
                                                <div class="info-label">Code: <b>${product.materialCode}</b></div>
                                                <div class="info-label">
                                                    Status: <b class="status-${product.materialStatus}">${product.materialStatus}</b>
                                                </div>
                                                <div class="info-label">Unit: <b>${product.unit.unitName}</b></div>
                                                <div class="info-label">
                                                    Stock: 
                                                    <b class="${product.quantity == 0 ? 'stock-zero' : ''}">${product.quantity}</b>
                                                    <c:if test="${product.quantity == 0}">
                                                        <i class="fas fa-exclamation-circle" style="color:#e74c3c;" title="Out of stock"></i>
                                                    </c:if>
                                                    <c:if test="${product.quantity > 0 && product.quantity < 10}">
                                                        <span class="badge bg-warning text-dark" style="margin-left:6px;">
                                                            <i class="fas fa-exclamation-triangle"></i>
                                                        </span>
                                                    </c:if>
                                                </div>
                                                <a href="ProductDetail?id=${product.materialId}" class="btn-detail">
                                                    <i class="fas fa-eye me-1"></i> View Detail
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <!-- Pagination cố định ở cuối -->
                                <div class="d-flex justify-content-center mt-4 mb-5">
                                    <nav aria-label="Material pagination">
                                        <ul class="pagination">
                                            <c:forEach var="i" begin="1" end="${totalPages}">
                                                <li class="page-item <c:if test='${i == currentPage}'>active</c:if>">
                                                    <a class="page-link" href="home?page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </nav>
                                </div>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
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