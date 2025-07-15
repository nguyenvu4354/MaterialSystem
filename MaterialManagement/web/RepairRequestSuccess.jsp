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
                border: 2px solid #DEB887;
            }

            .success {
                font-size: 24px;
                color: #DEB887; 
                margin-bottom: 20px;
                font-weight: bold;
            }

            .status {
                font-size: 18px;
                color: #c49b63; 
                margin-bottom: 20px;
            }

            .btn {
                margin-top: 20px;
                display: inline-block;
                padding: 12px 25px;
                font-size: 16px;
                text-decoration: none;
                color: #fff;
                background-color: #DEB887; 
                border-radius: 8px;
                transition: background-color 0.3s ease;
                margin-right: 10px;
            }

            .btn:hover {
                background-color: #c49b63; 
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="success">🛠️ Repair Request Created Successfully!</div>
            <div class="status">⏳ Current Status: <strong>Waiting for Manager Approval (Pending)</strong></div>
            <a href="home" class="btn">Back to Home</a>
            <a href="repairrequestlist" class="btn">View List</a>
        </div>
    </body>
</html>