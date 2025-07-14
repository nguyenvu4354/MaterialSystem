<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="entity.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || user.getRoleId() != 1) {
        response.sendRedirect("error.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thêm Phòng Ban</title>
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
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="unit-card card shadow">
            <div class="card-header card-header-brown text-white">
                Add Department
            </div>
            <div class="card-body p-4">
                <!-- Error Alert Box -->
                <c:if test="${not empty errors or not empty error}">
                    <div class="error-alert">
                        <h6 class="mb-2"><strong>Please fix the following errors:</strong></h6>
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
                <form action="depairmentlist" method="post" id="departmentForm">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Department Name</label>
                            <input type="text" class="form-control" name="name" value="${name}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Phone number</label>
                            <input type="text" class="form-control" name="phone" value="${phone}">
                            <div class="error-message" id="phoneError"></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" name="email" value="${email}">
                            <div class="error-message" id="emailError"></div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Location</label>
                            <input type="text" class="form-control" name="location" value="${location}">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="3">${description}</textarea>
                    </div>
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <button type="submit" class="btn btn-brown px-4">Add Department</button>
                        <a href="depairmentlist" class="btn btn-secondary px-4">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('departmentForm').addEventListener('submit', function(event) {
            let isValid = true;
            const phone = document.querySelector('input[name="phone"]').value.trim();
            const email = document.querySelector('input[name="email"]').value.trim();

            // Reset error messages
            document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
            document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

            // Validate phone
            if (phone && !/^\d{10,12}$/.test(phone)) {
                document.getElementById('phoneError').textContent = 'Số điện thoại phải chứa từ 10 đến 12 chữ số.';
                document.querySelector('input[name="phone"]').classList.add('is-invalid');
                isValid = false;
            }

            // Validate email
            if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                document.getElementById('emailError').textContent = 'Email không hợp lệ.';
                document.querySelector('input[name="email"]').classList.add('is-invalid');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    </script>
</body>
</html>