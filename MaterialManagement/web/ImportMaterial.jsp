<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Import Materials</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .card {
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.075);
            margin-bottom: 24px;
        }
        .table th {
            background-color: #f8f9fa;
        }
        .form-label {
            font-weight: 500;
        }
        .alert {
            margin-bottom: 16px;
        }
        .btn-icon {
            margin-right: 8px;
        }
        .material-list {
            max-height: 400px;
            overflow-y: auto;
        }
    </style>
</head>
<body>
    <div class="container-fluid py-4">
        <div class="row">
            <div class="col-12">
                <h2 class="mb-4"><i class="fas fa-box-open me-2"></i>Import Materials</h2>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Debug: Hiển thị số lượng vật tư và nhà cung cấp -->
                <c:if test="${empty materials}">
                    <div class="alert alert-warning">Không có vật tư nào trong hệ thống!</div>
                </c:if>
                <c:if test="${empty suppliers}">
                    <div class="alert alert-warning">Không có nhà cung cấp nào trong hệ thống!</div>
                </c:if>

                <!-- Add Material Form -->
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="card-title mb-0"><i class="fas fa-plus-circle me-2"></i>Add Material to Import</h5>
                    </div>
                    <div class="card-body">
                        <form action="ImportMaterial" method="post" class="row g-3">
                            <input type="hidden" name="action" value="add">
                            <div class="col-md-3">
                                <label class="form-label">Material</label>
                                <select name="materialId" class="form-select" required>
                                    <option value="">Select Material</option>
                                    <c:forEach var="material" items="${materials}">
                                        <option value="${material.materialId}">
                                            ${material.materialName} (${material.unit.unitName}) - ${material.category.category_name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Quantity</label>
                                <input type="number" name="quantity" class="form-control" min="1" required onchange="validateQuantity(this)">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Unit Price</label>
                                <input type="number" name="unitPrice" class="form-control" min="0" step="0.01" required onchange="validateUnitPrice(this)">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Condition</label>
                                <select name="materialCondition" class="form-select" required>
                                    <option value="new">New</option>
                                    <option value="used">Used</option>
                                    <option value="refurbished">Refurbished</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Expiry Date</label>
                                <input type="date" name="expiryDate" class="form-control">
                            </div>
                            <div class="col-md-1">
                                <label class="form-label">Damaged</label>
                                <div class="form-check mt-2">
                                    <input type="checkbox" name="isDamaged" class="form-check-input" value="true">
                                </div>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-plus-circle me-2"></i>Add to Import
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Current Import List -->
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5 class="card-title mb-0"><i class="fas fa-list me-2"></i>Current Import List</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty importDetails}">
                            <div class="table-responsive material-list">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Material</th>
                                            <th>Unit</th>
                                            <th>Category</th>
                                            <th>Quantity</th>
                                            <th>Unit Price</th>
                                            <th>Condition</th>
                                            <th>Expiry Date</th>
                                            <th>Stock Available</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="detail" items="${importDetails}">
                                            <tr>
                                                <td>${materialMap[detail.materialId].materialName}</td>
                                                <td>${materialMap[detail.materialId].unit.name}</td>
                                                <td>${materialMap[detail.materialId].category.categoryName}</td>
                                                <td>
                                                    <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                        <input type="hidden" name="action" value="updateQuantity">
                                                        <input type="hidden" name="materialId" value="${detail.materialId}">
                                                        <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                        <input type="number" name="quantity" value="${detail.quantity}" class="form-control form-control-sm" style="width: 80px" min="1" required onchange="validateQuantity(this)">
                                                        <button type="submit" class="btn btn-sm btn-outline-primary ms-2">
                                                            <i class="fas fa-sync-alt"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                                <td>${detail.unitPrice}</td>
                                                <td>${detail.materialCondition}</td>
                                                <td>${detail.expiryDate}</td>
                                                <td>${stockMap[detail.materialId]}</td>
                                                <td>
                                                    <form action="ImportMaterial" method="post" class="d-inline">
                                                        <input type="hidden" name="action" value="remove">
                                                        <input type="hidden" name="materialId" value="${detail.materialId}">
                                                        <input type="hidden" name="quantity" value="${detail.quantity}">
                                                        <input type="hidden" name="materialCondition" value="${detail.materialCondition}">
                                                        <button type="submit" class="btn btn-sm btn-danger">
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
                    </div>
                </div>

                <!-- Confirm Import Form -->
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="card-title mb-0"><i class="fas fa-check-circle me-2"></i>Confirm Import</h5>
                    </div>
                    <div class="card-body">
                        <form action="ImportMaterial" method="post" class="row g-3">
                            <input type="hidden" name="action" value="import">
                            <div class="col-md-4">
                                <label class="form-label">Supplier</label>
                                <select name="supplierId" class="form-select" required>
                                    <option value="">Select Supplier</option>
                                    <c:forEach var="supplier" items="${suppliers}">
                                        <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Destination</label>
                                <input type="text" name="destination" class="form-control" placeholder="Enter destination" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Batch Number</label>
                                <input type="text" name="batchNumber" class="form-control" maxlength="50" placeholder="Enter batch number" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Actual Arrival</label>
                                <input type="datetime-local" name="actualArrival" class="form-control" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Is Damaged</label>
                                <div class="form-check mt-2">
                                    <input type="checkbox" name="isDamaged" class="form-check-input" value="true">
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Note</label>
                                <textarea name="note" class="form-control" rows="1" placeholder="Enter note"></textarea>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-success">
                                    <i class="fas fa-check-circle me-2"></i>Confirm Import
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function validateQuantity(input) {
            if (input.value <= 0) {
                alert("Quantity must be greater than 0.");
                input.value = 1;
            }
        }
        
        function validateUnitPrice(input) {
            if (input.value < 0) {
                alert("Unit price cannot be negative.");
                input.value = 0;
            }
        }
    </script>
</body>
</html>