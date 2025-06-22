<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Chi tiết Yêu cầu Sửa chữa</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 20px; 
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e9ecef;
        }
        .back-btn {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .back-btn:hover {
            background-color: #5a6268;
        }
        .request-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 30px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        .info-item {
            margin-bottom: 15px;
        }
        .info-label {
            font-weight: bold;
            color: #495057;
            margin-bottom: 5px;
        }
        .info-value {
            color: #212529;
            font-size: 16px;
        }
        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 14px;
        }
        .status-pending { background-color: #fff3cd; color: #856404; }
        .status-approved { background-color: #d4edda; color: #155724; }
        .status-completed { background-color: #d1ecf1; color: #0c5460; }
        .status-rejected { background-color: #f8d7da; color: #721c24; }
        
        .materials-section {
            margin-top: 30px;
        }
        .materials-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: white;
            border-radius: 6px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .materials-table th {
            background-color: #343a40;
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: bold;
        }
        .materials-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #dee2e6;
        }
        .materials-table tr:hover {
            background-color: #f8f9fa;
        }
        .cost-cell {
            font-weight: bold;
            color: #28a745;
        }
        .total-section {
            background-color: #e9ecef;
            padding: 20px;
            border-radius: 6px;
            margin-top: 20px;
            text-align: right;
        }
        .total-amount {
            font-size: 24px;
            font-weight: bold;
            color: #28a745;
        }
        .no-materials {
            text-align: center;
            padding: 40px;
            color: #6c757d;
            font-style: italic;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📋 Chi tiết Yêu cầu Sửa chữa</h1>
            <a href="repairrequestlist" class="back-btn">← Quay lại Danh sách</a>
        </div>

        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <c:if test="${not empty repairRequest}">
            <div class="request-info">
                <h2>Thông tin Yêu cầu</h2>
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Mã yêu cầu:</div>
                        <div class="info-value">${repairRequest.requestCode}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Trạng thái:</div>
                        <div class="info-value">
                            <span class="status-badge status-${repairRequest.status}">
                                <c:choose>
                                    <c:when test="${repairRequest.status == 'pending'}">Đang chờ</c:when>
                                    <c:when test="${repairRequest.status == 'approved'}">Đã phê duyệt</c:when>
                                    <c:when test="${repairRequest.status == 'completed'}">Đã hoàn tất</c:when>
                                    <c:when test="${repairRequest.status == 'rejected'}">Đã từ chối</c:when>
                                    <c:otherwise>${repairRequest.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Người gửi (ID):</div>
                        <div class="info-value">${repairRequest.userId}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Ngày yêu cầu:</div>
                        <div class="info-value">
                            <fmt:formatDate value="${repairRequest.requestDate}" pattern="dd/MM/yyyy HH:mm"/>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Số điện thoại người sửa:</div>
                        <div class="info-value">${repairRequest.repairPersonPhoneNumber}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Email người sửa:</div>
                        <div class="info-value">${repairRequest.repairPersonEmail}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Địa điểm sửa chữa:</div>
                        <div class="info-value">${repairRequest.repairLocation}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Ngày dự kiến hoàn thành:</div>
                        <div class="info-value">
                            <fmt:formatDate value="${repairRequest.estimatedReturnDate}" pattern="dd/MM/yyyy"/>
                        </div>
                    </div>
                </div>
                <div class="info-item" style="margin-top: 20px;">
                    <div class="info-label">Lý do sửa chữa:</div>
                    <div class="info-value">${repairRequest.reason}</div>
                </div>
            </div>

            <div class="materials-section">
                <h2>📦 Danh sách Vật tư cần Sửa chữa</h2>
                
                <c:choose>
                    <c:when test="${not empty details}">
                        <table class="materials-table">
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Vật tư ID</th>
                                    <th>Số lượng</th>
                                    <th>Mô tả hư hỏng</th>
                                    <th>Chi phí sửa chữa</th>
                                    <th>Ngày tạo</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${details}" varStatus="status">
                                    <tr>
                                        <td>${status.index + 1}</td>
                                        <td>${detail.materialId}</td>
                                        <td>${detail.quantity}</td>
                                        <td>${detail.damageDescription}</td>
                                        <td class="cost-cell">
                                            <c:choose>
                                                <c:when test="${detail.repairCost != null}">
                                                    <fmt:formatNumber value="${detail.repairCost}" type="currency" currencySymbol="₫"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #6c757d;">Chưa có</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${detail.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div class="total-section">
                            <c:set var="totalCost" value="0" />
                            <c:set var="validCosts" value="0" />
                            <c:forEach var="detail" items="${details}">
                                <c:if test="${detail.repairCost != null}">
                                    <c:set var="totalCost" value="${totalCost + detail.repairCost}" />
                                    <c:set var="validCosts" value="${validCosts + 1}" />
                                </c:if>
                            </c:forEach>
                            
                            <div style="margin-bottom: 10px;">
                                <strong>Tổng số vật tư:</strong> ${details.size()} | 
                                <strong>Vật tư có chi phí:</strong> ${validCosts}
                            </div>
                            <div class="total-amount">
                                Tổng chi phí: <fmt:formatNumber value="${totalCost}" type="currency" currencySymbol="₫"/>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="no-materials">
                            <h3>📭 Không có vật tư nào trong yêu cầu này</h3>
                            <p>Có thể yêu cầu này chưa được thêm vật tư hoặc đã bị xóa.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <c:if test="${empty repairRequest}">
            <div class="error-message">
                <h3>❌ Không tìm thấy yêu cầu sửa chữa</h3>
                <p>Yêu cầu sửa chữa bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.</p>
            </div>
        </c:if>
    </div>
</body>
</html> 