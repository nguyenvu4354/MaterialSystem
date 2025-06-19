<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý yêu cầu xuất vật tư</title>
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
        .status-draft {
            background-color: #fff3cd;
            color: #856404;
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
            background-color: #009966;
            border-color: #009966;
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
                <h1>Quản lý yêu cầu xuất vật tư</h1>
                <p class="text-muted">Danh sách và quản lý các yêu cầu xuất vật tư trong hệ thống</p>
                <form class="filter-bar align-items-center" method="GET" action="${pageContext.request.contextPath}/ExportRequestList" style="gap: 8px; flex-wrap:nowrap;">
                    <input type="text" class="form-control" name="searchCode" value="${searchCode}" placeholder="Tìm theo mã yêu cầu..." style="max-width:260px; min-width:200px;">
                    <select class="form-select" name="searchRecipient" style="max-width:260px; min-width:200px;">
                        <option value="">Tất cả người nhận</option>
                        <c:forEach var="recipient" items="${recipients}">
                            <option value="${recipient.userId}" ${searchRecipient == recipient.userId ? 'selected' : ''}>${recipient.fullName}</option>
                        </c:forEach>
                    </select>
                    <select class="form-select" name="status" style="max-width:150px;" onchange="this.form.submit()">
                        <option value="">Tất cả trạng thái</option>
                        <option value="draft" ${status == 'draft' ? 'selected' : ''}>Nháp</option>
                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Phê duyệt</option>
                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Từ chối</option>
                        <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Hủy</option>
                    </select>
                    <button type="submit" class="btn btn-primary">Lọc</button>
                    <button type="button" class="btn-export" onclick="exportTableToExcel('exportRequestTable', 'DanhSachYeuCauXuat')">Xuất Excel</button>
                    <button type="button" class="btn-print" onclick="printTableList()">In danh sách</button>
                </form>
                <c:if test="${not empty exportRequests}">
                    <div class="table-responsive" id="printTableListArea">
                        <table id="exportRequestTable" class="table custom-table">
                            <thead>
                            <tr>
                                <th>Mã YC</th>
                                <th>Ngày Yêu Cầu</th>
                                <th>Trạng Thái</th>
                                <th>Ngày Giao</th>
                                <th>Người Gửi</th>
                                <th>Người Nhận</th>
                                <th>Thao Tác</th>
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
                                                <span class="status-badge status-approved">Phê duyệt</span>
                                            </c:when>
                                            <c:when test="${request.status == 'rejected'}">
                                                <span class="status-badge status-rejected">Từ chối</span>
                                            </c:when>
                                            <c:when test="${request.status == 'cancel'}">
                                                <span class="status-badge status-cancel">Hủy</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-draft">Nháp</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.deliveryDate}">
                                                ${request.deliveryDate}
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.userName}">
                                                ${request.userName}
                                            </c:when>
                                            <c:otherwise>
                                                Chưa rõ
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty request.recipientName}">
                                                ${request.recipientName}
                                            </c:when>
                                            <c:otherwise>
                                                Chưa rõ
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                      <a href="${pageContext.request.contextPath}/ViewExportRequest?id=${request.exportRequestId}&status=${status}&search=${search}" class="btn-detail" style="pointer-events:auto;z-index:9999;position:relative;">
                                            <i class="fas fa-eye"></i> Chi tiết
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
                                <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${currentPage - 1}&status=${status}&search=${search}">Trước</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${i}&status=${status}&search=${search}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${currentPage + 1}&status=${status}&search=${search}">Sau</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
                <c:if test="${empty exportRequests}">
                    <div class="alert alert-info mt-4">
                        Không tìm thấy yêu cầu xuất kho nào.
                    </div>
                </c:if>
            </div>
        </main>
    </div>
</div>
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