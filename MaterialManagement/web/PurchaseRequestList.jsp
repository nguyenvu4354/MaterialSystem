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
        <title>Waggy - Danh sách yêu cầu mua hàng</title>
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
            .status-draft {
                background-color: #e9ecef;
                color: #495057;
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
        </style>
    </head>
    <body>
        <jsp:include page="HeaderAdmin.jsp"/>

        <section id="purchase-list" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow purchase-list">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h2 class="display-4 fw-normal">Danh sách <span class="text-primary">Yêu Cầu Mua Hàng</span></h2>
                            <a href="PurchaseRequestForm.jsp" class="btn btn-dark btn-lg rounded-1">
                                <i class="fas fa-plus"></i> Tạo yêu cầu mới
                            </a>
                        </div>

                        <form action="ListPurchaseRequestsServlet" method="get" class="mb-4">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="search-box">
                                        <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Tìm kiếm...">
                                    </div>
                                </div>
                                <div class="col-md-6 text-end">
                                    <select class="form-select d-inline-block w-auto" name="status">
                                        <option value="">Tất cả trạng thái</option>
                                        <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Đã duyệt</option>
                                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Từ chối</option>
                                        <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Hủy</option>
                                    </select>
                                    <button type="submit" class="btn btn-primary ms-2">Lọc</button>
                                </div>
                            </div>
                        </form>

                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th><a href="ListPurchaseRequestsServlet?sort=code_${sortOption == 'code_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Mã yêu cầu</a></th>
                                        <th>Người yêu cầu</th>
                                        <th><a href="ListPurchaseRequestsServlet?sort=date_${sortOption == 'date_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Ngày yêu cầu</a></th>
                                        <th><a href="ListPurchaseRequestsServlet?sort=status_${sortOption == 'status_asc' ? 'desc' : 'asc'}&status=${status}&keyword=${keyword}&page=${currentPage}">Trạng thái</a></th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${purchaseRequests}" var="request">
                                        <tr>
                                            <td>${request.requestCode}</td>
                                            <td>${request.userName}</td>
                                            <td>${request.requestDate}</td>
                                            <td>
                                                <span class="status-badge status-${request.status}">
                                                    ${request.status == 'PENDING' ? 'Chờ duyệt' : 
                                                      request.status == 'APPROVED' ? 'Đã duyệt' : 
                                                      request.status == 'REJECTED' ? 'Từ chối' : 
                                                      request.status == 'CANCELLED' ? 'Đã hủy' : request.status}
                                                </span>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <a href="PurchaseRequestDetail.jsp?id=${request.purchaseRequestId}" class="btn btn-info btn-sm">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <c:if test="${request.status == 'PENDING'}">
                                                        <button onclick="deleteRequest(${request.purchaseRequestId})" class="btn btn-danger btn-sm">
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

                        <!-- Phân trang -->
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${pageIndex == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="ListPurchaseRequestsServlet?page=${pageIndex - 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Trước</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${pageIndex == i ? 'active' : ''}">
                                        <a class="page-link" href="ListPurchaseRequestsServlet?page=${i}&status=${status}&keyword=${keyword}&sort=${sortOption}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${pageIndex == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="ListPurchaseRequestsServlet?page=${pageIndex + 1}&status=${status}&keyword=${keyword}&sort=${sortOption}">Sau</a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </section>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Delete request
            function deleteRequest(id) {
                if (confirm('Bạn có chắc chắn muốn xóa yêu cầu này?')) {
                    window.location.href = '${pageContext.request.contextPath}/DeletePurchaseRequest?id=' + id;
                }
            }
        </script>
    </body>
</html>
