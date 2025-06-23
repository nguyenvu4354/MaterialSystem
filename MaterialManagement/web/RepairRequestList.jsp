<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Danh sách yêu cầu sửa chữa</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f4f6f9;
                margin: 0;
                padding: 20px;
            }

            h2 {
                color: #2c3e50;
                text-align: center;
            }

            .container {
                background-color: #ffffff;
                padding: 20px 30px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                max-width: 1100px;
                margin: 0 auto;
            }

            table {
                border-collapse: collapse;
                width: 100%;
                margin-top: 20px;
                font-size: 15px;
            }

            th, td {
                border: 1px solid #ddd;
                padding: 10px 14px;
                text-align: left;
            }

            th {
                background-color: #2c3e50;
                color: #ffffff;
            }

            tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            tr:hover {
                background-color: #e0f7fa;
            }

            .error {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 10px;
            }

            .status {
                padding: 4px 8px;
                border-radius: 6px;
                font-weight: bold;
                display: inline-block;
            }

            .btn {
                margin-left: 1048px;
                padding: 10px 4px;
            }
            .home-btn-container {
                display: flex;
                justify-content: flex-end;
                margin-top: 10px
                  
            }
            .home-btn {
                background-color: #2c3e50;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: bold;
                text-decoration: none;
                display: inline-block;
                transition: background-color 0.2s;
            }

            .home-btn:hover {
                background-color: #34495e;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <h2>List repair request</h2>

            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Request Code</th>
                    <th>User ID</th>
                    <th>Request Date</th>
                    <th>Status</th>
                    <th>Reason</th>
                    <th>Action</th> <!-- Thêm cột cho nút -->
                </tr>
                <c:forEach var="r" items="${repairRequests}">
                    <tr>
                        <td>${r.repairRequestId}</td>
                        <td>${r.requestCode}</td>
                        <td>${r.userId}</td>
                        <td>${r.requestDate}</td>
                        <td>
                            <span class="status
                                  <c:choose>
                                      <c:when test="${r.status == 'pending'}">pending</c:when>
                                      <c:when test="${r.status == 'approved'}">approved</c:when>
                                      <c:when test="${r.status == 'rejected'}">rejected</c:when>
                                  </c:choose>">
                                ${r.status}
                            </span>
                        </td>
                        <td>${r.reason}</td>
                        <td>
                            <!-- Nút View Detail, truyền repairRequestId -->
                            <form action="repairrequestdetailbyID" method="get">
                                <input type="hidden" name="requestId" value="${r.repairRequestId}" />
                                <button type="submit">View Detail</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
                <a href="home" class="home-btn-container"><button type="button" class="home-btn">Home</button></a>

        </div>

    </body>
</html>
