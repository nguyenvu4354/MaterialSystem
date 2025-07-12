<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Unit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .card-header-brown {
            background-color: #DEB887;
            color: #fff;
            font-size: 2rem;
            font-weight: bold;
            border-radius: 8px 8px 0 0;
            padding: 18px 32px;
        }
        .unit-card {
            border-radius: 8px;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08);
            background: #fff;
            max-width: 900px;
            margin: 40px auto;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .form-control.is-invalid {
            border-color: #dc3545;
        }
        .error-alert {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .error-list {
            margin: 0;
            padding-left: 20px;
        }
        .error-list li {
            margin-bottom: 5px;
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
        .permission-denied {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 20px;
            border-radius: 5px;
            text-align: center;
            margin: 40px auto;
            max-width: 600px;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <!-- Kiểm tra quyền trước khi hiển thị form -->
        <c:choose>
            <c:when test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_UNIT')}">
                <div class="unit-card card shadow">
                    <div class="card-header card-header-brown text-white">
                        Edit Unit
                    </div>
                    <div class="card-body p-4">
                        <!-- Error Alert Box -->
                        <c:if test="${not empty errors or not empty error}">
                            <div class="error-alert">
                                <h6 class="mb-2"><strong>Please correct the following errors:</strong></h6>
                                <ul class="error-list">
                                    <c:if test="${not empty error}">
                                        <li>${error}</li>
                                    </c:if>
                                    <c:forEach var="entry" items="${errors}">
                                        <li><strong>${entry.key}:</strong> ${entry.value}</li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <form action="EditUnit" method="post">
                            <input type="hidden" name="id" value="${unit.id}" />
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Unit Name</label>
                                    <input type="text" class="form-control ${not empty errors.unitName ? 'is-invalid' : ''}" 
                                           name="unitName" value="${not empty unitName ? unitName : unit.unitName}">
                                    <c:if test="${not empty errors.unitName}">
                                        <div class="error-message">${errors.unitName}</div>
                                    </c:if>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Symbol</label>
                                    <input type="text" class="form-control ${not empty errors.symbol ? 'is-invalid' : ''}" 
                                           name="symbol" value="${not empty symbol ? symbol : unit.symbol}">
                                    <c:if test="${not empty errors.symbol}">
                                        <div class="error-message">${errors.symbol}</div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Description</label>
                                <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" 
                                          name="description" rows="3">${not empty description ? description : unit.description}</textarea>
                                <c:if test="${not empty errors.description}">
                                    <div class="error-message">${errors.description}</div>
                                </c:if>
                            </div>
                            <div class="d-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-brown flex-fill px-4">Save Changes</button>
                                <a href="UnitList" class="btn btn-secondary flex-fill px-4">Cancel</a>
                            </div>
                        </form>
                        <!-- Chỉ hiển thị nút Delete nếu user có quyền DELETE_UNIT hoặc là admin -->
                        <c:if test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_UNIT')}">
                            <form action="DeleteUnit" method="post" onsubmit="return confirm('Are you sure you want to delete this unit?');" class="mt-2">
                                <input type="hidden" name="id" value="${unit.id}" />
                                <button type="submit" class="btn btn-danger w-100">Delete Unit</button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="permission-denied">
                    <h4><i class="fas fa-exclamation-triangle"></i> Access Denied</h4>
                    <p>Bạn không có quyền chỉnh sửa đơn vị.</p>
                    <a href="UnitList" class="btn btn-primary">Quay lại danh sách</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 