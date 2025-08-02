<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Purchase Order</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        .purchase-order-form .form-control, .purchase-order-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .purchase-order-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .purchase-order-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .purchase-order-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
        .total-amount {
            font-size: 1.2rem;
            font-weight: bold;
            color: #dc3545;
        }
    </style>
</head>
<body>
    <c:set var="roleId" value="${sessionScope.user.roleId}" />
    <c:set var="hasCreatePurchaseOrderPermission" value="${rolePermissionDAO.hasPermission(roleId, 'CREATE_PURCHASE_ORDER')}" scope="request" />

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
            <c:if test="${!hasCreatePurchaseOrderPermission}">
                <div class="access-denied">
                    <div class="access-denied-card">
                        <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                        <h2 class="text-danger mb-3">Access Denied</h2>
                        <p class="text-muted mb-4">You do not have permission to create purchase orders.</p>
                        <div class="d-grid gap-2">
                            <a href="PurchaseOrderList" class="btn btn-primary">Back to Purchase Orders</a>
                            <a href="dashboardmaterial" class="btn btn-outline-secondary">Back to Dashboard</a>
                        </div>
                    </div>
                </c:if>
                <c:if test="${hasCreatePurchaseOrderPermission}">
                    <jsp:include page="Header.jsp"/>
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-3 col-lg-2 bg-light p-0">
                                <jsp:include page="SidebarEmployee.jsp" />
                            </div>
                            <div class="col-md-9 col-lg-10">
                                <section id="create-purchase-order" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                                    <div class="container">
                                        <div class="row my-5 py-5">
                                            <div class="col-12 bg-white p-4 rounded shadow purchase-order-form">
                                                <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Purchase Order</span></h2>
                                                
                                                <c:if test="${not empty error}">
                                                    <div class="alert alert-danger">${error}</div>
                                                </c:if>
                                                <c:if test="${not empty success}">
                                                    <div class="alert alert-success text-center" style="font-size:1.1rem; font-weight:600;">${success}</div>
                                                </c:if>
                                                <c:if test="${not empty errors}">
                                                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                                                        <ul style="margin-bottom: 0;">
                                                            <c:forEach var="error" items="${errors}">
                                                                <li>${error.value}</li>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                </c:if>
                                                
                                              
                                                <!-- Form filter chọn Purchase Request (GET) -->
                                                <form action="CreatePurchaseOrder" method="get" class="filter-bar align-items-center mb-3" style="gap: 8px; flex-wrap:nowrap;">
                                                    <div class="row g-3">
                                                        <div class="col-md-6">
                                                            <label for="poCode" class="form-label text-muted">Purchase Order Code</label>
                                                            <input type="text" class="form-control" id="poCode" name="poCode" value="${submittedPoCode != null ? submittedPoCode : poCode}" readonly>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <label for="purchaseRequestId" class="form-label text-muted">Purchase Request</label>
                                                            <select class="form-select" id="purchaseRequestId" name="purchaseRequestId" onchange="this.form.submit()">
                                                                <option value="">Select Purchase Request</option>
                                                                <c:forEach var="purchaseRequest" items="${purchaseRequests}">
                                                                    <option value="${purchaseRequest.purchaseRequestId}" ${(submittedPurchaseRequestId != null ? submittedPurchaseRequestId : selectedPurchaseRequestId) == purchaseRequest.purchaseRequestId ? 'selected' : ''}>
                                                                        ${purchaseRequest.requestCode} - ${purchaseRequest.reason}
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errors.purchaseRequestId}">
                                                                <div class="text-danger small mt-1">${errors.purchaseRequestId}</div>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </form>

                                                <!-- Form tạo Purchase Order (POST) -->
                                                <form action="CreatePurchaseOrder" method="post">
                                                    <input type="hidden" name="poCode" value="${poCode}">
                                                    <input type="hidden" name="purchaseRequestId" value="${selectedPurchaseRequestId}">
                                                    <div class="col-12 mb-3">
                                                        <label for="note" class="form-label text-muted">Note</label>
                                                        <textarea class="form-control" id="note" name="note" rows="3" placeholder="Enter any additional notes...">${submittedNote != null ? submittedNote : note}</textarea>
                                                    </div>

                                                    <h3 class="fw-normal mt-5 mb-3">Materials</h3>
                                                    <div id="materialList">
                                                        <c:if test="${not empty purchaseRequestDetailList}">
                                                            <div class="table-responsive">
                                                                <table class="table table-bordered">
                                                                    <thead>
                                                                        <tr>
                                                                            <th>Material</th>
                                                                            <th>Image</th>
                                                                            <th>Quantity</th>
                                                                            <th>Unit Price</th>
                                                                            <th>Supplier</th>
                                                                            <th>Total</th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        <c:forEach var="mat" items="${purchaseRequestDetailList}" varStatus="matStatus">
                                                                            <tr>
                                                                                <td>
                                                                                    <input type="text" class="form-control" name="materialNames[]" value="${mat.materialName}" readonly>
                                                                                    <input type="hidden" name="materials[]" value="${mat.materialId}">
                                                                                </td>
                                                                                <td>
                                                                                    <img src="images/material/${not empty materialImages[mat.materialId] ? materialImages[mat.materialId] : 'default.jpg'}" alt="${mat.materialName}" style="width: 80px; height: auto; object-fit: cover;">
                                                                                </td>
                                                                                <td>
                                                                                    <input type="number" class="form-control quantity-input" name="quantities[]" value="${mat.quantity}" readonly>
                                                                                </td>
                                                                                <td>
                                                                                    <input type="number" class="form-control unit-price-input" name="unitPrices[]" min="0" step="0.01" 
                                                                                           value="${submittedUnitPrices != null && submittedUnitPrices[matStatus.index] != null ? submittedUnitPrices[matStatus.index] : ''}">
                                                                                    <c:set var="unitPriceKey" value="unitPrice_${matStatus.index}" />
                                                                                    <c:if test="${not empty errors[unitPriceKey]}">
                                                                                        <div class="text-danger small mt-1">${errors[unitPriceKey]}</div>
                                                                                    </c:if>
                                                                                    <!-- Debug for unitPrice -->
                                                                                    <div class="text-info small mt-1">
                                                                                        Debug unitPrice_${matStatus.index}: ${not empty errors[unitPriceKey]}
                                                                                    </div>
                                                                                </td>
                                                                                <td>
                                                                                    <select class="form-select" name="suppliers[]">
                                                                                        <option value="">Select Supplier</option>
                                                                                        <c:forEach var="supplier" items="${suppliers}">
                                                                                            <option value="${supplier.supplierId}" 
                                                                                                    ${submittedSuppliers != null && submittedSuppliers[matStatus.index] != null && submittedSuppliers[matStatus.index] == supplier.supplierId ? 'selected' : ''}>
                                                                                                ${supplier.supplierName}
                                                                                            </option>
                                                                                        </c:forEach>
                                                                                    </select>
                                                                                    <c:set var="supplierKey" value="supplier_${matStatus.index}" />
                                                                                    <c:if test="${not empty errors[supplierKey]}">
                                                                                        <div class="text-danger small mt-1">${errors[supplierKey]}</div>
                                                                                    </c:if>
                                                                                    <!-- Debug for supplier -->
                                                                                    <div class="text-info small mt-1">
                                                                                        Debug supplier_${matStatus.index}: ${not empty errors[supplierKey]}
                                                                                    </div>
                                                                                </td>
                                                                                <td>
                                                                                    <input type="text" class="form-control total-price" readonly>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </c:if>
                                                    </div>

                                                    <div class="mt-5 d-grid gap-2">
                                                        <button type="submit" class="btn btn-dark btn-lg rounded-1">Create Purchase Order</button>
                                                        <a href="ListPurchaseRequests" class="btn btn-outline-secondary btn-lg rounded-1">Back to Purchase Request List</a>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </section>
                            </div>
                        </div>
                    </div>
                    <jsp:include page="Footer.jsp" />
                </c:if>
        </c:otherwise>
    </c:choose>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Tính tổng tiền cho từng dòng và tổng cộng
        function calculateRowTotal(row) {
            const quantity = parseFloat(row.querySelector('.quantity-input').value) || 0;
            const unitPrice = parseFloat(row.querySelector('.unit-price-input').value) || 0;
            const totalPrice = quantity * unitPrice;
            row.querySelector('.total-price').value = totalPrice.toFixed(2);
            calculateGrandTotal();
        }
        
        function calculateGrandTotal() {
            let grandTotal = 0;
            document.querySelectorAll('.total-price').forEach(input => {
                grandTotal += parseFloat(input.value) || 0;
            });
            if (document.getElementById('grandTotal')) {
                document.getElementById('grandTotal').textContent = '$' + grandTotal.toFixed(2);
            }
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.unit-price-input').forEach(input => {
                input.addEventListener('input', function() {
                    calculateRowTotal(this.closest('tr'));
                });
            });
            
            // Restore submitted data if there were validation errors
            <c:if test="${not empty submittedUnitPrices}">
                const submittedUnitPrices = [
                    <c:forEach var="unitPrice" items="${submittedUnitPrices}" varStatus="status">
                        "${fn:escapeXml(unitPrice)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedSuppliers = [
                    <c:forEach var="supplier" items="${submittedSuppliers}" varStatus="status">
                        "${fn:escapeXml(supplier)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                
                // Restore data to existing rows
                const rows = document.querySelectorAll('tbody tr');
                for (let i = 0; i < submittedUnitPrices.length && i < rows.length; i++) {
                    const row = rows[i];
                    if (submittedUnitPrices[i]) {
                        row.querySelector('.unit-price-input').value = submittedUnitPrices[i];
                    }
                    if (submittedSuppliers[i]) {
                        row.querySelector('select[name="suppliers[]"]').value = submittedSuppliers[i];
                    }
                    calculateRowTotal(row);
                }
            </c:if>
        });
    </script>
</body>
</html> 