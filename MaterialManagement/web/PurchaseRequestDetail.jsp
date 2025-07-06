<%-- 
    Document   : PurchaseRequestDetail
    Created on : Jun 10, 2025, 7:21:25 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Purchase Request Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            body {
                font-family: 'Segoe UI', Arial, sans-serif;
                background-color: #faf4ee;
            }
            .container-main {
                max-width: 900px;
                margin: 40px auto;
                background: #fff;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }
            h2 {
                font-size: 2rem;
                font-weight: bold;
                margin-bottom: 1rem;
                color: #DEAD6F;
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
            .card-header {
                background-color: #DEAD6F;
                color: #fff;
                font-weight: 600;
                border-radius: 12px 12px 0 0;
                padding: 1rem 1.5rem;
            }
            .card {
                border: none;
                margin-bottom: 20px;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            .table {
                margin-top: 20px;
            }
            .btn-action {
                margin-right: 10px;
                background-color: #DEAD6F;
                border: none;
                color: #fff;
                font-weight: 500;
                border-radius: 8px;
                padding: 10px 24px;
                font-size: 1rem;
            }
            .btn-action:hover {
                background-color: #cfa856;
                color: #fff;
            }
            .alert {
                margin-top: 20px;
            }
            .total-amount {
                font-size: 1.2em;
                font-weight: bold;
                color: #dc3545;
            }
            .action-buttons {
                margin: 2rem 0;
                text-align: center;
            }
            .status-message {
                padding: 15px 25px;
                border-radius: 8px;
                margin: 2rem 0;
                text-align: center;
                font-weight: 500;
            }
            .status-approved {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            .status-rejected {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            .btn-back {
                background: #212529;
                border: none;
                color: #fff;
                padding: 0.75rem 2rem;
                border-radius: 8px;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                transition: all 0.2s;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            .btn-back:hover {
                background: #e6b800;
                color: #212529;
                text-decoration: none;
            }
            @media (max-width: 768px) {
                .container-main {
                    padding: 12px;
                }
                h2 {
                    font-size: 1.3rem;
                }
            }
            .btn-action-custom {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                font-size: 1.1rem;
                font-weight: 600;
                padding: 12px 32px;
                border-radius: 16px;
                border: none;
                margin-right: 18px;
                margin-bottom: 8px;
                transition: all 0.2s;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            .btn-approve {
                background: #219653;
                color: #fff;
            }
            .btn-approve:hover {
                background: #17643a;
                color: #fff;
            }
            .btn-reject {
                background: #eb3d3d;
                color: #fff;
            }
            .btn-reject:hover {
                background: #b71c1c;
                color: #fff;
            }
            .btn-cancel {
                background: #fff3cd;
                color: #b8860b;
                border: 1.5px solid #ffe58f;
            }
            .btn-cancel:hover {
                background: #ffe58f;
                color: #b8860b;
            }
            .btn-request {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                font-size: 1.15rem;
                font-weight: 600;
                padding: 12px 36px;
                border-radius: 18px;
                border: none;
                margin-right: 20px;
                margin-bottom: 8px;
                transition: all 0.2s;
                box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            }
            .btn-request-approve {
                background: #219653;
                color: #fff;
            }
            .btn-request-approve:hover {
                background: #17643a;
                color: #fff;
            }
            .btn-request-reject {
                background: #eb3d3d;
                color: #fff;
            }
            .btn-request-reject:hover {
                background: #b71c1c;
                color: #fff;
            }
            .btn-request-cancel {
                background: #fff3cd;
                color: #b8860b;
                border: 1.5px solid #ffe58f;
            }
            .btn-request-cancel:hover {
                background: #ffe58f;
                color: #b8860b;
            }
            .status-badge {
                display: inline-block;
                background: #6c757d;
                color: #fff;
                font-size: 1.2rem;
                font-weight: 500;
                border-radius: 8px;
                padding: 2px 16px;
                margin-left: 8px;
                vertical-align: middle;
            }
        </style>
    </head>
    <body>
        <c:set var="roleId" value="${sessionScope.user.roleId}" />
        <c:set var="hasViewPurchaseRequestListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_PURCHASE_REQUEST_LIST')}" scope="request" />

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <div class="access-denied">
                    <div class="access-denied-card">
                        <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                        <h2 class="text-danger mb-3">Login Required</h2>
                        <p class="text-muted mb-4">You need to login to access this page.</p>
                        <div class="d-grid gap-2">
                            <a href="Login.jsp" class="btn btn-primary">Login</a>
                            <a href="HomeServlet" class="btn btn-outline-secondary">Back to Home</a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${!hasViewPurchaseRequestListPermission}">
                    <div class="access-denied">
                        <div class="access-denied-card">
                            <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                            <h2 class="text-danger mb-3">Access Denied</h2>
                            <p class="text-muted mb-4">You do not have permission to view purchase request details.</p>
                            <div class="d-grid gap-2">
                                <a href="ListPurchaseRequests" class="btn btn-primary">Back to Purchase Requests</a>
                                <a href="dashboardmaterial" class="btn btn-outline-secondary">Back to Dashboard</a>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${hasViewPurchaseRequestListPermission}">
                    <jsp:include page="Header.jsp"/>
                    <div class="container-main">
                        <div style="display:flex;align-items:center;gap:12px;margin-bottom:4px;">
                            <span style="font-size:2rem;font-weight:600;">${purchaseRequest.requestCode}</span>
                            <span class="status-badge">${purchaseRequest.status}</span>
                        </div>
                        <div style="margin-bottom:18px;color:#555;">Created at: ${purchaseRequest.requestDate}</div>
                        <div class="mb-3">
                            <div class="card-header" style="background:#dead6f;color:#fff;font-weight:600;border-radius:12px 12px 0 0;padding:1rem 1.5rem;">Requester Information</div>
                            <div class="card-body d-flex align-items-center" style="background:#fff;border-radius:0 0 12px 12px;">
                                <div style="flex: 1;">
                                    <p><strong>Name:</strong> ${requester.fullName}</p>
                                    <p><strong>Email:</strong> ${requester.email}</p>
                                    <p><strong>Phone:</strong> ${requester.phoneNumber}</p>
                                    <p><strong>Department:</strong> ${requester.departmentName}</p>
                                    <p><strong>Role:</strong> ${requester.roleName}</p>
                                </div>
                                <div style="flex: 0 0 100px; margin-left: 20px;">
                                    <img src="images/profiles/${not empty requester.userPicture ? requester.userPicture : 'DefaultUser.jpg'}" alt="${requester.fullName}" class="img-fluid rounded-circle" style="width:100px;height:100px;object-fit:cover;">
                                </div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <div class="card-header" style="background:#dead6f;color:#fff;font-weight:600;border-radius:12px 12px 0 0;padding:1rem 1.5rem;">Request Information</div>
                            <div class="card-body" style="background:#fff;border-radius:0 0 12px 12px;">
                                <div style="margin-bottom:12px;color:#555;">Created at: ${purchaseRequest.requestDate}</div>
                                <p><strong>Estimated Price:</strong> ${purchaseRequest.estimatedPrice}</p>
                                <p><strong>Reason:</strong> ${purchaseRequest.reason}</p>
                            </div>
                        </div>
                        <div class="mb-3">
                            <div class="card-header" style="background:#dead6f;color:#fff;font-weight:600;border-radius:12px 12px 0 0;padding:1rem 1.5rem;">Material List</div>
                            <div class="card-body" style="background:#fff;border-radius:0 0 12px 12px;">
                                <div class="table-responsive">
                                    <table class="table custom-table" id="materialTable">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Material Name</th>
                                                <th>Quantity</th>
                                                <th>Notes</th>
                                                <th>Created</th>
                                                <th>Updated</th>
                                            </tr>
                                        </thead>
                                        <tbody id="materialTableBody">
                                            <c:choose>
                                                <c:when test="${empty purchaseRequestDetailList}">
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted">No materials found</td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="item" items="${purchaseRequestDetailList}">
                                                        <tr>
                                                            <td>${item.purchaseRequestDetailId}</td>
                                                            <td>${item.materialName}</td>
                                                            <td>${item.quantity}</td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${not empty item.notes}">
                                                                        ${item.notes}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted fst-italic">No notes</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${item.createdAt}</td>
                                                            <td>${item.updatedAt}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="action-buttons text-start mb-4">
                            <c:if test="${hasHandleRequestPermission && purchaseRequest.status == 'pending'}">
                                <form id="actionForm" action="PurchaseRequestDetail" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="id" value="${purchaseRequest.purchaseRequestId}">
                                    <input type="hidden" id="actionType" name="action" value="">
                                    <div class="mb-2">
                                        <textarea name="reason" id="reasonInput" class="form-control" placeholder="Enter reason..." required></textarea>
                                    </div>
                                    <button type="submit" class="btn btn-success me-2" onclick="return setActionType('approve')">
                                        Approve
                                    </button>
                                    <button type="submit" class="btn btn-danger me-2" onclick="return setActionType('reject')">
                                        Reject
                                    </button>
                                    <button type="submit" class="btn btn-secondary" onclick="return setActionType('cancel')">
                                        Cancel Request
                                    </button>
                                </form>
                                <script>
                                    function setActionType(type) {
                                        document.getElementById('actionType').value = type;
                                        if(type === 'approve') {
                                            return confirm('Are you sure you want to approve this request?');
                                        } else if(type === 'reject') {
                                            return confirm('Are you sure you want to reject this request?');
                                        } else if(type === 'cancel') {
                                            return confirm('Are you sure you want to cancel this request?');
                                        }
                                    }
                                </script>
                            </c:if>
                            <c:choose>
                                <c:when test="${purchaseRequest.status eq 'approved'}">
                                    <div class="status-message status-approved">
                                        Request approved.<br>
                                        <strong>Approval reason:</strong> ${purchaseRequest.approvalReason}
                                    </div>
                                </c:when>
                                <c:when test="${purchaseRequest.status eq 'rejected'}">
                                    <div class="status-message status-rejected">
                                        Request rejected.<br>
                                        <strong>Rejection reason:</strong> ${purchaseRequest.approvalReason}
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="text-center">
                            <a href="ListPurchaseRequests" class="btn-back">
                                <i class="fas fa-arrow-left"></i> Back to List
                            </a>
                        </div>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Auto redirect to login if not logged in
            <c:if test="${empty sessionScope.user}">
                window.location.href = 'Login.jsp';
            </c:if>
        </script>
    </body>
</html>