<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="entity.User" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>User Detail - Computer Accessories</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- Bootstrap & Custom CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
    </head>
    <body>

        <!-- Header -->
        <header>
            <div class="container py-2">
                <div class="row py-4 pb-0 pb-sm-4 align-items-center">
                    <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                        <a href="HomePage.jsp">
                            <img src="images/logo.png" alt="logo" class="img-fluid" width="300px">
                        </a>
                    </div>
                    <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                        <div class="text-end d-none d-xl-block">
                            <span class="fs-6 text-muted">Admin</span>
                            <h5 class="mb-0">admin@accessories.com</h5>
                        </div>
                        <a href="logout" class="btn btn-outline-dark btn-lg ms-4">Logout</a>
                    </div>
                </div>
            </div>
        </header>

        <!-- Main content -->
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>

                <!-- Content -->
                <div class="col-md-9 col-lg-10 px-md-4 py-4">
                    <c:if test="${user != null}">
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <img src="images/profiles/${empty user.userPicture ? 'default-avatar.png' : user.userPicture}" 
                                     class="img-fluid rounded-circle mb-3" 
                                     style="max-width: 200px;" 
                                     alt="Profile Picture">
                            </div>


                            <div class="col-md-8">
                                <h3 class="text-primary border-bottom pb-2">👤 User Detail Information</h3>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Username:</div>
                                    <div class="col-sm-8">${user.username}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Full Name:</div>
                                    <div class="col-sm-8">${user.fullName}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Email:</div>
                                    <div class="col-sm-8">${user.email}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Phone Number:</div>
                                    <div class="col-sm-8">${user.phoneNumber}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Address:</div>
                                    <div class="col-sm-8">${user.address}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Gender:</div>
                                    <div class="col-sm-8">${user.gender}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Date of Birth:</div>
                                    <div class="col-sm-8">${user.dateOfBirth}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Role:</div>
                                    <div class="col-sm-8">${user.roleName}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Status:</div>
                                    <div class="col-sm-8">${user.status}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Description:</div>
                                    <div class="col-sm-8">${user.description}</div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-sm-4 fw-bold">Created At:</div>
                                    <div class="col-sm-8">${user.createdAt}</div>
                                </div>
                                <div class="row mb-4">
                                    <div class="col-sm-4 fw-bold">Last Updated:</div>
                                    <div class="col-sm-8">${user.updatedAt}</div>
                                </div>

                                <a href="UserList" class="btn btn-secondary">← Back to User List</a>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${user == null}">
                        <div class="alert alert-danger">User not found.</div>
                        <a href="UserList" class="btn btn-secondary">← Back to User List</a>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <footer class="footer py-4 bg-light mt-auto">
            <div class="container text-center">
                <span class="text-muted">&copy; 2025 Computer Accessories - All Rights Reserved.</span>
            </div>
        </footer>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>
