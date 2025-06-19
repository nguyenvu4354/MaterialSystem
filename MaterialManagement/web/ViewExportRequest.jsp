<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Waggy - Request Details</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .card {
            border: none;
            margin-bottom: 20px;
        }
        .card-header {
            background-color: #e9ecef;
            border-bottom: none;
            font-weight: bold;
        }
        .table {
            margin-top: 20px;
        }
        .btn {
            margin-right: 10px;
        }
        .alert {
            margin-top: 20px;
        }
        .total-amount {
            font-size: 1.2em;
            font-weight: bold;
            color: #dc3545;
        }
        .status-tag {
            padding: 5px 10px;
            border-radius: 5px;
            color: #fff;
        }
        .status-draft { background-color: #6c757d; }
        .status-approved { background-color: #198754; }
        .status-rejected { background-color: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <h2>${exportRequest.requestCode} <span class="status-tag status-${exportRequest.status}">${exportRequest.status}</span></h2>
        <p>Tạo lúc: ${exportRequest.requestDate}</p>

        <div class="card">
            <div class="card-header">Người gửi</div>
            <div class="card-body">
                <p><strong>Họ tên:</strong> ${sender.fullName}</p>
                <p><strong>Email:</strong> ${sender.email}</p>
                <p><strong>Điện thoại:</strong> ${sender.phoneNumber}</p>
                <p><strong>Địa chỉ:</strong> Hà Nội</p>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Người nhận</div>
            <div class="card-body">
                <p><strong>Họ tên:</strong> ${recipient.fullName}</p>
                <p><strong>Email:</strong> ${recipient.email}</p>
                <p><strong>Điện thoại:</strong> ${recipient.phoneNumber}</p>
                <p><strong>Địa chỉ:</strong> Hà Nội</p>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Thông tin đơn</div>
            <div class="card-body">
                <p><strong>Ngày giao đơn:</strong> ${exportRequest.deliveryDate}</p>
                <p>Lý do thực hiện đơn:</p>
                <p>${exportRequest.reason != null ? exportRequest.reason : "N/A"}</p>
                <c:if test="${not empty exportRequest.approvalReason}">
                    <p><strong>Lý do phê duyệt:</strong> ${exportRequest.approvalReason}</p>
                    <p><strong>Phê duyệt lúc:</strong> ${exportRequest.approvedAt}</p>
                </c:if>
                <c:if test="${not empty exportRequest.rejectionReason}">
                    <p><strong>Lý do từ chối:</strong> ${exportRequest.rejectionReason}</p>
                </c:if>
            </div>
        </div>

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Danh sách vật tư</span>
            </div>
            <div class="card-body">
                <div class="table-responsive" id="printTableArea">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Mã vật tư</th>
                                <th>Tên vật tư</th>
                                <th>Số lượng</th>
                                <th>Đơn vị</th>
                                <th>Trạng thái xuất</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${details}">
                                <tr>
                                    <td>${detail.materialCode}</td>
                                    <td>${detail.materialName}</td>
                                    <td>${detail.quantity}</td>
                                    <td>${detail.materialUnit}</td>
                                    <td>${detail.exportCondition}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>

        <c:if test="${isDirector && exportRequest.status == 'draft'}">
            <div class="d-flex gap-2 mb-2">
                <button type="button" class="btn btn-success" onclick="showReasonBox('approve')">Phê duyệt</button>
                <button type="button" class="btn btn-danger" onclick="showReasonBox('reject')">Từ chối</button>
                <button type="button" class="btn btn-warning" onclick="window.history.back()">Quay lại</button>
            </div>
            <!-- Box nhập lý do phê duyệt -->
            <form id="approveForm" action="${pageContext.request.contextPath}/ApproveExportRequest" method="post" style="display:none; margin-top:10px;">
                <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                <input type="text" name="approvalReason" placeholder="Nhập lý do phê duyệt" required class="form-control mb-2">
                <button type="submit" class="btn btn-success">Xác nhận phê duyệt</button>
                <button type="button" class="btn btn-secondary" onclick="hideReasonBox()">Hủy</button>
            </form>
            <!-- Box nhập lý do từ chối -->
            <form id="rejectForm" action="${pageContext.request.contextPath}/RejectExportRequest" method="post" style="display:none; margin-top:10px;">
                <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                <input type="text" name="rejectionReason" placeholder="Nhập lý do từ chối" required class="form-control mb-2">
                <button type="submit" class="btn btn-danger">Xác nhận từ chối</button>
                <button type="button" class="btn btn-secondary" onclick="hideReasonBox()">Hủy</button>
            </form>
            <script>
                function showReasonBox(type) {
                    document.getElementById('approveForm').style.display = (type === 'approve') ? 'block' : 'none';
                    document.getElementById('rejectForm').style.display = (type === 'reject') ? 'block' : 'none';
                }
                function hideReasonBox() {
                    document.getElementById('approveForm').style.display = 'none';
                    document.getElementById('rejectForm').style.display = 'none';
                }
            </script>
        </c:if>
    </div>
    <script>
    </script>
</body>
</html>