<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Unit Management Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .table-responsive {
            margin: 20px 0;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .dashboard-title {
            color: #e2b77a;
            font-weight: bold;
            font-size: 2.2rem;
            margin-bottom: 24px;
            border-bottom: 2px solid #e2b77a;
            padding-bottom: 8px;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>
            <!-- Page Content -->
            <div class="col-md-9 col-lg-10 content px-md-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h2 class="dashboard-title">Unit List</h2>
                    <a href="AddUnit" class="btn btn-primary">
                        <i class="fas fa-plus me-1"></i> Add New Unit
                    </a>
                </div>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Unit Name</th>
                                <th>Symbol</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty units}">
                                    <c:forEach var="unit" items="${units}">
                                        <tr>
                                            <td>${unit.id}</td>
                                            <td>${unit.unitName}</td>
                                            <td>${unit.symbol}</td>
                                            <td>${unit.description}</td>
                                            <td>
                                                <a href="EditUnit?id=${unit.id}" class="btn btn-warning btn-sm me-1" title="Edit"><i class="fas fa-edit"></i></a>
                                                <form action="DeleteUnit" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this unit?');">
                                                    <input type="hidden" name="id" value="${unit.id}" />
                                                    <button type="submit" class="btn btn-danger btn-sm" title="Delete"><i class="fas fa-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="5" class="text-center text-muted">No units found.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div> <!-- end content -->
        </div> <!-- end row -->
    </div> <!-- end container-fluid -->
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 