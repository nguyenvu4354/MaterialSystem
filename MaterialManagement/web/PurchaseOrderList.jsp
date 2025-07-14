<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Purchase Order Management</title>
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
            background-color: #DEAD6F;
            color: #fff;
            border: none;
            padding: 6px 14px;
            border-radius: 6px;
            font-weight: 500;
            height:50px;
            transition: background 0.2s;
        }
        .btn-export:hover, .btn-print:hover {
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
        .btn-detail {
            background-color: #fff7e6;
            color: #b8860b;
            border: 1px solid #ffe58f;
            border-radius: 6px;
            padding: 6px 14px;
            font-weight: 500;
        }
        .pagination .page-item.active .page-link {
            background-color: #0d6efd;
            border-color: #0d6efd;
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
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h2 class="fw-bold display-6 border-bottom pb-2 m-0" style="color: #DEAD6F;"><i class="fas fa-file-invoice"></i> Purchase Order List</h2>
                    <div class="d-flex gap-2">
                        <!-- Đã xóa nút Export to Excel và Print List -->
                    </div>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <c:if test="${!hasViewPurchaseOrderListPermission}">
                    <div class="alert alert-danger">You do not have permission to view purchase order list.</div>
                    <jsp:include page="HomePage.jsp"/>
                </c:if>
                <c:if test="${hasViewPurchaseOrderListPermission}">
                    <form class="filter-bar align-items-center" method="GET" action="${pageContext.request.contextPath}/PurchaseOrderList" style="gap: 8px; flex-wrap:nowrap;">
                        <input type="text" class="form-control" name="poCode" value="${poCode}" placeholder="Search by PO code" style="width:230px;">
                        <select class="form-select" name="status" style="max-width:150px;" onchange="this.form.submit()">
                            <option value="">All Statuses</option>
      
                            <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                            <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                            <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                            <option value="sent_to_supplier" ${status == 'sent_to_supplier' ? 'selected' : ''}>Sent to Supplier</option>
                            <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                        </select>
                        <input type="date" class="form-control" name="startDate" value="${startDate}" placeholder="Start Date" style="width:200px;">
                        <input type="date" class="form-control" name="endDate" value="${endDate}" placeholder="End Date" style="width:200px;">
                        <button type="submit" class="btn" style="background-color: #DEAD6F; border-color: #DEAD6F; color:white;">Filter</button>
                         <a href="${pageContext.request.contextPath}/PurchaseOrderList" class="btn btn-secondary" style="width: 75px; height: 50px;">Clear</a>
                    </form>
                    
                    <c:if test="${not empty purchaseOrders}">
                        <div class="table-responsive" id="printTableListArea">
                            <table id="purchaseOrderTable" class="table custom-table">
                                <thead>
                                <tr>
                                    <th>PO Code</th>
                                    <th>Request Code</th>
                                    <th>Created Date</th>
                                    <th>Status</th>
                                    <th>Created By</th>
                                    <th>Total Amount</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="po" items="${purchaseOrders}">
                                    <tr>
                                        <td><strong>${po.poCode}</strong></td>
                                        <td>${po.purchaseRequestCode}</td>
                                        <td>${po.createdAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${po.status == 'approved'}">
                                                    <span class="status-badge status-approved">Approved</span>
                                                </c:when>
                                                <c:when test="${po.status == 'rejected'}">
                                                    <span class="status-badge status-rejected">Rejected</span>
                                                </c:when>
                                                <c:when test="${po.status == 'sent_to_supplier'}">
                                                    <span class="status-badge status-sent_to_supplier">Sent to Supplier</span>
                                                </c:when>
                                                <c:when test="${po.status == 'cancel' || po.status == 'cancelled'}">
                                                    <span class="status-badge status-cancel">Cancelled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-pending">Pending</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty po.createdByName}">
                                                    ${po.createdByName}
                                                </c:when>
                                                <c:otherwise>
                                                    Unknown
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <strong>$<fmt:formatNumber value="${po.totalAmount}" type="number" minFractionDigits="2"/></strong>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-1">
                                                <a href="PurchaseOrderDetail?id=${po.poId}" class="btn btn-detail btn-sm" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${hasSendToSupplierPermission && po.status == 'approved'}">
                                                    <button type="button" class="btn btn-detail btn-sm" style="background-color: #0d6efd; color: #fff; border: none;" title="Send to Supplier" onclick="updateStatus('${po.poId}', 'sent_to_supplier')">
                                                        <i class="fas fa-paper-plane"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Pagination -->
                       
                            <nav aria-label="Purchase Order pagination">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="PurchaseOrderList?page=${currentPage - 1}&status=${status}&poCode=${poCode}&startDate=${startDate}&endDate=${endDate}">
                                            Previous
                                        </a>
                                    </li>
                                    
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="PurchaseOrderList?page=${i}&status=${status}&poCode=${poCode}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="PurchaseOrderList?page=${currentPage + 1}&status=${status}&poCode=${poCode}&startDate=${startDate}&endDate=${endDate}">
                                            Next
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                       
                    </c:if>
                    
                    <c:if test="${empty purchaseOrders}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Purchase Orders Found</h4>
                            <p class="text-muted">No purchase orders match your current filters.</p>
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
                        <select class="form-select" name="status" id="status" required>
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

<form id="sendToSupplierForm" method="POST" action="PurchaseOrderList" style="display:none;">
    <input type="hidden" name="action" value="updateStatus">
    <input type="hidden" name="poId" id="sendToSupplierPoId">
    <input type="hidden" name="status" value="sent_to_supplier">
</form>
 <jsp:include page="Footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function updateStatus(poId, status) {
        if (status === 'sent_to_supplier') {
            if (confirm('Are you sure you want to send this purchase order to the supplier?')) {
                document.getElementById('sendToSupplierPoId').value = poId;
                document.getElementById('sendToSupplierForm').submit();
            }
            return;
        }
        document.getElementById('poId').value = poId;
        document.getElementById('status').value = status;
        
        // Show/hide reason fields based on status
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
        
        new bootstrap.Modal(document.getElementById('statusModal')).show();
    }
    
    // Đã xóa hàm exportTableToExcel và printTableList ở cuối file
</script>
</body>
</html> 