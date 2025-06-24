<%-- 
    Document   : RepairRequestSuccess
    Created on : Jun 21, 2025, 2:16:01 PM
    Author     : Nhat Anh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Repair Request Submitted</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f5f5;
                text-align: center;
                padding: 50px;
            }

            .container {
                background-color: #fff;
                padding: 40px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                display: inline-block;
            }

            .success {
                font-size: 24px;
                color: #4CAF50;
                margin-bottom: 20px;
            }

            .status {
                font-size: 18px;
                color: #ff9800;
            }

            .btn {
                margin-top: 30px;
                display: inline-block;
                padding: 10px 20px;
                font-size: 16px;
                text-decoration: none;
                color: #fff;
                background-color: #2196F3;
                border-radius: 8px;
                transition: background-color 0.3s ease;
            }

            .btn:hover {
                background-color: #1976D2;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="success">🛠 ️Repair request created successfully!</div>
            <div class="status">⏳ Trạng thái hiện tại: <strong> Waiting for boss approval (Pending)</strong></div>
            <a href="home" class="btn">Back To Home</a>
            <a href="repairrequestlist" class="btn">List</a>

        </div>
    </body>
</html>
