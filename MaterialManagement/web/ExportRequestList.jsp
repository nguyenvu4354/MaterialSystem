<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Export Request List</title>
        <link href="${pageContext.request.contextPath}/your.style.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
 <jsp:include page="header.jsp" />
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="DirectorSidebar.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Export Request List</h1>
                    </div>

                    <form class="mb-3" method="GET" action="${pageContext.request.contextPath}/ExportRequestList">
                        <div class="row">
                            <div class="col-md-4">
                                <select class="form-select" name="status">
                                    <option value="">All Status</option>
                                    <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                                    <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                    <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                    <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Cancel</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <input type="text" class="form-control" name="search" value="${search}" placeholder="Search by code">
                            </div>
                            <div class="col-md-4">
                                <button type="submit" class="btn btn-primary">Filter</button>
                            </div>
                        </div>
                    </form>

                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Request Code</th>
                                    <th>Request Date</th>
                                    <th>Status</th>
                                    <th>Delivery Date</th>
                                    <th>Recipient</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="request" items="${exportRequests}">
                                    <tr>
                                        <td>${request.requestCode}</td>
                                        <td>${request.requestDate}</td>
                                        <td>
                                            <span class="badge ${request.status == 'approved' ? 'bg-success' : 
                                               request.status == 'rejected' ? 'bg-danger' : 
                                               request.status == 'cancel' ? 'bg-warning' : 'bg-secondary'}">
                                                ${request.status}
                                            </span>
                                        </td>
                                        <td>${request.deliveryDate}</td>
                                        <td>${request.recipientName != null ? request.recipientName : 'N/A'}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/ViewExportRequest?id=${request.exportRequestId}" class="btn btn-info btn-sm">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <nav aria-label="Page navigation">
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${currentPage - 1}&status=${status}&search=${search}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${i}&status=${status}&search=${search}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${currentPage + 1}&status=${status}&search=${search}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>