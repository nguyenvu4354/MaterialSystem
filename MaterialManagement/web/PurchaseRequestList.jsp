<%-- 
    Document   : PurchaseRequestList
    Created on : Jun 10, 2025, 7:21:05 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Waggy - Purchase Request List</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">

        <style>
            .purchase-list .table th {
                font-weight: 500;
                color: #6c757d;
            }
            .purchase-list .table td {
                vertical-align: middle;
            }
            .status-badge {
                padding: 0.5rem 1rem;
                border-radius: 50px;
                font-size: 0.875rem;
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
            .status-cancel {
                background-color: #fff3cd;
                color: #856404;
            }
            .search-box {
                max-width: 300px;
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
        </style>
    </head>
    <body>
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <!-- User not logged in -->
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
            <c:when test="${sessionScope.user.roleId != 2}">
                <!-- User doesn't have permission -->
                <div class="access-denied">
                    <div class="access-denied-card">
                        <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">⚠️</div>
                        <h2 class="text-danger mb-3">Access Denied</h2>
                        <p class="text-muted mb-4">Only directors can view the purchase request list.</p>
                        <div class="d-grid gap-2">
                            <a href="HomeServlet" class="btn btn-primary">Back to Home</a>
                            <a href="javascript:history.back()" class="btn btn-outline-secondary">Go Back</a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <!-- User has permission - show list -->
                <jsp:include page="HeaderAdmin.jsp"/>

                <section id="purchase-list" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                    <div class="container">
                        <div class="row my-5 py-5">
                            <div class="col-12 bg-white p-4 rounded shadow purchase-list">
                                <div class="text-center mb-4">
                                    <h2 class="display-4 fw-normal">Purchase <span class="text-primary">Request List</span></h2>
                                </div>

                                <!-- Success and Error Messages -->
                                <c:if test="${not empty param.success}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <c:choose>
                                            <c:when test="${param.success == 'list_email_sent_selected'}">
                                                Selected requests have been successfully sent to the warehouse.
                                            </c:when>
                                            <c:when test="${param.success == 'no_valid_requests_found'}">
                                                No valid requests were found to send.
                                            </c:when>
                                        </c:choose>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>
                                <c:if test="${not empty param.error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <c:choose>
                                            <c:when test="${param.error == 'no_selection'}">
                                                <strong>Error:</strong> Please select at least one request to send.
                                            </c:when>
                                            <c:when test="${param.error == 'no_warehouse_staff'}">
                                                <strong>Error:</strong> No warehouse staff found to receive the notification.
                                            </c:when>
                                            <c:when test="${param.error == 'email_failed'}">
                                                <strong>Error:</strong> Failed to send the email notification. Please try again later.
                                            </c:when>
                                            <c:when test="${param.error == 'permission_denied'}">
                                                <strong>Error:</strong> You do not have permission to perform this action.
                                            </c:when>
                                            <c:otherwise>
                                                <strong>Error:</strong> An unexpected error occurred.
                                            </c:otherwise>
                                        </c:choose>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>

                                <form action="ListPurchaseRequestsServlet" method="get" class="mb-4">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="search-box">
                                                <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Search...">
                                            </div>
                                        </div>
                                        <div class="col-md-6 text-end">
                                            <select class="form-select d-inline-block w-auto" name="status">
                                                <option value="">All Status</option>
                                                <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                                <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                                <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Cancelled</option>
                                            </select>
                                            <button type="submit" class="btn btn-primary ms-2">Filter</button>
                                            <!-- Button to trigger modal -->
                                            <button type="button" class="btn btn-secondary ms-2" data-bs-toggle="modal" data-bs-target="#sendListModal">
                                                <i class="fas fa-paper-plane"></i> Send List
                                            </button>
                                        </div>
                                    </div>
                                </form>

                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th><a href="ListPurchaseRequestsServlet?sort=id_${sortOption == 'id_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">ID</a></th>
                                                <th><a href="ListPurchaseRequestsServlet?sort=code_${sortOption == 'code_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Request Code</a></th>
                                                <th>Requester</th>
                                                <th><a href="ListPurchaseRequestsServlet?sort=date_${sortOption == 'date_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Request Date</a></th>
                                                <th>Estimated Price</th>
                                                <th>Reason</th>
                                                <th><a href="ListPurchaseRequestsServlet?sort=status_${sortOption == 'status_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Status</a></th>
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
                                                        <span class="status-badge status-${request.status}">
                                                            ${request.status == 'PENDING' ? 'Pending' : 
                                                              request.status == 'APPROVED' ? 'Approved' : 
                                                              request.status == 'REJECTED' ? 'Rejected' : 
                                                              request.status == 'CANCELLED' ? 'Cancelled' : request.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="action-buttons d-flex">
                                                            <a href="PurchaseRequestDetailServlet?id=${request.purchaseRequestId}" class="btn btn-info btn-sm me-1" title="View Details">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${request.status == 'PENDING'}">
                                                                <button onclick="deleteRequest(${request.purchaseRequestId})" class="btn btn-danger btn-sm me-1" title="Delete Request">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <nav aria-label="Page navigation" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${pageIndex == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="ListPurchaseRequestsServlet?page=${pageIndex - 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${pageIndex == i ? 'active' : ''}">
                                                <a class="page-link" href="ListPurchaseRequestsServlet?page=${i}&status=${status}&keyword=${keyword}&sort=${sortOption}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${pageIndex == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="ListPurchaseRequestsServlet?page=${pageIndex + 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Modal -->
                <div class="modal fade" id="sendListModal" tabindex="-1" aria-labelledby="sendListModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg modal-dialog-scrollable">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="sendListModalLabel">Select Approved Requests to Send</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form id="sendSelectedForm" action="SendPurchaseListServlet" method="post">
                                    <table class="table table-sm table-hover">
                                        <thead>
                                            <tr>
                                                <th style="width: 5%;"><input class="form-check-input" type="checkbox" id="selectAllCheckbox" title="Select All"></th>
                                                <th style="width: 10%;">ID</th>
                                                <th style="width: 25%;">Request Code</th>
                                                <th>Reason</th>
                                                <th style="width: 15%;">Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${purchaseRequests}" var="request">
                                                <c:if test="${request.status == 'approved'}">
                                                    <tr>
                                                        <td><input class="form-check-input request-checkbox" type="checkbox" name="selectedIds" value="${request.purchaseRequestId}"></td>
                                                        <td>${request.purchaseRequestId}</td>
                                                        <td>${request.requestCode}</td>
                                                        <td>${request.reason}</td>
                                                        <td><span class="status-badge status-${request.status}">${request.status}</span></td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary" id="sendSelectedButton">Send Selected</button>
                            </div>
                        </div>
                    </div>
                </div>

            </c:otherwise>
        </c:choose>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const sendListModal = document.getElementById('sendListModal');
                if (sendListModal) {
                    const selectAllCheckbox = sendListModal.querySelector('#selectAllCheckbox');
                    const requestCheckboxes = sendListModal.querySelectorAll('.request-checkbox');
                    const sendSelectedButton = sendListModal.querySelector('#sendSelectedButton');
                    const sendSelectedForm = sendListModal.querySelector('#sendSelectedForm');

                    if (selectAllCheckbox) {
                        selectAllCheckbox.addEventListener('change', function () {
                            requestCheckboxes.forEach(checkbox => {
                                checkbox.checked = selectAllCheckbox.checked;
                            });
                        });
                    }

                    if (sendSelectedButton) {
                        sendSelectedButton.addEventListener('click', function () {
                            if (sendListModal.querySelectorAll('.request-checkbox:checked').length > 0) {
                                if (confirm('Are you sure you want to send the selected requests to the warehouse?')) {
                                    sendSelectedForm.submit();
                                }
                            } else {
                                alert('Please select at least one request to send.');
                            }
                        });
                    }
                }
            });

            // Auto redirect to login if not logged in
            <c:if test="${empty sessionScope.user}">
                window.location.href = 'Login.jsp';
            </c:if>
            
            // Delete request function
            function deleteRequest(id) {
                if (confirm('Are you sure you want to delete this request?')) {
                    window.location.href = '${pageContext.request.contextPath}/DeletePurchaseRequest?id=' + id;
                }
            }
        </script>
    </body>
</html>
