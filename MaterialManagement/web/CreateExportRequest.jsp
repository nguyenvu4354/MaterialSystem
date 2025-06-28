<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Waggy - Create Export Request</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- Bootstrap & Fonts -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">

    <style>
        .export-form .form-control, .export-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .export-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .export-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .export-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
    </style>
</head>
<body>
<jsp:include page="Header.jsp"/>

<section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
    <div class="container">
        <div class="row my-5 py-5">
            <div class="col-12 bg-white p-4 rounded shadow export-form">
                <c:if test="${not hasCreateExportRequestPermission}">
                    <div class="alert alert-danger">You do not have permission to create export requests.</div>
                    <div class="text-center mt-3">
                        <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                    </div>
                </c:if>
                <c:if test="${hasCreateExportRequestPermission}">
                    <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Export Request</span></h2>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="CreateExportRequest" method="post">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label for="requestCode" class="form-label text-muted">Request Code</label>
                                <input type="text" class="form-control" id="requestCode" name="requestCode" value="${requestCode}" readonly>
                            </div>
                            <div class="col-md-6">
                                <label for="deliveryDate" class="form-label text-muted">Delivery Date</label>
                                <input type="date" class="form-control" id="deliveryDate" name="deliveryDate" required>
                            </div>
                            <div class="col-12">
                                <label for="reason" class="form-label text-muted">Reason</label>
                                <textarea class="form-control" id="reason" name="reason" required rows="3"></textarea>
                            </div>
                            <div class="col-md-6">
                                <label for="recipientId" class="form-label text-muted">Recipient</label>
                                <select class="form-select" id="recipientId" name="recipientId" required>
                                    <option value="">Select Recipient</option>
                                    <c:forEach var="user" items="${users}">
                                        <option value="${user.userId}">${user.fullName}</option>
                                    </c:forEach>
                                </select>
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
                                    <label class="form-label text-muted">In Stock</label>
                                    <input type="text" class="form-control stock-quantity" readonly value="0">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label text-muted">Quantity</label>
                                    <input type="number" class="form-control quantity-input" name="quantities[]" min="1" required>
                                    <div class="invalid-feedback">Not enough stock!</div>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label text-muted">Condition</label>
                                    <select class="form-select" name="conditions[]" required>
                                        <option value="new">New</option>
                                        <option value="used">Used</option>
                                        <option value="refurbished">Refurbished</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <img src="images/placeholder.png" class="img-fluid rounded material-image" style="height: 80px; width: 100%; object-fit: cover;" alt="Material Image">
                                </div>
                                <div class="col-md-1 d-flex align-items-end">
                                    <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                </div>
                            </div>
                        </div>

                        <div class="mt-3">
                            <button type="button" class="btn btn-outline-secondary" id="addMaterial">Add Material</button>
                        </div>

                        <div class="mt-5 d-grid gap-2">
                            <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                            <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                        </div>
                    </form>
                </c:if>
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
                "imageUrl": "${material.materialsUrl}",
                "stock": ${material.quantity}
            }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    function updateMaterialRow(selectElement) {
        const selectedId = selectElement.value;
        const material = materialsData.find(m => m.id === selectedId);
        const row = selectElement.closest('.material-row');
        const img = row.querySelector('.material-image');
        const stockInput = row.querySelector('.stock-quantity');
        const quantityInput = row.querySelector('.quantity-input');
        
        if (material) {
            let imgUrl = material.imageUrl && material.imageUrl !== 'null' ? material.imageUrl : '';
            if (imgUrl.startsWith('http') || imgUrl.startsWith('/') || imgUrl.startsWith('images/material/')) {
                img.src = imgUrl;
            } else if (imgUrl) {
                img.src = 'images/material/' + imgUrl;
            } else {
                img.src = 'images/placeholder.png';
            }
            stockInput.value = material.stock;
            quantityInput.max = material.stock; // Set max attribute for validation
        } else {
            img.src = 'images/placeholder.png'; // Default image
            stockInput.value = 0;
            quantityInput.max = null;
        }
        validateQuantity(quantityInput); // Validate after selection changes
    }
    
    function validateQuantity(quantityInput) {
        const stock = parseInt(quantityInput.max, 10);
        const quantity = parseInt(quantityInput.value, 10);

        if (!isNaN(stock) && !isNaN(quantity) && quantity > stock) {
            quantityInput.classList.add('is-invalid');
        } else {
            quantityInput.classList.remove('is-invalid');
        }
    }

    document.getElementById('addMaterial').addEventListener('click', function () {
        const materialList = document.getElementById('materialList');
        const firstRow = materialList.querySelector('.material-row');
        const newRow = firstRow.cloneNode(true);
        
        // Reset fields in the new row
        newRow.querySelector('select[name="materials[]"]').value = '';
        newRow.querySelector('input[name="quantities[]"]').value = '';
        newRow.querySelector('input[name="quantities[]"]').classList.remove('is-invalid');
        newRow.querySelector('.stock-quantity').value = '0';
        newRow.querySelector('select[name="conditions[]"]').value = 'new';
        newRow.querySelector('.material-image').src = 'images/placeholder.png';
        
        materialList.appendChild(newRow);
    });

    document.addEventListener('change', function(e) {
        if (e.target.classList.contains('material-select')) {
            updateMaterialRow(e.target);
        }
    });
    
    document.addEventListener('input', function(e) {
        if(e.target.classList.contains('quantity-input')) {
            validateQuantity(e.target);
        }
    });

    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('remove-material')) {
            const materialRows = document.querySelectorAll('.material-row');
            if (materialRows.length > 1) {
                e.target.closest('.material-row').remove();
            }
        }
    });

    // Initial image setup for the first row if a material is pre-selected (e.g., on form error)
    document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.material-select').forEach(select => {
            if (select.value) {
                updateMaterialRow(select);
            }
        });
        
        // Also add listeners for quantity inputs that might have values on load
        document.querySelectorAll('.quantity-input').forEach(input => {
            if (input.value) {
                validateQuantity(input);
            }
        });
    });
</script>
</body>
</html>