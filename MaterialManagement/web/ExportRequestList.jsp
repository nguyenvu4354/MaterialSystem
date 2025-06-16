<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Danh Sách Yêu Cầu Xuất Kho</title>
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
                    <h1 class="h2">Danh Sách Yêu Cầu Xuất Kho</h1>
                </div>

                <form class="mb-3" method="GET" action="${pageContext.request.contextPath}/ExportRequestList">
                    <div class="row">
                        <div class="col-md-4">
                            <select class="form-select" name="status" onchange="this.form.submit()">
                                <option value="">Tất Cả Trạng Thái</option>
                                <option value="draft" ${status == 'draft' ? 'selected' : ''}>Nháp</option>
                                <option value="approved" ${status == 'approved' ? 'selected' : ''}>Đã Duyệt</option>
                                <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Bị Từ Chối</option>
                                <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Đã Hủy</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <input type="text" class="form-control" name="search" value="${search}" placeholder="Tìm theo mã" onkeypress="if(event.key === 'Enter') this.form.submit()">
                        </div>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-primary">Lọc</button>
                        </div>
                    </div>
                </form>

                <!-- Debug information -->
                <div class="alert alert-warning mb-3">
                    <strong>Debug:</strong> exportRequests is ${empty exportRequests ? 'empty or null' : 'not empty'}, Total items: ${totalItems}, Current page: ${currentPage}, Total pages: ${totalPages}.
                </div>

                <c:if test="${not empty exportRequests}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Mã Yêu Cầu</th>
                                    <th>Ngày Yêu Cầu</th>
                                    <th>Trạng Thái</th>
                                    <th>Ngày Giao Hàng</th>
                                    <th>Người Nhận</th>
                                    <th>Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="request" items="${exportRequests}" varStatus="loop">
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
                                        <td>${request.deliveryDate != null ? request.deliveryDate : 'Chưa đặt'}</td>
                                        <td>${request.recipientName != null ? request.recipientName : 'Chưa xác định'}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/ViewExportRequest?id=${request.exportRequestId}" class="btn btn-info btn-sm">
                                                <i class="fas fa-eye"></i> Xem
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ExportRequestList?page=${currentPage - 1}&status=${status}&search=${search}" tabindex="-1">Trước</a>
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
                    <div class="alert alert-info">
                        Không tìm thấy yêu cầu xuất kho nào. Vui lòng kiểm tra bộ lọc hoặc dữ liệu trong database.
                    </div>
                </c:if>
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.querySelector('select[name="status"]').addEventListener('change', function() {
            this.form.submit();
        });
        document.querySelector('input[name="search"]').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                this.form.submit();
            }
        });
    </script>
</body>
</html>