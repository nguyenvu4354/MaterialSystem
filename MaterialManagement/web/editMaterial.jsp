<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Material - Material Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-select {
            padding: 0.6rem 1rem;
            border-color: #dee2e6;
            cursor: pointer;
        }

        .form-select:focus {
            border-color: #86b7fe;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
        }

        .form-select option {
            padding: 8px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h3 class="mb-0">Edit Material</h3>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success" role="alert">
                        ${param.success}
                    </div>
                </c:if>
                
                <form action="editmaterial" method="POST" class="needs-validation" novalidate>
                    <input type="hidden" name="materialId" value="${details.material.materialId}">
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialCode" class="form-label">Material Code</label>
                            <input type="text" class="form-control" id="materialCode" name="materialCode" 
                                   value="${details.material.materialCode}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="materialName" class="form-label">Material Name</label>
                            <input type="text" class="form-control" id="materialName" name="materialName" 
                                   value="${details.material.materialName}" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="materialsUrl" class="form-label">Image URL</label>
                        <input type="url" class="form-control" id="materialsUrl" name="materialsUrl" 
                               value="${details.material.materialsUrl}">
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="categoryId" class="form-label">Category</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">Select Category</option>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.category_id}" ${details.material.categoryId == category.category_id ? 'selected' : ''}>
                                        ${category.category_name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="quantity" class="form-label">Quantity</label>
                            <input type="number" class="form-control" id="quantity" name="quantity" 
                                   value="${details.material.quantity}" min="0" required>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="price" class="form-label">Price (VND)</label>
                            <input type="number" class="form-control" id="price" name="price" 
                                   value="${details.material.price}" min="0" step="1000" required>
                        </div>
                        <div class="col-md-6">
                            <label for="conditionPercentage" class="form-label">Condition (%)</label>
                            <input type="number" class="form-control" id="conditionPercentage" name="conditionPercentage" 
                                   value="${details.material.conditionPercentage}" min="0" max="100" required>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialStatus" class="form-label">Status</label>
                            <select class="form-select" id="materialStatus" name="materialStatus" required>
                                <option value="NEW" ${details.material.materialStatus == 'NEW' ? 'selected' : ''}>New</option>
                                <option value="USED" ${details.material.materialStatus == 'USED' ? 'selected' : ''}>Used</option>
                                <option value="DAMAGED" ${details.material.materialStatus == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="disable" class="form-label">Active Status</label>
                            <select class="form-select" id="disable" name="disable" required>
                                <option value="false" ${!details.material.disable ? 'selected' : ''}>Active</option>
                                <option value="true" ${details.material.disable ? 'selected' : ''}>Disabled</option>
                            </select>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="supplierId" class="form-label">Supplier</label>
                            <select class="form-select" id="supplierId" name="supplierId">
                                <option value="">Select Supplier</option>
                                <c:forEach items="${suppliers}" var="supplier">
                                    <option value="${supplier.supplierId}" ${details.material.supplierId == supplier.supplierId ? 'selected' : ''}>
                                        ${supplier.supplierName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="mt-4">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Save Changes
                        </button>
                        <a href="dashboardmaterial" class="btn btn-secondary ms-2">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        (function () {
            'use strict'
            var forms = document.querySelectorAll('.needs-validation')
            Array.prototype.slice.call(forms)
                .forEach(function (form) {
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault()
                            event.stopPropagation()
                        }
                        form.classList.add('was-validated')
                    }, false)
                })
        })()
    </script>
</body>
</html> 