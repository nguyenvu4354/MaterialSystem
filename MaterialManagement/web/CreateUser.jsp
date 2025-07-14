<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="entity.User, entity.Department, java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User admin = (User) session.getAttribute("user");
    if (admin == null || admin.getRoleId() != 1) {
        response.sendRedirect("Login.jsp");
        return;
    }
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    User enteredUser = (User) request.getAttribute("enteredUser");
    String dateOfBirthStr = (String) request.getAttribute("enteredDateOfBirth");
    String enteredGender = (String) request.getAttribute("enteredGender");
    String enteredRoleId = (String) request.getAttribute("enteredRoleId");
    String enteredDepartmentId = (String) request.getAttribute("enteredDepartmentId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Waggy - Create New User</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap & Custom CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .two-column-form .form-control, .two-column-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .two-column-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .two-column-form .btn {
            font-size: 1rem;
            padding: 0.75rem;
        }
        .two-column-form .img-thumbnail {
            max-height: 150px;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>

            <!-- Content -->
            <div class="col-md-9 col-lg-10 px-md-4 py-4">
                <section id="CreateUser" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                    <div class="container">
                        <div class="row my-5 py-5">
                            <div class="col-10 bg-white p-4 mx-auto rounded shadow two-column-form">
                                <h2 class="display-4 fw-normal text-center mb-4">Create New <span class="text-primary">User</span></h2>
                                <%
                                    String message = (String) request.getAttribute("message");
                                    String error = (String) request.getAttribute("error");
                                %>
                                <% if (message != null) { %>
                                    <div class="alert alert-success">${message}</div>
                                <% } %>
                                <% if (error != null) { %>
                                    <div class="alert alert-danger">${error}</div>
                                <% } %>
                                <form action="${pageContext.request.contextPath}/CreateUser" method="post" enctype="multipart/form-data">
                                    <div class="row">
                                        <!-- Left Column -->
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="username" class="form-label text-muted">Username</label>
                                                <input type="text" class="form-control <% if(errors != null && errors.containsKey("username")) { %>is-invalid<% } %>" 
                                                       name="username" id="username"
                                                       placeholder="Enter Username" maxlength="50"
                                                       value="<%= enteredUser != null && enteredUser.getUsername() != null ? enteredUser.getUsername() : "" %>" required>
                                                <% if (errors != null && errors.containsKey("username")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("username") %></div>
                                                <% } %>
                                            </div>
                                            <div class="mb-3">
                                                <label for="password" class="form-label text-muted">Password</label>
                                                <input type="password" class="form-control <% if(errors != null && errors.containsKey("password")) { %>is-invalid<% } %>" 
                                                       name="password" id="password"
                                                       placeholder="Enter Password" maxlength="255" required>
                                                <% if (errors != null && errors.containsKey("password")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("password") %></div>
                                                <% } %>
                                            </div>
                                            <div class="mb-3">
                                                <label for="fullName" class="form-label text-muted">Full Name</label>
                                                <input type="text" class="form-control" name="fullName" id="fullName"
                                                       placeholder="Enter Full Name" maxlength="100"
                                                       value="<%= enteredUser != null && enteredUser.getFullName() != null ? enteredUser.getFullName() : "" %>">
                                            </div>
                                            <div class="mb-3">
                                                <label for="email" class="form-label text-muted">Email</label>
                                                <input type="email" class="form-control <% if(errors != null && errors.containsKey("email")) { %>is-invalid<% } %>" 
                                                       name="email" id="email"
                                                       placeholder="Enter Email" maxlength="100"
                                                       value="<%= enteredUser != null && enteredUser.getEmail() != null ? enteredUser.getEmail() : "" %>">
                                                <% if (errors != null && errors.containsKey("email")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("email") %></div>
                                                <% } %>
                                            </div>
                                            <div class="mb-3">
                                                <label for="phoneNumber" class="form-label text-muted">Phone Number</label>
                                                <input type="text" class="form-control <% if(errors != null && errors.containsKey("phoneNumber")) { %>is-invalid<% } %>" 
                                                       name="phoneNumber" id="phoneNumber"
                                                       placeholder="Enter Phone Number" maxlength="20"
                                                       value="<%= enteredUser != null && enteredUser.getPhoneNumber() != null ? enteredUser.getPhoneNumber() : "" %>">
                                                <% if (errors != null && errors.containsKey("phoneNumber")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("phoneNumber") %></div>
                                                <% } %>
                                            </div>
                                        </div>
                                        <!-- Right Column -->
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="address" class="form-label text-muted">Address</label>
                                                <input type="text" class="form-control" name="address" id="address"
                                                       placeholder="Enter Address" maxlength="255"
                                                       value="<%= enteredUser != null && enteredUser.getAddress() != null ? enteredUser.getAddress() : "" %>">
                                            </div>
                                            <div class="mb-3">
                                                <label for="dateOfBirth" class="form-label text-muted">Date of Birth</label>
                                                <input type="date" class="form-control" name="dateOfBirth" id="dateOfBirth"
                                                       value="<%= dateOfBirthStr != null ? dateOfBirthStr : "" %>">
                                            </div>
                                            <div class="mb-3">
                                                <label for="gender" class="form-label text-muted">Gender</label>
                                                <select class="form-select" name="gender" id="gender">
                                                    <option value="" <%= enteredGender == null || enteredGender.isEmpty() ? "selected" : "" %>>Select Gender</option>
                                                    <option value="male" <%= "male".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Male</option>
                                                    <option value="female" <%= "female".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Female</option>
                                                    <option value="other" <%= "other".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Other</option>
                                                </select>
                                            </div>
                                            <div class="mb-3">
                                                <label for="roleId" class="form-label text-muted">Role</label>
                                                <select class="form-select <% if(errors != null && errors.containsKey("roleId")) { %>is-invalid<% } %>" 
                                                        name="roleId" id="roleId" required>
                                                    <option value="" <%= enteredRoleId == null || enteredRoleId.isEmpty() ? "selected" : "" %>>Select Role</option>
                                                    <option value="2" <%= "2".equals(enteredRoleId) ? "selected" : "" %>>Director</option>
                                                    <option value="3" <%= "3".equals(enteredRoleId) ? "selected" : "" %>>Staff</option>
                                                    <option value="4" <%= "4".equals(enteredRoleId) ? "selected" : "" %>>Employee</option>
                                                </select>
                                                <% if (errors != null && errors.containsKey("roleId")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("roleId") %></div>
                                                <% } %>
                                            </div>
                                            <div class="mb-3">
                                                <label for="departmentId" class="form-label text-muted">Department</label>
                                                <select class="form-select <% if(errors != null && errors.containsKey("departmentId")) { %>is-invalid<% } %>" 
                                                        name="departmentId" id="departmentId">
                                                    <option value="" <%= enteredDepartmentId == null || enteredDepartmentId.isEmpty() ? "selected" : "" %>>Select Department</option>
                                                    <c:forEach var="dept" items="${departments}">
                                                        <option value="${dept.departmentId}" <%= enteredDepartmentId != null && enteredDepartmentId.equals(String.valueOf(((Department)pageContext.getAttribute("dept")).getDepartmentId())) ? "selected" : "" %>>
                                                            ${dept.departmentName}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <% if (errors != null && errors.containsKey("departmentId")) { %>
                                                    <div class="invalid-feedback"><%= errors.get("departmentId") %></div>
                                                <% } %>
                                            </div>
                                            <div class="mb-3">
                                                <label for="description" class="form-label text-muted">Description</label>
                                                <textarea class="form-control" name="description" id="description" rows="3"
                                                          placeholder="Enter Description"><%= enteredUser != null && enteredUser.getDescription() != null ? enteredUser.getDescription() : "" %></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="userPicture" class="form-label text-muted">User Picture</label>
                                                <input class="form-control" type="file" name="userPicture" id="userPicture" accept="image/*">
                                                <img id="previewImage" src="#" alt="Image Preview" class="img-thumbnail mt-3" style="display:none; max-height: 150px;">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="d-grid gap-2 mb-3">
                                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Create User</button>
                                            </div>
                                            <div class="d-grid gap-2 mb-3">
                                                <a href="UserList" class="btn btn-secondary btn-lg rounded-1">← Back to User List</a>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer py-4 bg-light mt-auto">
        <div class="container text-center">
            <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
    <script src="js/jquery-1.11.0.min.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/script.js"></script>
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    <script>
        document.getElementById("userPicture").addEventListener("change", function () {
            const fileInput = this;
            const previewImage = document.getElementById("previewImage");
            if (fileInput.files && fileInput.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewImage.style.display = "block";
                };
                reader.readAsDataURL(fileInput.files[0]);
            } else {
                previewImage.src = "#";
                previewImage.style.display = "none";
            }
        });
    </script>
</body>
</html>