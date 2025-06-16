<%-- 
    Document   : PurchaseRequestForm
    Created on : Jun 10, 2025, 7:20:31 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Waggy - Tạo yêu cầu mua hàng</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <!-- Bootstrap & Fonts -->
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
            .error {
                color: red;
            }
            .success {
                color: green;
            }
        </style>
    </head>
    <body>
        <jsp:include page="HeaderAdmin.jsp"/>

        <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow purchase-form">
                        <h2 class="display-4 fw-normal text-center mb-4">Tạo <span class="text-primary">Yêu Cầu Mua Hàng</span></h2>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success">${success}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/SavePurchaseRequest" method="post">
                            <input type="hidden" name="purchaseRequestId" value="${request.purchaseRequestId}">
                            
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Mã yêu cầu</label>
                                    <input type="text" class="form-control" value="${request.requestCode}" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Ngày tạo</label>
                                    <input type="datetime-local" class="form-control" value="${request.requestDate}" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Người yêu cầu</label>
                                    <select class="form-select" name="userId" required>
                                        <c:forEach var="user" items="${users}">
                                            <option value="${user.userId}" ${request.userId == user.userId ? 'selected' : ''}>
                                                ${user.fullName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Giá dự kiến</label>
                                    <input type="number" class="form-control" name="estimatedPrice" value="${request.estimatedPrice}" min="0">
                                </div>
                                <div class="col-12">
                                    <label class="form-label text-muted">Lý do</label>
                                    <textarea class="form-control" name="reason" rows="3" required>${request.reason}</textarea>
                                </div>
                            </div>

                            <h3 class="fw-normal mt-5 mb-3">Danh sách vật tư</h3>
                            <div id="materialList">
                                <c:forEach var="detail" items="${requestDetails}" varStatus="loop">
                                    <div class="row material-row">
                                        <div class="col-md-4">
                                            <label class="form-label text-muted">Tên vật tư</label>
                                            <input type="text" class="form-control" name="materialName" value="${detail.materialName}" required>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label text-muted">Danh mục</label>
                                            <select class="form-select" name="categoryId" required>
                                                <c:forEach var="category" items="${categories}">
                                                    <option value="${category.categoryId}" ${detail.categoryId == category.categoryId ? 'selected' : ''}>
                                                        ${category.categoryName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label text-muted">Số lượng</label>
                                            <input type="number" class="form-control" name="quantity" value="${detail.quantity}" min="1" required>
                                        </div>
                                        <div class="col-md-2 d-flex align-items-end">
                                            <button type="button" class="btn btn-outline-danger remove-material">Xóa</button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <div class="mt-3">
                                <button type="button" class="btn btn-outline-secondary" id="addMaterial">Thêm vật tư</button>
                            </div>

                            <div class="mt-5 d-grid gap-2">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Lưu yêu cầu</button>
                                <a href="PurchaseRequestList.jsp" class="btn btn-outline-secondary btn-lg rounded-1">Quay lại danh sách</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.getElementById('addMaterial').addEventListener('click', function () {
                const materialList = document.getElementById('materialList');
                const newRow = document.createElement('div');
                newRow.className = 'row material-row';
                newRow.innerHTML = `
                    <div class="col-md-4">
                        <label class="form-label text-muted">Tên vật tư</label>
                        <input type="text" class="form-control" name="materialName" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label text-muted">Danh mục</label>
                        <select class="form-select" name="categoryId" required>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}">${category.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label text-muted">Số lượng</label>
                        <input type="number" class="form-control" name="quantity" min="1" required>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="button" class="btn btn-outline-danger remove-material">Xóa</button>
                    </div>
                `;
                materialList.appendChild(newRow);
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
