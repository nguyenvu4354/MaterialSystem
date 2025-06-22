<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Export Request Management</title>
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
        .filter-bar {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin: 20px 0;
            align-items: center;
        }
        .filter-bar .form-control,
        .filter-bar .form-select,
        .filter-bar .btn {
            height: 48px;
            min-width: 120px;
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
        .status-approved {
            background-color: #d4edda;
            color: #155724;
        }
        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
        }
        .status-cancel {
            background-color: #e2e3f3;
            color: #4b0082;
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
            background-color: #DEAD6F;
            border-color: #DEAD6F;
        }
    </style>
</head>
<body>
<jsp:include page="HeaderAdmin.jsp" />
<div class="container-fluid">
    <div class="row">
        <jsp:include page="DirectorSidebar.jsp"/>
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="container-main">
                <h2 class="fw-bold display-6 border-bottom pb-2" style="color: #DEAD6F;"><i class="fas fa-file-export"></i> Export Request Management</h2>
                <p class="text-muted">List and manage export requests in the system</p>
                <form class="filter-bar align-items-center" method="GET" action="${pageContext.request.contextPath}/ExportRequestList" style="gap: 8px; flex-wrap:nowrap;">
                    <input type="text" class="form-control" name="search" value="${search}" placeholder="Search by request code" style="width:230px;">
                    <select class="form-select" name="searchRecipient" style="max-width:260px; min-width:200px;">
                        <option value="">All Recipients</option>
                        <c:forEach var="recipient" items="${recipients}">
                            <option value="${recipient.userId}" ${searchRecipient == recipient.userId ? 'selected' : ''}>${recipient.fullName}</option>
                        </c:forEach>
                    </select>
                    <select class="form-select" name="status" style="max-width:150px;" onchange="this.form.submit()">
                        <option value="">All Statuses</option>
                        <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                        <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Cancelled</option>
                    </select>
                    <button type="submit" class="btn" style="background-color: #DEAD6F; border-color: #DEAD6F; color:white;">Filter</button>
                    <button type="button" class="btn-export" onclick="exportTableToExcel('exportRequestTable', 'ExportRequestList')">Export to Excel</button>
                    <button type="button" class="btn-print" onclick="printTableList()">Print List</button>
                </form>
                <c:if test="${not empty exportRequests}">
                    <div class="table-responsive" id="printTableListArea">
                        <table id="exportRequestTable" class="table custom-table">
                            <thead>
                            <tr>
                                <th>Request Code</th>
                                <th>Request Date</th>
                                <th>Status</th>
                                <th>Delivery Date</th>
                                <th>Sender</th>
                                <th>Recipient</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="request" items="${exportRequests}">
                                <tr>
                                    <td>${request.requestCode}</td>
                                    <td>${request.requestDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${request.status == 'approved'}">
                                                <span class="status-badge status-approved">Approved</span>
                                            </c:when>
                                            <c:when test="${request.status == 'rejected'}">
                                                <span class="status-badge status-rejected">Rejected</span>
                                            </c:when>
                                            <c:when test="${request.status == 'cancel'}">
                                                <span class="status-badge status-cancel">Cancelled</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-pending">Pending</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.deliveryDate}">
                                                ${request.deliveryDate}
                                            </c:when>
                                            <c:otherwise>
                                                Not available
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.userName}">
                                                ${request.userName}
                                            </c:when>
                                            <c:otherwise>
                                                Unknown
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.recipientName}">
                                                ${request.recipientName}
                                            </c:when>
                                            <c:otherwise>
                                                Unknown
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                      <a href="${pageContext.request.contextPath}/ViewExportRequest?id=${request.exportRequestId}&status=${status}&search=${search}" class="btn-detail" style="pointer-events:auto;z-index:9999;position:relative;">
                                            <i class="fas fa-eye"></i> Details
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
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
                </c:if>
                <c:if test="${empty exportRequests}">
                    <div class="alert alert-info mt-4">
                        No export request found.
                    </div>
                </c:if>
            </div>
        </main>
    </div>
</div>
<jsp:include page="Footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
// Xuất Excel
function exportTableToExcel(tableID, filename = '') {
    var downloadLink;
    var dataType = 'application/vnd.ms-excel';
    var tableSelect = document.getElementById(tableID);
    var tableHTML = tableSelect.outerHTML.replace(/ /g, '%20');
    filename = filename? filename+'.xls':'excel_data.xls';
    downloadLink = document.createElement("a");
    document.body.appendChild(downloadLink);
    if(navigator.msSaveOrOpenBlob){
        var blob = new Blob(['\ufeff', tableHTML], { type: dataType });
        navigator.msSaveOrOpenBlob( blob, filename);
    }else{
        downloadLink.href = 'data:' + dataType + ', ' + tableHTML;
        downloadLink.download = filename;
        downloadLink.click();
    }
    document.body.removeChild(downloadLink);
}

function printTableList() {
    var printContents = document.getElementById('printTableListArea').innerHTML;
    var originalContents = document.body.innerHTML;
    document.body.innerHTML = printContents;
    window.print();
    document.body.innerHTML = originalContents;
    location.reload();
}
</script>
</body>
</html>