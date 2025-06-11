<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Trang JSP sử dụng JSTL để hiển thị dữ liệu động -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add New Material - Material Management</title>
    
    <!-- Import Bootstrap và FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

    <!-- Style tùy chỉnh cho form -->
    <style>
        .form-select { padding: 0.6rem 1rem; border-color: #dee2e6; cursor: pointer; }
        .form-select:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25); }
        .form-select option { padding: 8px; }
        .required-field::after { content: "*"; color: red; margin-left: 4px; } <!-- Dấu * đỏ cho trường bắt buộc -->
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h3 class="mb-0">Add New Material</h3>
            </div>

            <div class="card-body">
                <!-- Hiển thị thông báo lỗi nếu có -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <!-- Hiển thị thông báo thành công nếu có -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        ${success}
                    </div>
                </c:if>

                <!-- Form thêm mới vật tư -->
                <form action="addmaterial" method="POST" class="needs-validation" enctype="multipart/form-data" novalidate>
                    <!-- Mã và Tên vật tư -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialCode" class="form-label required-field">Material Code</label>
                            <input type="text" class="form-control" id="materialCode" name="materialCode" required>
                            <div class="invalid-feedback">Please enter a material code.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="materialName" class="form-label required-field">Material Name</label>
                            <input type="text" class="form-control" id="materialName" name="materialName" required>
                            <div class="invalid-feedback">Please enter a material name.</div>
                        </div>
                    </div>

                    <!-- Ảnh vật tư: chọn ảnh hoặc URL -->
                    <div class="mb-3">
                        <label class="form-label">Material Image</label>
                        <div class="mb-3">
                            <div id="imagePreview" style="display: none;">
                                <img id="previewImg" class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;">
                            </div>
                        </div>

                        <!-- Tabs lựa chọn upload ảnh hoặc nhập URL -->
                        <ul class="nav nav-tabs" id="imageInputTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="upload-tab" data-bs-toggle="tab" data-bs-target="#upload-content" type="button" role="tab">Media Upload</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="url-tab" data-bs-toggle="tab" data-bs-target="#url-content" type="button" role="tab">Media URL</button>
                            </li>
                        </ul>

                        <!-- Nội dung của từng tab -->
                        <div class="tab-content pt-3" id="imageInputTabContent">
                            <!-- Tab upload ảnh -->
                            <div class="tab-pane fade show active" id="upload-content" role="tabpanel">
                                <label class="form-label">Upload New Image</label>
                                <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                                <div class="form-text">Upload a new image file (Max size: 10MB)</div>
                            </div>

                            <!-- Tab nhập URL ảnh -->
                            <div class="tab-pane fade" id="url-content" role="tabpanel">
                                <label class="form-label">Or Use Image URL</label>
                                <input type="text" class="form-control" id="materialsUrl" name="materialsUrl" placeholder="Enter an image URL from the internet">
                                <div class="form-text">Enter an image URL from the internet</div>
                            </div>
                        </div>
                    </div>

                    <!-- Danh mục và đơn vị -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="categoryId" class="form-label required-field">Category</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">Select Category</option>
                                <!-- Lặp danh sách category -->
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.category_id}">${category.category_name}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">Please select a category.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="unitId" class="form-label required-field">Unit</label>
                            <select class="form-select" id="unitId" name="unitId" required>
                                <option value="" selected disabled>Select Unit</option>
                                <c:forEach items="${units}" var="unit">
                                    <option value="${unit.id}">${unit.unitName}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">Please select a unit.</div>
                        </div>
                    </div>

                    <!-- Giá và tình trạng vật tư -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="price" class="form-label required-field">Price ($)</label>
                            <input type="number" class="form-control" id="price" name="price" min="0.01" step="0.01" required oninput="validatePrice(this)" onchange="validatePrice(this)">
                            <div class="invalid-feedback">Price must be greater than $0</div>
                        </div>
                        <div class="col-md-6">
                            <label for="conditionPercentage" class="form-label required-field">Condition (%)</label>
                            <input type="number" class="form-control" id="conditionPercentage" name="conditionPercentage" min="0" max="100" value="100" required>
                            <div class="invalid-feedback">Please enter a condition between 0 and 100.</div>
                        </div>
                    </div>

                    <!-- Trạng thái vật tư -->
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="materialStatus" class="form-label required-field">Status</label>
                            <select class="form-select" id="materialStatus" name="materialStatus" required>
                                <option value="NEW">New</option>
                                <option value="USED">Used</option>
                                <option value="DAMAGED">Damaged</option>
                            </select>
                            <div class="invalid-feedback">Please select a status.</div>
                        </div>
                    </div>

                    <!-- Nút Submit và Cancel -->
                    <div class="mt-4">
                        <button type="submit" class="btn btn-primary">
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
        // Xác thực form khi submit
        (function () {
            'use strict'
            var forms = document.querySelectorAll('.needs-validation')
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity() || !validatePriceOnSubmit()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    form.classList.add('was-validated')
                }, false)
            })
        })()

        // Kiểm tra giá hợp lệ (> 0)
        function validatePrice(input) {
            const price = parseFloat(input.value);
            if (isNaN(price) || price <= 0) {
                input.setCustomValidity('Price must be greater than $0');
                return false;
            }
            input.setCustomValidity('');
            return true;
        }

        function validatePriceOnSubmit() {
            const priceInput = document.getElementById('price');
            return validatePrice(priceInput);
        }

        // Xử lý khi chọn file ảnh -> hiển thị ảnh + chuyển tab
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

        // Xử lý khi nhập URL ảnh -> hiển thị ảnh + chuyển tab
        document.getElementById('materialsUrl').addEventListener('input', function(event) {
            const url = event.target.value.trim();
            if (url) {
                document.getElementById('imageFile').value = '';
                document.getElementById('previewImg').src = url;
                document.getElementById('imagePreview').style.display = 'block';
                const urlTab = document.getElementById('url-tab');
                bootstrap.Tab.getOrCreateInstance(urlTab).show();
            }
        });

        // Khi chuyển giữa tab upload / url thì clear ô còn lại
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
