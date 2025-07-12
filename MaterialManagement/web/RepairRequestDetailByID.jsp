<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.RepairRequestDetail"%>
<%@page import="entity.Material"%>
<%@page import="java.util.List"%>
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
            max-width: 920px;
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
    </style>
</head>
<body>
    <div class="container">
        <%
            List<RepairRequestDetail> details = (List<RepairRequestDetail>) request.getAttribute("details");
            int roleId = (Integer) request.getAttribute("roleId");
            int requestId = (Integer) request.getAttribute("requestId");
            String error = (String) request.getAttribute("error");
        %>

        <h2>Repair Request Details - Request Code: <%= requestId %> </h2>

        <% if (error != null && !error.isEmpty()) { %>
        <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <div class="card">
            <div class="card-header">Repair Request Details</div>
            <div class="card-body">
                <% if (details == null || details.isEmpty()) { %>
                <div class="text-muted fst-italic text-center">There are no details for this repair request.</div>
                <% } else { %>
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
                                <th>Description of Damage</th>
                                <th>Repair Costs</th>
                                <th>Created</th>
                                <th>Updated</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (RepairRequestDetail d : details) {
                                Material m = d.getMaterial();
                            %>
                            <tr>
                                <td>
                                    <img src="<%= request.getContextPath() + "/images/material/" + m.getMaterialsUrl() %>" 
                                         alt="Material Image" 
                                         class="img-fluid" 
                                         style="width: 100px; height: auto; object-fit: cover;">
                                    <br>
                                    <span style="font-size:12px; color:#888;">
                                        <%= m.getMaterialsUrl() %>
                                    </span>
                                </td>
                                <td><%= m != null ? m.getMaterialCode() : "N/A" %></td>
                                <td><%= m != null ? m.getMaterialName() : "N/A" %></td>
                                <td><%= (m != null && m.getCategory() != null) ? m.getCategory().getCategory_name() : "N/A" %></td>
                                <td><%= (m != null && m.getUnit() != null) ? m.getUnit().getUnitName() : "N/A" %></td>
                                <td><%= d.getQuantity() %></td>
                                <td><%= d.getDamageDescription() %></td>
                                <td><%= d.getRepairCost() %></td>
                                <td><%= d.getCreatedAt() %></td>
                                <td><%= d.getUpdatedAt() %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
        </div>

        <% if (roleId == 2) { %>
        <div class="card">
            <div class="card-header">Action</div>
            <div class="card-body">
                <form action="approve" method="post">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <div class="mb-2">
                        <textarea class="form-control" name="reason" placeholder="Enter reason approve..." required></textarea>
                    </div>
                    <div class="d-flex gap-2 mb-2">
                        <button class="btn btn-approve" type="submit" onclick="return confirm('Are you sure you want to approve this request?')">✅ Approve</button>
                    </div>
                </form>
                <form action="reject" method="post">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <div class="mb-2">
                        <textarea class="form-control" name="reason" placeholder="Enter reason reject..." required></textarea>
                    </div>
                    <div class="d-flex gap-2 mb-2">
                        <button class="btn btn-reject" type="submit" onclick="return confirm('Are you sure you want to reject this request?')">❌ Reject</button>
                    </div>
                </form>
            </div>
        </div>
        <% } else { %>
        <div class="text-muted fst-italic text-center">You only have the right to view the repair request details.</div>
        <% } %>

        <div class="d-flex gap-2 mb-2">
            <a href="repairrequestlist" class="btn btn-cancel">← Back To List</a>
            <a href="home" class="btn btn-cancel">Home</a>
        </div>
    </div>
</body>
</html>