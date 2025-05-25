<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.Map" %>

<%
    User admin = (User) session.getAttribute("user");
    if (admin == null || admin.getRoleId() != 1) {
        response.sendRedirect("Login.jsp");
        return;
    }

    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    User enteredUser = (User) request.getAttribute("enteredUser");
    String dateOfBirthStr = (String) request.getAttribute("enteredDateOfBirth");
%>

<html lang="en">

    <head>
        <title>Waggy - Create New User</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="format-detection" content="telephone=no">
        <meta name="apple-mobile-web-app-capable" content="yes">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
    </head>

    <body>
        <section id="create-user" style="background: url('images/background-img.png') no-repeat;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="offset-md-3 col-md-6 my-5 bg-white p-4 rounded shadow">
                        <h2 class="display-4 fw-normal text-center mb-4">Create New <span class="text-primary">User</span></h2>

                        <% 
                          String message = (String) request.getAttribute("message");
                          String error = (String) request.getAttribute("error"); 
                        %>
                        <% if (message != null) { %>
                        <div class="alert alert-success"><%= message %></div>
                        <% } %>
                        <% if (error != null) { %>
                        <div class="alert alert-danger"><%= error %></div>
                        <% } %>

                        <form action="${pageContext.request.contextPath}/create-user" method="post" enctype="multipart/form-data">
                            <div class="mb-3">
                                <input type="text" class="form-control form-control-lg <% if(errors != null && errors.containsKey("username")) { %>is-invalid<% } %>" 
                                       name="username" id="username"
                                       placeholder="Enter Username" maxlength="50"
                                       value="<%= enteredUser != null ? enteredUser.getUsername() : "" %>" required>
                                <% if (errors != null && errors.containsKey("username")) { %>
                                <div class="invalid-feedback"><%= errors.get("username") %></div>
                                <% } %>
                            </div>

                            <div class="mb-3">
                                <input type="password" class="form-control form-control-lg <% if(errors != null && errors.containsKey("password")) { %>is-invalid<% } %>" 
                                       name="password" id="password"
                                       placeholder="Enter Password" maxlength="255" required>
                                <% if (errors != null && errors.containsKey("password")) { %>
                                <div class="invalid-feedback"><%= errors.get("password") %></div>
                                <% } %>
                            </div>

                            <div class="mb-3">
                                <input type="text" class="form-control form-control-lg" name="fullName" id="fullName"
                                       placeholder="Enter Full Name" maxlength="100"
                                       value="<%= enteredUser != null ? enteredUser.getFullName() : "" %>">
                            </div>

                            <div class="mb-3">
                                <input type="email" class="form-control form-control-lg <% if(errors != null && errors.containsKey("email")) { %>is-invalid<% } %>" 
                                       name="email" id="email"
                                       placeholder="Enter Email" maxlength="100"
                                       value="<%= enteredUser != null ? enteredUser.getEmail() : "" %>">
                                <% if (errors != null && errors.containsKey("email")) { %>
                                <div class="invalid-feedback"><%= errors.get("email") %></div>
                                <% } %>
                            </div>

                            <div class="mb-3">
                                <input type="text" class="form-control form-control-lg <% if(errors != null && errors.containsKey("phoneNumber")) { %>is-invalid<% } %>" 
                                       name="phoneNumber" id="phoneNumber"
                                       placeholder="Enter Phone Number" maxlength="20"
                                       value="<%= enteredUser != null ? enteredUser.getPhoneNumber() : "" %>">
                                <% if (errors != null && errors.containsKey("phoneNumber")) { %>
                                <div class="invalid-feedback"><%= errors.get("phoneNumber") %></div>
                                <% } %>
                            </div>

                            <div class="mb-3">
                                <input type="text" class="form-control form-control-lg" name="address" id="address"
                                       placeholder="Enter Address" maxlength="255"
                                       value="<%= enteredUser != null ? enteredUser.getAddress() : "" %>">
                            </div>

                            <div class="mb-3">
                                <label for="dateOfBirth" class="form-label text-muted">Date of Birth</label>
                                <input type="date" class="form-control form-control-lg" name="dateOfBirth" id="dateOfBirth"
                                       value="<%= dateOfBirthStr != null ? dateOfBirthStr : "" %>">
                            </div>

                            <div class="mb-3">
                                <select class="form-select form-select-lg" name="gender" id="gender">
                                    <option value="" <%= (enteredUser == null || enteredUser.getGender() == null) ? "selected" : "" %>>Select Gender</option>
                                    <option value="male" <%= (enteredUser != null && "male".equalsIgnoreCase(enteredUser.getGender() != null ? enteredUser.getGender().name() : "") ) ? "selected" : "" %>>Male</option>
                                    <option value="female" <%= (enteredUser != null && "female".equalsIgnoreCase(enteredUser.getGender() != null ? enteredUser.getGender().name() : "") ) ? "selected" : "" %>>Female</option>
                                    <option value="other" <%= (enteredUser != null && "other".equalsIgnoreCase(enteredUser.getGender() != null ? enteredUser.getGender().name() : "") ) ? "selected" : "" %>>Other</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <select class="form-select form-select-lg <% if(errors != null && errors.containsKey("roleId")) { %>is-invalid<% } %>" 
                                        name="roleId" id="roleId" required>
                                    <option value="" <%= (enteredUser == null || enteredUser.getRoleId() == 0) ? "selected" : "" %>>Select Role</option>
                                    <option value="2" <%= (enteredUser != null && enteredUser.getRoleId() == 2) ? "selected" : "" %>>Warehouse staff</option>
                                    <option value="3" <%= (enteredUser != null && enteredUser.getRoleId() == 3) ? "selected" : "" %>>Director</option>
                                    <option value="4" <%= (enteredUser != null && enteredUser.getRoleId() == 4) ? "selected" : "" %>>Employee</option>
                                </select>
                                <% if (errors != null && errors.containsKey("roleId")) { %>
                                <div class="invalid-feedback"><%= errors.get("roleId") %></div>
                                <% } %>
                            </div>

                            <div class="mb-3">
                                <textarea class="form-control form-control-lg" name="description" id="description" rows="3"
                                          placeholder="Enter Description"><%= enteredUser != null ? enteredUser.getDescription() : "" %></textarea>
                            </div>

                            <div class="mb-3">
                                <input class="form-control form-control-lg" type="file" name="userPicture" id="userPicture" accept="image/*">
                            </div>

                            <div class="d-grid gap-2 mb-3">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Create User</button>
                            </div>
                            <div class="d-grid gap-2 mb-3">
                                <a href="UserList.jsp" class="btn btn-outline-secondary btn-lg rounded-1"> Back to User List</a>
                            </div>

                        </form>

                    </div>
                </div>
            </div>
        </section>

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    </body>

</html>