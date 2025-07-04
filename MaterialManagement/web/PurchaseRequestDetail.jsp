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
        <title>Waggy - Purchase Request Details</title>
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
            
            .access-denied {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: url('images/background-img.png') no-repeat;
                background-size: cover;
            }
            .access-denied-card {
                background: white;
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                padding: 3rem;
                text-align: center;
                max-width: 500px;
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
        <c:set var="roleId" value="${sessionScope.user.roleId}" />
        <c:set var="hasViewPurchaseRequestListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_PURCHASE_REQUEST_LIST')}" scope="request" />

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <div class="access-denied">
                    <div class="access-denied-card">
                        <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                        <h2 class="text-danger mb-3">Login Required</h2>
                        <p class="text-muted mb-4">You need to login to access this page.</p>
                        <div class="d-grid gap-2">
                            <a href="Login.jsp" class="btn btn-primary">Login</a>
                            <a href="HomeServlet" class="btn btn-outline-secondary">Back to Home</a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${!hasViewPurchaseRequestListPermission}">
                    <div class="access-denied">
                        <div class="access-denied-card">
                            <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                            <h2 class="text-danger mb-3">Access Denied</h2>
                            <p class="text-muted mb-4">You do not have permission to view purchase request details.</p>
                            <div class="d-grid gap-2">
                                <a href="ListPurchaseRequests" class="btn btn-primary">Back to Purchase Requests</a>
                                <a href="dashboardmaterial" class="btn btn-outline-secondary">Back to Dashboard</a>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${hasViewPurchaseRequestListPermission}">
                    <jsp:include page="Header.jsp"/>
                    <div class="container-fluid">
                        <div class="row">
                            <jsp:include page="SidebarDirector.jsp"/>
                            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                                <div class="main-content">
                                    <div class="container">
                                        <!-- Page Header -->
                                        <div class="page-header text-center">
                                            <h1 class="page-title">
                                                <i class="fas fa-clipboard-list me-3"></i>
                                                Purchase Request Details
                                            </h1>
                                            <p class="page-subtitle">View detailed information about the purchase request</p>
                                        </div>

                                        <!-- Stats Card -->
                                        <div class="row">
                                            <div class="col-md-4">
                                                <div class="stats-card text-center">
                                                    <div class="stats-number">${purchaseRequestDetailList.size()}</div>
                                                    <div class="stats-label">Total Materials</div>
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
                                                    <div class="stats-label">Total Quantity</div>
                                                </div>
                                            </div>

                                        </div>

                                        <!-- Thông tin người yêu cầu -->
                                        <div class="content-card mb-3">
                                            <strong>Người yêu cầu:</strong>
                                            <ul style="margin-bottom:0;">
                                                <li><b>Họ tên:</b> ${requester.fullName}</li>
                                                <li><b>Email:</b> ${requester.email}</li>
                                                <li><b>Số điện thoại:</b> ${requester.phoneNumber}</li>
                                                <li><b>Phòng ban:</b> ${requester.departmentName}</li>
                                                <li><b>Chức vụ:</b> ${requester.roleName}</li>
                                                <li><b>Ngày sinh:</b> <c:if test="${not empty requester.dateOfBirth}">${requester.dateOfBirth}</c:if></li>
                                            </ul>
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
                                                                    <th><i class="fas fa-file-invoice me-2"></i>Request Code</th>
                                                                    <th><i class="fas fa-box me-2"></i>Material Name</th>
                                                                    <th><i class="fas fa-sort-numeric-up me-2"></i>Quantity</th>
                                                                    <th><i class="fas fa-sticky-note me-2"></i>Notes</th>
                                                                    <th><i class="fas fa-calendar-plus me-2"></i>Created Date</th>
                                                                    <th><i class="fas fa-calendar-check me-2"></i>Updated Date</th>
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
                                                                        <span class="badge badge-success">${item.quantity}</span>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when test="${not empty item.notes}">
                                                                                <span class="text-muted">${item.notes}</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="text-muted fst-italic">No notes</span>
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
                                                    
                                                    <!-- Pagination -->
                                                    <c:if test="${showPagination}">
                                                        <nav aria-label="Page navigation" class="mt-4">
                                                            <ul class="pagination justify-content-center">
                                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                                    <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${currentPage - 1}">
                                                                        <i class="fas fa-chevron-left"></i> Previous
                                                                    </a>
                                                                </li>
                                                                
                                                                <c:forEach begin="1" end="${totalPages}" var="i">
                                                                    <c:choose>
                                                                        <c:when test="${i == currentPage}">
                                                                            <li class="page-item active">
                                                                                <span class="page-link">${i}</span>
                                                                            </li>
                                                                        </c:when>
                                                                        <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                                                            <li class="page-item">
                                                                                <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${i}">${i}</a>
                                                                            </li>
                                                                        </c:when>
                                                                        <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                                                            <li class="page-item disabled">
                                                                                <span class="page-link">...</span>
                                                                            </li>
                                                                        </c:when>
                                                                    </c:choose>
                                                                </c:forEach>
                                                                
                                                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                                    <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${currentPage + 1}">
                                                                        Next <i class="fas fa-chevron-right"></i>
                                                                    </a>
                                                                </li>
                                                            </ul>
                                                        </nav>
                                                        
                                                        <!-- Page Info -->
                                                        <div class="text-center text-muted mt-2">
                                                            <small>
                                                                Showing ${(currentPage - 1) * 10 + 1} - ${Math.min(currentPage * 10, totalItems)} 
                                                                of ${totalItems} details
                                                            </small>
                                                        </div>
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="empty-state">
                                                        <i class="fas fa-inbox"></i>
                                                        <h4>No Data</h4>
                                                        <p>No purchase request details found.</p>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <!-- Action Buttons and Status Messages -->
                                        <div class="action-buttons">
                                            <!-- Hiển thị lý do nhân viên gửi lên -->
                                            <c:if test="${not empty purchaseRequest.reason}">
                                                <div class="content-card mb-3">
                                                    <strong>Employee's request reason:</strong>
                                                    <div class="text-muted">${purchaseRequest.reason}</div>
                                                </div>
                                            </c:if>
                                            <c:if test="${hasHandleRequestPermission && purchaseRequest.status == 'pending'}">
                                                <div class="content-card">
                                                    <h4 class="mb-3">Approval Action</h4>
                                                    <form id="actionForm" action="PurchaseRequestDetail" method="POST" style="display:inline-block;">
                                                        <input type="hidden" name="id" value="${purchaseRequest.purchaseRequestId}">
                                                        <input type="hidden" id="actionType" name="action" value="">
                                                        <div class="mb-2">
                                                            <textarea name="reason" id="reasonInput" class="form-control" placeholder="Enter reason..." required></textarea>
                                                        </div>
                                                        <button type="submit" class="btn-action btn-approve" onclick="return setActionType('approve')">
                                                            <i class="fas fa-check-circle me-2"></i>
                                                            Approve
                                                        </button>
                                                        <button type="submit" class="btn-action btn-reject" onclick="return setActionType('reject')">
                                                            <i class="fas fa-times-circle me-2"></i>
                                                            Reject
                                                        </button>
                                                    </form>
                                                    <script>
                                                        function setActionType(type) {
                                                            document.getElementById('actionType').value = type;
                                                            if(type === 'approve') {
                                                                return confirm('Are you sure you want to approve this request?');
                                                            } else {
                                                                return confirm('Are you sure you want to reject this request?');
                                                            }
                                                        }
                                                    </script>
                                                </div>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${purchaseRequest.status eq 'approved'}">
                                                    <div class="status-message status-approved">
                                                        <i class="fas fa-check-circle me-2"></i>
                                                        Request approved.<br>
                                                        <strong>Approval reason:</strong> ${purchaseRequest.approvalReason}
                                                    </div>
                                                </c:when>
                                                <c:when test="${purchaseRequest.status eq 'rejected'}">
                                                    <div class="status-message status-rejected">
                                                        <i class="fas fa-times-circle me-2"></i>
                                                        Request rejected.<br>
                                                        <strong>Rejection reason:</strong> ${purchaseRequest.approvalReason}
                                                    </div>
                                                </c:when>
                                            </c:choose>
                                        </div>

                                        <!-- Back Button -->
                                        <div class="text-center">
                                            <a href="ListPurchaseRequests" class="btn-back">
                                                <i class="fas fa-arrow-left"></i>
                                                Back to List
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </main>
                        </div>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Auto redirect to login if not logged in
            <c:if test="${empty sessionScope.user}">
                window.location.href = 'Login.jsp';
            </c:if>
        </script>
    </body>
</html>