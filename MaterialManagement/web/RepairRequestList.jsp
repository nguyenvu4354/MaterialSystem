<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Repair Request Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <style>
            body {
                font-family: 'Poppins', sans-serif;
            }
            .container-main {
                max-width: 1200px;
                margin: 30px auto;
                background: #fff;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }
            h1 {
                font-size: 2 rem;
                font-weight: bold;
                margin-bottom: 0.5rem;
                color: #DEAD6F;
            }
            .custom-table thead th {
                background-color: #f9f5f0;
                color: #5c4434;
                font-weight: 600;
            }
            .custom-table tbody tr:hover {
                background-color: #f1f1f1;
            }
            .custom-table th,
            .custom-table td {
                vertical-align: middle;
                min-height: 48px;
                padding: 14px;
            }
            .status-badge {
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: bold;
                display: inline-block;
                min-width: 90px;
                text-align: center;
            }
            .status-pending {
                background-color: #d6d6d6;
                color: #555;
            }
            .status-approved {
                background-color: #c2f0c2;
                color: #1b5e20;
            }
            .status-rejected {
                background-color: #f8d7da;
                color: #721c24;
            }
            .btn-detail {
                background-color: #fff7e6;
                color: #b8860b;
                border: 1px solid #ffe58f;
                border-radius: 6px;
                padding: 6px 14px;
                font-weight: 500;
            }
            .error {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 10px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <div class="container-fluid">
            <div class="row">
                <jsp:include page="SidebarDirector.jsp" />

                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="container-main">
                        <h1><i class="fas fa-tools"></i> Repair Request List</h1>
                        <form method="get" action="repairrequestlist" class="row g-3 align-items-center mb-4">
                            <div class="col-auto">
                                <input type="text" name="search" class="form-control" placeholder="Search by Request Code"
                                       value="${searchKeyword != null ? searchKeyword : ''}">
                            </div>
                            <div class="col-auto">
                                <select name="status" class="form-select">
                                    <option value="all" ${selectedStatus == null || selectedStatus == 'all' ? 'selected' : ''}>All Status</option>
                                    <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>Pending</option>
                                    <option value="approved" ${selectedStatus == 'approved' ? 'selected' : ''}>Approved</option>
                                    <option value="rejected" ${selectedStatus == 'rejected' ? 'selected' : ''}>Rejected</option>
                                </select>
                            </div>
                            <div class="col-auto">
                               <button type="submit" class="btn" style="background-color: #DEAD6F; border-color: #DEAD6F; color:white;">Filter</button>
                            </div>
                        </form>


                        <c:if test="${not empty error}">
                            <p class="error">${error}</p>
                        </c:if>

                        <table class="table custom-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Request Code</th>
                                    <th>User ID</th>
                                    <th>Request Date</th>
                                    <th>Status</th>
                                    <th>Reason</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${repairRequests}">
                                    <c:if test="${r.status ne 'cancel'}">
                                        <tr>
                                            <td>${r.repairRequestId}</td>
                                            <td>${r.requestCode}</td>
                                            <td>${r.userId}</td>
                                            <td>${r.requestDate}</td>
                                            <td>
                                                <span class="status-badge
                                                      <c:choose>
                                                          <c:when test="${r.status == 'pending'}">status-pending</c:when>
                                                          <c:when test="${r.status == 'approved'}">status-approved</c:when>
                                                          <c:when test="${r.status == 'rejected'}">status-rejected</c:when>
                                                      </c:choose>">
                                                    ${r.status}
                                                </span>
                                            </td>
                                            <td>${r.reason}</td>
                                            <td>
                                                <form action="repairrequestdetailbyID" method="get">
                                                    <input type="hidden" name="requestId" value="${r.repairRequestId}" />
                                                    <button type="submit" class="btn-detail">
                                                        <i class="fas fa-eye"></i> Detail
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
