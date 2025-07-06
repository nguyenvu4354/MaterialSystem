<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
<!-- JSP page using JSTL for dynamic data display -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add New Material - Material Management</title>
    
    <!-- Import Bootstrap and FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

    <!-- Custom form styling -->
    <style>
        .form-select { padding: 0.6rem 1rem; border-color: #dee2e6; cursor: pointer; }
        .form-select:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25); }
        .form-select option { padding: 8px; }
        .required-field::after { content: "*"; color: red; margin-left: 4px; } /* Red asterisk for required fields */
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
                <h3 class="mb-0">Add New Material</h3>
            </div>

            <div class="card-body">
                <!-- Display error message if it exists -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <!-- Display success message if it exists -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        ${success}
                    </div>
                </c:if>

                <% Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                   if (errors != null && !errors.isEmpty()) { %>
                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                        <ul style="margin-bottom: 0;">
                        <% for (String err : errors.values()) { %>
                            <li><%= err %></li>
                        <% } %>
                        </ul>
                    </div>
                <% } %>

                <!-- Form for adding new material -->
                <form action="addmaterial" method="POST" enctype="multipart/form-data">
                    <!-- Material Code and Name -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialCode" class="form-label required-field">Material Code</label>
                            <input type="text" class="form-control" id="materialCode" name="materialCode" value="${param.materialCode != null ? param.materialCode : (materialCode != null ? materialCode : '')}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label for="materialName" class="form-label required-field">Material Name</label>
                            <input type="text" class="form-control" id="materialName" name="materialName" value="${param.materialName != null ? param.materialName : (materialName != null ? materialName : '')}">
                        </div>
                    </div>

                    <!-- Material Image: choose image or URL -->
                    <div class="mb-3">
                        <label class="form-label">Material Image</label>
                        <div class="mb-3">
                            <div id="imagePreview" style="display: none;">
                                <img id="previewImg" class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;">
                            </div>
                        </div>

                        <!-- Tabs for choosing image upload or URL input -->
                        <ul class="nav nav-tabs" id="imageInputTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="upload-tab" data-bs-toggle="tab" data-bs-target="#upload-content" type="button" role="tab">Media Upload</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="url-tab" data-bs-toggle="tab" data-bs-target="#url-content" type="button" role="tab">Media URL</button>
                            </li>
                        </ul>

                        <!-- Content of each tab -->
                        <div class="tab-content pt-3" id="imageInputTabContent">
                            <!-- Image upload tab -->
                            <div class="tab-pane fade show active" id="upload-content" role="tabpanel">
                                <label class="form-label">Upload New Image</label>
                                <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                                <div class="form-text">Upload a new image file (Max size: 10MB)</div>
                            </div>

                            <!-- Image URL input tab -->
                            <div class="tab-pane fade" id="url-content" role="tabpanel">
                                <label class="form-label">Or Use Image URL</label>
                                <input type="text" class="form-control" id="materialsUrl" name="materialsUrl" placeholder="Enter an image URL from the internet">
                                <div class="form-text">Enter an image URL from the internet</div>
                            </div>
                        </div>
                    </div>

                    <!-- Category and Unit -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="categoryId" class="form-label required-field">Category</label>
                            <select class="form-select" id="categoryId" name="categoryId">
                                <option value="">Select Category</option>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.category_id}" <c:if test="${(param.categoryId != null ? param.categoryId : categoryId) == category.category_id}">selected</c:if>>${category.category_name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="unitId" class="form-label required-field">Unit</label>
                            <select class="form-select" id="unitId" name="unitId">
                                <option value="" selected disabled>Select Unit</option>
                                <c:forEach items="${units}" var="unit">
                                    <option value="${unit.id}" <c:if test="${(param.unitId != null ? param.unitId : unitId) == unit.id}">selected</c:if>>${unit.unitName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <!-- Price and Condition -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="price" class="form-label required-field">Price ($)</label>
                            <input type="number" class="form-control" id="price" name="price" value="${param.price != null ? param.price : (price != null ? price : '')}">
                        </div>
                        <div class="col-md-6">
                            <label for="conditionPercentage" class="form-label required-field">Condition (%)</label>
                            <input type="number" class="form-control" id="conditionPercentage" name="conditionPercentage" value="${param.conditionPercentage != null ? param.conditionPercentage : (conditionPercentage != null ? conditionPercentage : '100')}">
                        </div>
                    </div>

                    <!-- Material Status -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialStatus" class="form-label required-field">Status</label>
                            <select class="form-select" id="materialStatus" name="materialStatus">
                                <option value="NEW" <c:if test="${(param.materialStatus != null ? param.materialStatus : materialStatus) == 'NEW'}">selected</c:if>>New</option>
                                <option value="USED" <c:if test="${(param.materialStatus != null ? param.materialStatus : materialStatus) == 'USED'}">selected</c:if>>Used</option>
                                <option value="DAMAGED" <c:if test="${(param.materialStatus != null ? param.materialStatus : materialStatus) == 'DAMAGED'}">selected</c:if>>Damaged</option>
                            </select>
                        </div>
                    </div>

                    <!-- Submit and Cancel Buttons -->
                    <div class="mt-4">
                        <button type="submit" class="btn btn-brown">
                            <i class="fas fa-save"></i> Save Material
                        </button>
                        <a href="dashboardmaterial" class="btn btn-secondary ms-2">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Import Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Preview ảnh khi chọn file hoặc nhập URL, không validate gì ở client
        document.getElementById('imageFile').addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                document.getElementById('materialsUrl').value = '';
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('previewImg').src = e.target.result;
                    document.getElementById('imagePreview').style.display = 'block';
                }
                reader.readAsDataURL(file);
                const uploadTab = document.getElementById('upload-tab');
                bootstrap.Tab.getOrCreateInstance(uploadTab).show();
            }
        });

        document.getElementById('materialsUrl').addEventListener('input', function(event) {
            const url = event.target.value.trim();
            if (url) {
                document.getElementById('imageFile').value = '';
                // Nếu là tên file, tự động thêm prefix images/material/
                let previewUrl = url;
                if (!url.startsWith('http')) {
                    previewUrl = 'images/material/' + url;
                }
                document.getElementById('previewImg').src = previewUrl;
                document.getElementById('imagePreview').style.display = 'block';
                const urlTab = document.getElementById('url-tab');
                bootstrap.Tab.getOrCreateInstance(urlTab).show();
            }
        });

        document.getElementById('imageInputTabs').addEventListener('shown.bs.tab', function (event) {
            if (event.target.id === 'upload-tab') {
                document.getElementById('materialsUrl').value = '';
            } else if (event.target.id === 'url-tab') {
                document.getElementById('imageFile').value = '';
            }
        });
    </script>
</body>
</html>
