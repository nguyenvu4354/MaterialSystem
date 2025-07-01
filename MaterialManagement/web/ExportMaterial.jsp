<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User, java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    User admin = (User) session.getAttribute("user");
    if (admin == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<html lang="en">
    <head>
        <title>Waggy - Export Material</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <style>
            .material-form .form-control, .material-form .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .material-form .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .material-form .btn {
                font-size: 1rem;
                padding: 0.75rem;
            }
            .material-form .material-img {
                max-height: 100px;
                max-width: 100px;
                object-fit: cover;
                border-radius: 5px;
            }
            .material-form table {
                border-collapse: collapse;
                width: 100%;
            }
            .material-form th, .material-form td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #dee2e6;
            }
            .material-form th {
                background-color: #f8f9fa;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 1rem;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="export-material" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow material-form">
                        <c:if test="${not hasExportMaterialPermission}">
                            <div class="alert alert-danger">You do not have permission to export materials.</div>
                            <div class="text-center mt-3">
                                <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                            </div>
                        </c:if>
                        <c:if test="${hasExportMaterialPermission}">
                            <h2 class="display-4 fw-normal text-center mb-4">
                                <i class="fas fa-box-open me-2"></i>Export <span class="text-primary">Material</span>
                            </h2>
                            <c:if test="${not empty success}">
                                <div class="alert alert-success">${success}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>

                            <!-- Add Material Form -->
                            <h3 class="fw-normal mb-3">Add Material to Export List</h3>
                            <form action="ExportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                                <input type="hidden" name="action" value="add">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="materialId" class="form-label text-muted">Material</label>
                                        <select name="materialId" id="materialId" class="form-select" required>
                                            <option value="">Select Material</option>
                                            <c:forEach var="material" items="${materials}">
                                                <option value="${material.materialId}">${material.materialName}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Please select a material.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="quantity" class="form-label text-muted">Quantity</label>
                                        <input type="number" name="quantity" id="quantity" min="1" class="form-control" required>
                                        <div class="invalid-feedback">Please enter a quantity greater than 0.</div>
                                    </div>
                                    <div class="col-12 mt-4">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1">Add to Export List</button>
                                    </div>
                                </div>
                            </form>

                            <!-- Current Export List -->
                            <h3 class="fw-normal mb-3">Current Export List</h3>
                            <c:if test="${not empty exportDetails}">
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Image</th>
                                                <th>Material</th>
                                                <th>Quantity</th>
                                                <th>Available Stock</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${exportDetails}">
                                                <tr>
                                                    <c:set var="imgUrl" value="${materialMap[detail.materialId].materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${empty imgUrl}">
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/default-material.png' />
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(imgUrl, 'http://') || fn:startsWith(imgUrl, 'https://')}">
                                                            <c:set var="finalUrl" value='${imgUrl}' />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/${imgUrl}' />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <td>
                                                        <img src="${finalUrl}" class="material-img" alt="${materialMap[detail.materialId].materialName}">
                                                    </td>
                                                    <td>${materialMap[detail.materialId].materialName}</td>
                                                    <td>
                                                        <form action="ExportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="updateQuantity">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="number" name="quantity" value="${detail.quantity}" min="1"
                                                                   class="form-control me-2" style="width: 100px;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Quantity">
                                                                <i class="fas fa-sync-alt"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                    <td>${stockMap[detail.materialId]}</td>
                                                    <td>
                                                        <form action="ExportMaterial" method="post">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="quantity" value="${detail.quantity}">
                                                            <button type="submit" class="btn btn-outline-danger btn-sm">Remove</button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>
                            <c:if test="${empty exportDetails}">
                                <div class="text-center py-5">
                                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No materials added to the export list yet.</p>
                                </div>
                            </c:if>
                            <!-- Confirm Export Form -->
                            <h3 class="fw-normal mb-3 mt-5">Confirm Export</h3>
                            <form action="ExportMaterial" method="post" class="confirm-form" onsubmit="return validateConfirmForm()">
                                <input type="hidden" name="action" value="export">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="recipientUserId" class="form-label text-muted">Recipient</label>
                                        <select name="recipientUserId" id="recipientUserId" class="form-select" required>
                                            <option value="">Select Recipient</option>
                                            <c:forEach var="user" items="${users}">
                                                <option value="${user.userId}">${user.fullName} (ID: ${user.userId}, ${user.departmentName != null ? user.departmentName : 'No Department'})</option>
                                            </c:forEach>
                                        </select>
                                        <div class="invalid-feedback">Please select a recipient.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="batchNumber" class="form-label text-muted">Batch Number</label>
                                        <input type="text" name="batchNumber" id="batchNumber" maxlength="50" class="form-control">
                                    </div>
                                    <div class="col-12">
                                        <label for="note" class="form-label text-muted">Note</label>
                                        <input type="text" name="note" id="note" maxlength="100" class="form-control" placeholder="Enter Note">
                                    </div>
                                    <div class="col-12 mt-4">
                                        <div class="d-grid gap-2">
                                            <button type="submit" class="btn btn-dark btn-lg rounded-1">Confirm Export</button>
                                            <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    setTimeout(function() {
                        if (alert.style.display !== 'none') {
                            alert.style.transition = 'all 0.5s ease-out';
                            alert.style.opacity = '0';
                            alert.style.transform = 'translateY(-10px)';
                            setTimeout(function() {
                                alert.style.display = 'none';
                            }, 500);
                        }
                    }, 3000);
                });
            });

            function validateAddForm() {
                const form = document.querySelector('form[action="ExportMaterial"]');
                let isValid = true;
                const materialId = form.querySelector('#materialId');
                const quantity = form.querySelector('#quantity');

                if (!materialId.value) {
                    materialId.classList.add('is-invalid');
                    isValid = false;
                } else {
                    materialId.classList.remove('is-invalid');
                }

                if (!quantity.value || quantity.value <= 0) {
                    quantity.classList.add('is-invalid');
                    isValid = false;
                } else {
                    quantity.classList.remove('is-invalid');
                }

                return isValid;
            }

            function validateConfirmForm() {
                const form = document.querySelector('.confirm-form');
                let isValid = true;
                const recipient = form.querySelector('#recipientUserId');

                if (!recipient.value) {
                    recipient.classList.add('is-invalid');
                    isValid = false;
                } else {
                    recipient.classList.remove('is-invalid');
                }

                if (${fn:length(exportDetails)} === 0) {
                    alert("Cannot confirm export: No materials in the export list.");
                    isValid = false;
                }

                return isValid;
            }
        </script>
    </body>
</html>