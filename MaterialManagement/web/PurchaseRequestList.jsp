<%-- 
    Document   : PurchaseRequestList
    Created on : Jun 10, 2025, 7:21:05 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Purchase Request List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            body {
                font-family: 'Segoe UI', Arial, sans-serif;
                background-color: #faf4ee;
            }
            .container-main {
                max-width: 1200px;
                margin: 30px auto;
                background: #fff;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }
            h1 {
                font-size: 1.75rem;
                font-weight: bold;
                margin-bottom: 0.5rem;
                color: #5c4434;
            }
            .filter-bar {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                margin: 20px 0;
            }
            .btn-export, .btn-print {
                padding: 6px 14px;
                border-radius: 6px;
                font-weight: 500;
            }
            .btn-export {
                background-color: #009966;
                color: white;
            }
            .btn-print {
                background-color: #6c757d;
                color: white;
            }
            .custom-table thead th {
                background-color: #f9f5f0;
                color: #5c4434;
                font-weight: 600;
            }
            .custom-table tbody tr:hover {
                background-color: #f1f1f1;
            }
            .status-badge {
                padding: 2px 10px;
                border-radius: 10px;
                font-size: 13px;
                font-weight: 500;
            }
            .status-approved {
                background-color: #d4edda;
                color: #155724;
            }
            .status-rejected {
                background-color: #f8d7da;
                color: #721c24;
            }
            .status-pending {
                background-color: #fff3cd;
                color: #856404;
            }
            .btn-detail {
                background-color: #fff7e6;
                color: #b8860b;
                border: 1px solid #ffe58f;
                border-radius: 6px;
                padding: 6px 14px;
                font-weight: 500;
            }
            .pagination .page-item.active .page-link {
                background-color: #009966;
                border-color: #009966;
            }
        </style>
    </head>
    <body>
        <jsp:include page="HeaderAdmin.jsp"/>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="SidebarDirector.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="container-main">
                        <h1>Purchase Request List</h1>
                        <p class="text-muted">Danh sách và quản lý các yêu cầu mua vật tư trong hệ thống</p>
                        <form class="filter-bar align-items-center" method="GET" action="ListPurchaseRequests" style="gap: 8px; flex-wrap:nowrap;">
                            <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Search..." style="max-width:260px; min-width:200px;">
                            <select class="form-select" name="status" style="max-width:150px;">
                                <option value="">All Status</option>
                                <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                            </select>
                            <button type="submit" class="btn btn-primary">Filter</button>
                            <a href="CreatePurchaseRequest" class="btn-export">Add Purchase Request</a>
                        </form>
                        <c:if test="${not empty purchaseRequests}">
                            <div class="table-responsive" id="printTableListArea">
                                <table class="table custom-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Request Code</th>
                                            <th>Requester</th>
                                            <th>Request Date</th>
                                            <th>Estimated Price</th>
                                            <th>Reason</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${purchaseRequests}" var="request">
                                            <tr>
                                                <td>${request.purchaseRequestId}</td>
                                                <td>${request.requestCode}</td>
                                                <td>${request.userId}</td>
                                                <td>${request.requestDate}</td>
                                                <td>${request.estimatedPrice}</td>
                                                <td>${request.reason}</td>
                                                <td>
                                                    <span class="status-badge status-${request.status.toLowerCase()}">
                                                        ${request.status == 'PENDING' ? 'Pending' : 
                                                          request.status == 'APPROVED' ? 'Approved' : 
                                                          request.status == 'REJECTED' ? 'Rejected' : 
                                                          request.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="PurchaseRequestDetail?id=${request.purchaseRequestId}" class="btn-detail">
                                                        <i class="fas fa-eye"></i> Detail
                                                    </a>
                                                    <c:if test="${request.status == 'PENDING' && request.userId == sessionScope.user.userId}">
                                                        <button onclick="deleteRequest(${request.purchaseRequestId})" class="btn btn-danger btn-sm">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <nav class="mt-3">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="ListPurchaseRequests?page=${currentPage - 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Previous</a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="ListPurchaseRequests?page=${i}&status=${status}&keyword=${keyword}&sort=${sortOption}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="ListPurchaseRequests?page=${currentPage + 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Next</a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                        <c:if test="${empty purchaseRequests}">
                            <div class="alert alert-info mt-4">
                                Không tìm thấy yêu cầu mua vật tư nào.
                            </div>
                        </c:if>
                    </div>
                </main>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function deleteRequest(id) {
                if (confirm('Are you sure you want to delete this request?')) {
                    window.location.href = 'DeletePurchaseRequest?id=' + id;
                }
            }
        </script>
    </body>
</html>
