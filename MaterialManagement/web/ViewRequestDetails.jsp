<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Request Details</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h2 { color: #333; }
        .detail-container { max-width: 800px; margin: 0 auto; }
        .detail-container p { margin: 5px 0; }
        .detail-container ul { list-style-type: disc; margin-left: 20px; }
        .back-btn { padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
        .back-btn:hover { background-color: #45a049; }
        .cancel-btn { padding: 10px 20px; background-color: #f44336; color: white; border: none; cursor: pointer; border-radius: 5px; margin-left: 10px; }
        .cancel-btn:hover { background-color: #d32f2f; }
        .message { color: green; margin-bottom: 10px; }
        .error { color: red; margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="detail-container">
        <h2>${requestType} Request Details</h2>

        <!-- Display success or error message -->
        <c:if test="${not empty message}">
            <div class="${message.startsWith('Error') ? 'error' : 'message'}">
                ${message}
            </div>
        </c:if>

        <p><strong>Request Code:</strong> ${request.requestCode}</p>
        <p><strong>Request Date:</strong> ${request.requestDate}</p>
        <p><strong>Status:</strong> ${request.status}</p>
        <p><strong>Reason:</strong> ${request.reason != null ? request.reason : "N/A"}</p>
        <p><strong>Approval Reason:</strong> ${request.approvalReason != null ? request.approvalReason : "N/A"}</p>
        <p><strong>Rejection Reason:</strong> ${request.rejectionReason != null ? request.rejectionReason : "N/A"}</p>

        <c:if test="${requestType == 'Export'}">
            <p><strong>Recipient:</strong> ${request.recipientName}</p>
            <p><strong>Approver:</strong> ${request.approverName != null ? request.approverName : "N/A"}</p>
            <p><strong>Approved At:</strong> ${request.approvedAt != null ? request.approvedAt : "N/A"}</p>
            <p><strong>Delivery Date:</strong> ${request.deliveryDate != null ? request.deliveryDate : "N/A"}</p>
        </c:if>
        <c:if test="${requestType == 'Purchase'}">
            <p><strong>Estimated Price:</strong> ${request.estimatedPrice != null ? request.estimatedPrice : "N/A"}</p>
        </c:if>
        <c:if test="${requestType == 'Repair'}">
            <p><strong>Repair Person Phone:</strong> ${request.repairPersonPhoneNumber != null ? request.repairPersonPhoneNumber : "N/A"}</p>
            <p><strong>Repair Person Email:</strong> ${request.repairPersonEmail != null ? request.repairPersonEmail : "N/A"}</p>
            <p><strong>Repair Location:</strong> ${request.repairLocation != null ? request.repairLocation : "N/A"}</p>
            <p><strong>Estimated Return Date:</strong> ${request.estimatedReturnDate != null ? request.estimatedReturnDate : "N/A"}</p>
        </c:if>

        <h3>Details</h3>
        <ul>
            <c:forEach var="detail" items="${request.details}">
                <c:choose>
                    <c:when test="${requestType == 'Export'}">
                        <li>${detail.materialName} (${detail.materialCode}): ${detail.quantity} ${detail.materialUnit} (${detail.exportCondition})</li>
                    </c:when>
                    <c:when test="${requestType == 'Purchase'}">
                        <li>${detail.materialName}: ${detail.quantity} (Notes: ${detail.notes != null ? detail.notes : "N/A"})</li>
                    </c:when>
                    <c:when test="${requestType == 'Repair'}">
                        <li>Material ID: ${detail.materialId}, Quantity: ${detail.quantity}, Damage: ${detail.damageDescription != null ? detail.damageDescription : "N/A"}, Repair Cost: ${detail.repairCost != null ? detail.repairCost : "N/A"}</li>
                    </c:when>
                </c:choose>
            </c:forEach>
        </ul>

        <a href="${pageContext.request.contextPath}/ViewRequests" class="back-btn">Back to Requests</a>
        <c:if test="${request.status == 'draft'}">
            <form action="${pageContext.request.contextPath}/ViewRequestDetails" method="post" style="display:inline;">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="type" value="${requestType.toLowerCase()}">
                <input type="hidden" name="id" value="${requestType == 'Export' ? request.exportRequestId : (requestType == 'Purchase' ? request.id : request.repairRequestId)}">
                <button type="submit" class="cancel-btn" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel Request</button>
            </form>
        </c:if>
    </div>
</body>
</html>