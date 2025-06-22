<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.RepairRequestDetail"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết yêu cầu sửa chữa</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f6f8;
                margin: 0;
                padding: 0;
            }

            .container {
                width: 90%;
                margin: 30px auto;
                background-color: white;
                padding: 25px 30px;
                box-shadow: 0 0 15px rgba(0,0,0,0.1);
                border-radius: 10px;
            }

            h2 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 25px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
            }

            th, td {
                border: 1px solid #dcdcdc;
                padding: 12px;
                text-align: center;
            }

            th {
                background-color: #3498db;
                color: white;
            }

            tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            .btn-group {
                margin-top: 20px;
                text-align: center;
            }

            .btn {
                display: inline-block;
                padding: 10px 20px;
                margin: 5px;
                background-color: #2980b9;
                color: white;
                text-decoration: none;
                border-radius: 6px;
                transition: background-color 0.3s ease;
                font-weight: bold;
                border: none;
                cursor: pointer;
            }

            .btn:hover {
                background-color: #1c5980;
            }

            .textarea-reason {
                width: 100%;
                max-width: 500px;
                height: 60px;
                margin: 5px auto;
                padding: 10px;
                border-radius: 5px;
                border: 1px solid #ccc;
                resize: vertical;
                display: block;
            }

            .no-data {
                text-align: center;
                color: #888;
                font-style: italic;
                padding: 15px 0;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <%
                List<RepairRequestDetail> details = (List<RepairRequestDetail>) request.getAttribute("details");
                int roleId = (Integer) request.getAttribute("roleId");
                int requestId = (Integer) request.getAttribute("requestId");
            %>

            <h2>Chi tiết yêu cầu sửa chữa - Mã yêu cầu: <%= requestId %></h2>

            <% if (details == null || details.isEmpty()) { %>
            <div class="no-data">Không có chi tiết nào cho yêu cầu sửa chữa này.</div>
            <% } else { %>

            <% if (roleId == 2) { %>
            <div class="btn-group">
                <form action="approve" method="post">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Nhập lý do phê duyệt..." required></textarea>
                    <button class="btn" type="submit">✅ Duyệt yêu cầu</button>
                </form>

                <form action="reject" method="post">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Nhập lý do từ chối..." required></textarea>
                    <button class="btn" type="submit">❌ Từ chối yêu cầu</button>
                </form>
            </div>
            <% } else { %>
            <p style="text-align:center; color:#888; margin-top:20px;">
                <i>Bạn chỉ có quyền xem chi tiết yêu cầu sửa chữa.</i>
            </p>
            <% } %>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Mã vật tư</th>
                    <th>Số lượng</th>
                    <th>Mô tả hư hỏng</th>
                    <th>Chi phí sửa chữa</th>
                    <th>Ngày tạo</th>
                    <th>Ngày cập nhật</th>
                </tr>
                <% for (RepairRequestDetail d : details) { %>
                <tr>
                    <td><%= d.getDetailId() %></td>
                    <td><%= d.getMaterialId() %></td>
                    <td><%= d.getQuantity() %></td>
                    <td><%= d.getDamageDescription() %></td>
                    <td><%= d.getRepairCost() %></td>
                    <td><%= d.getCreatedAt() %></td>
                    <td><%= d.getUpdatedAt() %></td>
                </tr>
                <% } %>
            </table>
            <% } %>

            <div class="btn-group">
                <a href="repairrequestlist" class="btn">← Quay lại danh sách</a>
                <a href="home" class="btn">Trang chủ</a>
            </div>
        </div>

    </body>
</html>
