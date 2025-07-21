<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Export History - Material Management</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
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
            .search-box {
                margin-bottom: 20px;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .btn-action {
                width: 50px;
                height: 32px;
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 2px;
            }
            .content {
                padding-left: 20px;
                font-family: 'Roboto', sans-serif;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>
                <div class="col-md-9 col-lg-10 content px-md-4">
                    <div class="bg-white rounded shadow-sm p-4 mt-4">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2 mb-3">Export History</h2>
                        <!-- Filter Form -->
                        <form action="ExportHistory" method="get" class="d-flex gap-2 align-items-center search-box">
                            <input type="date" name="fromDate" class="form-control" value="${fromDate}" placeholder="From Date" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="date" name="toDate" class="form-control" value="${toDate}" placeholder="To Date" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="text" name="exportCode" class="form-control" placeholder="Search By Code" value="${exportCode}" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="text" name="materialName" id="materialNameInput" class="form-control" placeholder="Search By Material" list="materialNameList" value="${materialName}" style="width: 200px; height: 50px; border: 2px solid gray" />
                            <datalist id="materialNameList">
                                <c:forEach var="mat" items="${materialList}">
                                    <option value="${mat.materialName}">
                                    </c:forEach>
                            </datalist>
                            <input type="text" name="recipientName" id="recipientNameInput" class="form-control" placeholder="Search By Recipient" list="recipientNameList" value="${recipientName}" style="width: 200px; height: 50px; border: 2px solid gray" />
                            <datalist id="recipientNameList">
                                <c:forEach var="user" items="${userList}">
                                    <option value="${user.fullName}">
                                    </c:forEach>
                            </datalist>
                            <button type="submit" class="btn d-flex align-items-center justify-content-center" style="background-color: #e2b176; color: #fff; width: 150px; height: 50px; border-radius: 8px; font-weight: 500;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="ExportHistory" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 100px; height: 50px; border-radius: 8px; font-weight: 500;">Clear</a>
                        </form>
                        <!-- Table -->
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover align-middle text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Export Code</th>
                                        <th>Date</th>
                                        <th>Exported By</th>
                                        <th>Recipient</th>
                                        <th>Total Quantity</th>
                                        <th>Total Value</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty exportList}">
                                            <c:forEach var="exp" items="${exportList}" varStatus="loop">
                                                <tr>
                                                    <td>${(currentPage-1)*10 + loop.index + 1}</td>
                                                    <td>${exp.exportCode}</td>
                                                    <td>${exp.exportDate}</td>
                                                    <td>${exp.exportedByName}</td>
                                                    <td>${exp.recipientName}</td>
                                                    <td>${exp.totalQuantity}</td>
                                                    <td>${exp.totalValue}</td>
                                                    <td>
                                                        <a href="ExportDetail?exportId=${exp.exportId}" class="btn btn-info btn-action btn-sm">View</a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr><td colspan="8" class="text-center text-muted">No export records found.</td></tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <nav>
                                <ul class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <li class="page-item"><a class="page-link" href="ExportHistory?page=${currentPage-1}&fromDate=${fromDate}&toDate=${toDate}&exportCode=${exportCode}&materialName=${materialName}&recipientName=${recipientName}">Previous</a></li>
                                        </c:if>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}"><a class="page-link" href="ExportHistory?page=${i}&fromDate=${fromDate}&toDate=${toDate}&exportCode=${exportCode}&materialName=${materialName}&recipientName=${recipientName}">${i}</a></li>
                                        </c:forEach>
                                        <c:if test="${currentPage < totalPages}">
                                        <li class="page-item"><a class="page-link" href="ExportHistory?page=${currentPage+1}&fromDate=${fromDate}&toDate=${toDate}&exportCode=${exportCode}&materialName=${materialName}&recipientName=${recipientName}">Next</a></li>
                                        </c:if>
                                </ul>
                            </nav>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <script>
            var materialNames = [
            <c:forEach var="mat" items="${materialList}" varStatus="loop">
            "${mat.materialName}"<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
            ];
            var recipientNames = [
            <c:forEach var="user" items="${userList}" varStatus="loop">
            "${user.fullName}"<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
            ];
            $(function () {
                $("#materialNameInput").autocomplete({source: materialNames, minLength: 1});
                $("#recipientNameInput").autocomplete({source: recipientNames, minLength: 1});
            });
        </script>
    </body>
    
</html>
