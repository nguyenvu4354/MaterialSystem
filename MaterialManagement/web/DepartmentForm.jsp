<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Department</title>
    <style>
        body {
            font-family: 'Roboto', Arial, sans-serif;
            margin: 40px;
            background-color: #f8f8f8;
        }
        h1 {
            color: #d59f39;
        }
        .form-section {
            background: #fff;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        input[type=text], input[type=email] {
            width: 100%;
            padding: 10px;
            margin: 8px 0 16px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        button {
            padding: 10px 18px;
            background-color: #d59f39;
            border: none;
            color: white;
            border-radius: 8px;
            cursor: pointer;
            margin-right: 10px;
        }
        a.button-link {
            display: inline-block;
            padding: 10px 18px;
            background-color: #ccc;
            color: black;
            text-decoration: none;
            border-radius: 8px;
        }
    </style>
</head>
<body>
    <h1>Add Department</h1>

    <div class="form-section">
        <form action="depairmentlist" method="post">
            <label>Name:</label>
            <input type="text" name="name" required/>

            <label>Phone:</label>
            <input type="text" name="phone" />

            <label>Email:</label>
            <input type="email" name="email" />

            <label>Location:</label>
            <input type="text" name="location" />

            <label>Description:</label>
            <input type="text" name="description" />

            <button type="submit">Add</button>
            <a class="button-link" href="depairmentlist">Cancel</a>
        </form>
    </div>
</body>
</html>