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
        body {
            background: gainsboro;
            color: black;
        }
        .import-form {
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.07);
            background: white;
        }
        .import-form .form-control, .import-form .form-select {
            height: 48px;
            font-size: 18px;
            color: black;
            background: white;
            border-radius: 8px;
            border: 1px solid #bdbdbd;
        }
        .import-form .form-label {
            font-size: 16px;
            margin-bottom: 6px;
            font-weight: 500;
            color: black;
        }
        .import-form .btn {
            font-size: 18px;
            padding: 14px 24px;
            color: black;
            background: gainsboro;
            border: 1px solid gray;
            border-radius: 8px;
            font-weight: 600;
            box-shadow: 0 1px 4px rgba(0,0,0,0.04);
        }
        .import-form .btn:hover {
            background: gray;
            color: white;
        }
        .import-form .material-row {
            margin-bottom: 16px;
            border-bottom: 1px solid gray;
            padding-bottom: 16px;
        }
        .img-thumbnail {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #bdbdbd;
        }
        .status-badge {
            padding: 10px 22px;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            background: lightgray;
            color: black;
            border: 1px solid #bdbdbd;
        }
        .status-new {
            background-color: #198754;
            color: #fff;
        }
        .status-used {
            background-color: #ffc107;
            color: #212529;
        }
        .status-refurbished {
            background-color: #0dcaf0;
            color: #fff;
        }
        .badge.bg-secondary, .badge.bg-info, .badge.bg-success {
            font-size: 16px;
            padding: 10px 22px;
            font-weight: 600;
            border-radius: 12px;
            border: 1px solid #bdbdbd;
            background: lightgray !important;
            color: black !important;
        }
        .badge.bg-info {
            background-color: #0dcaf0 !important;
            color: #fff !important;
        }
        .badge.bg-secondary {
            background-color: #6c757d !important;
            color: #fff !important;
        }
        .badge.bg-success {
            background-color: #198754 !important;
            color: #fff !important;
        }
        .table {
            background: white;
            color: black;
            border: 1px solid #bdbdbd;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 1px 8px rgba(0,0,0,0.04);
        }
        .table th, .table td {
            color: black;
            font-size: 16px;
            font-weight: 400;
            border: 1px solid #bdbdbd;
            padding: 10px 12px;
            background: white;
        }
        .table th {
            font-weight: 700;
            background: lightgray;
            color: black;
            border-top: none;
        }
        .card, .card-header, .card-body {
            border-radius: 12px !important;
            background: white;
            border: none;
        }
        .card-header {
            background: lightgray !important;
            color: black;
            font-weight: 700;
            border-bottom: 1px solid #bdbdbd;
        }
        .alert {
            border-radius: 8px;
            font-size: 16px;
        }
    </style>
</head>
<body>
<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-12 col-lg-10">
            <div class="bg-white p-4 rounded shadow import-form">
                <h2 class="display-4 fw-normal text-center mb-4">
                    <i class="fas fa-box-open me-2"></i>Import <span class="text-primary">Material</span>
                </h2>
                <!-- Alert Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><i class="fas fa-exclamation-triangle me-2"></i>${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success"><i class="fas fa-check-circle me-2"></i>${success}</div>
                </c:if>
                <c:if test="${empty materials}">
                    <div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>No materials available in the system!</div>
                </c:if>
                <c:if test="${empty suppliers}">
                    <div class="alert alert-warning"><i class="fas fa-exclamation-triangle me-2"></i>No suppliers available in the system!</div>
                </c:if>

                <!-- Add Material Form -->
                <form id="addMaterialForm" action="ImportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                            <input type="hidden" name="action" value="add">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label class="form-label">Material <span class="text-danger">*</span></label>
                                        <select name="materialId" class="form-select" required>
                                <option value="">Select Material</option>
                                            <c:forEach var="material" items="${materials}">
                                    <option value="${material.materialId}">${material.materialName} (${material.materialCode})</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                        <div class="col-md-2">
                            <label class="form-label">Condition <span class="text-danger">*</span></label>
                                        <select name="materialCondition" class="form-select" required>
                                <option value="">Select</option>
                                <option value="new">New</option>
                                <option value="used">Used</option>
                                <option value="refurbished">Refurbished</option>
                                        </select>
                                    </div>
                        <div class="col-md-2">
                            <label class="form-label">Quantity <span class="text-danger">*</span></label>
                            <input type="number" name="quantity" class="form-control" min="1" required placeholder="Qty">
                                </div>
                        <div class="col-md-2">
                            <label class="form-label">Unit Price (VND) <span class="text-danger">*</span></label>
                            <input type="number" name="unitPrice" class="form-control" min="0" step="1000" required placeholder="Unit Price">
                            </div>
                        <div class="col-md-2">
                            <label class="form-label">Total Value</label>
                            <input type="text" id="totalValue" class="form-control" readonly placeholder="Auto">
                                    </div>
                                </div>
                    <div class="mt-4 text-center">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="fas fa-plus-circle me-2"></i>Add to Import List
                                </button>
                            </div>
                        </form>

                <!-- Current Import List -->
                <div class="card mb-5">
                    <div class="card-header bg-info text-blue">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-list me-2"></i>Current Import List
                            <span class="badge bg-light text-dark ms-2">${importDetails.size()}</span>
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty importDetails}">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle text-center">
                                    <thead class="table-light">
                                    <tr>
                                        <th>Image</th>
                                        <th>Material</th>
                                        <th>Unit</th>
                                        <th>Category</th>
                                        <th>Quantity</th>
                                        <th>Unit Price</th>
                                        <th>Total Value</th>
                                        <th>Condition</th>
                                        <th>Stock</th>
                                        <th>Actions</th>
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
                                                <form action="ImportMaterial" method="post" class="d-flex align-items-center justify-content-center">
                                                        <input type="hidden" name="action" value="update">
                                                        <input type="hidden" name="materialId" value="${detail.materialId}">
                                                        <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                    <input type="number" name="newQuantity" value="${detail.quantity}" class="form-control quantity-input" min="1" required style="width:80px;">
                                                        <button type="submit" class="btn btn-sm btn-outline-primary ms-2" title="Update Quantity">
                                                            <i class="fas fa-sync-alt"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                                <td><strong>${detail.unitPrice}</strong></td>
                                                <td><strong><fmt:formatNumber value="${detail.unitPrice * detail.quantity}" type="currency" currencySymbol="₫"/></strong></td>
                                                <td>
                                                <span class="status-badge ${detail.materialCondition == 'new' ? 'status-new' : detail.materialCondition == 'used' ? 'status-used' : 'status-refurbished'}">
                                                        ${detail.materialCondition}
                                                    </span>
                                                </td>
                                            <td><span class="badge bg-success">${stockMap[detail.materialId]}</span></td>
                                                <td>
                                                        <form action="ImportMaterial" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="quantity" value="${detail.quantity}">
                                                            <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                            <button type="submit" class="btn btn-sm btn-danger" title="Remove Item">
                                                                <i class="fas fa-trash-alt"></i>
                                                            </button>
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
                    </div>
                </div>

                <!-- Confirm Import Form -->
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-check-circle me-2"></i>Confirm Import Details
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="confirmImportForm" action="ImportMaterial" method="post" onsubmit="return validateConfirmForm()">
                            <input type="hidden" name="action" value="import">
                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Supplier <span class="text-danger">*</span></label>
                                        <select name="supplierId" class="form-select" required>
                                        <option value="">Select Supplier</option>
                                            <c:forEach var="supplier" items="${suppliers}">
                                                <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                <div class="col-md-6">
                                    <label class="form-label">Destination <span class="text-danger">*</span></label>
                                        <input type="text" name="destination" class="form-control" placeholder="e.g., Warehouse A, Building 1" required>
                                </div>
                            </div>
                            <div class="row g-3 mb-3">
                                <div class="col-md-4">
                                    <label class="form-label">Batch Number <span class="text-danger">*</span></label>
                                    <input type="text" name="batchNumber" class="form-control" maxlength="50" placeholder="e.g., BATCH001-2024" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Actual Arrival <span class="text-danger">*</span></label>
                                        <input type="datetime-local" name="actualArrival" class="form-control" required>
                                    </div>
                                <div class="col-md-4">
                                    <label class="form-label">Import Date</label>
                                        <input type="text" id="importDate" class="form-control" readonly value="${importDetails.size() > 0 ? 'Will be set automatically' : 'No materials added'}">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Notes</label>
                                <textarea name="note" class="form-control" rows="3" ></textarea>
                            </div>
                            <div class="text-center">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">
                                    <i class="fas fa-check-circle me-2"></i>Confirm Import
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Sau khi confirm import thành công, hiển thị nút Check Inventory -->
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
</div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Calculate total value when quantity or unit price changes
        function calculateTotal() {
            const quantity = document.querySelector('input[name="quantity"]').value;
            const unitPrice = document.querySelector('input[name="unitPrice"]').value;
            const totalField = document.getElementById('totalValue');
            if (quantity && unitPrice) {
                const total = (parseInt(quantity) * parseFloat(unitPrice)).toLocaleString('vi-VN');
                totalField.value = total + ' VND';
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
            if (!materialId.value) { materialId.classList.add('is-invalid'); isValid = false; } else { materialId.classList.remove('is-invalid'); }
            if (!quantity.value || quantity.value <= 0) { quantity.classList.add('is-invalid'); isValid = false; } else { quantity.classList.remove('is-invalid'); }
            if (!unitPrice.value || unitPrice.value < 0) { unitPrice.classList.add('is-invalid'); isValid = false; } else { unitPrice.classList.remove('is-invalid'); }
            if (!condition.value) { condition.classList.add('is-invalid'); isValid = false; } else { condition.classList.remove('is-invalid'); }
            return isValid;
        }
        function validateConfirmForm() {
            const form = document.getElementById('confirmImportForm');
            let isValid = true;
            const supplierId = form.querySelector('select[name="supplierId"]');
            const destination = form.querySelector('input[name="destination"]');
            const batchNumber = form.querySelector('input[name="batchNumber"]');
            const actualArrival = form.querySelector('input[name="actualArrival"]');
            if (!supplierId.value) { supplierId.classList.add('is-invalid'); isValid = false; } else { supplierId.classList.remove('is-invalid'); }
            if (!destination.value.trim()) { destination.classList.add('is-invalid'); isValid = false; } else { destination.classList.remove('is-invalid'); }
            if (!batchNumber.value.trim()) { batchNumber.classList.add('is-invalid'); isValid = false; } else { batchNumber.classList.remove('is-invalid'); }
            if (!actualArrival.value) { actualArrival.classList.add('is-invalid'); isValid = false; } else { actualArrival.classList.remove('is-invalid'); }
            return isValid;
        }
    </script>
</body>
</html>