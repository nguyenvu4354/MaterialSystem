<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Purchase Request</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        .purchase-form .form-control, .purchase-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .purchase-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .purchase-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .purchase-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
        .purchase-form .material-image {
            height: 80px;
            width: 100%;
            object-fit: cover;
            border-radius: 12px;
            background: #f8f9fa;
            box-shadow: 0 1px 4px rgba(0,0,0,0.07);
            border: 1px solid #eee;
            display: block;
        }
        .ui-autocomplete {
            background: #fff;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            max-height: 250px;
            overflow-y: auto;
            box-shadow: 0 4px 16px rgba(0,0,0,0.08);
            z-index: 9999 !important;
            font-size: 1rem;
        }
        .ui-menu-item-wrapper {
            padding: 8px 16px;
            cursor: pointer;
        }
        .ui-menu-item-wrapper.ui-state-active {
            background: #f0f4fa;
            color: #0d6efd;
            border-radius: 6px;
        }
    </style>
</head>
<body>
    <c:set var="roleId" value="${sessionScope.user.roleId}" />
    <c:set var="hasCreatePurchaseRequestPermission" value="${rolePermissionDAO.hasPermission(roleId, 'CREATE_PURCHASE_REQUEST')}" scope="request" />

    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <div class="access-denied">
                <div class="access-denied-card">
                    <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                    <h2 class="text-danger mb-3">Login Required</h2>
                    <p class="text-muted mb-4">You need to login to access this page.</p>
                    <div class="d-grid gap-2">
                        <a href="Login.jsp" class="btn btn-primary">Login</a>
                        <a href="HomeServlet" class="btn btn-outline-secondary">Back to Home</a>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${!hasCreatePurchaseRequestPermission}">
                <div class="access-denied">
                    <div class="access-denied-card">
                        <div style="font-size: 4rem; color: #dc3545; margin-bottom: 1rem;">🔒</div>
                        <h2 class="text-danger mb-3">Access Denied</h2>
                        <p class="text-muted mb-4">You do not have permission to create purchase requests.</p>
                        <div class="d-grid gap-2">
                            <a href="ListPurchaseRequests" class="btn btn-primary">Back to Purchase Requests</a>
                            <a href="dashboardmaterial" class="btn btn-outline-secondary">Back to Dashboard</a>
                        </div>
                    </div>
                </c:if>
                <c:if test="${hasCreatePurchaseRequestPermission}">
                    <jsp:include page="Header.jsp"/>
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-3 col-lg-2 bg-light p-0">
                                <jsp:include page="SidebarEmployee.jsp" />
                            </div>
                            <div class="col-md-9 col-lg-10">
                                <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                                    <div class="container">
                                        <div class="row my-5 py-5">
                                            <div class="col-12 bg-white p-4 rounded shadow purchase-form">
                                                <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Purchase Request</span></h2>
                                                <c:if test="${not empty errors}">
                                                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                                                        <ul style="margin-bottom: 0;">
                                                            <c:forEach var="error" items="${errors}">
                                                                <li>${error.value}</li>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                </c:if>
                                                <form action="CreatePurchaseRequest" method="post">
                                                    <div class="row g-3">
                                                        <div class="col-md-6">
                                                            <label class="form-label text-muted">Request Code</label>
                                                            <input type="text" class="form-control" name="requestCode" value="${requestCode}" readonly>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <label class="form-label text-muted">Request Date</label>
                                                            <input type="text" class="form-control" name="requestDate" value="${requestDate}" readonly>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <label class="form-label text-muted">Purchase Reason</label>
                                                            <textarea class="form-control" name="reason" rows="3" placeholder="Please describe the reason for purchase request...">${submittedReason}</textarea>
                                                        </div>
                                                    </div>
                                                    <h3 class="fw-normal mt-5 mb-3">Materials</h3>
                                                    <div id="materialList">
                                                        <div class="row material-row align-items-center gy-2">
                                                            <div class="col-md-3">
                                                                <label class="form-label text-muted">Material</label>
                                                                <input type="text" class="form-control material-name-input" name="materialName" placeholder="Type material name or code" autocomplete="off">
                                                                <input type="hidden" name="materialId" class="material-id-input">
                                                                <c:if test="${not empty errors.material_0}">
                                                                    <div class="text-danger small mt-1">${errors.material_0}</div>
                                                                </c:if>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label text-muted">Quantity</label>
                                                                <input type="number" class="form-control" name="quantity" min="1" step="1" oninput="this.value = this.value.replace(/[^0-9]/g, '')" placeholder="Enter quantity">
                                                                <c:if test="${not empty errors['quantity_0']}">
                                                                    <div class="text-danger small mt-1">${errors['quantity_0']}</div>
                                                                </c:if>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <label class="form-label text-muted">Notes</label>
                                                                <input type="text" class="form-control" name="note">
                                                            </div>
                                                            <div class="col-md-2">
                                                                <img class="material-image" src="<c:out value='${materials[0].materialsUrl}'/>" alt="Material Image">
                                                            </div>
                                                            <div class="col-md-1 d-flex align-items-center">
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
                                            </div>
                                        </div>
                                    </div>
                                </section>
                                                            
                            </div>
                                                             <jsp:include page="Footer.jsp" />
                        </div>
                    </div>
                </c:if>
        </c:otherwise>
    </c:choose>
    <script src="js/jquery-1.11.0.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        var materialsData = [
        <c:forEach var="material" items="${materials}" varStatus="status">
            {
                label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
                value: "${fn:escapeXml(material.materialName)}",
                id: "${material.materialId}",
                name: "${fn:escapeXml(material.materialName)}",
                code: "${fn:escapeXml(material.materialCode)}",
                imageUrl: "${fn:escapeXml(material.materialsUrl)}",
                categoryId: "${material.category.category_id}",
                categoryName: "${material.category.category_name}"
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
        ];
        function updateMaterialRowAutocomplete(row) {
            const nameInput = row.querySelector('.material-name-input');
            const idInput = row.querySelector('.material-id-input');
            const img = row.querySelector('.material-image');
            $(nameInput).autocomplete({
                source: function(request, response) {
                    const term = request.term.toLowerCase();
                    const matches = materialsData.filter(material => 
                        material.name.toLowerCase().includes(term) || 
                        material.code.toLowerCase().includes(term)
                    );
                    response(matches);
                },
                select: function(event, ui) {
                    idInput.value = ui.item.id;
                    nameInput.value = ui.item.name;
                    let imgUrl = ui.item.imageUrl && ui.item.imageUrl !== 'null' ? ui.item.imageUrl : '';
                    if (imgUrl.startsWith('http') || imgUrl.startsWith('/') || imgUrl.startsWith('images/material/')) {
                        img.src = imgUrl;
                    } else if (imgUrl) {
                        img.src = 'images/material/' + imgUrl;
                    } else {
                        img.src = 'images/material/default.jpg';
                    }
                },
                change: function(event, ui) {
                    if (!ui.item) {
                        const inputValue = nameInput.value.toLowerCase().trim();
                        const selectedMaterial = materialsData.find(material => 
                            material.name.toLowerCase() === inputValue || 
                            material.code.toLowerCase() === inputValue
                        );
                        if (selectedMaterial) {
                            idInput.value = selectedMaterial.id;
                            nameInput.value = selectedMaterial.name;
                            let imgUrl = selectedMaterial.imageUrl && selectedMaterial.imageUrl !== 'null' ? selectedMaterial.imageUrl : '';
                            if (imgUrl.startsWith('http') || imgUrl.startsWith('/') || imgUrl.startsWith('images/material/')) {
                                img.src = imgUrl;
                            } else if (imgUrl) {
                                img.src = 'images/material/' + imgUrl;
                            } else {
                                img.src = 'images/material/default.jpg';
                            }
                        } else {
                            idInput.value = '';
                            img.src = 'images/material/default.jpg';
                        }
                    }
                },
                minLength: 1
            });
        }
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.material-row').forEach(row => {
                updateMaterialRowAutocomplete(row);
            });
            
            // Restore submitted data if there were validation errors
            <c:if test="${not empty submittedMaterialNames}">
                const submittedMaterialNames = [
                    <c:forEach var="materialName" items="${submittedMaterialNames}" varStatus="status">
                        "${fn:escapeXml(materialName)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedQuantities = [
                    <c:forEach var="quantity" items="${submittedQuantities}" varStatus="status">
                        "${fn:escapeXml(quantity)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedNotes = [
                    <c:forEach var="note" items="${submittedNotes}" varStatus="status">
                        "${fn:escapeXml(note)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                
                // Clear existing rows and restore data
                const materialList = document.getElementById('materialList');
                materialList.innerHTML = '';
                
                for (let i = 0; i < submittedMaterialNames.length; i++) {
                    if (i === 0) {
                        // Create first row manually
                        const firstRowTemplate = `
                            <div class="row material-row align-items-center gy-2">
                                <div class="col-md-3">
                                    <label class="form-label text-muted">Material</label>
                                    <input type="text" class="form-control material-name-input" name="materialName" placeholder="Type material name or code" autocomplete="off">
                                    <input type="hidden" name="materialId" class="material-id-input">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label text-muted">Quantity</label>
                                    <input type="number" class="form-control" name="quantity" min="1" step="1" oninput="this.value = this.value.replace(/[^0-9]/g, '')" placeholder="Enter quantity">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label text-muted">Notes</label>
                                    <input type="text" class="form-control" name="note">
                                </div>
                                <div class="col-md-2">
                                    <img class="material-image" src="images/material/default.jpg" alt="Material Image">
                                </div>
                                <div class="col-md-1 d-flex align-items-center">
                                    <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                </div>
                            </div>
                        `;
                        materialList.innerHTML = firstRowTemplate;
                    } else {
                        // Add new row for additional materials
                        const addButton = document.getElementById('addMaterial');
                        addButton.click();
                    }
                    
                    const currentRow = materialList.querySelector('.material-row:last-child');
                    if (currentRow) {
                        const nameInput = currentRow.querySelector('.material-name-input');
                        const quantityInput = currentRow.querySelector('input[name="quantity"]');
                        const noteInput = currentRow.querySelector('input[name="note"]');
                        
                        if (nameInput && submittedMaterialNames[i]) {
                            nameInput.value = submittedMaterialNames[i];
                            // Trigger autocomplete to set material ID and image
                            setTimeout(() => {
                                $(nameInput).autocomplete('search', submittedMaterialNames[i]);
                            }, 100);
                        }
                        if (quantityInput && submittedQuantities[i]) {
                            quantityInput.value = submittedQuantities[i];
                        }
                        if (noteInput && submittedNotes[i]) {
                            noteInput.value = submittedNotes[i];
                        }
                        
                        // Update autocomplete for the new row
                        updateMaterialRowAutocomplete(currentRow);
                    }
                }
            </c:if>
        });
        document.getElementById('addMaterial').addEventListener('click', function () {
            const materialList = document.getElementById('materialList');
            const firstRow = materialList.querySelector('.material-row');
            const newRow = firstRow.cloneNode(true);
            newRow.querySelector('.material-name-input').value = '';
            newRow.querySelector('.material-id-input').value = '';
            newRow.querySelector('input[name="quantity"]').value = '';
            newRow.querySelector('input[name="note"]').value = '';
            newRow.querySelector('.material-image').src = 'images/material/default.jpg';
            materialList.appendChild(newRow);
            updateMaterialRowAutocomplete(newRow);
        });
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('remove-material')) {
                const materialRows = document.querySelectorAll('.material-row');
                if (materialRows.length > 1) {
                    e.target.closest('.material-row').remove();
                }
            }
        });
    </script>
</body>
</html>