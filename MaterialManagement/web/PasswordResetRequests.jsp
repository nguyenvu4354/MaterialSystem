<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
    <meta charset="UTF-8">
    <title>Password Reset Requests</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
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
        h2 {
            font-size: 1.75rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
            color: #5c4434;
        }
        .custom-table thead th {
            background-color: #f9f5f0;
            color: #5c4434;
            font-weight: 600;
        }
        .custom-table tbody tr:hover {
            background-color: #f1f1f1;
        }
        .custom-table th,
        .custom-table td {
            vertical-align: middle;
            min-height: 48px;
        }
        .status-badge {
            padding: 2px 10px;
            border-radius: 10px;
            font-size: 13px;
            font-weight: 500;
        }
        .status-pending {
            background-color: #6c757d;
            color: #fff;
        }
        .status-processing {
            background-color: #cce5ff;
            color: #004085;
        }
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
        }
        .btn-update {
            background-color: #DEAD6F;
            color: #fff;
            border: 1px solid #DEAD6F;
            padding: 6px 14px;
            border-radius: 8px;
            font-weight: 500;
            transition: background 0.2s, border 0.2s;
        }
        .btn-update:hover {
            background-color: #cfa856;
            color: #fff;
            border: 1px solid #cfa856;
        }
        .form-control-sm, .form-select-sm {
            min-height: 36px;
            font-size: 15px;
        }
        /* Style giống ExportRequestList */
        .filter-bar {
            gap: 12px;
            margin: 20px 0 24px 0;
        }
        .filter-bar .form-control,
        .filter-bar .form-select,
        .filter-bar .btn-filter {
            height: 48px;
            min-width: 120px;
            border-radius: 12px;
            font-size: 16px;
        }
        .filter-bar .filter-input {
            min-width: 220px;
            max-width: 260px;
            border-radius: 12px;
            font-size: 16px;
        }
        .filter-bar .filter-select {
            min-width: 140px;
            max-width: 180px;
            border-radius: 12px;
            font-size: 16px;
        }
        .btn-filter {
            background-color: #DEAD6F;
            color: #fff;
            border: none;
            padding: 10px 28px;
            border-radius: 12px;
            font-weight: 600;
            font-size: 17px;
            transition: background 0.2s;
            box-shadow: 0 2px 5px rgba(0,0,0,0.04);
        }
        .btn-filter:hover {
            background-color: #cfa856;
            color: #fff;
        }
        .custom-table thead th {
            background-color: #f9f5f0;
            color: #5c4434;
            font-weight: 700;
            font-size: 17px;
            border-bottom: 2px solid #e7d3b7;
        }
        .custom-table tbody tr:hover {
            background-color: #f1f1f1;
        }
        .custom-table th,
        .custom-table td {
            vertical-align: middle;
            min-height: 48px;
            font-size: 16px;
        }
        .status-badge {
            padding: 4px 16px;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            letter-spacing: 0.5px;
            display: inline-block;
        }
        .status-pending {
            background-color: #6c757d;
            color: #fff;
        }
        .status-processing {
            background-color: #cce5ff;
            color: #004085;
        }
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
        }
        .btn-action {
            background-color: #fff7e6;
            color: #b8860b;
            border: 1px solid #ffe58f;
            border-radius: 10px;
            padding: 8px 18px;
            font-weight: 600;
            font-size: 16px;
            transition: background 0.2s, color 0.2s, border 0.2s;
            box-shadow: 0 2px 5px rgba(0,0,0,0.04);
        }
        .btn-action:hover {
            background-color: #DEAD6F;
            color: #fff;
            border: 1px solid #DEAD6F;
        }
        .pagination .page-item.active .page-link {
            background-color: #DEAD6F;
            border-color: #DEAD6F;
            color: #fff;
        }
    </style>
    </head>
    <body>
<jsp:include page="Header.jsp" />
<div class="container-fluid">
    <div class="row">
        <jsp:include page="SidebarDirector.jsp"/>
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="container-main">
                <c:if test="${sessionScope.user.roleId != 1}">
                    <div class="alert alert-danger mt-4">You do not have permission to view password reset requests.</div>
                </c:if>
                <c:if test="${sessionScope.user.roleId == 1}">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h2 class="fw-bold display-6 border-bottom pb-2 m-0" style="color: #DEAD6F;"><i class="fas fa-key"></i> Password Reset Requests</h2>
                    </div>
                    <div class="filter-bar d-flex flex-wrap gap-3 align-items-center mb-4">
                        <form method="get" action="PasswordResetRequests" class="d-flex flex-wrap gap-2 align-items-center w-100">
                            <input type="text" name="searchEmail" class="form-control filter-input" placeholder="Search by email" value="${param.searchEmail != null ? param.searchEmail : ''}" />
                            <select name="status" class="form-select filter-select">
                                <option value="all" ${status == 'all' ? 'selected' : ''}>All Statuses</option>
                                <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                                <option value="completed" ${status == 'completed' ? 'selected' : ''}>Completed</option>
                                <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                            </select>
                            <button type="submit" class="btn btn-filter"><i class="fas fa-search"></i> Search</button>
                        </form>
                    </div>
                    <c:if test="${not empty requests}">
                        <div class="table-responsive">
                            <table class="table custom-table table-bordered table-hover">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>User Email</th>
                                        <th>Request Date</th>
                                        <th>Status</th>
                                        <th>New Password</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${requests}">
                                        <tr>
                                            <td>${req.requestId}</td>
                                            <td>${req.userEmail}</td>
                                            <td>${req.requestDate}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${req.status == 'pending'}">
                                                        <span class="status-badge status-pending">Pending</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'processing'}">
                                                        <span class="status-badge status-processing">Processing</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'completed'}">
                                                        <span class="status-badge status-completed">Completed</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'rejected'}">
                                                        <span class="status-badge status-rejected">Rejected</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${req.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <input type="text" name="newPassword" class="form-control form-control-sm w-auto" placeholder="New Password" value="${req.newPassword}" style="min-width:120px;" readonly />
                                            </td>
                                            <td>
                                                <form method="post" action="PasswordResetRequests" class="d-flex gap-2 align-items-center mb-0">
                                                    <input type="hidden" name="requestId" value="${req.requestId}" />
                                                    <input type="hidden" name="newPassword" value="${req.newPassword}" />
                                                    <select name="status" class="form-select form-select-sm w-auto d-inline-block" style="min-width:120px;">
                                                        <option value="completed" ${req.status == 'completed' ? 'selected' : ''}>Completed</option>
                                                        <option value="rejected" ${req.status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-action"><i class="fas fa-save"></i> Update</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <!-- Pagination -->
                        <nav class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="PasswordResetRequests?page=${currentPage - 1}&searchEmail=${param.searchEmail}&status=${status}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="PasswordResetRequests?page=${i}&searchEmail=${param.searchEmail}&status=${status}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="PasswordResetRequests?page=${currentPage + 1}&searchEmail=${param.searchEmail}&status=${status}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                    <c:if test="${empty requests}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Password Reset Requests Found</h4>
                            <p class="text-muted">No password reset requests at this time.</p>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
