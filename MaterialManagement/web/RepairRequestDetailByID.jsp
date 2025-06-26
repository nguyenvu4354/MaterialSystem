<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.RepairRequestDetail"%>
<%@page import="entity.Material"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết yêu cầu sửa chữa</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f6f9;
                margin: 0;
                padding: 0;
            }

            .container {
                width: 92%;
                max-width: 1100px;
                margin: 30px auto;
                background-color: white;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            }

            h2 {
                text-align: center;
                color: #d59f39;
                font-size: 26px;
                margin-bottom: 25px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
                font-size: 15px;
            }

            th, td {
                border: 1px solid #eee;
                padding: 12px;
                text-align: center;
            }

            th {
                background-color: #f4f2ef;
                color: #2c3e50;
                font-weight: 600;
            }

            tr:nth-child(even) {
                background-color: #fafafa;
            }

            tr:hover {
                background-color: #f1f9ff;
            }

            .btn-group {
                margin-top: 30px;
                text-align: center;
            }

            .btn {
                display: inline-block;
                padding: 10px 22px;
                margin: 10px;
                background-color: #d59f39;
                color: white;
                text-decoration: none;
                border-radius: 8px;
                font-weight: bold;
                border: none;
                cursor: pointer;
                transition: background-color 0.2s ease;
            }

            .btn:hover {
                background-color: #c4892a;
            }

            .form-container {
                width: 100%;
                max-width: 650px;
                margin: 30px auto 0 auto;
                background-color: #fff9f2;
                padding: 20px 25px;
                border-radius: 10px;
                border: 1px solid #f1d9b5;
            }

            .textarea-reason {
                width: 100%;
                height: 70px;
                margin: 10px 0;
                padding: 10px;
                border-radius: 6px;
                border: 1px solid #ccc;
                resize: vertical;
                font-size: 14px;
                font-family: inherit;
            }

            .no-data {
                text-align: center;
                color: #999;
                font-style: italic;
                padding: 15px 0;
                font-size: 16px;
            }

            .note {
                text-align: center;
                color: #999;
                font-style: italic;
                margin-top: 20px;
            }

            .error-message {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-bottom: 15px;
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

            <h2>Repair Request Details - Request Code: <%= requestId %></h2>

            <% if (error != null && !error.isEmpty()) { %>
            <div class="error-message"><%= error %></div>
            <% } %>

            <% if (details == null || details.isEmpty()) { %>
            <div class="no-data">There are no details for this repair request.</div>
            <% } else { %>
            <table>
                <tr>
                    <th>Images</th>
                    <th>Detail ID</th>
                    <th>Material Code</th>
                    <th>Material Name</th>
                    <th>Category</th> 
                    <th>Unit</th>     
                    <th>Quantity</th>
                    <th>Description of Damage</th>
                    <th>Repair Costs</th>
                    <th>Date Created</th>
                    <th>Update Date</th>
                </tr>
                <% for (RepairRequestDetail d : details) {
                       Material m = d.getMaterial(); 

                %>
                <tr>
                    <td>
                       <img src="<%= request.getContextPath() + "/images/material1/" + m.getMaterialsUrl() %>" 
     alt="Material Image" 
     style="max-height: 60px; max-width: 60px; border-radius: 6px;">
                    </td>
                    <td><%= d.getDetailId() %></td>
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
            </table>

            <% if (roleId == 2) { %>
            <div class="form-container">
                <form action="approve" method="post" class="form">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Enter reason approve..." required></textarea>
                    <div class="btn-group">
                        <button class="btn" type="submit">✅ Approve </button>
                    </div>
                </form>

                <form action="reject" method="post" class="form">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Enter reason reject..." required></textarea>
                    <div class="btn-group">
                        <button class="btn" type="submit">❌ Reject</button>
                    </div>
                </form>
            </div>
            <% } else { %>
            <div class="note">
                <i>You only have the right to view the repair request details.</i>
            </div>
            <% } %>
            <% } %>

            <div class="btn-group">
                <a href="repairrequestlist" class="btn">← Back To List</a>
                <a href="home" class="btn">Home</a>
            </div>
        </div>
    </body>
</html>
