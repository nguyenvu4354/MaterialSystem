<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
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

            .img-thumbnail {
                width: 150px;
                height: 150px;
                object-fit: cover;
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

            <!-- Card chính chứa form sửa vật tư -->
            <div class="card shadow">

                <!-- Header card màu xanh, tiêu đề -->
                <div class="card-header btn-brown text-white">
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

                    <% Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                       String nameError = (errors != null) ? errors.get("materialName") : null;
                    %>

                    <% if (errors != null && !errors.isEmpty()) { %>
                        <div class="alert alert-danger" style="margin-bottom: 16px;">
                            <ul style="margin-bottom: 0;">
                            <% for (String err : errors.values()) { %>
                                <li><%= err %></li>
                            <% } %>
                            </ul>
                        </div>
                    <% } %>

                    <!-- Form gửi POST lên servlet xử lý 'editmaterial', enctype để upload file ảnh -->
                    <form action="${pageContext.request.contextPath}/editmaterial" method="POST" enctype="multipart/form-data">

                        <!-- Ẩn input chứa ID vật tư để biết sửa cái nào -->
                        <input type="hidden" name="materialId" value="${m.materialId}">

                        <!-- Thông tin cơ bản -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="materialCode" class="form-label">Material Code</label>
                                <input type="text" class="form-control" id="materialCode" name="materialCode" 
                                       value="${m.materialCode}" readonly>
                            </div>
                            <div class="col-md-6">
                                <label for="materialName" class="form-label">Material Name</label>
                                <input type="text" class="form-control" id="materialName" name="materialName" value="${param.materialName != null ? param.materialName : m.materialName}">
                            </div>
                        </div>

                        <!-- Hình ảnh và trạng thái -->
                        <div class="mb-3">
                            <label class="form-label">Material Image</label>
                            <div class="mb-3">
                                <c:set var="imgUrl" value="" />
                                <c:choose>
                                    <c:when test="${not empty param.materialsUrl}">
                                        <c:choose>
                                            <c:when test="${param.materialsUrl.startsWith('http')}">
                                                <c:set var="imgUrl" value="${param.materialsUrl}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="imgUrl" value="images/material/${param.materialsUrl}" />
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:when test="${not empty m.materialsUrl}">
                                        <c:set var="imgUrl" value="images/material/${m.materialsUrl}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="imgUrl" value="images/material/default.jpg" />
                                    </c:otherwise>
                                </c:choose>
                                <img id="previewImage" src="${imgUrl}" alt="${m.materialCode}" class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;" onerror="this.onerror=null;this.src='images/material/default.jpg';">
                            </div>
                            <ul class="nav nav-tabs" id="imageInputTabs" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active" id="upload-tab" data-bs-toggle="tab" data-bs-target="#upload-content" type="button" role="tab">Media Upload</button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="url-tab" data-bs-toggle="tab" data-bs-target="#url-content" type="button" role="tab">Media URL</button>
                                </li>
                            </ul>
                            <div class="tab-content pt-3" id="imageInputTabContent">
                                <div class="tab-pane fade show active" id="upload-content" role="tabpanel">
                                    <label class="form-label">Upload New Image</label>
                                    <input type="file" class="form-control" id="materialImage" name="materialImage">
                                    <div class="form-text">Upload a new image file (Max size: 10MB)</div>
                                </div>
                                <div class="tab-pane fade" id="url-content" role="tabpanel">
                                    <label class="form-label">Or Use Image URL</label>
                                    <input type="text" class="form-control" id="materialsUrl" name="materialsUrl" value="${param.materialsUrl != null ? param.materialsUrl : (m.materialsUrl != null && m.materialsUrl.startsWith('material_images/') ? '' : m.materialsUrl)}" placeholder="Enter an image URL from the internet">
                                    <div class="form-text">Enter an image URL from the internet</div>
                                </div>
                            </div>
                            <div class="form-text text-info mt-2">
                                You can either upload a new image or provide an image URL. If both are provided, the uploaded file will take precedence.
                            </div>
                        </div>

                        <!-- Danh mục và đơn vị -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="categoryInput" class="form-label">Category</label>
                                <div style="position: relative; display: inline-block; width: 100%;">
                                    <input type="text" id="categoryInput" name="categoryName" class="form-control" placeholder="Category..." autocomplete="off" value="${param.categoryName != null ? param.categoryName : (m.category != null ? m.category.category_name : '')}" />
                                    <input type="hidden" id="categoryId" name="categoryId" value="${param.categoryId != null ? param.categoryId : (m.category != null ? m.category.category_id : '')}" />
                                    <div id="categorySuggestions" class="list-group" style="position: absolute; left: 0; top: 100%; z-index: 1000; width: 100%;"></div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="unitInput" class="form-label">Unit</label>
                                <div style="position: relative; display: inline-block; width: 100%;">
                                    <input type="text" id="unitInput" name="unitName" class="form-control" placeholder="Unit..." autocomplete="off" value="${param.unitName != null ? param.unitName : (m.unit != null ? m.unit.unitName : '')}" />
                                    <input type="hidden" id="unitId" name="unitId" value="${param.unitId != null ? param.unitId : (m.unit != null ? m.unit.id : '')}" />
                                    <div id="unitSuggestions" class="list-group" style="position: absolute; left: 0; top: 100%; z-index: 1000; width: 100%;"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Nhà cung cấp -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="materialStatus" class="form-label">Status</label>
                                <select class="form-select" id="materialStatus" name="materialStatus">
                                    <option value="new" <c:if test="${param.materialStatus != null ? param.materialStatus == 'new' : m.materialStatus == 'new'}">selected</c:if>>New</option>
                                    <option value="used" <c:if test="${param.materialStatus != null ? param.materialStatus == 'used' : m.materialStatus == 'used'}">selected</c:if>>Used</option>
                                    <option value="damaged" <c:if test="${param.materialStatus != null ? param.materialStatus == 'damaged' : m.materialStatus == 'damaged'}">selected</c:if>>Damaged</option>
                                </select>
                            </div>
                        </div>

                        <!-- Nút lưu và hủy -->
                        <div class="mt-4">
                            <button type="submit" class="btn btn-brown">
                                <i class="fas fa-save"></i> Save Changes
                            </button>
                            <a href="dashboardmaterial" class="btn btn-secondary ms-2">
                                <i class="fas fa-times"></i> Cancel
                            </a>
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

        <!-- JavaScript để xử lý preview ảnh -->
        <script>
            // Xử lý preview ảnh khi upload file
            document.getElementById('materialImage').addEventListener('change', function(event) {
                const file = event.target.files[0];
                const previewImage = document.getElementById('previewImage');
                
                if (file) {
                    // Kiểm tra xem file có phải là ảnh không
                    if (!file.type.startsWith('image/')) {
                        alert('Please select an image file!');
                        event.target.value = '';
                        return;
                    }
                    
                    // Kiểm tra kích thước file (10MB)
                    if (file.size > 10 * 1024 * 1024) {
                        alert('File size must be less than 10MB!');
                        event.target.value = '';
                        return;
                    }
                    
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        previewImage.src = e.target.result;
                    };
                    reader.readAsDataURL(file);
                    
                    // Xóa URL input khi upload file
                    document.getElementById('materialsUrl').value = '';
                }
            });
            
            // Xử lý preview ảnh khi nhập URL
            document.getElementById('materialsUrl').addEventListener('input', function(event) {
                const url = event.target.value.trim();
                const previewImage = document.getElementById('previewImage');
                
                if (url) {
                    // Kiểm tra xem URL có hợp lệ không
                    try {
                        new URL(url);
                        previewImage.src = url;
                    } catch (e) {
                        // URL không hợp lệ, không thay đổi preview
                    }
                } else {
                    // Nếu URL trống, hiển thị ảnh mặc định
                    previewImage.src = 'data:image/png;base64,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';
                }
            });
            
            // Xử lý khi chuyển tab
            document.addEventListener('DOMContentLoaded', function() {
                const uploadTab = document.getElementById('upload-tab');
                const urlTab = document.getElementById('url-tab');
                
                uploadTab.addEventListener('click', function() {
                    // Xóa URL input khi chuyển sang tab upload
                    document.getElementById('materialsUrl').value = '';
                });
                
                urlTab.addEventListener('click', function() {
                    // Xóa file input khi chuyển sang tab URL
                    document.getElementById('materialImage').value = '';
                });
            });
            
            // Hàm xác nhận xóa material
            function confirmDelete() {
                if (confirm('Are you sure you want to delete this material? This action cannot be undone.')) {
                    const materialId = document.querySelector('input[name="materialId"]').value;
                    window.location.href = '${pageContext.request.contextPath}/deletematerial?materialId=' + materialId;
                }
            }
        </script>
        <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Nếu có giá trị materialsUrl (tức là vừa nhập URL), tự động chuyển sang tab Media URL
            var urlValue = '<c:out value="${param.materialsUrl}" />';
            if (urlValue && urlValue.trim() !== '') {
                var urlTab = document.getElementById('url-tab');
                var urlTabContent = new bootstrap.Tab(urlTab);
                urlTabContent.show();
            }
        });
        </script>
        <script>
        // Truyền categories và units sang JS
        var categories = [
            <c:forEach var="cat" items="${categories}" varStatus="loop">
                {id: ${cat.category_id}, name: "${cat.category_name}"}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        var units = [
            <c:forEach var="u" items="${units}" varStatus="loop">
                {id: ${u.id}, name: "${u.unitName}"}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];

        // Autocomplete cho category
        const categoryInput = document.getElementById('categoryInput');
        const categorySuggestions = document.getElementById('categorySuggestions');
        const categoryHiddenId = document.getElementById('categoryId');
        categoryInput.addEventListener('input', function() {
            const value = this.value.trim().toLowerCase();
            categorySuggestions.innerHTML = '';
            categoryHiddenId.value = '';
            if (value.length === 0) {
                categorySuggestions.style.display = 'none';
                return;
            }
            const matches = categories.filter(cat => cat.name.toLowerCase().includes(value));
            if (matches.length === 0) {
                categorySuggestions.style.display = 'none';
                return;
            }
            matches.forEach(cat => {
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'list-group-item list-group-item-action';
                item.textContent = cat.name;
                item.onclick = function() {
                    categoryInput.value = cat.name;
                    categoryHiddenId.value = cat.id;
                    categorySuggestions.innerHTML = '';
                    categorySuggestions.style.display = 'none';
                };
                categorySuggestions.appendChild(item);
            });
            categorySuggestions.style.display = 'block';
        });
        document.addEventListener('click', function(e) {
            if (!categoryInput.contains(e.target) && !categorySuggestions.contains(e.target)) {
                categorySuggestions.innerHTML = '';
                categorySuggestions.style.display = 'none';
            }
        });

        // Autocomplete cho unit
        const unitInput = document.getElementById('unitInput');
        const unitSuggestions = document.getElementById('unitSuggestions');
        const unitHiddenId = document.getElementById('unitId');
        unitInput.addEventListener('input', function() {
            const value = this.value.trim().toLowerCase();
            unitSuggestions.innerHTML = '';
            unitHiddenId.value = '';
            if (value.length === 0) {
                unitSuggestions.style.display = 'none';
                return;
            }
            const matches = units.filter(u => u.name.toLowerCase().includes(value));
            if (matches.length === 0) {
                unitSuggestions.style.display = 'none';
                return;
            }
            matches.forEach(u => {
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'list-group-item list-group-item-action';
                item.textContent = u.name;
                item.onclick = function() {
                    unitInput.value = u.name;
                    unitHiddenId.value = u.id;
                    unitSuggestions.innerHTML = '';
                    unitSuggestions.style.display = 'none';
                };
                unitSuggestions.appendChild(item);
            });
            unitSuggestions.style.display = 'block';
        });
        document.addEventListener('click', function(e) {
            if (!unitInput.contains(e.target) && !unitSuggestions.contains(e.target)) {
                unitSuggestions.innerHTML = '';
                unitSuggestions.style.display = 'none';
            }
        });
    </script>
    </body>
</html>
