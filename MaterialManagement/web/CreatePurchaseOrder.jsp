<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Waggy - Create Purchase Order</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- Bootstrap & Fonts -->
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
<jsp:include page="Header.jsp"/>

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

                <c:if test="${!hasCreatePurchaseOrderPermission}">
                    <div class="alert alert-danger">You do not have permission to create purchase orders.</div>
                    <jsp:include page="HomePage.jsp"/>
                </c:if>
                <c:if test="${hasCreatePurchaseOrderPermission}">

                <!-- Form filter chọn Purchase Request (GET) -->
                <form action="CreatePurchaseOrder" method="get" class="filter-bar align-items-center mb-3" style="gap: 8px; flex-wrap:nowrap;">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="poCode" class="form-label text-muted">Purchase Order Code</label>
                            <input type="text" class="form-control" id="poCode" name="poCode" value="${poCode}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label for="purchaseRequestId" class="form-label text-muted">Purchase Request</label>
                            <select class="form-select" id="purchaseRequestId" name="purchaseRequestId" required onchange="this.form.submit()">
                                <option value="">Select Purchase Request</option>
                                <c:forEach var="purchaseRequest" items="${purchaseRequests}">
                                    <option value="${purchaseRequest.purchaseRequestId}" ${selectedPurchaseRequestId == purchaseRequest.purchaseRequestId ? 'selected' : ''}>
                                        ${purchaseRequest.requestCode} - ${purchaseRequest.reason}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </form>

                <!-- Form tạo Purchase Order (POST) -->
                <form action="CreatePurchaseOrder" method="post">
                    <input type="hidden" name="poCode" value="${poCode}">
                    <input type="hidden" name="purchaseRequestId" value="${selectedPurchaseRequestId}">
                    <div class="col-12 mb-3">
                        <label for="note" class="form-label text-muted">Note</label>
                        <textarea class="form-control" id="note" name="note" rows="3" placeholder="Enter any additional notes...">${note}</textarea>
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
                                        <c:forEach var="mat" items="${purchaseRequestDetailList}">
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
                                                    <input type="number" class="form-control unit-price-input" name="unitPrices[]" min="0" step="0.01" required>
                                                </td>
                                                <td>
                                                    <select class="form-select" name="suppliers[]" required>
                                                        <option value="">Select Supplier</option>
                                                        <c:forEach var="supplier" items="${suppliers}">
                                                            <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                                        </c:forEach>
                                                    </select>
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
                </c:if>
            </div>
        </div>
    </div>
</section>

<!-- Thêm thẻ ẩn chứa option supplier render bằng JSTL -->
<div id="supplierOptions" style="display:none">
    <option value="">Select Supplier</option>
    <c:forEach var="supplier" items="${suppliers}">
        <option value="${supplier.supplierId}">${supplier.supplierName}</option>
    </c:forEach>
</div>

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
        document.getElementById('grandTotal').textContent = '$' + grandTotal.toFixed(2);
    }
    document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.unit-price-input').forEach(input => {
            input.addEventListener('input', function() {
                calculateRowTotal(this.closest('tr'));
            });
        });
    });
    
</script>
</body>
</html> 