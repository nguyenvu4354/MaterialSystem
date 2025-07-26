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
    <title>Cannot Modify Request</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f0e1;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 600px;
            margin: 80px auto;
            background-color: #fff8e1;
            border: 1px solid #d4a373;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            text-align: center;
        }

        .error-icon {
            font-size: 60px;
            color: #8b4513;
            margin-bottom: 20px;
        }

        h1 {
            color: #8b4513;
            margin-bottom: 15px;
        }

        p {
            font-size: 18px;
            color: #4a3728;
        }

        .btn {
            margin-top: 30px;
            display: inline-block;
            padding: 10px 20px;
            background-color: #d4a373;
            color: #fff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .btn:hover {
            background-color: #b8860b;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-icon">⚠️</div>
        <h1>Request Already Processed!</h1>
        <p>This repair request has been approved or rejected and cannot be modified.</p>
        <a href="repairrequestlist" class="btn">← Back to List</a>
    </div>
</body>
</html>