<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>View My Requests</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .tab { overflow: hidden; border: 1px solid #ccc; background-color: #f1f1f1; }
        .tab button { background-color: inherit; float: left; border: none; outline: none; cursor: pointer; padding: 14px 16px; }
        .tab button.active { background-color: #ccc; }
        .tabcontent { display: none; padding: 6px 12px; border: 1px solid #ccc; border-top: none; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .pagination { margin-top: 10px; }
        .pagination a { margin: 0 5px; text-decoration: none; }
        .pagination a.active { font-weight: bold; }
        .filter { margin-bottom: 10px; }
        .detail-btn { padding: 5px 10px; background-color: #4CAF50; color: white; border: none; cursor: pointer; text-decoration: none; }
        .detail-btn:hover { background-color: #45a049; }
        .cancel-btn { padding: 5px 10px; background-color: #f44336; color: white; border: none; cursor: pointer; }
        .cancel-btn:hover { background-color: #d32f2f; }
        .message { color: green; margin-bottom: 10px; }
        .error { color: red; margin-bottom: 10px; }
        .filter label { margin-right: 10px; }
        .filter input, .filter select { margin-right: 10px; }
    </style>
</head>
<body>
    <h2>My Requests</h2>

    <!-- Display success or error message -->
    <c:if test="${not empty message}">
        <div class="${message.startsWith('Error') ? 'error' : 'message'}">
            ${message}
        </div>
    </c:if>

    <div class="filter">
        <form action="${pageContext.request.contextPath}/ViewRequests" method="get">
            <label>Filter by Status:</label>
            <select name="status">
                <option value="">All</option>
                <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                <option value="cancel" ${status == 'cancel' ? 'selected' : ''}>Cancelled</option>
            </select>
            <label>Request Code:</label>
            <input type="text" name="requestCode" value="${requestCode}">
            <label>Start Date:</label>
            <input type="date" name="startDate" value="${startDate}">
            <label>End Date:</label>
            <input type="date" name="endDate" value="${endDate}">
            <input type="submit" value="Filter">
        </form>
    </div>

    <div class="tab">
        <button class="tablinks" onclick="openTab(event, 'Export')">Export Requests</button>
        <button class="tablinks" onclick="openTab(event, 'Purchase')">Purchase Requests</button>
        <button class="tablinks" onclick="openTab(event, 'Repair')">Repair Requests</button>
    </div>

    <div id="Export" class="tabcontent">
        <h3>Export Requests</h3>
        <table>
            <tr>
                <th>Request Code</th>
                <th>Request Date</th>
                <th>Recipient</th>
                <th>Status</th>
                <th>Details</th>
                <th>Action</th>
            </tr>
            <c:forEach var="request" items="${exportRequests}">
                <tr>
                    <td>${request.requestCode}</td>
                    <td>${request.requestDate}</td>
                    <td>${request.recipientName}</td>
                    <td>${request.status}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=export&id=${request.exportRequestId}" class="detail-btn">Detail</a>
                    </td>
                    <td>
                        <c:if test="${request.status == 'draft'}">
                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="type" value="export">
                                <input type="hidden" name="id" value="${request.exportRequestId}">
                                <button type="submit" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="pagination">
            <c:forEach begin="1" end="${exportTotalPages}" var="i">
                <a href="${pageContext.request.contextPath}/ViewRequests?page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}" class="${currentPage == i ? 'active' : ''}">${i}</a>
            </c:forEach>
        </div>
    </div>

    <div id="Purchase" class="tabcontent">
        <h3>Purchase Requests</h3>
        <table>
            <tr>
                <th>Request Code</th>
                <th>Request Date</th>
                <th>Status</th>
                <th>Details</th>
                <th>Action</th>
            </tr>
            <c:forEach var="request" items="${purchaseRequests}">
                <tr>
                    <td>${request.requestCode}</td>
                    <td>${request.requestDate}</td>
                    <td>${request.status}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=purchase&id=${request.id}" class="detail-btn">Detail</a>
                    </td>
                    <td>
                        <c:if test="${request.status == 'draft'}">
                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="type" value="purchase">
                                <input type="hidden" name="id" value="${request.id}">
                                <button type="submit" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="pagination">
            <c:forEach begin="1" end="${purchaseTotalPages}" var="i">
                <a href="${pageContext.request.contextPath}/ViewRequests?page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}" class="${currentPage == i ? 'active' : ''}">${i}</a>
            </c:forEach>
        </div>
    </div>

    <div id="Repair" class="tabcontent">
        <h3>Repair Requests</h3>
        <table>
            <tr>
                <th>Request Code</th>
                <th>Request Date</th>
                <th>Status</th>
                <th>Details</th>
                <th>Action</th>
            </tr>
            <c:forEach var="request" items="${repairRequests}">
                <tr>
                    <td>${request.requestCode}</td>
                    <td>${request.requestDate}</td>
                    <td>${request.status}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/ViewRequestDetails?type=repair&id=${request.repairRequestId}" class="detail-btn">Detail</a>
                    </td>
                    <td>
                        <c:if test="${request.status == 'draft'}">
                            <form action="${pageContext.request.contextPath}/ViewRequests" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="type" value="repair">
                                <input type="hidden" name="id" value="${request.repairRequestId}">
                                <button type="submit" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="pagination">
            <c:forEach begin="1" end="${repairTotalPages}" var="i">
                <a href="${pageContext.request.contextPath}/ViewRequests?page=${i}&status=${status}&requestCode=${requestCode}&startDate=${startDate}&endDate=${endDate}" class="${currentPage == i ? 'active' : ''}">${i}</a>
            </c:forEach>
        </div>
    </div>

    <script>
        function openTab(evt, tabName) {
            var i, tabcontent, tablinks;
            tabcontent = document.getElementsByClassName("tabcontent");
            for (i = 0; i < tabcontent.length; i++) {
                tabcontent[i].style.display = "none";
            }
            tablinks = document.getElementsByClassName("tablinks");
            for (i = 0; i < tablinks.length; i++) {
                tablinks[i].className = tablinks[i].className.replace(" active", "");
            }
            document.getElementById(tabName).style.display = "block";
            evt.currentTarget.className += " active";
        }
        document.getElementsByClassName("tablinks")[0].click();
    </script>
</body>
</html>