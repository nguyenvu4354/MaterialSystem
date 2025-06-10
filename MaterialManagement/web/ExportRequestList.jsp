<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Export Request List</title>
        <link href="css/style.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="DirectorSidebar.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Export Request List</h1>
                        <div class="btn-toolbar mb-2 mb-md-0">
                            <a href="CreateExportRequest.jsp" class="btn btn-primary">
                                <i class="fas fa-plus"></i> New Export Request
                            </a>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Request Code</th>
                                    <th>Request Date</th>
                                    <th>Status</th>
                                    <th>Delivery Date</th>
                                    <th>Destination</th>
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
                                        <td>${request.destination}</td>
                                        <td>
                                            <a href="ViewExportRequest?id=${request.exportRequestId}" class="btn btn-info btn-sm">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${request.status == 'draft'}">
                                                <a href="EditExportRequest?id=${request.exportRequestId}" class="btn btn-warning btn-sm">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <button onclick="deleteRequest(${request.exportRequestId})" class="btn btn-danger btn-sm">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function deleteRequest(id) {
                if (confirm('Are you sure you want to delete this request?')) {
                    window.location.href = 'DeleteExportRequest?id=' + id;
                }
            }
        </script>
    </body>
</html> 