<%-- 
    Document   : PurchaseRequestDetail
    Created on : Jun 10, 2025, 7:21:25 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Waggy - Chi tiết yêu cầu mua hàng</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">

        <style>
            .purchase-detail .info-label {
                font-size: 0.9rem;
                color: #6c757d;
                margin-bottom: 0.25rem;
            }
            .purchase-detail .info-value {
                font-size: 1.1rem;
                font-weight: 500;
            }
            .purchase-detail .table th {
                font-weight: 500;
                color: #6c757d;
            }
            .purchase-detail .table td {
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
        </style>
    </head>
    <body>
        <jsp:include page="HeaderAdmin.jsp"/>

        <section id="purchase-detail" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow purchase-detail">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h2 class="display-4 fw-normal">Chi tiết <span class="text-primary">Yêu Cầu Mua Hàng</span></h2>
                            <div class="btn-group">
                                <a href="PurchaseRequestForm.jsp?id=${request.purchaseRequestId}" class="btn btn-outline-secondary btn-lg rounded-1">
                                    <i class="fas fa-edit"></i> Chỉnh sửa
                                </a>
                                <a href="PurchaseRequestList.jsp" class="btn btn-outline-dark btn-lg rounded-1">
                                    <i class="fas fa-arrow-left"></i> Quay lại
                                </a>
                            </div>
                        </div>

                        <div class="row g-4 mb-5">
                            <div class="col-md-3">
                                <div class="info-label">Mã yêu cầu</div>
                                <div class="info-value">${request.requestCode}</div>
                            </div>
                            <div class="col-md-3">
                                <div class="info-label">Ngày tạo</div>
                                <div class="info-value">${request.requestDate}</div>
                            </div>
                            <div class="col-md-3">
                                <div class="info-label">Người yêu cầu</div>
                                <div class="info-value">${request.userName}</div>
                            </div>
                            <div class="col-md-3">
                                <div class="info-label">Trạng thái</div>
                                <div class="info-value">
                                    <span class="status-badge status-${request.status}">
                                        ${request.status}
                                    </span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-label">Giá dự kiến</div>
                                <div class="info-value">${request.estimatedPrice}</div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-label">Lý do</div>
                                <div class="info-value">${request.reason}</div>
                            </div>
                        </div>

                        <h3 class="fw-normal mb-4">Danh sách vật tư</h3>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên vật tư</th>
                                        <th>Danh mục</th>
                                        <th>Số lượng</th>
                                        <th>Ghi chú</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="detail" items="${requestDetails}" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>${detail.materialName}</td>
                                            <td>${detail.categoryName}</td>
                                            <td>${detail.quantity}</td>
                                            <td>${detail.notes}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <c:if test="${request.status == 'approved' || request.status == 'rejected'}">
                            <div class="row g-4 mt-5">
                                <div class="col-md-6">
                                    <div class="info-label">Người duyệt</div>
                                    <div class="info-value">${request.approvedByName}</div>
                                </div>
                                <div class="col-md-6">
                                    <div class="info-label">Ngày duyệt</div>
                                    <div class="info-value">${request.approvedAt}</div>
                                </div>
                                <div class="col-12">
                                    <div class="info-label">Ghi chú duyệt</div>
                                    <div class="info-value">${request.approvalNotes}</div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
