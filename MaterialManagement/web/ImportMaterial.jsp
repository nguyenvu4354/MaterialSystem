<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="entity.User" %>
<% 
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Import Material</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <style>
            .material-form {
                border-radius: 12px;
                box-shadow: 0 2px 12px rgba(0,0,0,0.07);
                background: white;
            }
            .material-form .form-control, .material-form .form-select {
                height: 48px;
                font-size: 18px;
                color: black;
                background: white;
                border-radius: 8px;
                border: 1px solid #bdbdbd;
            }
            .material-form .form-label {
                font-size: 16px;
                margin-bottom: 6px;
                font-weight: 500;
                color: black;
            }
            .material-form .btn {
                font-size: 16px;
                padding: 12px;
            }
            .material-form .material-row {
                margin-bottom: 16px;
                border-bottom: 1px solid gray;
                padding-bottom: 16px;
            }
            .material-form .img-thumbnail {
                width: 60px;
                height: 60px;
                object-fit: cover;
                border-radius: 8px;
                border: 1px solid #bdbdbd;
            }
            .material-form .status-badge {
                padding: 10px 22px;
                border-radius: 12px;
                font-size: 16px;
                font-weight: 600;
                background: lightgray;
                color: black;
                border: 1px solid #bdbdbd;
            }
            .material-form .status-new {
                background-color: #198754;
                color: #fff;
            }
            .material-form .status-used {
                background-color: #ffc107;
                color: #212529;
            }
            .material-form .status-refurbished {
                background-color: #0dcaf0;
                color: #fff;
            }
            .material-form .badge.bg-secondary, .material-form .badge.bg-info, .material-form .badge.bg-success {
                font-size: 16px;
                padding: 10px 22px;
                font-weight: 600;
                border-radius: 12px;
                border: 1px solid #bdbdbd;
                background: lightgray !important;
                color: black !important;
            }
            .material-form .badge.bg-info {
                background-color: #0dcaf0 !important;
                color: #fff !important;
            }
            .material-form .badge.bg-secondary {
                background-color: #6c757d !important;
                color: #fff !important;
            }
            .material-form .badge.bg-success {
                background-color: #198754 !important;
                color: #fff !important;
            }
            .material-form .table {
                background: white;
                color: black;
                border: 1px solid #bdbdbd;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 1px 8px rgba(0,0,0,0.04);
            }
            .material-form .table th, .material-form .table td {
                color: black;
                font-size: 16px;
                font-weight: 400;
                border: 1px solid #bdbdbd;
                padding: 10px 12px;
                background: white;
            }
            .material-form .table th {
                font-weight: 700;
                background: lightgray;
                color: black;
                border-top: none;
            }
            .material-form .card, .material-form .card-header, .material-form .card-body {
                border-radius: 12px !important;
                background: white;
                border: none;
            }
            .material-form .card-header {
                background: lightgray !important;
                color: black;
                font-weight: 700;
                border-bottom: 1px solid #bdbdbd;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 16px;
            }
            .condition-bar {
                height: 5px;
                background-color: #e9ecef;
                border-radius: 3px;
                margin-top: 5px;
            }
            .condition-fill {
                height: 100%;
                border-radius: 3px;
                transition: width 0.3s ease;
            }
            .condition-good {
                background-color: #28a745;
            }
            .condition-warning {
                background-color: #ffc107;
            }
            .condition-bad {
                background-color: #dc3545;
            }
            .material-img {
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
                font-size: 1rem;
            }
            .material-form th {
                background-color: #f8f9fa;
                font-weight: 700;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 1rem;
            }
            .material-form .form-control:focus {
                border-color: #86b7fe;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
                outline: 0;
            }
            .material-form .form-control.is-invalid {
                border-color: #dc3545;
            }
            .material-form .form-control.is-invalid:focus {
                box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
            }
            .invalid-feedback {
                color: #dc3545 !important;
                font-weight: bold;
                font-size: 1rem;
                opacity: 1 !important;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="import-material" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow material-form">
                        <c:if test="${not hasImportMaterialPermission}">
                            <div class="alert alert-danger">You do not have permission to import materials.</div>
                            <div class="text-center mt-3">
                                <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                            </div>
                        </c:if>
                        <c:if test="${hasImportMaterialPermission}">
                            <h2 class="display-4 fw-normal text-center mb-4">
                                <i class="fas fa-box-open me-2"></i>Import <span class="text-primary">Material</span>
                            </h2>
                            <!-- Alert Messages -->
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <c:if test="${not empty success}">
                                <div class="alert alert-success">${success}</div>
                            </c:if>

                            <!-- Add Material Form -->
                            <h3 class="fw-normal mb-3">Add Material to Import List</h3>
                            <form id="addMaterialForm" action="ImportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                                <input type="hidden" name="action" value="add">
                                <div class="row g-3">
                                    <div class="col-12">
                                        <label for="materialName" class="form-label text-muted">Material</label>
                                        <input type="text" id="materialName" name="materialName" class="form-control" required placeholder="Type material name or code" autocomplete="off">
                                        <input type="hidden" name="materialId" id="materialId">
                                        <div class="invalid-feedback">Please enter a valid material name or code.</div>
                                        <c:if test="${not empty formErrors['materialId']}">
                                            <div class="text-danger small mt-1">${formErrors['materialId']}</div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-4">
                                        <label for="quantity" class="form-label text-muted">Quantity</label>
                                        <input type="number" name="quantity" id="quantity" class="form-control" min="1" required placeholder="Qty">
                                        <div class="invalid-feedback">Please enter a quantity greater than 0.</div>
                                        <c:if test="${not empty formErrors['quantity']}">
                                            <div class="text-danger small mt-1">${formErrors['quantity']}</div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-4">
                                        <label for="unitPrice" class="form-label text-muted">Unit Price ($)</label>
                                        <input type="number" name="unitPrice" id="unitPrice" class="form-control" min="0" step="0.01" required placeholder="Unit Price ($)">
                                        <c:if test="${not empty formErrors['unitPrice']}">
                                            <div class="text-danger small mt-1">${formErrors['unitPrice']}</div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-4">
                                        <label for="totalValue" class="form-label text-muted">Total Value</label>
                                        <input type="text" id="totalValue" class="form-control" readonly placeholder="Auto">
                                    </div>
                                    <div class="col-12 mt-4">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1 w-100">
                                            <i class="fas fa-plus-circle me-2"></i>Add to Import List
                                        </button>
                                    </div>
                                </div>
                            </form>

                            <!-- Current Import List -->
                            <h3 class="fw-normal mb-3">Current Import List</h3>
                            <c:if test="${not empty importDetails}">
                                <div class="table-responsive">
                                    <table class="table align-middle text-center">
                                        <thead>
                                            <tr>
                                                <th style="width: 8%">Image</th>
                                                <th style="width: 18%">Material</th>
                                                <th style="width: 12%">Unit</th>
                                                <th style="width: 15%">Category</th>
                                                <th style="width: 10%">Quantity</th>
                                                <th style="width: 15%">Price</th>
                                                <th style="width: 12%">Value</th>
                                                <th style="width: 10%">Status</th>
                                                <th style="width: 8%">Stock</th>
                                                <th style="width: 5%">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${importDetails}">
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
                                                    <td>
                                                        <strong>${materialMap[detail.materialId].materialName}</strong><br>
                                                        <small class="text-muted">${materialMap[detail.materialId].materialCode}</small>
                                                    </td>
                                                    <td>${materialMap[detail.materialId].unit.unitName}</td>
                                                    <td>${materialMap[detail.materialId].category.category_name}</td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="update">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="number" name="newQuantity" value="${detail.quantity}" min="1"
                                                                   class="form-control me-2" style="width: 100px; text-align: center;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Quantity">
                                                                <i class="fas fa-sync-alt"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="updatePrice">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="number" name="newPrice" value="${detail.unitPrice}" min="0" step="0.01"
                                                                   class="form-control me-2" style="width: 80px; text-align: center;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Price">
                                                                <i class="fas fa-dollar-sign"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                    <td><strong>$<fmt:formatNumber value="${detail.unitPrice * detail.quantity}" type="number" minFractionDigits="2"/></strong></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${materialMap[detail.materialId].materialStatus == 'new'}">
                                                                <span style="color: #000; font-weight: 600;">New</span>
                                                            </c:when>
                                                            <c:when test="${materialMap[detail.materialId].materialStatus == 'used'}">
                                                                <span style="color: #000; font-weight: 600;">Used</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: #000; font-weight: 600;">Damaged</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${stockMap[detail.materialId] > 50}">
                                                                <span style="background:#4ade80;color:#000;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #22c55e;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:when test="${stockMap[detail.materialId] >= 10}">
                                                                <span style="background:#fde047;color:#000;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #facc15;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:when test="${stockMap[detail.materialId] > 0}">
                                                                <span style="background:#fd7e14;color:#fff;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #b35c00;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="background:#dc2626;color:#fff;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #b91c1c;">${stockMap[detail.materialId]}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post">
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
                                <!-- Pagination Controls -->
                                <nav aria-label="Page navigation" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <c:if test="${currentPage > 1}">
                                            <li class="page-item">
                                                <a class="page-link" href="ImportMaterial?page=${currentPage - 1}" aria-label="Previous">
                                                    <span aria-hidden="true">&laquo;</span>
                                                </a>
                                            </li>
                                        </c:if>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="ImportMaterial?page=${i}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <c:if test="${currentPage < totalPages}">
                                            <li class="page-item">
                                                <a class="page-link" href="ImportMaterial?page=${currentPage + 1}" aria-label="Next">
                                                    <span aria-hidden="true">&raquo;</span>
                                                </a>
                                            </li>
                                        </c:if>
                                    </ul>
                                </nav>
                            </c:if>
                            <c:if test="${empty importDetails}">
                                <div class="text-center py-5">
                                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No materials added to the import list yet.</p>
                                </div>
                            </c:if>

                            <h3 class="fw-normal mb-3 mt-5">Confirm Import</h3>
                            <c:if test="${not empty formErrors}">
                                <div class="alert alert-danger">
                                    <ul class="mb-0">
                                        <c:forEach var="err" items="${formErrors.values()}">
                                            <li>${err}</li>
                                            </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                            <form id="confirmImportForm" action="ImportMaterial" method="post" onsubmit="return validateConfirmForm()">
                                <input type="hidden" name="action" value="import">
                                <div class="row g-4"> <!-- Increased gap for better spacing -->
                                    <div class="col-md-6">
                                        <label class="form-label text-muted">Supplier</label>
                                        <input type="text" id="supplierName" name="supplierName" class="form-control" list="supplierList" required placeholder="Type Or Select Supplier">
                                        <input type="hidden" name="supplierId" id="supplierId">
                                        <datalist id="supplierList">
                                            <c:forEach var="supplier" items="${suppliers}">
                                                <option value="${supplier.supplierName}" data-id="${supplier.supplierId}"></option>
                                            </c:forEach>
                                        </datalist>
                                        <div class="invalid-feedback">Please select a valid supplier.</div>
                                        <c:if test="${not empty formErrors['supplierId']}">
                                            <div class="text-danger small mt-1">${formErrors['supplierId']}</div>
                                        </c:if>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label text-muted">Destination</label>
                                        <input type="text" id="destination" name="destination" class="form-control<c:if test='${not empty formErrors["destination"]}'> is-invalid</c:if>" value="${param.destination}">
                                            <div class="invalid-feedback" id="destinationError">
                                            <c:choose>
                                                <c:when test="${not empty formErrors['destination']}">
                                                    ${formErrors['destination']}
                                                </c:when>
                                                <c:otherwise>
                                                    Please enter a destination (max 100 characters).
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <input type="hidden" name="actualArrival" id="actualArrivalField">
                                    <div class="col-md-12">
                                        <label class="form-label text-muted">Notes</label>
                                        <input type="text" name="note" class="form-control" placeholder="Enter Note" value="${param.note}">
                                        <c:if test="${not empty formErrors['note']}">
                                            <div class="text-danger small mt-1">${formErrors['note']}</div>
                                        </c:if>
                                    </div>
                                    <div class="col-12 mt-4">
                                        <div class="d-grid gap-2">
                                            <button type="submit" class="btn btn-dark btn-lg rounded-1">Confirm Import</button>
                                            <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script>
                                document.addEventListener('DOMContentLoaded', function () {

                                    const actualArrivalField = document.getElementById('actualArrivalField');
                                    if (actualArrivalField) {
                                        const now = new Date();
                                        const year = now.getFullYear();
                                        const month = String(now.getMonth() + 1).padStart(2, '0');
                                        const day = String(now.getDate()).padStart(2, '0');
                                        const hours = String(now.getHours()).padStart(2, '0');
                                        const minutes = String(now.getMinutes()).padStart(2, '0');
                                        actualArrivalField.value = `${year}-${month}-${day}T${hours}:${minutes}`;
                                    }

                                    const alerts = document.querySelectorAll('.alert');
                                    alerts.forEach(function (alert) {
                                        setTimeout(function () {
                                            if (alert.style.display !== 'none') {
                                                alert.style.transition = 'all 0.5s ease-out';
                                                alert.style.opacity = '0';
                                                alert.style.transform = 'translateY(-10px)';
                                                setTimeout(function () {
                                                    alert.style.display = 'none';
                                                }, 500);
                                            }
                                        }, 5000);

                                        alert.addEventListener('click', function (e) {
                                            if (e.target.classList.contains('btn-close') || e.target.classList.contains('alert')) {
                                                this.style.transition = 'all 0.3s ease-out';
                                                this.style.opacity = '0';
                                                this.style.transform = 'translateY(-10px)';
                                                setTimeout(() => {
                                                    this.style.display = 'none';
                                                }, 300);
                                            }
                                        });
                                    });
                                });

                                function calculateTotal() {
                                    const quantity = document.querySelector('input[name="quantity"]').value;
                                    const unitPrice = document.querySelector('input[name="unitPrice"]').value;
                                    const totalField = document.getElementById('totalValue');
                                    if (quantity && unitPrice) {
                                        const total = (parseInt(quantity) * parseFloat(unitPrice)).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
                                        totalField.value = '$' + total;
                                    } else {
                                        totalField.value = '';
                                    }
                                }
                                document.querySelector('input[name="quantity"]').addEventListener('input', calculateTotal);
                                document.querySelector('input[name="unitPrice"]').addEventListener('input', calculateTotal);

                                function validateAddForm() {
                                    const form = document.getElementById('addMaterialForm');
                                    let isValid = true;
                                    const materialNameInput = form.querySelector('input[name="materialName"]');
                                    const materialIdInput = form.querySelector('input[name="materialId"]');
                                    const quantity = form.querySelector('input[name="quantity"]');
                                    const unitPrice = form.querySelector('input[name="unitPrice"]');

                                    if (!materialNameInput.value.trim()) {
                                        materialNameInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        materialNameInput.classList.remove('is-invalid');
                                    }

                                    if (!materialIdInput.value) {
                                        materialIdInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        materialIdInput.classList.remove('is-invalid');
                                    }

                                    if (!quantity.value || quantity.value <= 0) {
                                        quantity.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        quantity.classList.remove('is-invalid');
                                    }
                                    if (!unitPrice.value || unitPrice.value < 0) {
                                        unitPrice.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        unitPrice.classList.remove('is-invalid');
                                    }
                                    return isValid;
                                }

                                function validateConfirmForm() {
                                    const form = document.getElementById('confirmImportForm');
                                    let isValid = true;
                                    const supplierNameInput = form.querySelector('input[name="supplierName"]');
                                    const supplierIdInput = form.querySelector('input[name="supplierId"]');
                                    const destination = form.querySelector('input[name="destination"]');
                                    const destinationOther = form.querySelector('input[name="destinationOther"]');

                                    if (!supplierNameInput.value.trim()) {
                                        supplierNameInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        supplierNameInput.classList.remove('is-invalid');
                                    }
                                    if (!supplierIdInput.value) {
                                        supplierIdInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        supplierIdInput.classList.remove('is-invalid');
                                    }

                                    if (destination && destination.value === 'other') {
                                        if (!destinationOther.value.trim()) {
                                            destinationOther.classList.add('is-invalid');
                                            isValid = false;
                                        } else {
                                            destinationOther.classList.remove('is-invalid');
                                        }
                                    } else if (destination) {
                                        destination.classList.remove('is-invalid');
                                    }
                                    return isValid;
                                }
        </script>
        <script>
            // Supplier and material autocomplete logic (consolidated, no duplicates)
            $(function() {
            var materials = [
            <c:forEach var="material" items="${materials}" varStatus="loop">
            {
            label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
                    value: "${fn:escapeXml(material.materialName)}",
                    id: "${material.materialId}",
                    name: "${fn:escapeXml(material.materialName)}",
                    code: "${fn:escapeXml(material.materialCode)}"
            }${loop.last ? '' : ','}
            </c:forEach>
            ];
                    $("#materialName").autocomplete({
            source: function(request, response) {
            var term = request.term.toLowerCase();
                    var matches = materials.filter(function(material) {
                    return material.name.toLowerCase().includes(term) || material.code.toLowerCase().includes(term);
                    });
                    response(matches);
            },
                    select: function(event, ui) {
                    $("#materialId").val(ui.item.id);
                            $("#materialName").val(ui.item.name);
                            $("#materialName").removeClass('is-invalid');
                    },
                    change: function(event, ui) {
                    if (!ui.item) {
                    var inputValue = $("#materialName").val().toLowerCase().trim();
                            var selectedMaterial = materials.find(function(material) {
                            return material.name.toLowerCase() === inputValue || material.code.toLowerCase() === inputValue;
                            });
                            if (selectedMaterial) {
                    $("#materialId").val(selectedMaterial.id);
                            $("#materialName").val(selectedMaterial.name);
                            $("#materialName").removeClass('is-invalid');
                    } else {
                    $("#materialId").val('');
                            $("#materialName").addClass('is-invalid');
                    }
                    }
                    },
                    minLength: 1
            });
                    var supplierNameInput = document.getElementById('supplierName');
                    var supplierIdInput = document.getElementById('supplierId');
                    var supplierList = document.getElementById('supplierList');
                    if (supplierNameInput && supplierIdInput && supplierList) {
            supplierNameInput.addEventListener('input', function() {
            var inputValue = supplierNameInput.value.toLowerCase().trim();
                    var options = supplierList.querySelectorAll('option');
                    var selectedOption = Array.from(options).find(function(option) {
            return option.value.toLowerCase() === inputValue;
            });
                    supplierIdInput.value = selectedOption ? selectedOption.getAttribute('data-id') : '';
                    if (!selectedOption) {
            supplierNameInput.classList.add('is-invalid');
            } else {
            supplierNameInput.classList.remove('is-invalid');
            }
            });
            }
            });
        </script>
    </body>
</html>