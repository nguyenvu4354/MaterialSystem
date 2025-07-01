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

                <form action="CreatePurchaseOrder" method="post">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="poCode" class="form-label text-muted">Purchase Order Code</label>
                            <input type="text" class="form-control" id="poCode" name="poCode" value="${poCode}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label for="purchaseRequestId" class="form-label text-muted">Purchase Request</label>
                            <select class="form-select" id="purchaseRequestId" name="purchaseRequestId" required>
                                <option value="">Select Purchase Request</option>
                                <c:forEach var="purchaseRequest" items="${purchaseRequests}">
                                    <option value="${purchaseRequest.purchaseRequestId}">${purchaseRequest.requestCode} - ${purchaseRequest.reason}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-12">
                            <label for="note" class="form-label text-muted">Note</label>
                            <textarea class="form-control" id="note" name="note" rows="3" placeholder="Enter any additional notes...">${note}</textarea>
                        </div>
                    </div>

                    <h3 class="fw-normal mt-5 mb-3">Materials</h3>
                    <div id="materialList">
                        <div class="row material-row align-items-center gy-2">
                            <div class="col-md-3">
                                <label class="form-label text-muted">Material</label>
                                <select class="form-select material-select" name="materials[]" required>
                                    <option value="">Select Material</option>
                                    <c:forEach var="material" items="${materials}">
                                        <option value="${material.materialId}">${material.materialName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Category</label>
                                <input type="text" class="form-control category-name" readonly>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Quantity</label>
                                <input type="number" class="form-control quantity-input" name="quantities[]" min="1" required>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Unit Price</label>
                                <input type="number" class="form-control unit-price-input" name="unitPrices[]" min="0" step="0.01" required>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Supplier</label>
                                <select class="form-select" name="suppliers[]" required>
                                    <option value="">Select Supplier</option>
                                    <c:forEach var="supplier" items="${suppliers}">
                                        <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-1">
                                <label class="form-label text-muted">Total</label>
                                <input type="text" class="form-control total-price" readonly>
                            </div>
                            <div class="col-md-1 d-flex align-items-end">
                                <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                            </div>
                        </div>
                    </div>

                    <div class="mt-3">
                        <button type="button" class="btn btn-outline-secondary" id="addMaterial">Add Material</button>
                    </div>

                    <div class="row mt-4">
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">Purchase Request Details</h5>
                                    <div id="purchaseRequestDetails">
                                        <p class="text-muted">Select a purchase request to view details</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">Order Summary</h5>
                                    <div class="d-flex justify-content-between">
                                        <span>Total Amount:</span>
                                        <span class="total-amount" id="grandTotal">$0.00</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mt-5 d-grid gap-2">
                        <button type="submit" class="btn btn-dark btn-lg rounded-1">Create Purchase Order</button>
                        <a href="purchaseorderlist" class="btn btn-outline-secondary btn-lg rounded-1">Back to Purchase Order List</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Store materials data in a JavaScript variable
    const materialsData = [
        <c:forEach var="material" items="${materials}" varStatus="loop">
            {
                "id": "${material.materialId}",
                "name": "${material.materialName}",
                "price": ${material.price},
                "categoryName": "${material.category.category_name}"
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

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

    function updateMaterialRow(selectElement) {
        const selectedId = selectElement.value;
        const material = materialsData.find(m => m.id === selectedId);
        const row = selectElement.closest('.material-row');
        const categoryInput = row.querySelector('.category-name');
        const unitPriceInput = row.querySelector('.unit-price-input');
        
        if (material) {
            categoryInput.value = material.categoryName;
            unitPriceInput.value = material.price;
        } else {
            categoryInput.value = '';
            unitPriceInput.value = '';
        }
        
        calculateRowTotal(row);
    }

    document.getElementById('addMaterial').addEventListener('click', function () {
        const materialList = document.getElementById('materialList');
        const firstRow = materialList.querySelector('.material-row');
        const newRow = firstRow.cloneNode(true);
        
        // Reset fields in the new row
        newRow.querySelector('select[name="materials[]"]').value = '';
        newRow.querySelector('input[name="quantities[]"]').value = '';
        newRow.querySelector('input[name="unitPrices[]"]').value = '';
        newRow.querySelector('select[name="suppliers[]"]').value = '';
        newRow.querySelector('.category-name').value = '';
        newRow.querySelector('.total-price').value = '';
        
        materialList.appendChild(newRow);
    });

    document.addEventListener('change', function(e) {
        if (e.target.classList.contains('material-select')) {
            updateMaterialRow(e.target);
        }
    });
    
    document.addEventListener('input', function(e) {
        if(e.target.classList.contains('quantity-input') || e.target.classList.contains('unit-price-input')) {
            calculateRowTotal(e.target.closest('.material-row'));
        }
    });

    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('remove-material')) {
            const materialRows = document.querySelectorAll('.material-row');
            if (materialRows.length > 1) {
                e.target.closest('.material-row').remove();
                calculateGrandTotal();
            }
        }
    });

    // Initial setup
    document.addEventListener('DOMContentLoaded', function() {
        calculateGrandTotal();
    });
</script>
</body>
</html> 