<%@ page contentType="text/html;charset=UTF-8" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>${supplier != null ? "Edit Supplier" : "Add New Supplier"}</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
      rel="stylesheet"
    />
    <link rel="stylesheet" type="text/css" href="css/vendor.css" />
    <link rel="stylesheet" type="text/css" href="style.css" />
    <style>
      body {
        background-color: #f8f9fa;
        padding: 20px;
      }
      .content {
        padding-left: 20px;
        font-family: "Roboto", sans-serif;
      }
      .form-container .form-control,
      .form-container .form-select {
        height: 48px;
        font-size: 1rem;
      }
      .form-container .form-label {
        font-size: 0.9rem;
        margin-bottom: 0.25rem;
      }
      .form-container .btn {
        font-size: 1rem;
        padding: 0.75rem;
      }
      .required-field::after {
        content: "*";
        color: red;
        margin-left: 4px;
      }
      .btn-brown {
        background-color: #deb887 !important;
        color: #fff !important;
        border: none;
      }
      .btn-brown:hover,
      .btn-brown:focus {
        background-color: #c49b63 !important;
        color: #fff !important;
      }
    </style>
  </head>
  <body>
    <jsp:include page="Header.jsp" />

    <div class="container-fluid">
      <div class="row">
        <div class="col-md-3 col-lg-2 bg-light p-0">
          <jsp:include page="Sidebar.jsp" />
        </div>

        <div class="col-md-9 col-lg-10 px-md-4 py-4">
          <section
            id="SupplierEdit"
            style="
              background: url('images/background-img.png') no-repeat;
              background-size: cover;
            "
          >
            <div class="container">
              <div class="row my-5 py-5">
                <div
                  class="col-10 bg-white p-4 mx-auto rounded shadow form-container"
                >
                  <h2 class="display-4 fw-normal text-center mb-4">
                    ${supplier != null ? "Edit" : "Add New"}
                    <span class="text-primary">Supplier</span>
                  </h2>

                  <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">${error}</div>
                  </c:if>
                  <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                      ${success}
                    </div>
                  </c:if>

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

                    <div class="row">
                      <div class="col-md-6">
                        <div class="mb-3">
                          <label
                            for="supplier_code"
                            class="form-label text-muted required-field"
                            >Supplier Code</label
                          >
                          <input
                            type="text"
                            class="form-control"
                            id="supplier_code"
                            name="supplier_code"
                            value="${supplier != null ? supplier.supplierCode :
                          newSupplierCode}"
                            readonly
                            required
                          />
                          <div class="invalid-feedback">
                            Please enter a supplier code.
                          </div>
                          <c:if test="${supplier == null}">
                            <div class="form-text text-info">
                              Auto-generated code
                            </div>
                          </c:if>
                          <c:if test="${supplier != null}">
                            <div class="form-text text-muted">
                              Supplier code cannot be modified
                            </div>
                          </c:if>
                        </div>

                        <div class="mb-3">
                          <label
                            for="supplier_name"
                            class="form-label text-muted required-field"
                            >Supplier Name</label
                          >
                          <input
                            type="text"
                            class="form-control"
                            id="supplier_name"
                            name="supplier_name"
                            value="${supplier != null ? supplier.supplierName : ''}"
                            required
                            maxlength="100"
                            minlength="3"
                            pattern=".*\S.*"
                          />
                          <div class="invalid-feedback">
                            Please enter a valid supplier name (not empty, 3-100
                            characters).
                          </div>
                        </div>

                        <div class="mb-3">
                          <label
                            for="contact_info"
                            class="form-label text-muted required-field"
                            >Contact Info</label
                          >
                          <input
                            type="text"
                            class="form-control"
                            id="contact_info"
                            name="contact_info"
                            value="${supplier != null ? supplier.contactInfo : ''}"
                            maxlength="100"
                            required
                            minlength="3"
                          />
                          <div class="invalid-feedback">
                            Please enter contact information (at least 3
                            characters).
                          </div>
                        </div>

                        <div class="mb-3">
                          <label
                            for="address"
                            class="form-label text-muted required-field"
                            >Address</label
                          >
                          <input
                            type="text"
                            class="form-control"
                            id="address"
                            name="address"
                            value="${supplier != null ? supplier.address : ''}"
                            maxlength="200"
                            required
                            minlength="5"
                          />
                          <div class="invalid-feedback">
                            Please enter supplier address (at least 5
                            characters).
                          </div>
                        </div>
                      </div>

                      <div class="col-md-6">
                        <div class="mb-3">
                          <label
                            for="phone_number"
                            class="form-label text-muted"
                            >Phone Number</label
                          >
                          <input
                            type="tel"
                            class="form-control"
                            id="phone_number"
                            name="phone_number"
                            value="${supplier != null ? supplier.phoneNumber : ''}"
                            pattern="[0-9\-\s]{10,15}"
                            maxlength="15"
                            inputmode="numeric"
                            required
                          />
                          <div class="invalid-feedback">
                            Please enter a valid phone number (10-15 digits,
                            numbers, dash or space allowed).
                          </div>
                        </div>

                        <div class="mb-3">
                          <label
                            for="email"
                            class="form-label text-muted required-field"
                            >Email</label
                          >
                          <input
                            type="email"
                            class="form-control"
                            id="email"
                            name="email"
                            value="${supplier != null ? supplier.email : ''}"
                            required
                          />
                          <div class="invalid-feedback">
                            Please enter a valid email address.
                          </div>
                        </div>

                        <div class="mb-3">
                          <label
                            for="tax_id"
                            class="form-label text-muted required-field"
                            >Tax ID</label
                          >
                          <input
                            type="text"
                            class="form-control"
                            id="tax_id"
                            name="tax_id"
                            value="${supplier != null ? supplier.taxId : ''}"
                            pattern="[A-Za-z0-9]{1,20}"
                            maxlength="20"
                            minlength="5"
                            required
                          />
                          <div class="invalid-feedback">
                            Please enter a valid Tax ID (letters and numbers
                            only, 5-20 characters).
                          </div>
                          <div class="form-text">
                            Enter the supplier's tax identification number.
                          </div>
                        </div>

                        <div class="mb-3">
                          <label for="description" class="form-label text-muted"
                            >Description</label
                          >
                          <textarea
                            class="form-control"
                            id="description"
                            name="description"
                            rows="3"
                            maxlength="200"
                          >
${supplier != null ? supplier.description : ''}</textarea
                          >
                        </div>
                      </div>
                    </div>

                    <div class="row">
                      <div class="col-12">
                        <div class="d-grid gap-2 mb-3">
                          <button
                            type="submit"
                            class="btn btn-dark btn-lg rounded-1"
                          >
                            <i class="fas fa-save"></i> ${supplier != null ?
                            'Update Supplier' : 'Add Supplier'}
                          </button>
                        </div>
                        <div class="d-grid gap-2 mb-3">
                          <a
                            href="Supplier?action=list"
                            class="btn btn-secondary btn-lg rounded-1"
                          >
                            <i class="fas fa-times"></i> Cancel
                          </a>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>

    <jsp:include page="Footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/script.js"></script>
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
