<%@ page contentType="text/html;charset=UTF-8" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>${supplier != null ? "Edit Supplier" : "Add New Supplier"}</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
    />
    <style>
      .required-field::after {
        content: "*";
        color: red;
        margin-left: 4px;
      }
      .btn-brown {
        background-color: #DEB887 !important;
        color: #fff !important;
        border: none;
      }
      .btn-brown:hover, .btn-brown:focus {
        background-color: #c49b63 !important;
        color: #fff !important;
      }
    </style>
  </head>
  <body class="bg-light">
    <div class="container mt-5">
      <div class="card shadow">
        <div class="card-header btn-brown text-white">
          <h3 class="mb-0">
            ${supplier != null ? "Edit Supplier" : "Add New Supplier"}
          </h3>
        </div>
        <div class="card-body">
          <!-- Error and Success Messages -->
          <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">${error}</div>
          </c:if>
          <c:if test="${not empty success}">
            <div class="alert alert-success" role="alert">${success}</div>
          </c:if>
          <!-- Form with Validation -->
          <form
            action="Supplier"
            method="post"
            class="needs-validation"
            novalidate
          >
            <c:if test="${supplier != null}">
              <input
                type="hidden"
                name="supplier_id"
                value="${supplier.supplierId}"
              />
            </c:if>

            <!-- Supplier Code & Name -->
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="supplier_code" class="form-label required-field">Supplier Code</label>
                <input type="text" class="form-control" id="supplier_code" name="supplier_code" value="${supplier != null ? supplier.supplierCode : newSupplierCode}" ${supplier == null ? 'readonly' : ''} required />
                <div class="invalid-feedback">Please enter a supplier code.</div>
                <c:if test="${supplier == null}">
                  <div class="form-text text-info"></div>
                </c:if>
              </div>
              <div class="col-md-6">
                <label for="supplier_name" class="form-label required-field">Supplier Name</label>
                <input type="text" class="form-control" id="supplier_name" name="supplier_name" value="${supplier != null ? supplier.supplierName : ''}" required maxlength="100" pattern=".*\S.*" />
                <div class="invalid-feedback">Please enter a valid supplier name (not empty, max 100 characters).</div>
              </div>
            </div>

            <!-- Contact Info & Address -->
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="contact_info" class="form-label required-field">Contact Info</label>
                <input type="text" class="form-control" id="contact_info" name="contact_info" value="${supplier != null ? supplier.contactInfo : ''}" maxlength="100" required />
                <div class="invalid-feedback">Please enter contact information.</div>
              </div>
              <div class="col-md-6">
                <label for="address" class="form-label required-field">Address</label>
                <input type="text" class="form-control" id="address" name="address" value="${supplier != null ? supplier.address : ''}" maxlength="200" required />
                <div class="invalid-feedback">Please enter supplier address.</div>
              </div>
            </div>

            <!-- Phone Number and Email -->
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="phone_number" class="form-label">Phone Number</label>
                <input type="tel" class="form-control" id="phone_number" name="phone_number" value="${supplier != null ? supplier.phoneNumber : ''}" pattern="[0-9\-\s]{10,15}" maxlength="15" inputmode="numeric" required />
                <div class="invalid-feedback">Please enter a valid phone number (10-15 digits, numbers, dash or space allowed).</div>
              </div>
              <div class="col-md-6">
                <label for="email" class="form-label required-field">Email</label>
                <input type="email" class="form-control" id="email" name="email" value="${supplier != null ? supplier.email : ''}" required />
                <div class="invalid-feedback">Please enter a valid email address.</div>
              </div>
            </div>

            <!-- Description & Tax ID -->
            <div class="row mb-3">
              <div class="col-md-6">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3" maxlength="200">${supplier != null ? supplier.description : ''}</textarea>
              </div>
              <div class="col-md-6">
                <label for="tax_id" class="form-label required-field">Tax ID</label>
                <input type="text" class="form-control" id="tax_id" name="tax_id" value="${supplier != null ? supplier.taxId : ''}" pattern="[A-Za-z0-9]{1,20}" maxlength="20" required />
                <div class="invalid-feedback">Please enter a valid Tax ID (letters and numbers only, max 20 characters).</div>
                <div class="form-text">Enter the supplier's tax identification number.</div>
              </div>
            </div>

            <!-- Buttons -->
            <div class="mt-4">
              <button type="submit" class="btn btn-brown">
                <i class="fas fa-save"></i> ${supplier != null ? 'Update Supplier' : 'Add Supplier'}
              </button>
              <a href="Supplier?action=list" class="btn btn-secondary ms-2">
                <i class="fas fa-times"></i> Cancel
              </a>
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
        "use strict";
        var forms = document.querySelectorAll(".needs-validation");
        Array.prototype.slice.call(forms).forEach(function (form) {
          form.addEventListener(
            "submit",
            function (event) {
              if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
              }
              form.classList.add("was-validated");
            },
            false
          );
        });
      })();
    </script>
  </body>
</html>
