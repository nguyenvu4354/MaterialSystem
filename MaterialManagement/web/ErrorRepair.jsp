<%-- 
    Document   : ErrorRepair
    Created on : Jun 23, 2025, 8:22:40 PM
    Author     : Nhat Anh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Không thể thay đổi yêu cầu</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 600px;
            margin: 80px auto;
            background-color: #fff;
            border: 1px solid #ddd;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            text-align: center;
        }

        .error-icon {
            font-size: 60px;
            color: #e74c3c;
            margin-bottom: 20px;
        }

        h1 {
            color: #e74c3c;
            margin-bottom: 15px;
        }

        p {
            font-size: 18px;
            color: #333;
        }

        .btn {
            margin-top: 30px;
            display: inline-block;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .btn:hover {
            background-color: #2c80b4;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-icon">⚠️</div>
        <h1>Yêu cầu đã được xét duyệt!</h1>
        <p>Yêu cầu sửa chữa này đã được phê duyệt hoặc từ chối nên không thể thay đổi nữa.</p>
        <a href="repairrequestlist" class="btn">← Quay lại danh sách</a>
    </div>
</body>
</html>
