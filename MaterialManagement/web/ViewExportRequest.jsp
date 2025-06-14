<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Export Request Details</title>
        <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.15.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="DirectorSidebar.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Export Request Details</h1>
                        <div class="btn-toolbar mb-2 mb-md-0">
                            <a href="http://ExportRequestList" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">Request Information</h5>
                                </div>
                                <div class="card-body">
                                    <dl class="row">
                                        <dt class="col-sm-4">Request Code</dt>
                                        <dd class="col-sm-8">${exportRequest.requestCode}</dd>

                                        <dt class="col-sm-4">Status</dt>
                                        <dd class="col-sm-8">
                                            <span class="badge ${exportRequest.status == 'approved' ? 'bg-success' : 
                                                             exportRequest.status == 'rejected' ? 'bg-danger' : 
                                                             exportRequest.status == 'cancel' ? 'bg-warning' : 'bg-secondary'}">
                                                ${exportRequest.status}
                                            </span>
                                        </dd>

                                        <dt class="col-sm-4">Request Date</dt>
                                        <dd class="col-sm-8">${exportRequest.requestDate}</dd>

                                        <dt class="col-sm-4">Delivery Date</dt>
                                        <dd class="col-sm-8">${exportRequest.deliveryDate}</dd>

                                        <dt class="col-sm-4">Requested By</dt>
                                        <dd class="col-sm-8">${exportRequest.userName}</dd>

                                        <dt class="col-sm-4">Recipient</dt>
                                        <dd class="col-sm-8">${exportRequest.recipientName != null ? exportRequest.recipientName : 'N/A'}</dd>

                                        <dt class="col-sm-4">Reason</dt>
                                        <dd class="col-sm-8">${exportRequest.reason}</dd>
                                    </dl>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="card mb-4">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">Approval Information</h5>
                                </div>
                                <div class="card-body">
                                    <dl class="row">
                                        <dt class="col-sm-4">Approved By</dt>
                                        <dd class="col-sm-8">${exportRequest.approverName != null ? exportRequest.approverName : 'Not approved yet'}</dd>

                                        <dt class="col-sm-4">Approval Date</dt>
                                        <dd class="col-sm-8">${exportRequest.approvedAt != null ? exportRequest.approvedAt : 'Not approved yet'}</dd>

                                        <dt class="col-sm-4">Approval Reason</dt>
                                        <dd class="col-sm-8">${exportRequest.approvalReason != null ? exportRequest.approvalReason : 'Not provided'}</dd>

                                        <c:if test="${exportRequest.status == 'rejected'}">
                                            <dt class="col-sm-4">Rejection Reason</dt>
                                            <dd class="col-sm-8">${exportRequest.rejectionReason}</dd>
                                        </c:if>
                                    </dl>

                                    <c:if test="${sessionScope.user.roleName == 'director' && exportRequest.status == 'draft'}">
                                        <div class="mt-4">
                                            <button type="button" class="btn btn-success me-2" data-bs-toggle="modal" data-bs-target="#approveModal">
                                                <i class="fas fa-check"></i> Approve
                                            </button>
                                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#rejectModal">
                                                <i class="fas fa-times"></i> Reject
                                            </button>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <h5 class="card-title mb-0">Requested Materials</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>Material Code</th>
                                            <th>Material Name</th>
                                            <th>Quantity</th>
                                            <th>Unit</th>
                                            <th>Condition</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="detail" items="${exportRequest.details}">
                                            <tr>
                                                <td>${detail.materialCode}</td>
                                                <td>${detail.materialName}</td>
                                                <td>${detail.quantity}</td>
                                                <td>${detail.materialUnit}</td>
                                                <td>
                                                    <span class="badge ${detail.exportCondition == 'new' ? 'bg-success' : 
                                                                     detail.exportCondition == 'used' ? 'bg-warning' : 'bg-info'}">
                                                        ${detail.exportCondition}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty exportRequest.details}">
                                            <tr><td colspan="5" class="text-center text-muted">No materials requested.</td></tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <!-- Approve Modal -->
        <div class="modal fade" id="approveModal" tabindex="-1" aria-labelledby="approveModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="approveModalLabel">Approve Export Request</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="http://ApproveExportRequest" method="POST">
                        <div class="modal-body">
                            <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                            <div class="mb-3">
                                <label for="approvalReason" class="form-label">Approval Reason <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="approvalReason" name="approvalReason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success">Approve</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Reject Modal -->
        <div class="modal fade" id="rejectModal" tabindex="-1" aria-labelledby="rejectModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="rejectModalLabel">Reject Export Request</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="http://RejectExportRequest" method="POST">
                        <div class="modal-body">
                            <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                            <div class="mb-3">
                                <label for="rejectionReason" class="form-label">Rejection Reason <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="rejectionReason" name="rejectionReason" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger">Reject</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>