<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Export Request</title>
        <link href="css/style.css" rel="stylesheet" type="text/css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="DirectorSidebar.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                        <h1 class="h2">Create Export Request</h1>
                    </div>

                    <form action="CreateExportRequest" method="POST" class="needs-validation" novalidate>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="requestCode" class="form-label">Request Code</label>
                                <input type="text" class="form-control" id="requestCode" name="requestCode" required>
                            </div>
                            <div class="col-md-6">
                                <label for="deliveryDate" class="form-label">Delivery Date</label>
                                <input type="date" class="form-control" id="deliveryDate" name="deliveryDate" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="destination" class="form-label">Destination</label>
                            <input type="text" class="form-control" id="destination" name="destination" required>
                        </div>

                        <div class="mb-3">
                            <label for="reason" class="form-label">Reason</label>
                            <textarea class="form-control" id="reason" name="reason" rows="3" required></textarea>
                        </div>

                        <div class="mb-3">
                            <h4>Materials</h4>
                            <div id="materialsList">
                                <div class="row mb-2 material-item">
                                    <div class="col-md-4">
                                        <select class="form-select" name="materials[]" required>
                                            <option value="">Select Material</option>
                                            <c:forEach var="material" items="${materials}">
                                                <option value="${material.materialId}">${material.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <input type="number" class="form-control" name="quantities[]" placeholder="Quantity" min="1" required>
                                    </div>
                                    <div class="col-md-4">
                                        <select class="form-select" name="conditions[]" required>
                                            <option value="new">New</option>
                                            <option value="used">Used</option>
                                            <option value="needs_inspection">Needs Inspection</option>
                                        </select>
                                    </div>
                                    <div class="col-md-2">
                                        <button type="button" class="btn btn-danger remove-material">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <button type="button" class="btn btn-secondary" id="addMaterial">
                                <i class="fas fa-plus"></i> Add Material
                            </button>
                        </div>

                        <div class="mb-3">
                            <button type="submit" class="btn btn-primary">Submit Request</button>
                            <a href="ExportRequestList.jsp" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('addMaterial').addEventListener('click', function() {
                const materialsList = document.getElementById('materialsList');
                const newMaterial = materialsList.children[0].cloneNode(true);
                
                // Clear values
                newMaterial.querySelectorAll('input, select').forEach(input => input.value = '');
                
                // Add remove functionality
                newMaterial.querySelector('.remove-material').addEventListener('click', function() {
                    newMaterial.remove();
                });
                
                materialsList.appendChild(newMaterial);
            });

            // Add remove functionality to initial material item
            document.querySelector('.remove-material').addEventListener('click', function() {
                if (document.querySelectorAll('.material-item').length > 1) {
                    this.closest('.material-item').remove();
                }
            });

            // Form validation
            (function () {
                'use strict'
                var forms = document.querySelectorAll('.needs-validation')
                Array.prototype.slice.call(forms).forEach(function (form) {
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