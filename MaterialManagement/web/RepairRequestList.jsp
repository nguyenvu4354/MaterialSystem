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
                color: #d59f39;
                text-align: center;
                font-size: 28px;
                margin-bottom: 10px;
            }

            .container {
                background-color: #ffffff;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
                border: 1px solid #eee;
                padding: 12px;
                text-align: left;
            }

            th {
                background-color: #f4f2ef;
                color: #2c3e50;
                font-weight: bold;
            }

            tr:nth-child(even) {
                background-color: #fbfbfb;
            }

            tr:hover {
                background-color: #f1f9ff;
            }

            .error {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 10px;
            }

            .status {
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 13px;
                font-weight: bold;
                text-align: center;
                display: inline-block;
                min-width: 80px;
            }

            .status.pending {
                background-color: #d6d6d6;
                color: #333;
            }

            .status.approved {
                background-color: #b7e4c7;
                color: #1b5e20;
            }

            .status.rejected {
                background-color: #f8d7da;
                color: #721c24;
            }

            .home-btn-container {
                display: flex;
                justify-content: flex-end;
                margin-top: 20px;
            }

            .home-btn {
                background-color: #d59f39;
                color: white;
                padding: 10px 22px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: bold;
                text-decoration: none;
                transition: background-color 0.2s;
            }

            .home-btn:hover {
                background-color: #c5892c;
            }

            form button {
                background-color: #f8c471;
                color: #fff;
                border: none;
                border-radius: 20px;
                padding: 6px 14px;
                cursor: pointer;
                font-size: 14px;
                transition: background-color 0.2s;
            }

            form button:hover {
                background-color: #e67e22;
            }
        </style>

    </head>
    <body>
        <jsp:include page="Header.jsp" />
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
                    <th>Action</th> 
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
        <jsp:include page="Footer.jsp" />


    </body>
</html>
