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
            max-width: 1000px;
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
            margin-top: 30px;
            text-align: center;
        }

        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin: 10px;
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

        .form-container {
            width: 100%;
            max-width: 600px;
            margin: 30px auto 0 auto;
            background-color: #fdfdfd;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }

        .textarea-reason {
            width: 100%;
            height: 60px;
            margin: 10px 0;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ccc;
            resize: vertical;
            font-size: 14px;
        }

        .no-data {
            text-align: center;
            color: #888;
            font-style: italic;
            padding: 15px 0;
        }

        .note {
            text-align: center;
            color: #888;
            font-style: italic;
            margin-top: 20px;
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

    <h2>Chi tiết yêu cầu sửa chữa - Mã yêu cầu: <%= requestId %></h2>

    <%-- Hiển thị thông báo lỗi nếu có --%>
    <% if (error != null && !error.isEmpty()) { %>
        <div class="error-message"><%= error %></div>
    <% } %>

    <% if (details == null || details.isEmpty()) { %>
        <div class="no-data">Không có chi tiết nào cho yêu cầu sửa chữa này.</div>
    <% } else { %>
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

        <% if (roleId == 2) { %>
            <div class="form-container">
                <form action="approve" method="post" class="form">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Nhập lý do phê duyệt..." required></textarea>
                    <div class="btn-group">
                        <button class="btn" type="submit">✅ Duyệt yêu cầu</button>
                    </div>
                </form>

                <form action="reject" method="post" class="form">
                    <input type="hidden" name="requestId" value="<%= requestId %>">
                    <textarea class="textarea-reason" name="reason" placeholder="Nhập lý do từ chối..." required></textarea>
                    <div class="btn-group">
                        <button class="btn" type="submit">❌ Từ chối yêu cầu</button>
                    </div>
                </form>
            </div>
        <% } else { %>
            <div class="note">
                <i>Bạn chỉ có quyền xem chi tiết yêu cầu sửa chữa.</i>
            </div>
        <% } %>
    <% } %>

    <div class="btn-group">
        <a href="repairrequestlist" class="btn">← Quay lại danh sách</a>
        <a href="home" class="btn">Trang chủ</a>
    </div>
</div>
</body>
</html>
