<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    String genderStr = user.getGender() != null ? user.getGender().toString().toLowerCase() : "";
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <title>User Profile - Waggy</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="description" content="View and update your Waggy account profile.">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <style>
            body {
                background: url('images/background-img.png') no-repeat center center fixed;
                background-size: cover;
            }
        </style>
    </head>
    <body>
        <section class="py-5">
            <div class="container">

                <!-- Navigation buttons -->
                <div class="text-center mb-4">
                    <div class="btn-group" role="group" aria-label="Profile Navigation">
                        <a href="profile.jsp" class="btn btn-outline-primary">Profile</a>
                        <a href="change-password.jsp" class="btn btn-outline-secondary">Change Password</a>
                        <a href="my-applications.jsp" class="btn btn-outline-success">My Applications</a>
                    </div>
                </div>

                <h2 class="text-center display-5 mb-4">Profile of: <span class="text-primary"><%= user.getUsername() %></span></h2>

                <!-- Message displays -->
                <%
                    String message = (String) request.getAttribute("message");
                    String error = (String) request.getAttribute("error");
                    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                %>
                <% if (message != null) { %>
                <div class="alert alert-success text-center" role="alert">
                    <%= message %>
                </div>
                <% } %>
                <% if (error != null) { %>
                <div class="alert alert-danger text-center" role="alert">
                    <%= error %>
                </div>
                <% } %>
                <% if (errors != null && !errors.isEmpty()) { %>
                <div class="alert alert-warning" role="alert">
                    <ul class="mb-0">
                        <% for (Map.Entry<String, String> entry : errors.entrySet()) { %>
                        <li><strong><%= entry.getKey() %>:</strong> <%= entry.getValue() %></li>
                            <% } %>
                    </ul>
                </div>
                <% } %>

                <div class="row justify-content-center">
                    <div class="col-md-10 p-4 bg-white rounded shadow">
                        <div class="row">
                            <!-- Avatar -->
                            <div class="col-md-4 text-center">
                                <% if (user.getUserPicture() != null && !user.getUserPicture().isEmpty()) { %>
                                <img id="previewImage" src="images/profiles/<%= user.getUserPicture() %>"
                                     class="img-fluid rounded-circle mb-3" width="200" alt="Profile Picture"/>
                                <% } else { %>
                                <img id="previewImage" src="images/default-avatar.png"
                                     class="img-fluid rounded-circle mb-3" width="200" alt="Profile Picture"/>
                                <% } %>
                                <div class="mb-3">
                                    <label class="form-label">Change Profile Picture</label>
                                    <input type="file" class="form-control" name="userPicture" id="userPictureInput"
                                           form="profileForm" accept="image/*" onchange="previewImage(event)">
                                </div>
                            </div>

                            <!-- Profile Form -->
                            <div class="col-md-8">
                                <form id="profileForm" action="profile" method="post" enctype="multipart/form-data">
                                    <div class="mb-3">
                                        <label for="fullName" class="form-label">Full Name</label>
                                        <input type="text" class="form-control" id="fullName" name="fullName"
                                               value="<%= user.getFullName() != null ? user.getFullName() : "" %>">
                                    </div>
                                    <div class="mb-3">
                                        <label for="email" class="form-label">Email</label>
                                        <input type="email" class="form-control" id="email" name="email"
                                               value="<%= user.getEmail() != null ? user.getEmail() : "" %>">
                                    </div>
                                    <div class="mb-3">
                                        <label for="phoneNumber" class="form-label">Phone Number</label>
                                        <input type="text" class="form-control" id="phoneNumber" name="phoneNumber"
                                               pattern="^[0-9]{1,11}$" title="Phone number must be 1-11 digits"
                                               value="<%= user.getPhoneNumber() != null ? user.getPhoneNumber() : "" %>" required>
                                    </div>

                                    <div class="mb-3">
                                        <label for="address" class="form-label">Address</label>
                                        <input type="text" class="form-control" id="address" name="address"
                                               value="<%= user.getAddress() != null ? user.getAddress() : "" %>">
                                    </div>
                                    <div class="mb-3">
                                        <label for="dateOfBirth" class="form-label">Date of Birth</label>
                                        <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth"
                                               value="<%= user.getDateOfBirth() != null ? user.getDateOfBirth() : "" %>">
                                    </div>
                                    <div class="mb-3">
                                        <label for="gender" class="form-label">Gender</label>
                                        <select class="form-select" id="gender" name="gender">
                                            <option value="">Select</option>
                                            <option value="male" <%= "male".equals(genderStr) ? "selected" : "" %>>Male</option>
                                            <option value="female" <%= "female".equals(genderStr) ? "selected" : "" %>>Female</option>
                                            <option value="other" <%= "other".equals(genderStr) ? "selected" : "" %>>Other</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="description" class="form-label">Description</label>
                                        <textarea class="form-control" id="description" name="description" rows="3"><%= user.getDescription() != null ? user.getDescription() : "" %></textarea>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary btn-lg">Update Profile</button>
                                    </div>
                                </form>
                            </div>
                        </div> <!-- End row -->
                    </div>
                </div>
            </div>
        </section>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
        crossorigin="anonymous"></script>

        <script>
                                               function previewImage(event) {
                                                   const input = event.target;
                                                   const preview = document.getElementById('previewImage');

                                                   if (input.files && input.files[0]) {
                                                       const reader = new FileReader();
                                                       reader.onload = function (e) {
                                                           preview.src = e.target.result;
                                                       }
                                                       reader.readAsDataURL(input.files[0]);
                                                   }
                                               }
        </script>
    </body>
</html>
