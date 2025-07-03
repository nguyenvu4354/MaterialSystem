<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Purchase Order Detail</title>
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
        .status-badge {
            padding: 2px 10px;
            border-radius: 10px;
            font-size: 13px;
            font-weight: 500;
        }
        .status-draft {
            background-color: #e2e3e5;
            color: #495057;
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
        .status-sent_to_supplier {
            background-color: #cce5ff;
            color: #004085;
        }
        .status-cancelled {
            background-color: #e2e3f3;
            color: #4b0082;
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
        .btn-back {
            background-color: #DEAD6F;
            color: #fff;
            border: none;
            padding: 6px 14px;
            border-radius: 6px;
            font-weight: 500;
            transition: background 0.2s;
        }
        .btn-back:hover {
            background-color: #cfa856;
            color: #fff;
        }

        /* Custom Pagination Styles */
        .pagination .page-link {
            color: #5c4434;
            background-color: #f4f1ed;
            border: 1px solid #e2d6c3;
            transition: background-color 0.3s, color 0.3s;
        }
        .pagination .page-link:hover {
            background-color: #e7dbc7;
            color: #5c4434;
        }
        .pagination .page-item.active .page-link {
            background-color: #DEAD6F;
            border-color: #DEAD6F;
            color: #fff;
        }
        .pagination .page-item.disabled .page-link {
            background-color: #eee;
            color: #aaa;
            border-color: #ddd;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<jsp:include page="Header.jsp" />
<div class="container-fluid">
    <div class="row">
        <jsp:include page="SidebarDirector.jsp"/>
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <div class="container-main">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="fw-bold display-6 border-bottom pb-2 m-0" style="color: #DEAD6F;">
                        <i class="fas fa-file-invoice"></i> Purchase Order Detail
                    </h2>
                    <a href="PurchaseOrderList" class="btn btn-back">
                        <i class="fas fa-arrow-left me-2"></i>Back to List
                    </a>
                </div>
                
                <c:if test="${!hasViewPurchaseOrderDetailPermission}">
                    <div class="alert alert-danger">You do not have permission to view purchase order detail.</div>
                    <jsp:include page="HomePage.jsp"/>
                </c:if>
                <c:if test="${hasViewPurchaseOrderDetailPermission}">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty purchaseOrder}">
                        <!-- Purchase Order Information -->
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="mb-0"><i class="fas fa-info-circle me-2"></i>Purchase Order Information</h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>PO Code:</strong></div>
                                            <div class="col-sm-8">${purchaseOrder.poCode}</div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>Request Code:</strong></div>
                                            <div class="col-sm-8">${purchaseOrder.purchaseRequestCode}</div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>Status:</strong></div>
                                            <div class="col-sm-8">
                                                <c:choose>
                                                    <c:when test="${purchaseOrder.status == 'draft'}">
                                                        <span class="status-badge status-draft">Draft</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'pending'}">
                                                        <span class="status-badge status-pending">Pending</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'approved'}">
                                                        <span class="status-badge status-approved">Approved</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'rejected'}">
                                                        <span class="status-badge status-rejected">Rejected</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'sent_to_supplier'}">
                                                        <span class="status-badge status-sent_to_supplier">Sent to Supplier</span>
                                                    </c:when>
                                                    <c:when test="${purchaseOrder.status == 'cancelled'}">
                                                        <span class="status-badge status-cancelled">Cancelled</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-pending">${purchaseOrder.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>Created By:</strong></div>
                                            <div class="col-sm-8">${purchaseOrder.createdByName}</div>
                                        </div>
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>Created Date:</strong></div>
                                            <div class="col-sm-8">${purchaseOrder.createdAt}</div>
                                        </div>
                                        <c:if test="${not empty purchaseOrder.note}">
                                            <div class="row mb-2">
                                                <div class="col-sm-4"><strong>Note:</strong></div>
                                                <div class="col-sm-8">${purchaseOrder.note}</div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="mb-0"><i class="fas fa-user-check me-2"></i>Approval Information</h5>
                                    </div>
                                    <div class="card-body">
                                        <c:if test="${not empty purchaseOrder.approvedByName}">
                                            <div class="row mb-2">
                                                <div class="col-sm-4"><strong>Approved By:</strong></div>
                                                <div class="col-sm-8">${purchaseOrder.approvedByName}</div>
                                            </div>
                                            <div class="row mb-2">
                                                <div class="col-sm-4"><strong>Approved Date:</strong></div>
                                                <div class="col-sm-8">${purchaseOrder.approvedAt}</div>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty purchaseOrder.rejectionReason}">
                                            <div class="row mb-2">
                                                <div class="col-sm-4"><strong>Rejection Reason:</strong></div>
                                                <div class="col-sm-8 text-danger">${purchaseOrder.rejectionReason}</div>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty purchaseOrder.sentToSupplierAt}">
                                            <div class="row mb-2">
                                                <div class="col-sm-4"><strong>Sent to Supplier:</strong></div>
                                                <div class="col-sm-8">${purchaseOrder.sentToSupplierAt}</div>
                                            </div>
                                        </c:if>
                                        <div class="row mb-2">
                                            <div class="col-sm-4"><strong>Total Amount:</strong></div>
                                            <div class="col-sm-8">
                                                <strong class="text-success">$<fmt:formatNumber value="${purchaseOrder.totalAmount}" type="number" minFractionDigits="2"/></strong>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Purchase Order Details -->
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="fas fa-list me-2"></i>Purchase Order Details</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty pagedDetails}">
                                    <div class="table-responsive">
                                        <table class="table custom-table">
                                            <thead>
                                                <tr>
                                                    <th>Material Name</th>
                                                    <th>Category</th>
                                                    <th>Quantity</th>
                                                    <th>Unit Price</th>
                                                    <th>Total</th>
                                                    <th>Supplier</th>
                                                    <th>Note</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="detail" items="${pagedDetails}">
                                                    <tr>
                                                        <td><strong>${detail.materialName}</strong></td>
                                                        <td>${detail.categoryName}</td>
                                                        <td>${detail.quantity}</td>
                                                        <td>$<fmt:formatNumber value="${detail.unitPrice}" type="number" minFractionDigits="2"/></td>
                                                        <td><strong>$<fmt:formatNumber value="${detail.quantity * detail.unitPrice}" type="number" minFractionDigits="2"/></strong></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty detail.supplierName}">
                                                                    ${detail.supplierName}
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">Not specified</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty detail.note}">
                                                                    ${detail.note}
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">-</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <!-- Pagination -->
                                    <nav class="mt-3">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${currentPage - 1}">Previous</a>
                                            </li>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${currentPage + 1}">Next</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                                
                                <c:if test="${empty pagedDetails}">
                                    <div class="text-center py-4">
                                        <i class="fas fa-inbox fa-2x text-muted mb-3"></i>
                                        <p class="text-muted">No details found for this purchase order.</p>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        
                        <!-- Action Buttons -->
                        <div class="mt-4 d-flex gap-2">
                            <c:if test="${hasHandleRequestPermission && purchaseOrder.status == 'pending'}">
                                <button type="button" class="btn btn-success" onclick="updateStatus('${purchaseOrder.poId}', 'approved')">
                                    <i class="fas fa-check me-2"></i>Approve
                                </button>
                                <button type="button" class="btn btn-danger" onclick="updateStatus('${purchaseOrder.poId}', 'rejected')">
                                    <i class="fas fa-times me-2"></i>Reject
                                </button>
                                <a href="PurchaseOrderList" class="btn btn-secondary">Cancel</a>
                            </c:if>
                            <c:if test="${!(hasHandleRequestPermission && purchaseOrder.status == 'pending')}">
                                <a href="PurchaseOrderList" class="btn btn-secondary">Cancel</a>
                            </c:if>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty purchaseOrder}">
                        <div class="text-center py-5">
                            <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                            <h4 class="text-warning">Purchase Order Not Found</h4>
                            <p class="text-muted">The requested purchase order could not be found.</p>
                            <a href="PurchaseOrderList" class="btn btn-back">
                                <i class="fas fa-arrow-left me-2"></i>Back to List
                            </a>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </main>
    </div>
</div>

<!-- Status Update Modal -->
<div class="modal fade" id="statusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Update Purchase Order Status</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="statusForm" method="POST" action="PurchaseOrderList">
                <div class="modal-body">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="poId" id="poId">
                    
                    <div class="mb-3">
                        <label for="status" class="form-label">New Status</label>
                        <select class="form-select" name="status" id="status" required onchange="onStatusChange()">
                            <option value="">Select Status</option>
                            <option value="approved">Approved</option>
                            <option value="rejected">Rejected</option>
                            <option value="sent_to_supplier">Sent to Supplier</option>
                        </select>
                    </div>
                    
                    <div class="mb-3" id="approvalReasonDiv" style="display: none;">
                        <label for="approvalReason" class="form-label">Approval Reason</label>
                        <textarea class="form-control" name="approvalReason" id="approvalReason" rows="3" placeholder="Enter approval reason..."></textarea>
                    </div>
                    
                    <div class="mb-3" id="rejectionReasonDiv" style="display: none;">
                        <label for="rejectionReason" class="form-label">Rejection Reason</label>
                        <textarea class="form-control" name="rejectionReason" id="rejectionReason" rows="3" placeholder="Enter rejection reason..." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Status</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function updateStatus(poId, status) {
        document.getElementById('poId').value = poId;
        document.getElementById('status').value = status;
        onStatusChange();
        new bootstrap.Modal(document.getElementById('statusModal')).show();
    }
    function onStatusChange() {
        const status = document.getElementById('status').value;
        const approvalReasonDiv = document.getElementById('approvalReasonDiv');
        const rejectionReasonDiv = document.getElementById('rejectionReasonDiv');
        const rejectionReason = document.getElementById('rejectionReason');
        if (status === 'approved') {
            approvalReasonDiv.style.display = 'block';
            rejectionReasonDiv.style.display = 'none';
            rejectionReason.removeAttribute('required');
        } else if (status === 'rejected') {
            approvalReasonDiv.style.display = 'none';
            rejectionReasonDiv.style.display = 'block';
            rejectionReason.setAttribute('required', 'required');
        } else {
            approvalReasonDiv.style.display = 'none';
            rejectionReasonDiv.style.display = 'none';
            rejectionReason.removeAttribute('required');
        }
    }
</script>
</body>
</html> 