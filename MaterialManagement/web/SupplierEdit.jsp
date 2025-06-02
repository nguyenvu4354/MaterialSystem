<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${supplier != null ? "Edit Supplier" : "Add New Supplier"}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">
    <style>
        .required-field::after {
            content: "*";
            color: red;
            margin-left: 4px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h3 class="mb-0">${supplier != null ? "Edit Supplier" : "Add New Supplier"}</h3>
            </div>
            <div class="card-body">
                <!-- Error and Success Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        ${success}
                    </div>
                </c:if>
                <!-- Form with Validation -->
                <form action="SupplierServlet" method="post" class="needs-validation" novalidate>
                    <c:if test="${supplier != null}">
                        <input type="hidden" name="supplier_id" value="${supplier.supplierId}" />
                    </c:if>

                    <!-- Supplier Name -->
                    <div class="mb-3">
                        <label for="supplier_name" class="form-label required-field"><i class="bi bi-building me-1"></i>Supplier Name</label>
                        <input type="text" class="form-control" id="supplier_name" name="supplier_name" 
                               placeholder="Enter supplier name" value="${supplier != null ? supplier.supplierName : ''}" required>
                        <div class="invalid-feedback">
                            Please enter a supplier name.
                        </div>
                    </div>

                    <!-- Contact Info -->
                    <div class="mb-3">
                        <label for="contact_info" class="form-label"><i class="bi bi-person-lines-fill me-1"></i>Contact Info</label>
                        <input type="text" class="form-control" id="contact_info" name="contact_info" 
                               placeholder="Enter contact details" value="${supplier != null ? supplier.contactInfo : ''}">
                    </div>

                    <!-- Address -->
                    <div class="mb-3">
                        <label for="address" class="form-label"><i class="bi bi-geo-alt me-1"></i>Address</label>
                        <input type="text" class="form-control" id="address" name="address" 
                               placeholder="Enter supplier address" value="${supplier != null ? supplier.address : ''}">
                    </div>

                    <!-- Phone Number and Email -->
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="phone_number" class="form-label"><i class="bi bi-telephone me-1"></i>Phone Number</label>
                                <input type="tel" class="form-control" id="phone_number" name="phone_number" 
                                       placeholder="e.g., 123-456-7890" 
                                       value="${supplier != null ? supplier.phoneNumber : ''}"
                                       pattern="[0-9]{10,12}"
                                       inputmode="numeric"
                                       oninput="formatPhoneNumber(this); validatePhoneNumber(this)"required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="email" class="form-label"><i class="bi bi-envelope me-1"></i>Email</label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       placeholder="e.g., supplier@example.com" value="${supplier != null ? supplier.email : ''}">
                            </div>
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <label for="description" class="form-label"><i class="bi bi-card-text me-1"></i>Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3" 
                                  placeholder="Enter a brief description of the supplier">${supplier != null ? supplier.description : ''}</textarea>
                    </div>

                    <!-- Tax ID -->
                    <div class="mb-3">
                        <label for="tax_id" class="form-label"><i class="bi bi-file-earmark-text me-1"></i>Tax ID</label>
                        <input type="text" class="form-control" id="tax_id" name="tax_id" 
                               placeholder="Enter tax ID" value="${supplier != null ? supplier.taxId : ''}">
                        <div class="form-text">Enter the supplier's tax identification number.</div>
                    </div>

                    <!-- Buttons -->
                    <div class="mt-4">
                        <button type="submit" class="btn btn-primary">
                            ${supplier != null ? 'Update Supplier' : 'Add Supplier'}
                        </button>
                        <a href="SupplierServlet?action=list" class="btn btn-secondary ms-2">Back to List</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Form Validation Script -->
    <script>
        (function () {
            'use strict';
            var forms = document.querySelectorAll('.needs-validation');
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();
    </script>
</body>
</html>