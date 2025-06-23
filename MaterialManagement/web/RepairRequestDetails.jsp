<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, entity.RepairRequestDetail" %>
<html>
    <head>
        <title>Repair Request Details</title>
        <style>
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 8px;
                border: 1px solid #ddd;
                text-align: center;
            }
            th {
                background-color: #f2f2f2;
            }
        </style>
    </head>
    <body>
        <h2>Repair Request Details</h2>
        <table>
            <tr>
                <th>ID</th>
                <th>Repair Request ID</th>
                <th>Material ID</th>
                <th>Quantity</th>
                <th>Damage Description</th>
                <th>Repair Cost</th>
                <th>Created At</th>
                <th>Updated At</th>
            </tr>
            <%
                List<RepairRequestDetail> details = (List<RepairRequestDetail>) request.getAttribute("details");
                if (details != null && !details.isEmpty()) {
                    for (RepairRequestDetail d : details) {
            %>
            <tr>
                <td><%= d.getDetailId() %></td>
                <td><%= d.getRepairRequestId() %></td>
                <td><%= d.getMaterialId() %></td>
                <td><%= d.getQuantity() %></td>
                <td><%= d.getDamageDescription() %></td>
                <td><%= d.getRepairCost() %></td>
                <td><%= d.getCreatedAt() %></td>
                <td><%= d.getUpdatedAt() %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="8">No repair request details found.</td>
            </tr>
            <%
                }
            %>
        </table>
    </body>
</html>
