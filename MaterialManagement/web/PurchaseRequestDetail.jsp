<%-- 
    Document   : PurchaseRequestDetail
    Created on : Jun 10, 2025, 7:21:25 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Waggy - Chi tiết yêu cầu mua hàng</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Montserrat', Arial, sans-serif;
            }
            
            .main-content {
                padding: 2rem 0;
                min-height: calc(100vh - 80px);
            }
            
            .page-header {
                background: #fff;
                color: #212529;
                padding: 2rem 0 1.5rem 0;
                margin-bottom: 2rem;
                border-radius: 12px 12px 0 0;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            
            .page-title {
                font-size: 2.5rem;
                font-weight: 400;
                margin-bottom: 0.5rem;
                color: #212529;
                letter-spacing: -1px;
            }
            
            .page-title .text-primary {
                color: #e6b800;
                font-weight: 500;
            }
            
            .page-subtitle {
                font-size: 1.1rem;
                color: #6c757d;
                font-weight: 400;
            }
            
            .content-card {
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
                padding: 2rem;
                margin-bottom: 2rem;
                border: none;
            }
            
            .table-container {
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
                overflow: hidden;
                margin-bottom: 2rem;
            }
            
            .table {
                margin-bottom: 0;
                border-collapse: separate;
                border-spacing: 0;
                background: #fff;
            }
            
            .table thead th {
                background: #f8f9fa;
                color: #6c757d;
                font-weight: 500;
                padding: 1rem 0.75rem;
                border: none;
                font-size: 1rem;
                text-transform: none;
                letter-spacing: 0;
            }
            
            .table thead th:first-child {
                border-top-left-radius: 12px;
            }
            
            .table thead th:last-child {
                border-top-right-radius: 12px;
            }
            
            .table tbody tr {
                transition: all 0.2s;
            }
            
            .table tbody tr:hover {
                background-color: #f6f6f6;
            }
            
            .table tbody td {
                padding: 1rem 0.75rem;
                border-bottom: 1px solid #ececec;
                vertical-align: middle;
                font-size: 1rem;
                color: #212529;
            }
            
            .table tbody tr:last-child td {
                border-bottom: none;
            }
            
            .badge {
                padding: 0.45em 1em;
                border-radius: 50px;
                font-size: 0.95em;
                font-weight: 500;
                text-transform: none;
                letter-spacing: 0;
                background: #ececec;
                color: #495057;
                display: inline-block;
            }
            
            .badge-primary {
                background: #ececec;
                color: #495057;
            }
            
            .badge-success {
                background: #d4edda;
                color: #155724;
            }
            
            .badge-warning {
                background: #fff3cd;
                color: #856404;
            }
            
            .badge-info {
                background: #e3f2fd;
                color: #1976d2;
            }
            
            .btn-back {
                background: #212529;
                border: none;
                color: #fff;
                padding: 0.75rem 2rem;
                border-radius: 8px;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                transition: all 0.2s;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            
            .btn-back:hover {
                background: #e6b800;
                color: #212529;
                text-decoration: none;
            }
            
            .empty-state {
                text-align: center;
                padding: 3rem 2rem;
                color: #bfa000;
            }
            
            .empty-state i {
                font-size: 4rem;
                margin-bottom: 1rem;
                opacity: 0.5;
            }
            
            .empty-state h4 {
                margin-bottom: 0.5rem;
                color: #495057;
            }
            
            .stats-card {
                background: #fffbe6;
                color: #bfa000;
                border-radius: 12px;
                padding: 1.5rem;
                margin-bottom: 2rem;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            
            .stats-number {
                font-size: 2.2rem;
                font-weight: 600;
                margin-bottom: 0.5rem;
                color: #e6b800;
            }
            
            .stats-label {
                font-size: 0.95rem;
                opacity: 0.9;
                text-transform: none;
                letter-spacing: 0;
                color: #bfa000;
            }
            
            .action-buttons {
                margin: 2rem 0;
                text-align: center;
            }
            
            .btn-action {
                padding: 12px 30px;
                font-size: 1rem;
                font-weight: 500;
                border-radius: 8px;
                margin: 0 10px;
                transition: all 0.3s ease;
                border: none;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            
            .btn-approve {
                background-color: #28a745;
                color: white;
            }
            
            .btn-approve:hover {
                background-color: #218838;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(40, 167, 69, 0.2);
            }
            
            .btn-reject {
                background-color: #dc3545;
                color: white;
            }
            
            .btn-reject:hover {
                background-color: #c82333;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(220, 53, 69, 0.2);
            }
            
            .status-message {
                padding: 15px 25px;
                border-radius: 8px;
                margin: 2rem 0;
                text-align: center;
                font-weight: 500;
            }
            
            .status-approved {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            
            .status-rejected {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            
            @media (max-width: 768px) {
                .page-title {
                    font-size: 2rem;
                }
                
                .table-responsive {
                    border-radius: 12px;
                }
                
                .table thead th {
                    font-size: 0.9rem;
                    padding: 0.8rem 0.4rem;
                }
                
                .table tbody td {
                    font-size: 0.9rem;
                    padding: 0.8rem 0.4rem;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="HeaderAdmin.jsp"/>

        <div class="main-content">
            <div class="container">
                <!-- Page Header -->
                <div class="page-header text-center">
                    <h1 class="page-title">
                        <i class="fas fa-clipboard-list me-3"></i>
                        Chi tiết yêu cầu mua hàng
                    </h1>
                    <p class="page-subtitle">Xem thông tin chi tiết về yêu cầu mua hàng</p>
                </div>

                <!-- Stats Card -->
                <div class="row">
                    <div class="col-md-4">
                        <div class="stats-card text-center">
                            <div class="stats-number">${purchaseRequestDetailList.size()}</div>
                            <div class="stats-label">Tổng số vật tư</div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stats-card text-center">
                            <div class="stats-number">
                                <c:set var="totalQuantity" value="0"/>
                                <c:forEach var="item" items="${purchaseRequestDetailList}">
                                    <c:set var="totalQuantity" value="${totalQuantity + item.quantity}"/>
                                </c:forEach>
                                ${totalQuantity}
                            </div>
                            <div class="stats-label">Tổng số lượng</div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stats-card text-center">
                            <div class="stats-number">
                                <c:set var="uniqueCategories" value="0"/>
                                <c:forEach var="item" items="${purchaseRequestDetailList}">
                                    <c:set var="uniqueCategories" value="${uniqueCategories + 1}"/>
                                </c:forEach>
                                ${uniqueCategories}
                            </div>
                            <div class="stats-label">Loại vật tư</div>
                        </div>
                    </div>
                </div>

                <!-- Table Container -->
                <div class="table-container">
                    <c:choose>
                        <c:when test="${not empty purchaseRequestDetailList}">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th><i class="fas fa-hashtag me-2"></i>ID</th>
                                            <th><i class="fas fa-file-invoice me-2"></i>Mã yêu cầu</th>
                                            <th><i class="fas fa-box me-2"></i>Tên vật tư</th>
                                            <th><i class="fas fa-tags me-2"></i>Danh mục</th>
                                            <th><i class="fas fa-sort-numeric-up me-2"></i>Số lượng</th>
                                            <th><i class="fas fa-sticky-note me-2"></i>Ghi chú</th>
                                            <th><i class="fas fa-calendar-plus me-2"></i>Ngày tạo</th>
                                            <th><i class="fas fa-calendar-check me-2"></i>Ngày cập nhật</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${purchaseRequestDetailList}" varStatus="status">
                                        <tr>
                                            <td>
                                                <span class="badge badge-primary">#${item.purchaseRequestDetailId}</span>
                                            </td>
                                            <td>
                                                <span class="badge badge-info">${item.purchaseRequestId}</span>
                                            </td>
                                            <td>
                                                <strong>${item.materialName}</strong>
                                            </td>
                                            <td>
                                                <span class="badge badge-warning">${item.categoryId}</span>
                                            </td>
                                            <td>
                                                <span class="badge badge-success">${item.quantity}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty item.notes}">
                                                        <span class="text-muted">${item.notes}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted fst-italic">Không có ghi chú</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <i class="fas fa-clock me-1"></i>
                                                    ${item.createdAt}
                                                </small>
                                            </td>
                                            <td>
                                                <small class="text-muted">
                                                    <i class="fas fa-edit me-1"></i>
                                                    ${item.updatedAt}
                                                </small>
                                            </td>
                                        </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-inbox"></i>
                                <h4>Không có dữ liệu</h4>
                                <p>Chưa có chi tiết yêu cầu mua hàng nào được tìm thấy.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Action Buttons and Status Messages -->
                <div class="action-buttons">
                    <c:choose>
                        <c:when test="${purchaseRequest.status eq 'pending'}">
                            <form action="PurchaseRequestDetailServlet" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="${purchaseRequest.purchaseRequestId}">
                                <input type="hidden" name="action" value="approve">
                                <button type="submit" class="btn-action btn-approve" onclick="return confirm('Bạn có chắc chắn muốn phê duyệt yêu cầu mua hàng này?')">
                                    <i class="fas fa-check-circle me-2"></i>
                                    Phê duyệt
                                </button>
                            </form>
                            
                            <form action="PurchaseRequestDetailServlet" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="${purchaseRequest.purchaseRequestId}">
                                <input type="hidden" name="action" value="reject">
                                <button type="submit" class="btn-action btn-reject" onclick="return confirm('Bạn có chắc chắn muốn từ chối yêu cầu mua hàng này?')">
                                    <i class="fas fa-times-circle me-2"></i>
                                    Từ chối
                                </button>
                            </form>
                        </c:when>
                        <c:when test="${purchaseRequest.status eq 'approved'}">
                            <div class="status-message status-approved">
                                <i class="fas fa-check-circle me-2"></i>
                                Yêu cầu này đã được phê duyệt
                            </div>
                        </c:when>
                        <c:when test="${purchaseRequest.status eq 'rejected'}">
                            <div class="status-message status-rejected">
                                <i class="fas fa-times-circle me-2"></i>
                                Yêu cầu này đã bị từ chối
                            </div>
                        </c:when>
                    </c:choose>
                </div>

                <!-- Back Button -->
                <div class="text-center">
                    <a href="ListPurchaseRequestsServlet" class="btn-back">
                        <i class="fas fa-arrow-left"></i>
                        Quay lại danh sách
                    </a>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
