<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Import History - Material Management</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style,z.css">
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
                color: #DEAD6F;
            }
            .filter-bar {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                margin: 20px 0;
                align-items: center;
            }
            .filter-bar .form-control,
            .filter-bar .btn {
                height: 48px;
                min-width: 120px;
            }
            .btn-filter:hover {
                background-color: #cfa856;
                color: #fff;
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
            .btn-detail {
                background-color: #fff7e6;
                color: #b8860b;
                border: 1px solid #ffe58f;
                border-radius: 6px;
                padding: 6px auto;
                font-weight: 300;
                width: 100px;
            }
            .pagination {
                margin-top: 20px;
                justify-content: center;
            }
            .pagination .page-link {
                color: #DEAD6F;
                border: 1px solid #DEAD6F;
                margin: 0 4px;
                border-radius: 6px;
            }
            .pagination .page-link:hover {
                background-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.active .page-link {
                background-color: #DEAD6F;
                border-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.disabled .page-link {
                color: #6c757d;
                border-color: #dee2e6;
                background-color: #f8f9fa;
            }
            .pagination-ellipsis {
                color: #6c757d;
                padding: 0 10px;
                line-height: 38px;
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
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2 mb-3">Import History</h2>
                        <!-- Filter Form -->
                        <form action="ImportHistory" method="get" class="d-flex gap-2 align-items-center search-box">
                            <input type="date" name="fromDate" class="form-control" value="${fromDate}" placeholder="From Date" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="date" name="toDate" class="form-control" value="${toDate}" placeholder="To Date" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="text" name="importCode" class="form-control" placeholder="Search By Code" value="${importCode}" style="width: 180px; height: 50px; border: 2px solid gray" />
                            <input type="text" name="materialName" id="materialNameInput" class="form-control" placeholder="Search By Material" list="materialNameList" value="${materialName}" style="width: 200px; height: 50px; border: 2px solid gray" />
                            <datalist id="materialNameList">
                                <c:forEach var="mat" items="${materialList}">
                                    <option value="${mat.materialName}">
                                </c:forEach>
                            </datalist>
                            <input type="text" name="supplierName" id="supplierNameInput" class="form-control" placeholder="Search By Supplier" list="supplierNameList" value="${supplierName}" style="width: 200px; height: 50px; border: 2px solid gray" />
                            <datalist id="supplierNameList">
                                <c:forEach var="sup" items="${supplierList}">
                                    <option value="${sup.supplierName}">
                                </c:forEach>
                            </datalist>
                            <button type="submit" class="btn d-flex align-items-center justify-content-center" style="background-color: #e2b176; color: #fff; width: 150px; height: 50px; border-radius: 8px; font-weight: 500;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="ImportHistory" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 100px; height: 50px; border-radius: 8px; font-weight: 500;">Clear</a>
                        </form>
                        <!-- Table -->
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover align-middle text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Import Code</th>
                                        <th>Date</th>
                                        <th>Imported By</th>
                                        <th>Supplier</th>
                                        <th>Total Quantity</th>
                                        <th>Total Value</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty importList}">
                                            <c:forEach var="imp" items="${importList}" varStatus="loop">
                                                <tr>
                                                    <td>${(currentPage-1)*10 + loop.index + 1}</td>
                                                    <td>${imp.importCode}</td>
                                                    <td>${imp.importDate}</td>
                                                    <td>${imp.importedByName}</td>
                                                    <td>${imp.supplierName}</td>
                                                    <td>${imp.totalQuantity}</td>
                                                    <td>${imp.totalValue}</td>
                                                    <td>
                                                        <a href="ImportDetail?importId=${imp.importId}" class="btn btn-detail">
                                                            <i class="fas fa-eye"></i> Detail
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr><td colspan="8" class="text-center text-muted">No import records found.</td></tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                        <!-- Enhanced Pagination -->
                        <c:if test="${totalPages > 0}">
                            <nav>
                                <ul class="pagination">
                                    <!-- Previous Button -->
                                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="ImportHistory?page=${currentPage-1}&fromDate=${fromDate}&toDate=${toDate}&importCode=${importCode}&materialName=${materialName}&supplierName=${supplierName}">Previous</a>
                                    </li>
                                    <!-- Page Numbers -->
                                    <c:set var="startPage" value="${currentPage - 2 > 1 ? currentPage - 2 : 1}"/>
                                    <c:set var="endPage" value="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}"/>
                                    <c:if test="${startPage > 1}">
                                        <li class="page-item"><a class="page-link" href="ImportHistory?page=1&fromDate=${fromDate}&toDate=${toDate}&importCode=${importCode}&materialName=${materialName}&supplierName=${supplierName}">1</a></li>
                                        <li class="page-item disabled"><span class="pagination-ellipsis">...</span></li>
                                    </c:if>
                                    <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link" href="ImportHistory?page=${i}&fromDate=${fromDate}&toDate=${toDate}&importCode=${importCode}&materialName=${materialName}&supplierName=${supplierName}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <c:if test="${endPage < totalPages}">
                                        <li class="page-item disabled"><span class="pagination-ellipsis">...</span></li>
                                        <li class="page-item"><a class="page-link" href="ImportHistory?page=${totalPages}&fromDate=${fromDate}&toDate=${toDate}&importCode=${importCode}&materialName=${materialName}&supplierName=${supplierName}">${totalPages}</a></li>
                                    </c:if>
                                    <!-- Next Button -->
                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="ImportHistory?page=${currentPage+1}&fromDate=${fromDate}&toDate=${toDate}&importCode=${importCode}&materialName=${materialName}&supplierName=${supplierName}">Next</a>
                                    </li>
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
            var supplierNames = [
            <c:forEach var="sup" items="${supplierList}" varStatus="loop">
            "${sup.supplierName}"<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
            ];
            $(function () {
                $("#materialNameInput").autocomplete({source: materialNames, minLength: 1});
                $("#supplierNameInput").autocomplete({source: supplierNames, minLength: 1});
            });
        </script>
    </body>
</html>