<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="Login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Create Repair Request</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

        <style>
            .repair-form .form-control, .repair-form .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .repair-form .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .repair-form .btn {
                font-size: 1rem;
                padding: 0.75rem 1.25rem;
            }
            .repair-form .material-row {
                margin-bottom: 1rem;
                border-bottom: 1px solid #dee2e6;
                padding-bottom: 1rem;
                align-items: center; 
            }
            .repair-form .remove-material {
                height: 48px; 
                width: 48px; 
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.2rem;
                margin-left: 0.5rem; 
                border: 1px solid #dc3545; 
            }
            .repair-cost-container {
                display: flex;
                align-items: center;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp"/>

        <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow repair-form">
                        <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Repair Request</span></h2>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>

                        <form action="repairrequest" method="post" id="repairForm">
                            <h3 class="fw-normal mt-4 mb-3">Materials for Repair</h3>
                            <div id="materialList">
                                <!-- Initial Material Row -->
                                <div class="row material-row align-items-end gy-2">
                                    <div class="col-md-3">
                                        <label class="form-label text-muted">Material Name</label>
                                        <input type="text" class="form-control material-autocomplete" name="materialName" required>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label text-muted">Quantity</label>
                                        <input type="number" class="form-control" name="quantity" required min="1" value="1">
                                    </div>
                                    <div class="col-md-5">
                                        <label class="form-label text-muted">Description of Damage</label>
                                        <input type="text" class="form-control" name="damageDescription" required>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="repair-cost-container">
                                            <button type="button" class="btn btn-sm btn-outline-danger remove-material">X</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mt-2 mb-4">
                                <button type="button" class="btn btn-outline-secondary" id="addMaterial">+ Add Material</button>
                            </div>

                            <h3 class="fw-normal mt-5 mb-3">Repairer and Schedule Information</h3>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Supplier</label>
                                    <select class="form-select" name="supplierId" required>
                                        <option value="" disabled selected>Select a supplier</option>
                                        <c:forEach var="supplier" items="${supplierList}">
                                            <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="invalid-feedback">Please select a supplier.</div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Estimated Return Date</label>
                                    <input type="date" class="form-control" name="estimatedReturnDate" id="returnDate" required>
                                    <div class="invalid-feedback">Estimated return date cannot be left blank.</div>
                                </div>
                                <div class="col-12">
                                    <label class="form-label text-muted">Reason for Repair</label>
                                    <textarea name="reason" rows="3" class="form-control" required></textarea>
                                    <div class="invalid-feedback">Reason for repair cannot be left blank.</div>
                                </div>
                            </div>

                            <div class="mt-5 d-grid gap-2">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                                <a href="home" class="btn btn-outline-secondary btn-lg rounded-1">Back to Home</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Set date to today only
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('returnDate').setAttribute('value', today);
            document.getElementById('returnDate').setAttribute('min', today);
            document.getElementById('returnDate').setAttribute('max', today);

            // Autocomplete for material names
            const availableMaterials = [
            <c:forEach var="m" items="${materialList}" varStatus="loop">
            "${m.materialName}"${loop.last ? '' : ','}
            </c:forEach>
            ];

            function setupAutocomplete(input) {
                $(input).autocomplete({
                    source: availableMaterials,
                    minLength: 1
                });
            }

            // Apply autocomplete to existing material inputs
            document.querySelectorAll('.material-autocomplete').forEach(setupAutocomplete);

            // Add new material row
            document.getElementById('addMaterial').addEventListener('click', function () {
                const materialList = document.getElementById('materialList');
                const firstRow = materialList.querySelector('.material-row');
                const newRow = firstRow.cloneNode(true);

                newRow.querySelectorAll('input').forEach(input => {
                    input.value = (input.name === 'quantity' ? '1' : '');
                });

                // Update remove button in new row
                const repairCostContainer = newRow.querySelector('.repair-cost-container');
                const removeButton = repairCostContainer.querySelector('.remove-material');
                repairCostContainer.appendChild(removeButton);

                materialList.appendChild(newRow);
                setupAutocomplete(newRow.querySelector('.material-autocomplete'));
            });

            // Remove material row
            document.addEventListener('click', function (e) {
                const removeBtn = e.target.closest('.remove-material');
                if (removeBtn) {
                    const rows = document.querySelectorAll('.material-row');
                    if (rows.length > 1) {
                        removeBtn.closest('.material-row').remove();
                    } else {
                        alert("At least one material is required.");
                    }
                }
            });

            // Form validation
            document.getElementById('repairForm').addEventListener('submit', function (e) {
                let isValid = true;

                // Reset validation styles
                document.querySelectorAll('.form-control, .form-select').forEach(input => {
                    input.classList.remove('is-invalid');
                });
                document.querySelectorAll('.invalid-feedback').forEach(feedback => {
                    feedback.style.display = 'none';
                });

                // Validate return date
                const returnDate = document.getElementById('returnDate').value;
                if (!returnDate) {
                    document.getElementById('returnDate').classList.add('is-invalid');
                    document.getElementById('returnDate').nextElementSibling.style.display = 'block';
                    isValid = false;
                } else if (returnDate !== today) {
                    document.getElementById('returnDate').classList.add('is-invalid');
                    document.getElementById('returnDate').nextElementSibling.textContent = 'Estimated return date must be today only.';
                    document.getElementById('returnDate').nextElementSibling.style.display = 'block';
                    isValid = false;
                }

                // Validate supplier
                const supplierId = document.querySelector('select[name="supplierId"]').value;
                if (!supplierId) {
                    document.querySelector('select[name="supplierId"]').classList.add('is-invalid');
                    document.querySelector('select[name="supplierId"]').nextElementSibling.style.display = 'block';
                    isValid = false;
                }

                // Validate reason
                const reason = document.querySelector('textarea[name="reason"]').value.trim();
                if (!reason) {
                    document.querySelector('textarea[name="reason"]').classList.add('is-invalid');
                    document.querySelector('textarea[name="reason"]').nextElementSibling.style.display = 'block';
                    isValid = false;
                }

                // Validate material inputs
                const materialInputs = document.querySelectorAll('.material-autocomplete');
                for (let input of materialInputs) {
                    if (!input.value.trim()) {
                        input.classList.add('is-invalid');
                        input.insertAdjacentHTML('afterend', '<div class="invalid-feedback">Material name cannot be left blank.</div>');
                        isValid = false;
                    } else if (!availableMaterials.includes(input.value)) {
                        input.classList.add('is-invalid');
                        input.insertAdjacentHTML('afterend', '<div class="invalid-feedback">Please select a valid material name from the suggestions.</div>');
                        isValid = false;
                    }
                }

                // Validate quantity inputs
                const quantityInputs = document.querySelectorAll('input[name="quantity"]');
                for (let input of quantityInputs) {
                    if (!input.value || input.value < 1) {
                        input.classList.add('is-invalid');
                        input.insertAdjacentHTML('afterend', '<div class="invalid-feedback">Quantity must be at least 1.</div>');
                        isValid = false;
                    }
                }

                // Validate damage description inputs
                const damageInputs = document.querySelectorAll('input[name="damageDescription"]');
                for (let input of damageInputs) {
                    if (!input.value.trim()) {
                        input.classList.add('is-invalid');
                        input.insertAdjacentHTML('afterend', '<div class="invalid-feedback">Damage description cannot be left blank.</div>');
                        isValid = false;
                    }
                }

                if (!isValid) {
                    e.preventDefault();
                }
            });
        </script>
    </body>
</html>