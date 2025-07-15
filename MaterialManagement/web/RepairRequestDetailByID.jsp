<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.RepairRequestDetail"%>
<%@page import="entity.Material"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.RolePermissionDAO" %>
<%@ page import="entity.User" %>
<%
    RolePermissionDAO rolePermissionDAO = (RolePermissionDAO) request.getAttribute("rolePermissionDAO");
    if (rolePermissionDAO == null) {
        rolePermissionDAO = new RolePermissionDAO();
    }
    User user = (User) session.getAttribute("user");
    boolean canApprove = user != null && (user.getRoleId() == 1 || user.getRoleId() == 2 || rolePermissionDAO.hasPermission(user.getRoleId(), "APPROVE_REPAIR_REQUEST"));
    boolean canReject = user != null && (user.getRoleId() == 1 || user.getRoleId() == 2 || rolePermissionDAO.hasPermission(user.getRoleId(), "REJECT_REPAIR_REQUEST"));
    request.setAttribute("canApprove", canApprove);
    request.setAttribute("canReject", canReject);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Repair Request Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .card {
            border: none;
            margin-bottom: 20px;
        }
        .card-header {
            background-color: #DEAD6F;
            border-bottom: none;
            font-weight: bold;
            color: #fff;
        }
        .table {
            margin-top: 20px;
        }
        .btn {
            margin-right: 10px;
            border: none;
            color: #fff;
        }
        .btn-approve {
            background-color: #198754;
        }
        .btn-approve:hover {
            background-color: #17643a;
        }
        .btn-reject {
            background-color: #dc3545;
        }
        .btn-reject:hover {
            background-color: #b52d3a;
        }
        .btn-cancel {
            background-color: #DEAD6F;
            color: #fff;
            border: none;
        }
        .btn-cancel:hover {
            background-color: #c79b5a;
            color: #fff;
        }
        .alert {
            margin-top: 20px;
        }
        .status-tag {
            padding: 5px 10px;
            border-radius: 5px;
            color: #fff;
            background-color: #6c757d;
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
        }
        .status-approved {
            background-color: #198754; 
        }
        .status-rejected {
            background-color: #dc3545; 
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Repair Request Details - Request ID: ${requestId}
            <span class="status-tag
                  <c:choose>
                      <c:when test="${status == 'pending'}">status-pending</c:when>
                      <c:when test="${status == 'approved'}">status-approved</c:when>
                      <c:when test="${status == 'rejected'}">status-rejected</c:when>
                      <c:otherwise>status-tag</c:otherwise>
                  </c:choose>">
                ${status != null ? status : "N/A"}
            </span>
        </h2>
        <c:if test="${not empty details && !details.isEmpty()}">
            <p>Created at: ${details[0].createdAt != null ? details[0].createdAt : "N/A"}</p>
            <p>Updated at: ${details[0].updatedAt != null ? details[0].updatedAt : "N/A"}</p>
        </c:if>
        <c:if test="${empty details}">
            <p>Created at: N/A</p>
            <p>Updated at: N/A</p>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="card">
            <div class="card-header">Request Information</div>
            <div class="card-body">
                <p><strong>Description of Damage:</strong></p>
                <p>
                    <c:choose>
                        <c:when test="${not empty details && !details.isEmpty()}">
                            ${details[0].damageDescription != null ? details[0].damageDescription : "N/A"}
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose>
                </p>
                <p><strong>Repair Cost:</strong></p>
                <p>
                    <c:choose>
                        <c:when test="${not empty details && !details.isEmpty()}">
                            ${details[0].repairCost != null ? details[0].repairCost : "N/A"}
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Material List</div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty details || details.isEmpty()}">
                        <div class="text-muted fst-italic text-center">There are no details for this repair request.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table custom-table">
                                <thead>
                                    <tr>
                                        <th>Images</th>
                                        <th>Material Code</th>
                                        <th>Material Name</th>
                                        <th>Category</th>
                                        <th>Unit</th>
                                        <th>Quantity</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="d" items="${details}">
                                        <c:set var="m" value="${d.material}"/>
                                        <tr>
                                            <td>
                                                <img src="${pageContext.request.contextPath}/images/material/${m.materialsUrl}" 
                                                     alt="Material Image" 
                                                     class="img-fluid" 
                                                     style="width: 100px; height: auto; object-fit: cover;">
                                                <br>
                                                <span style="font-size:12px; color:#888;">
                                                    ${m.materialsUrl}
                                                </span>
                                            </td>
                                            <td>${m != null ? m.materialCode : "N/A"}</td>
                                            <td>${m != null ? m.materialName : "N/A"}</td>
                                            <td>${m != null && m.category != null ? m.category.category_name : "N/A"}</td>
                                            <td>${m != null && m.unit != null ? m.unit.unitName : "N/A"}</td>
                                            <td>${d.quantity}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <c:if test="${canApprove || canReject}">
            <div class="card">
                <div class="card-header">Actions</div>
                <div class="card-body">
                    <c:if test="${canApprove}">
                        <form action="approve" method="post">
                            <input type="hidden" name="requestId" value="${requestId}">
                            <div class="mb-2">
                                <textarea class="form-control" name="reason" placeholder="Enter reason for approval..." required></textarea>
                            </div>
                            <div class="d-flex gap-2 mb-2">
                                <button class="btn btn-approve" type="submit" onclick="return confirm('Are you sure you want to approve this request?')">✅ Approve</button>
                            </div>
                        </form>
                    </c:if>
                    <c:if test="${canReject}">
                        <form action="reject" method="post">
                            <input type="hidden" name="requestId" value="${requestId}">
                            <div class="mb-2">
                                <textarea class="form-control" name="reason" placeholder="Enter reason for rejection..." required></textarea>
                            </div>
                            <div class="d-flex gap-2 mb-2">
                                <button class="btn btn-reject" type="submit" onclick="return confirm('Are you sure you want to reject this request?')">❌ Reject</button>
                            </div>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:if>
        <c:if test="${!canApprove && !canReject}">
            <div class="text-muted fst-italic text-center">You only have permission to view the repair request details.</div>
        </c:if>

        <div class="d-flex gap-2 mb-2">
            <a href="repairrequestlist" class="btn btn-cancel">Cancel</a>
            <a href="home" class="btn btn-cancel">Home</a>
        </div>
    </div>
</body>
</html>