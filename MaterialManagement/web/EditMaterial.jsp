<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Edit Material - Material Management</title>

        <!-- Bootstrap CSS để dùng style và responsive -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- FontAwesome để dùng icon -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <!-- Style tùy chỉnh cho select input -->
        <style>
            .form-select {
                padding: 0.6rem 1rem;
                border-color: #dee2e6;
                cursor: pointer;
            }

            .form-select:focus {
                border-color: #86b7fe;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
            }

            .form-select option {
                padding: 8px;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="container mt-5">

            <!-- Card chính chứa form sửa vật tư -->
            <div class="card shadow">

                <!-- Header card màu xanh, tiêu đề -->
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Edit Material</h3>
                </div>

                <div class="card-body">

                    <!-- Hiển thị lỗi nếu có -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            ${error}
                        </div>
                    </c:if>

                    <!-- Hiển thị thông báo thành công nếu có -->
                    <c:if test="${not empty param.success}">
                        <div class="alert alert-success" role="alert">
                            ${param.success}
                        </div>
                    </c:if>

                    <!-- Form gửi POST lên servlet xử lý 'editmaterial', enctype để upload file ảnh -->
                    <form action="editmaterial" method="POST" class="needs-validation" enctype="multipart/form-data" novalidate>
                        
                        <!-- Ẩn input chứa ID vật tư để biết sửa cái nào -->
                        <input type="hidden" name="materialId" value="${details.material.materialId}">

                        <!-- Row 1: Mã vật tư và tên vật tư -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="materialCode" class="form-label">Material Code</label>
                                <input type="text" class="form-control" id="materialCode" name="materialCode" 
                                       value="${details.material.materialCode}" required>
                            </div>
                            <div class="col-md-6">
                                <label for="materialName" class="form-label">Material Name</label>
                                <input type="text" class="form-control" id="materialName" name="materialName" 
                                       value="${details.material.materialName}" required>
                            </div>
                        </div>

                        <!-- Phần hiển thị và nhập ảnh vật tư -->
                        <div class="mb-3">
                            <label class="form-label">Material Image</label>

                            <!-- Hiển thị ảnh hiện tại với kích thước cố định -->
                            <div class="mb-3">
                                <img src="${details.material.materialsUrl}" alt="${details.material.materialCode}" 
                                     class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;">
                            </div>

                            <!-- Tabs cho chọn cách nhập ảnh: upload file hoặc URL -->
                            <ul class="nav nav-tabs" id="imageInputTabs" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active" id="upload-tab" data-bs-toggle="tab" 
                                            data-bs-target="#upload-content" type="button" role="tab">Media Upload</button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="url-tab" data-bs-toggle="tab" 
                                            data-bs-target="#url-content" type="button" role="tab">Media URL</button>
                                </li>
                            </ul>

                            <!-- Nội dung từng tab -->
                            <div class="tab-content pt-3" id="imageInputTabContent">

                                <!-- Tab upload file -->
                                <div class="tab-pane fade show active" id="upload-content" role="tabpanel">
                                    <label class="form-label">Upload New Image</label>
                                    <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                                    <div class="form-text">Upload a new image file (Max size: 10MB)</div>
                                </div>

                                <!-- Tab nhập URL ảnh -->
                                <div class="tab-pane fade" id="url-content" role="tabpanel">
                                    <label class="form-label">Or Use Image URL</label>
                                    <input type="text" class="form-control" id="materialsUrl" name="materialsUrl" 
                                           value="${details.material.materialsUrl.startsWith('material_images/') ? '' : details.material.materialsUrl}"
                                           placeholder="Enter an image URL from the internet">
                                    <div class="form-text">Enter an image URL from the internet</div>
                                </div>
                            </div>

                            <!-- Ghi chú cách sử dụng -->
                            <div class="form-text text-info mt-2">
                                You can either upload a new image or provide an image URL. If both are provided, the uploaded file will take precedence.
                            </div>
                        </div>

                        <!-- Row 2: Chọn category và nhập số lượng -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="categoryId" class="form-label">Category</label>
                                <select class="form-select" id="categoryId" name="categoryId" required>
                                    <option value="">Select Category</option>
                                    <!-- Lặp qua danh sách category để tạo option -->
                                    <c:forEach items="${categories}" var="category">
                                        <option value="${category.category_id}" ${details.material.categoryId == category.category_id ? 'selected' : ''}>
                                            ${category.category_name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="quantity" class="form-label">Quantity</label>
                                <input type="number" class="form-control" id="quantity" name="quantity" 
                                       value="${details.material.quantity}" min="0" required>
                            </div>
                        </div>

                        <!-- Row 3: Giá tiền và phần trăm tình trạng -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="price" class="form-label">Price ($)</label>
                                <input type="number" class="form-control" id="price" name="price" 
                                       value="${details.material.price}" min="0.01" step="0.01" required
                                       oninput="validatePrice(this)"
                                       onchange="validatePrice(this)">
                                <div class="invalid-feedback">
                                    Price must be greater than $0
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="conditionPercentage" class="form-label">Condition (%)</label>
                                <input type="number" class="form-control" id="conditionPercentage" name="conditionPercentage" 
                                       value="${details.material.conditionPercentage}" min="0" max="100" required>
                            </div>
                        </div>

                        <!-- Row 4: Trạng thái và nhà cung cấp -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="materialStatus" class="form-label">Status</label>
                                <select class="form-select" id="materialStatus" name="materialStatus" required>
                                    <option value="NEW" ${details.material.materialStatus == 'NEW' ? 'selected' : ''}>New</option>
                                    <option value="USED" ${details.material.materialStatus == 'USED' ? 'selected' : ''}>Used</option>
                                    <option value="DAMAGED" ${details.material.materialStatus == 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="supplierId" class="form-label">Supplier</label>
                                <select class="form-select" id="supplierId" name="supplierId">
                                    <option value="">Select Supplier</option>
                                    <!-- Lặp danh sách nhà cung cấp -->
                                    <c:forEach items="${suppliers}" var="supplier">
                                        <option value="${supplier.supplierId}" ${details.material.supplierId == supplier.supplierId ? 'selected' : ''}>
                                            ${supplier.supplierName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Nút submit, hủy và xóa -->
                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Save Changes
                            </button>

                            <!-- Nút trở về danh sách vật tư -->
                            <a href="dashboardmaterial" class="btn btn-secondary ms-2">
                                <i class="fas fa-times"></i> Cancel
                            </a>

                            <!-- Nút xóa vật tư, gọi confirm trước khi xóa -->
                            <button type="button" class="btn btn-danger ms-2" onclick="confirmDelete()">
                                <i class="fas fa-trash"></i> Delete Material
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS Bundle (chứa Popper.js) để hỗ trợ tương tác JS của Bootstrap -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            // Validation form với Bootstrap
            (function () {
                'use strict'
                var forms = document.querySelectorAll('.needs-validation')
                Array.prototype.slice.call(forms)
                        .forEach(function (form) {
                            form.addEventListener('submit', function (event) {
                                // Kiểm tra form có hợp lệ và giá tiền hợp lệ
                                if (!form.checkValidity() || !validatePriceOnSubmit()) {
                                    event.preventDefault()
                                    event.stopPropagation()
                                }
                                form.classList.add('was-validated')
                            }, false)
                        })
            })()

            // Hàm kiểm tra giá tiền phải lớn hơn 0
            function validatePrice(input) {
                const price = parseFloat(input.value);
                if (isNaN(price) || price <= 0) {
                    input.setCustomValidity('Price must be greater than $0');
                    return false;
                }
                input.setCustomValidity('');
                return true;
            }

            // Gọi kiểm tra giá tiền khi submit
            function validatePriceOnSubmit() {
                const priceInput = document.getElementById('price');
                return validatePrice(priceInput);
            }

            // Hàm làm sạch URL (nếu url bắt đầu bằng 'material_images/' thì bỏ đi)
            function cleanUrl(url) {
                return url.startsWith('material_images/') ? '' : url;
            }

            // Xử lý khi người dùng chọn file ảnh mới
            document.getElementById('imageFile').addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    // Xóa giá trị input URL
                    document.getElementById('materialsUrl').value = '';

                    // Hiển thị preview ảnh mới
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        document.querySelector('.img-thumbnail').src = e.target.result;
                    }
                    reader.readAsDataURL(file);

                    // Chuyển sang tab upload
                    const uploadTab = document.getElementById('upload-tab');
                    bootstrap.Tab.getOrCreateInstance(uploadTab).show();
                }
            });

            // Xử lý khi nhập URL ảnh
            document.getElementById('materialsUrl').addEventListener('input', function (event) {
                const url = event.target.value.trim();
                if (url) {
                    // Xóa giá trị input file
                    document.getElementById('imageFile').value = '';

                    // Cập nhật ảnh preview
                    document.querySelector('.img-thumbnail').src = url;

                    // Chuyển sang tab URL
                    const urlTab = document.getElementById('url-tab');
                    bootstrap.Tab.getOrCreateInstance(urlTab).show();
                }
            });

            // Khi chuyển tab, xóa input không dùng để tránh nhập đồng thời 2 nguồn ảnh
            document.getElementById('imageInputTabs').addEventListener('shown.bs.tab', function (event) {
                if (event.target.id === 'upload-tab') {
                    document.getElementById('materialsUrl').value = '';
                } else if (event.target.id === 'url-tab') {
                    document.getElementById('imageFile').value = '';
                }
            });

            // Hàm confirm xóa vật tư
            function confirmDelete() {
                if (confirm("Are you sure you want to delete this material? This action cannot be undone.")) {
                    // Tạo form mới gửi POST xóa với materialId
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = 'deletematerial'; // servlet xử lý xóa vật tư
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = 'materialId';
                    input.value = '${details.material.materialId}';
                    form.appendChild(input);
                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>
    </body>
</html>
