<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Import Material</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <style>
        .material-form {
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.07);
            background: white;
        }
        .material-form .form-control, .material-form .form-select {
            height: 48px;
            font-size: 18px;
            color: black;
            background: white;
            border-radius: 8px;
            border: 1px solid #bdbdbd;
        }
        .material-form .form-label {
            font-size: 16px;
            margin-bottom: 6px;
            font-weight: 500;
            color: black;
        }
        .material-form .btn {
            font-size: 16px;
            padding: 12px;
        }
        .material-form .material-row {
            margin-bottom: 16px;
            border-bottom: 1px solid gray;
            padding-bottom: 16px;
        }
        .material-form .img-thumbnail {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #bdbdbd;
        }
        .material-form .status-badge {
            padding: 10px 22px;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            background: lightgray;
            color: black;
            border: 1px solid #bdbdbd;
        }
        .material-form .status-new {
            background-color: #198754;
            color: #fff;
        }
        .material-form .status-used {
            background-color: #ffc107;
            color: #212529;
        }
        .material-form .status-refurbished {
            background-color: #0dcaf0;
            color: #fff;
        }
        .material-form .badge.bg-secondary, .material-form .badge.bg-info, .material-form .badge.bg-success {
            font-size: 16px;
            padding: 10px 22px;
            font-weight: 600;
            border-radius: 12px;
            border: 1px solid #bdbdbd;
            background: lightgray !important;
            color: black !important;
        }
        .material-form .badge.bg-info {
            background-color: #0dcaf0 !important;
            color: #fff !important;
        }
        .material-form .badge.bg-secondary {
            background-color: #6c757d !important;
            color: #fff !important;
        }
        .material-form .badge.bg-success {
            background-color: #198754 !important;
            color: #fff !important;
        }
        .material-form .table {
            background: white;
            color: black;
            border: 1px solid #bdbdbd;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 1px 8px rgba(0,0,0,0.04);
        }
        .material-form .table th, .material-form .table td {
            color: black;
            font-size: 16px;
            font-weight: 400;
            border: 1px solid #bdbdbd;
            padding: 10px 12px;
            background: white;
        }
        .material-form .table th {
            font-weight: 700;
            background: lightgray;
            color: black;
            border-top: none;
        }
        .material-form .card, .material-form .card-header, .material-form .card-body {
            border-radius: 12px !important;
            background: white;
            border: none;
        }
        .material-form .card-header {
            background: lightgray !important;
            color: black;
            font-weight: 700;
            border-bottom: 1px solid #bdbdbd;
        }
        .material-form .alert {
            border-radius: 8px;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    
<section id="import-material" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
    <div class="container">
        <div class="row my-5 py-5">
            <div class="col-12 bg-white p-4 rounded shadow material-form">
                <h2 class="display-4 fw-normal text-center mb-4">
                    <i class="fas fa-box-open me-2"></i>Import <span class="text-primary">Material</span>
                </h2>
                <!-- Alert Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <!-- Add Material Form -->
                <h3 class="fw-normal mb-3">Add Material to Import List</h3>
                <form id="addMaterialForm" action="ImportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                    <input type="hidden" name="action" value="add">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label text-muted">Material</label>
                            <select name="materialId" class="form-select" required>
                                <option value="">Select Material</option>
                                <c:forEach var="material" items="${materials}">
                                    <option value="${material.materialId}">${material.materialName} (${material.materialCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label text-muted">Condition</label>
                            <select name="materialCondition" class="form-select" required>
                                <option value="">Select</option>
                                <option value="new">New</option>
                                <option value="used">Used</option>
                                <option value="refurbished">Refurbished</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label text-muted">Quantity</label>
                            <input type="number" name="quantity" class="form-control" min="1" required placeholder="Qty">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label text-muted">Unit Price ($)</label>
                            <input type="number" name="unitPrice" class="form-control" min="0" step="0.01" required placeholder="Unit Price ($)">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label text-muted">Total Value</label>
                            <input type="text" id="totalValue" class="form-control" readonly placeholder="Auto">
                        </div>
                        <div class="col-12 mt-4">
                            <button type="submit" class="btn btn-dark btn-lg rounded-1">
                                <i class="fas fa-plus-circle me-2"></i>Add to Import List
                            </button>
                        </div>
                    </div>
                </form>

                <!-- Current Import List -->
                <h3 class="fw-normal mb-3">Current Import List</h3>
                <c:if test="${not empty importDetails}">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Material</th>
                                    <th>Unit</th>
                                    <th>Category</th>
                                    <th>Quantity</th>
                                    <th>Price</th>
                                    <th>Value</th>
                                    <th>Condition</th>
                                    <th>Stock</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${importDetails}">
                                    <tr>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${materialMap[detail.materialId].materialsUrl}" alt="${materialMap[detail.materialId].materialName}" class="img-thumbnail">
                                        </td>
                                        <td>
                                            <strong>${materialMap[detail.materialId].materialName}</strong><br>
                                            <small class="text-muted">${materialMap[detail.materialId].materialCode}</small>
                                        </td>
                                        <td><span class="badge bg-secondary">${materialMap[detail.materialId].unit.unitName}</span></td>
                                        <td><span class="badge bg-info">${materialMap[detail.materialId].category.category_name}</span></td>
                                        <td>
                                            <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="materialId" value="${detail.materialId}">
                                                <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                <input type="number" name="newQuantity" value="${detail.quantity}" class="form-control me-2" min="1" required style="width:80px;">
                                                <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Quantity">
                                                    <i class="fas fa-sync-alt"></i>
                                                </button>
                                            </form>
                                        </td>
                                        <td><strong>$<fmt:formatNumber value="${detail.unitPrice}" type="number" minFractionDigits="2"/></strong></td>
                                        <td><strong>$<fmt:formatNumber value="${detail.unitPrice * detail.quantity}" type="number" minFractionDigits="2"/></strong></td>
                                        <td>
                                            <span class="status-badge ${detail.materialCondition == 'new' ? 'status-new' : detail.materialCondition == 'used' ? 'status-used' : 'status-refurbished'}">
                                                ${detail.materialCondition}
                                            </span>
                                        </td>
                                        <td><span class="badge bg-success">${stockMap[detail.materialId]}</span></td>
                                        <td>
                                            <form action="ImportMaterial" method="post">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="materialId" value="${detail.materialId}">
                                                <input type="hidden" name="quantity" value="${detail.quantity}">
                                                <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm">Remove</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty importDetails}">
                    <div class="text-center py-5">
                        <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                        <p class="text-muted">No materials added to the import list yet.</p>
                    </div>
                </c:if>

                <!-- Confirm Import Form -->
                <h3 class="fw-normal mb-3 mt-5">Confirm Import</h3>
                <form id="confirmImportForm" action="ImportMaterial" method="post" onsubmit="return validateConfirmForm()">
                    <input type="hidden" name="action" value="import">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted">Supplier</label>
                            <select name="supplierId" class="form-select" required>
                                <option value="">Select Supplier</option>
                                <c:forEach var="supplier" items="${suppliers}">
                                    <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty formErrors['supplierId']}">
                                <div class="text-danger small mt-1">${formErrors['supplierId']}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted">Destination</label>
                            <input type="text" name="destination" class="form-control" placeholder="e.g., Warehouse A, Building 1" required>
                            <c:if test="${not empty formErrors['destination']}">
                                <div class="text-danger small mt-1">${formErrors['destination']}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted">Batch Number</label>
                            <input type="text" name="batchNumber" class="form-control" maxlength="50" required>
                            <c:if test="${not empty formErrors['batchNumber']}">
                                <div class="text-danger small mt-1">${formErrors['batchNumber']}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted">Actual Arrival</label>
                            <input type="datetime-local" name="actualArrival" class="form-control" required>
                            <c:if test="${not empty formErrors['actualArrival']}">
                                <div class="text-danger small mt-1">${formErrors['actualArrival']}</div>
                            </c:if>
                        </div>
                        <div class="col-12">
                            <label class="form-label text-muted">Notes</label>
                            <input type="text" name="note" class="form-control" placeholder="Enter Note">
                            <c:if test="${not empty formErrors['note']}">
                                <div class="text-danger small mt-1">${formErrors['note']}</div>
                            </c:if>
                        </div>
                        <div class="col-12 mt-4">
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Confirm Import</button>
                                <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                            </div>
                        </div>
                    </div>
                </form>
                <!-- After confirm import, show Check Inventory button if needed -->
                <c:if test="${not empty success && param.action == 'import'}">
                    <div class="text-center my-4">
                        <a href="StaticInventory" class="btn btn-success btn-lg">
                            <i class="fas fa-warehouse me-2"></i>Check Inventory
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</section>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                setTimeout(function() {
                    if (alert.style.display !== 'none') {
                        alert.style.transition = 'all 0.5s ease-out';
                        alert.style.opacity = '0';
                        alert.style.transform = 'translateY(-10px)';
                        setTimeout(function() {
                            alert.style.display = 'none';
                        }, 500);
                    }
                }, 3000);
                
                alert.addEventListener('click', function(e) {
                    if (e.target.classList.contains('btn-close') || e.target.classList.contains('alert')) {
                        this.style.transition = 'all 0.3s ease-out';
                        this.style.opacity = '0';
                        this.style.transform = 'translateY(-10px)';
                        setTimeout(() => {
                            this.style.display = 'none';
                        }, 300);
                    }
                });
            });
        });

        function calculateTotal() {
            const quantity = document.querySelector('input[name="quantity"]').value;
            const unitPrice = document.querySelector('input[name="unitPrice"]').value;
            const totalField = document.getElementById('totalValue');
            if (quantity && unitPrice) {
                const total = (parseInt(quantity) * parseFloat(unitPrice)).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
                totalField.value = '$' + total;
            } else {
                totalField.value = '';
            }
        }
        document.querySelector('input[name="quantity"]').addEventListener('input', calculateTotal);
        document.querySelector('input[name="unitPrice"]').addEventListener('input', calculateTotal);
        function validateAddForm() {
            const form = document.getElementById('addMaterialForm');
            let isValid = true;
            const materialId = form.querySelector('select[name="materialId"]');
            const quantity = form.querySelector('input[name="quantity"]');
            const unitPrice = form.querySelector('input[name="unitPrice"]');
            const condition = form.querySelector('select[name="materialCondition"]');
            if (!materialId.value) { 
                materialId.classList.add('is-invalid'); 
                isValid = false; 
            } else {
                materialId.classList.remove('is-invalid'); 
            }
            if (!quantity.value || quantity.value <= 0) {
                quantity.classList.add('is-invalid'); isValid = false; 
            } else { 
                quantity.classList.remove('is-invalid'); 
            }
            if (!unitPrice.value || unitPrice.value < 0) { 
                unitPrice.classList.add('is-invalid'); 
                isValid = false; 
            } else {
                unitPrice.classList.remove('is-invalid'); 
            }
            if (!condition.value) { 
                condition.classList.add('is-invalid');
                isValid = false; 
            } else { 
                condition.classList.remove('is-invalid'); 
            }
            return isValid;
        }
        function validateConfirmForm() {
            const form = document.getElementById('confirmImportForm');
            let isValid = true;
            const supplierId = form.querySelector('select[name="supplierId"]');
            const destination = form.querySelector('input[name="destination"]');
            const batchNumber = form.querySelector('input[name="batchNumber"]');
            const actualArrival = form.querySelector('input[name="actualArrival"]');
            if (!supplierId.value) { 
                supplierId.classList.add('is-invalid'); 
                isValid = false; }
            else {
                supplierId.classList.remove('is-invalid'); 
            }
            if (!destination.value.trim()) {
                destination.classList.add('is-invalid'); 
                isValid = false; 
            } else { 
                destination.classList.remove('is-invalid'); 
            }
            if (!batchNumber.value.trim()) {
                batchNumber.classList.add('is-invalid');
                isValid = false; 
            } else { 
                batchNumber.classList.remove('is-invalid'); 
            }
            if (!actualArrival.value) { 
                actualArrival.classList.add('is-invalid');
                isValid = false;
            } else { 
                actualArrival.classList.remove('is-invalid'); 
            }
            return isValid;
        }
    </script>
</body>
</html>