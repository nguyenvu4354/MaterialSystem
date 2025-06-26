<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Danh sách yêu cầu sửa chữa</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f6f9;
                margin: 0;
                padding: 0;
            }

            header {
                background-color: #fff;
                border-bottom: 2px solid #d59f39;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
            }

            header .container-fluid {
                padding: 15px 30px;
            }

            h2 {
                color: #d59f39;
                text-align: center;
                font-size: 30px;
                font-weight: bold;
                margin: 20px 0;
            }

            .main-layout {
                display: flex;
                margin: 0;
                padding: 0;
                min-height: calc(100vh - 120px);
            }

            .sidebar {
                width: 250px;
                background-color: #ffffff;
                padding: 20px 0;
                border-right: 1px solid #ddd;
                height: 100%;
            }

            .sidebar a {
                display: block;
                padding: 12px 24px;
                color: #333;
                text-decoration: none;
                font-size: 16px;
                transition: background-color 0.2s;
            }

            .sidebar a:hover {
                background-color: #f1f1f1;
            }

            .container {
                background-color: #ffffff;
                padding: 30px;
                border-radius: 16px;
                box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
                max-width: 1100px;
                margin: 20px 20px 20px 0;
                flex: 1;
            }

            table {
                border-collapse: collapse;
                width: 100%;
                margin-top: 20px;
                font-size: 15px;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            }

            th, td {
                border: 1px solid #f0f0f0;
                padding: 14px;
                text-align: left;
            }

            th {
                background-color: #f9f3ea;
                color: #333;
                font-weight: bold;
            }

            tr:nth-child(even) {
                background-color: #fcfcfc;
            }

            tr:hover {
                background-color: #f1f9ff;
            }

            .error {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 10px;
            }

            .status {
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: bold;
                text-align: center;
                display: inline-block;
                min-width: 90px;
            }

            .status.pending {
                background-color: #d6d6d6;
                color: #555;
            }

            .status.approved {
                background-color: #c2f0c2;
                color: #1b5e20;
            }

            .status.rejected {
                background-color: #f8d7da;
                color: #721c24;
            }

            .home-btn-container {
                display: flex;
                justify-content: flex-end;
                margin-top: 30px;
            }

            .home-btn {
                background-color: #d59f39;
                color: white;
                padding: 10px 24px;
                border: none;
                border-radius: 30px;
                cursor: pointer;
                font-weight: bold;
                text-decoration: none;
                transition: background-color 0.2s, box-shadow 0.2s;
            }

            .home-btn:hover {
                background-color: #c5892c;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }

            form button {
                background-color: #d59f39;
                color: #fff;
                border: none;
                border-radius: 20px;
                padding: 8px 18px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 500;
                transition: background-color 0.2s ease-in-out;
            }

            form button:hover {
                background-color: #c5892c;
            }
        </style>

    </head>
    <body>
        <header>
            <div class="container-fluid py-2">
                <div class="row align-items-center">
                    <div class="col-12 col-sm-4 text-center text-sm-start mb-3 mb-sm-0">
                        <a href="HomePage.jsp">
                            <img src="images/AdminLogo.png" alt="logo" class="img-fluid" style="max-width: 180px;">
                        </a>
                    </div>
                    <div class="col-12 col-sm-8 d-flex flex-column flex-sm-row justify-content-sm-end align-items-center gap-3">
                        <div class="text-center text-sm-end">
                            <c:choose>
                                <c:when test="${not empty sessionScope.user}">
                                    <span class="fs-6 text-muted">${sessionScope.user.fullName}</span><br>
                                    <strong>${sessionScope.user.email}</strong>
                                </c:when>
                                <c:otherwise>
                                    <span class="fs-6 text-muted">Guest</span><br>
                                    <strong>guest@example.com</strong>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <a href="logout" class="btn btn-outline-dark btn-sm">Logout</a>
                    </div>
                </div>
                <hr class="my-2">
            </div>

            <div class="container-fluid">
                <nav class="navbar navbar-expand-lg navbar-light">
                    <a class="navbar-brand d-lg-none" href="#">Menu</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar">
                        <span class="navbar-toggler-icon"></span>
                    </button>

                    <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar">
                        <div class="offcanvas-header">
                            <button type="button" class="btn-close ms-auto" data-bs-dismiss="offcanvas"></button>
                        </div>
                        <div class="offcanvas-body d-flex flex-column flex-lg-row align-items-lg-center justify-content-between">
                            <select class="form-select w-auto mb-3 mb-lg-0 me-lg-4">
                                <option selected disabled>Admin Options</option>
                                <option>Manage Products</option>
                                <option>Manage Users</option>
                                <option>Manage Orders</option>
                                <option>Reports</option>
                            </select>

                            <ul class="navbar-nav d-flex flex-row flex-wrap gap-3 mb-3 mb-lg-0">
                                <li class="nav-item">
                                    <a href="adminDashboard.jsp" class="nav-link active">Dashboard</a>
                                </li>
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" id="adminPages" data-bs-toggle="dropdown">Request</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="manageProducts.jsp" class="dropdown-item">Export Request</a></li>
                                        <li><a href="manageUsers.jsp" class="dropdown-item">Purchase Request</a></li>
                                        <li><a href="CreateRepairRequest.jsp" class="dropdown-item">Repair Request</a></li>
                                    </ul>
                                </li>
                                <li class="nav-item">
                                    <a href="profile" class="nav-link">Profile</a>
                                </li>
                                <li class="nav-item">
                                    <a href="support.jsp" class="nav-link">Support</a>
                                </li>
                            </ul>

                            <div class="d-none d-lg-flex align-items-center">
                                <a href="Profile.jsp" class="text-dark mx-2">
                                    <iconify-icon icon="healthicons:person" class="fs-4"></iconify-icon>
                                </a>
                            </div>
                        </div>
                    </div>
                </nav>
            </div>
        </header>

        <div class="main-layout">
            <div class="sidebar">
                <jsp:include page="SidebarDirector.jsp" />
            </div>

            <!-- Nội dung chính -->
            <div class="container">
                <h2>List repair request</h2>

                <c:if test="${not empty error}">
                    <p class="error">${error}</p>
                </c:if>

                <table>
                    <tr>
                        <th>ID</th>
                        <th>Request Code</th>
                        <th>User ID</th>
                        <th>Request Date</th>
                        <th>Status</th>
                        <th>Reason</th>
                        <th>Action</th>
                    </tr>
                    <c:forEach var="r" items="${repairRequests}">
                        <c:if test="${r.status ne 'cancel'}">
                            <tr>
                                <td>${r.repairRequestId}</td>
                                <td>${r.requestCode}</td>
                                <td>${r.userId}</td>
                                <td>${r.requestDate}</td>
                                <td>
                                    <span class="status
                                          <c:choose>
                                              <c:when test="${r.status == 'pending'}"> pending</c:when>
                                              <c:when test="${r.status == 'approved'}"> approved</c:when>
                                              <c:when test="${r.status == 'rejected'}"> rejected</c:when>
                                          </c:choose>">
                                        ${r.status}
                                    </span>
                                </td>
                                <td>${r.reason}</td>
                                <td>
                                    <form action="repairrequestdetailbyID" method="get">
                                        <input type="hidden" name="requestId" value="${r.repairRequestId}" />
                                        <button type="submit" class="btn-detail">View Detail</button>
                                    </form>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>

                <a href="home" class="home-btn-container">
                    <button type="button" class="home-btn">Home</button>
                </a>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>