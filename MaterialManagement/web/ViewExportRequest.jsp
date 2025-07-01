<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Request Details</title>
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
            background-color: #DEAD6F;
            border-bottom: none;
            font-weight: bold;
        }
        .table {
            margin-top: 20px;
        }
        .btn {
            margin-right: 10px;
            background-color:#DEAD6F;
        
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
        .status-pending { background-color: #6c757d; }
        .status-approved { background-color: #198754; }
        .status-rejected { background-color: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <h2>${exportRequest.requestCode} <span class="status-tag status-${exportRequest.status}">${exportRequest.status}</span></h2>
        <p>Created at: ${exportRequest.requestDate}</p>

        <div class="card">
            <div class="card-header">Sender</div>
            <div class="card-body d-flex align-items-center">
                <div style="flex: 1;">
                    <p><strong>Full Name:</strong> ${sender.fullName}</p>
                    <p><strong>Email:</strong> ${sender.email}</p>
                    <p><strong>Phone Number:</strong> ${sender.phoneNumber}</p>
                    <p><strong>Address:</strong> Hanoi</p>
                </div>
                <div style="flex: 0 0 100px; margin-left: 20px;">
                    <img src="${senderImg}" alt="${sender.fullName}" class="img-fluid rounded-circle">
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Recipient</div>
            <div class="card-body d-flex align-items-center">
                <div style="flex: 1;">
                    <p><strong>Full Name:</strong> ${recipient.fullName}</p>
                    <p><strong>Email:</strong> ${recipient.email}</p>
                    <p><strong>Phone Number:</strong> ${recipient.phoneNumber}</p>
                    <p><strong>Address:</strong> Hanoi</p>
                </div>
                <div style="flex: 0 0 100px; margin-left: 20px;">
                    <img src="${recipientImg}" alt="${recipient.fullName}" class="img-fluid rounded-circle">
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Order Information</div>
            <div class="card-body">
                <p><strong>Delivery Date:</strong> ${exportRequest.deliveryDate}</p>
                <p>Request Reason:</p>
                <p>${exportRequest.reason != null ? exportRequest.reason : "N/A"}</p>
                <c:if test="${not empty exportRequest.approvalReason}">
                    <p><strong>Approval Reason:</strong> ${exportRequest.approvalReason}</p>
                    <p><strong>Approved At:</strong> ${exportRequest.approvedAt}</p>
                </c:if>
                <c:if test="${not empty exportRequest.rejectionReason}">
                    <p><strong>Rejection Reason:</strong> ${exportRequest.rejectionReason}</p>
                </c:if>
            </div>
        </div>

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Material List</span>
            </div>
            <div class="card-body">
                <div class="table-responsive" id="printTableArea">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Material Code</th>
                                <th>Material Name</th>
                                <th>Image</th>
                                <th>Quantity</th>
                                <th>Unit</th>
                                <th>Export Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${details}">
                                <tr>
                                    <td>${detail.materialCode}</td>
                                    <td>${detail.materialName}</td>
                                    <td>
                                        <img src="${not empty detail.materialImageUrl ? detail.materialImageUrl : 'images/placeholder.png'}" 
                                             alt="${detail.materialName}" 
                                             style="width: 100px; height: auto; object-fit: cover;">
                                    </td>
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

        <c:if test="${canApproveExportRequest && exportRequest.status == 'pending'}">
            <div class="d-flex gap-2 mb-2">
                <button type="button" class="btn btn-success" onclick="showReasonBox('approve')">Approve</button>
                <button type="button" class="btn btn-danger" onclick="showReasonBox('reject')">Reject</button>
                <a href="${pageContext.request.contextPath}/ExportRequestList" class="btn btn-warning">Cancel</a>
            </div>
            <!-- Approval reason box -->
            <form id="approveForm" action="${pageContext.request.contextPath}/ApproveExportRequest" method="post" style="display:none; margin-top:10px;">
                <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                <input type="text" name="approvalReason" placeholder="Enter approval reason" required class="form-control mb-2">
                <button type="submit" class="btn btn-success">Confirm Approval</button>
                <button type="button" class="btn btn-secondary" onclick="hideReasonBox()">Cancel</button>
            </form>
            <!-- Rejection reason box -->
            <form id="rejectForm" action="${pageContext.request.contextPath}/RejectExportRequest" method="post" style="display:none; margin-top:10px;">
                <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                <input type="text" name="rejectionReason" placeholder="Enter rejection reason" required class="form-control mb-2">
                <button type="submit" class="btn btn-danger">Confirm Rejection</button>
                <button type="button" class="btn btn-secondary" onclick="hideReasonBox()">Cancel</button>
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

        <c:if test="${!(canApproveExportRequest && exportRequest.status == 'pending')}">
            <div class="mb-2">
                <a href="${pageContext.request.contextPath}/ExportRequestList" class="btn btn-warning">Cancel</a>
            </div>
        </c:if>
    </div>
    <script>
    </script>
</body>
</html>