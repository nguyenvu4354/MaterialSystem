<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Import Materials</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .main-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            margin: 20px;
            padding: 30px;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .card-header {
            border-radius: 15px 15px 0 0 !important;
            border: none;
            padding: 20px 25px;
        }
        .card-body {
            padding: 25px;
        }
        .table {
            border-radius: 10px;
            overflow: hidden;
        }
        .table th {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
            border: none;
            padding: 15px;
            font-weight: 600;
        }
        .table td {
            padding: 12px 15px;
            vertical-align: middle;
        }
        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 12px 15px;
            transition: all 0.3s ease;
            font-size: 14px;
        }
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn {
            border-radius: 10px;
            padding: 12px 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            border: none;
            font-size: 14px;
        }
        .btn-primary {
            background: linear-gradient(45deg, #667eea, #764ba2);
        }
        .btn-success {
            background: linear-gradient(45deg, #28a745, #20c997);
        }
        .btn-danger {
            background: linear-gradient(45deg, #dc3545, #fd7e14);
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        .alert {
            border-radius: 10px;
            border: none;
            padding: 15px 20px;
        }
        .page-title {
            color: #2c3e50;
            font-weight: 700;
            margin-bottom: 30px;
            text-align: center;
        }
        .material-list {
            max-height: 500px;
            overflow-y: auto;
        }
        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
            font-size: 14px;
        }
        .text-danger {
            color: #e74c3c !important;
        }
        .badge {
            border-radius: 8px;
            padding: 8px 12px;
        }
        .quantity-input {
            width: 80px;
            border-radius: 8px;
        }
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }
        .status-new { background-color: #d4edda; color: #155724; }
        .status-used { background-color: #fff3cd; color: #856404; }
        .status-refurbished { background-color: #cce5ff; color: #004085; }
        .form-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .form-section-title {
            font-weight: 600;
            color: #495057;
            margin-bottom: 15px;
            font-size: 16px;
        }
        .required-field {
            color: #e74c3c;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="main-container">
        <div class="row">
            <div class="col-12">
                <h1 class="page-title">
                    <i class="fas fa-box-open me-3"></i>Import Materials Management
                </h1>
                
                <!-- Alert Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle me-2"></i>${success}
                    </div>
                </c:if>
                <c:if test="${empty materials}">
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle me-2"></i>No materials available in the system!
                    </div>
                </c:if>
                <c:if test="${empty suppliers}">
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle me-2"></i>No suppliers available in the system!
                    </div>
                </c:if>

                <!-- Add Material Form -->
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-plus-circle me-2"></i>Add Material to Import List
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="addMaterialForm" action="ImportMaterial" method="post" onsubmit="return validateAddForm()">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="form-section">
                                <div class="form-section-title">
                                    <i class="fas fa-box me-2"></i>Material Information
                                </div>
                                <div class="row g-3">
                                    <div class="col-lg-6 col-md-12">
                                        <label class="form-label">
                                            Material <span class="required-field">*</span>
                                        </label>
                                        <select name="materialId" class="form-select" required>
                                            <option value="">-- Select Material --</option>
                                            <c:forEach var="material" items="${materials}">
                                                <option value="${material.materialId}">
                                                    ${material.materialName} (${material.materialCode})
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Please select a material.</div>
                                    </div>
                                    
                                    <div class="col-lg-6 col-md-12">
                                        <label class="form-label">
                                            Condition <span class="required-field">*</span>
                                        </label>
                                        <select name="materialCondition" class="form-select" required>
                                            <option value="">-- Select Condition --</option>
                                            <option value="new">New - Brand new items</option>
                                            <option value="used">Used - Previously used items</option>
                                            <option value="refurbished">Refurbished - Restored items</option>
                                        </select>
                                        <div class="invalid-feedback">Please select a condition.</div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-section">
                                <div class="form-section-title">
                                    <i class="fas fa-calculator me-2"></i>Quantity & Pricing
                                </div>
                                <div class="row g-3">
                                    <div class="col-lg-4 col-md-6">
                                        <label class="form-label">
                                            Quantity <span class="required-field">*</span>
                                        </label>
                                        <input type="number" name="quantity" class="form-control" min="1" required placeholder="Enter quantity">
                                        <div class="invalid-feedback">Quantity must be greater than 0.</div>
                                    </div>
                                    
                                    <div class="col-lg-4 col-md-6">
                                        <label class="form-label">
                                            Unit Price (VND) <span class="required-field">*</span>
                                        </label>
                                        <input type="number" name="unitPrice" class="form-control" min="0" step="1000" required placeholder="Enter unit price">
                                        <div class="invalid-feedback">Unit price cannot be negative.</div>
                                    </div>
                                    
                                    <div class="col-lg-4 col-md-12">
                                        <label class="form-label">
                                            Total Value
                                        </label>
                                        <input type="text" id="totalValue" class="form-control" readonly placeholder="Calculated automatically">
                                    </div>
                                </div>
                            </div>
                            
                            <div class="text-center">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="fas fa-plus-circle me-2"></i>Add to Import List
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Current Import List -->
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-list me-2"></i>Current Import List
                            <span class="badge bg-light text-dark ms-2">${importDetails.size()}</span>
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty importDetails}">
                            <div class="table-responsive material-list">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th><i class="fas fa-box me-2"></i>Material</th>
                                            <th><i class="fas fa-ruler me-2"></i>Unit</th>
                                            <th><i class="fas fa-tags me-2"></i>Category</th>
                                            <th><i class="fas fa-sort-numeric-up me-2"></i>Quantity</th>
                                            <th><i class="fas fa-dollar-sign me-2"></i>Unit Price</th>
                                            <th><i class="fas fa-tag me-2"></i>Condition</th>
                                            <th><i class="fas fa-warehouse me-2"></i>Stock</th>
                                            <th><i class="fas fa-cogs me-2"></i>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="detail" items="${importDetails}">
                                            <tr>
                                                <td>
                                                    <strong>${materialMap[detail.materialId].materialName}</strong>
                                                    <br><small class="text-muted">${materialMap[detail.materialId].materialCode}</small>
                                                </td>
                                                <td><span class="badge bg-secondary">${materialMap[detail.materialId].unit.unitName}</span></td>
                                                <td><span class="badge bg-info">${materialMap[detail.materialId].category.categoryName}</span></td>
                                                <td>
                                                    <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                        <input type="hidden" name="action" value="update">
                                                        <input type="hidden" name="materialId" value="${detail.materialId}">
                                                        <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                        <input type="number" name="newQuantity" value="${detail.quantity}" class="form-control quantity-input" min="1" required>
                                                        <button type="submit" class="btn btn-sm btn-outline-primary ms-2" title="Update Quantity">
                                                            <i class="fas fa-sync-alt"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                                <td><strong>${detail.unitPrice}</strong></td>
                                                <td>
                                                    <span class="status-badge status-${detail.materialCondition}">
                                                        ${detail.materialCondition}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="badge bg-success">${stockMap[detail.materialId]}</span>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <form action="ImportMaterial" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="quantity" value="${detail.quantity}">
                                                            <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                            <button type="submit" class="btn btn-sm btn-danger" title="Remove Item">
                                                                <i class="fas fa-trash-alt"></i>
                                                            </button>
                                                        </form>
                                                    </div>
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
                            
                            <div class="form-section">
                                <div class="form-section-title">
                                    <i class="fas fa-truck me-2"></i>Supplier Information
                                </div>
                                <div class="row g-3">
                                    <div class="col-lg-6 col-md-12">
                                        <label class="form-label">
                                            Supplier <span class="required-field">*</span>
                                        </label>
                                        <select name="supplierId" class="form-select" required>
                                            <option value="">-- Select Supplier --</option>
                                            <c:forEach var="supplier" items="${suppliers}">
                                                <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Please select a supplier.</div>
                                    </div>
                                    
                                    <div class="col-lg-6 col-md-12">
                                        <label class="form-label">
                                            Destination <span class="required-field">*</span>
                                        </label>
                                        <input type="text" name="destination" class="form-control" placeholder="e.g., Warehouse A, Building 1" required>
                                        <div class="invalid-feedback">Please enter a destination.</div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-section">
                                <div class="form-section-title">
                                    <i class="fas fa-barcode me-2"></i>Batch & Arrival Information
                                </div>
                                <div class="row g-3">
                                    <div class="col-lg-4 col-md-6">
                                        <label class="form-label">
                                            Batch Number <span class="required-field">*</span>
                                        </label>
                                        <input type="text" name="batchNumber" class="form-control" maxlength="50" placeholder="e.g., BATCH001-2024" required>
                                        <div class="invalid-feedback">Please enter a batch number.</div>
                                    </div>
                                    
                                    <div class="col-lg-4 col-md-6">
                                        <label class="form-label">
                                            Actual Arrival <span class="required-field">*</span>
                                        </label>
                                        <input type="datetime-local" name="actualArrival" class="form-control" required>
                                        <div class="invalid-feedback">Please select an arrival date and time.</div>
                                    </div>
                                    
                                    <div class="col-lg-4 col-md-12">
                                        <label class="form-label">
                                            Import Date
                                        </label>
                                        <input type="text" id="importDate" class="form-control" readonly value="${importDetails.size() > 0 ? 'Will be set automatically' : 'No materials added'}">
                                    </div>
                                </div>
                            </div>

                            <div class="form-section">
                                <div class="form-section-title">
                                    <i class="fas fa-sticky-note me-2"></i>Additional Information
                                </div>
                                <div class="row g-3">
                                    <div class="col-12">
                                        <label class="form-label">
                                            Notes
                                        </label>
                                        <textarea name="note" class="form-control" rows="4" placeholder="Enter any additional notes about this import (optional)"></textarea>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="text-center">
                                <button type="submit" class="btn btn-success btn-lg">
                                    <i class="fas fa-check-circle me-2"></i>Confirm Import
                                </button>
                            </div>
                        </form>
                    </div>
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

        // Add event listeners for calculation
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