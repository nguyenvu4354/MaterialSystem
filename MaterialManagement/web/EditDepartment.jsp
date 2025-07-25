<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Department</title>
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
        .department-card {
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
    <jsp:include page="Header.jsp" />
    <div class="container mt-5">
        <c:choose>
            <c:when test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT')}">
                <div class="department-card card shadow">
                    <div class="card-header card-header-brown text-white">
                        Edit Department
                    </div>
                    <div class="card-body p-4">
                        <c:if test="${not empty error}">
                            <div class="error-alert">
                                <h6 class="mb-2"><strong>Please correct the following errors:</strong></h6>
                                <ul class="error-list">
                                    <li>${error}</li>
                                </ul>
                            </div>
                        </c:if>
                        <form action="editdepartment" method="post">
                            <input type="hidden" name="id" value="${department.departmentId}">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="name" class="form-label">Department Name</label>
                                    <input type="text" class="form-control" id="name" name="name" value="${department.departmentName}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="code" class="form-label">Department Code</label>
                                    <input type="text" class="form-control" id="code" name="code" value="${department.departmentCode}" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="phone" class="form-label">Phone Number</label>
                                <input type="text" class="form-control" id="phone" name="phone" value="${department.phoneNumber}">
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email" value="${department.email}">
                            </div>
                            <div class="mb-3">
                                <label for="location" class="form-label">Location</label>
                                <input type="text" class="form-control" id="location" name="location" value="${department.location}">
                            </div>
                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea class="form-control" id="description" name="description" rows="4">${department.description}</textarea>
                            </div>
                            <div class="mb-3">
                                <label for="status" class="form-label">Status</label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="active" ${department.status == 'active' ? 'selected' : ''}>Active</option>
                                    <option value="inactive" ${department.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                            <div class="d-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-brown flex-fill px-4">Update Department</button>
                                <a href="depairmentlist" class="btn btn-secondary flex-fill px-4">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="permission-denied">
                    <h4><i class="fas fa-exclamation-triangle"></i> Access Denied</h4>
                    <p>Bạn không có quyền chỉnh sửa phòng ban.</p>
                    <a href="depairmentlist" class="btn btn-primary">Quay lại danh sách</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>