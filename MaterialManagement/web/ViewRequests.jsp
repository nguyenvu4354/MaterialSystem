<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - View My Requests</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <style>
            .request-section .form-control, .request-section .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .request-section .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .request-section .btn {
                font-size: 1rem;
                padding: 0.75rem;
            }
            .request-section .nav-tabs {
                border-bottom: 1px solid #dee2e6;
                margin-bottom: 1.5rem;
            }
            .request-section .nav-tabs .nav-link {
                color: #495057;
                border: none;
                border-bottom: 2px solid transparent;
                padding: 0.75rem 1.5rem;
                font-weight: 500;
            }
            .request-section .nav-tabs .nav-link.active {
                color: #0d6efd;
                border-bottom: 2px solid #0d6efd;
                background-color: transparent;
            }
            .request-section .table {
                border-collapse: collapse;
                width: 100%;
            }
            .request-section th, .request-section td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #dee2e6;
            }
            .request-section th {
                background-color: #f8f9fa;
            }
            .request-section .pagination .page-link {
                color: #0d6efd;
                border-radius: 0.25rem;
                margin: 0 3px;
                padding: 0.5rem 0.75rem;
            }
            .request-section .pagination .page-item.active .page-link {
                background-color: #0d6efd;
                border-color: #0d6efd;
                color: white;
            }
            .request-section .pagination .page-item.disabled .page-link {
                color: #6c757d;
                pointer-events: none;
                background-color: #f8f9fa;
            }
            .request-section .nav-buttons .btn {
                margin: 0 0.5rem;
                border-radius: 0.25rem;
                padding: 0.75rem 1.5rem;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="view-requests" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow request-section">
                        <!-- Navigation buttons -->
                        <div class="text-center mb-4 nav-buttons">
                            <a href="profile" class="btn btn-primary">Profile</a>
                            <a href="change-password.jsp" class="btn btn-secondary">Change Password</a>
                            <a href="ViewRequests" class="btn btn-success active">My Applications</a>
                        </div>

                        <h2 class="display-4 fw-normal text-center mb-4">My <span class="text-primary">Requests</span></h2>

                        <!-- Display success or error message -->
                        <c:if test="${not empty message}">
                            <div class="alert ${message.startsWith('Error') ? 'alert-danger' : 'alert-success'}">
                                ${message}
                            </div>
                        </c:if>

                        <!-- Filter Form -->
                        <h3 class="fw-normal mb-3">Filter Requests</h3>
                        <form action="${pageContext.request.contextPath}/ViewRequests" method="get" class="mb-4">
                            <div class="row g-3">
                                <div class="col-md-3">
                                    <label for="status" class="form-label text-muted">Status</label>
                                    <select name="status" id="status" class="form-select">
                                        <option value="">All</option>
                                        <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                        <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Cancelled</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <label for="requestCode" class="form-label text-muted">Request Code</label>
                                    <input type="text" name="requestCode" id="requestCode" value="${requestCode}" class="form-control">
                                </div>
                                <div class="col-md-3">
                                    <label for="startDate" class="form-label text-muted">Start Date</label>
                                    <input type="date" name="startDate" id="startDate" value="${startDate}" class="form-control">
                                </div>
                                <div class="col-md-3">
                                    <label for="endDate" class="form-label text-muted">End Date</label>
                                    <input type="date" name="endDate" id="endDate" value="${endDate}" class="form-control">
                                </div>
                                <div class="col-12 mt-3">
                                    <button type="submit" class="btn btn-dark btn-lg rounded-1">Filter</button>
                                </div>
                            </div>
                        </form>

                        <!-- Tabs -->
                        <ul class="nav nav-tabs mb-4">
                            <li class="nav-item">
                                <a class="nav-link active" id="export-tab" data-bs-toggle="tab" href="#Export">Export Requests</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="purchase-tab" data-bs-toggle="tab" href="#Purchase">Purchase Requests</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="repair-tab" data-bs-toggle="tab" href="#Repair">Repair Requests</a>
                            </li>
                        </ul>

                        <!-- Tab Content -->
                        <div class="tab-content">
                            <!-- Export Requests -->
                            <div class="tab-pane fade show active" id="Export">
                                <h3 class="fw-normal mb-3">Export Requests</h3>
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Request Code</th>
                                                <th>Request Date</th>
                                                <th>Recipient</th>
                                                <th>Status</th>
                                                <th>Details</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="request" items="${exportRequests}">
                                                <tr>
                                                    <td>${request.requestCode}</td>
                                                    <td>${request.requestDate}</td>
                                                    <td>${request.recipientName}</td>
                                                    <td>${request.status}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=export&id=${request.exportRequestId}" class="btn btn-outline-primary btn-sm mt-2 d-inline-flex align-items-center gap-1">
                                                            <i class="bi bi-eye"></i> View
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <c:if test="${request.status == 'pending'}">
                                                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                                                <input type="hidden" name="action" value="cancel">
                                                                <input type="hidden" name="type" value="export">
                                                                <input type="hidden" name="id" value="${request.exportRequestId}">
                                                                <button type="submit" class="btn btn-danger btn-sm mt-2"
                                                                        onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                                                            </form>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <nav aria-label="Export Requests Pagination" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=export&page=${currentPage - 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${exportTotalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=export&page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == exportTotalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=export&page=${currentPage + 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>

                            <!-- Purchase Requests -->
                            <div class="tab-pane fade" id="Purchase">
                                <h3 class="fw-normal mb-3">Purchase Requests</h3>
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Request Code</th>
                                                <th>Request Date</th>
                                                <th>Status</th>
                                                <th>Details</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="request" items="${purchaseRequests}">
                                                <tr>
                                                    <td>${request.requestCode}</td>
                                                    <td>${request.requestDate}</td>
                                                    <td>${request.status}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=purchase&id=${request.purchaseRequestId}" class="btn btn-outline-primary btn-sm mt-2 d-inline-flex align-items-center gap-1">
                                                            <i class="bi bi-eye"></i> View
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <c:if test="${request.status == 'pending'}">
                                                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                                                <input type="hidden" name="action" value="cancel">
                                                                <input type="hidden" name="type" value="purchase">
                                                                <input type="hidden" name="id" value="${request.purchaseRequestId}">
                                                                <button type="submit" class="btn btn-danger btn-sm mt-2"
                                                                        onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                                                            </form>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <nav aria-label="Purchase Requests Pagination" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=purchase&page=${currentPage - 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${purchaseTotalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=purchase&page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == purchaseTotalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=purchase&page=${currentPage + 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>

                            <!-- Repair Requests -->
                            <div class="tab-pane fade" id="Repair">
                                <h3 class="fw-normal mb-3">Repair Requests</h3>
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Request Code</th>
                                                <th>Request Date</th>
                                                <th>Status</th>
                                                <th>Details</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="request" items="${repairRequests}">
                                                <tr>
                                                    <td>${request.requestCode}</td>
                                                    <td>${request.requestDate}</td>
                                                    <td>${request.status}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=repair&id=${request.repairRequestId}" class="btn btn-outline-primary btn-sm mt-2 d-inline-flex align-items-center gap-1">
                                                            <i class="bi bi-eye"></i> View
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <c:if test="${request.status == 'pending'}">
                                                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                                                <input type="hidden" name="action" value="cancel">
                                                                <input type="hidden" name="type" value="repair">
                                                                <input type="hidden" name="id" value="${request.repairRequestId}">
                                                                <button type="submit" class="btn btn-danger btn-sm mt-2"
                                                                        onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                                                            </form>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <nav aria-label="Repair Requests Pagination" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=repair&page=${currentPage - 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${repairTotalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=repair&page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == repairTotalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/ViewRequests?tab=repair&page=${currentPage + 1}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
            $(document).ready(function () {
                const urlParams = new URLSearchParams(window.location.search);
                const tab = urlParams.get('tab');
                if (tab) {
                    $('.nav-tabs a[href="#' + tab.charAt(0).toUpperCase() + tab.slice(1) + '"]').tab('show');
                }
            });
        </script>
    </body>
</html>