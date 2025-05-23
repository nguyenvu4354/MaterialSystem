<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Material Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            .dashboard-container {
                padding: 20px;
            }
            .material-item {
                padding: 15px;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                margin-bottom: 15px;
                background: white;
                transition: transform 0.2s, box-shadow 0.2s;
            }
            .material-item:hover {
                transform: translateX(5px);
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            .material-info {
                display: flex;
                align-items: center;
                gap: 20px;
            }
            .material-avatar {
                width: 48px;
                height: 48px;
                border-radius: 4px;
                overflow: hidden;
                border: 1px solid #eee;
            }
            .material-avatar img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }
            .material-details {
                flex-grow: 1;
            }
            .material-name {
                font-weight: 600;
                color: #2c3e50;
                margin-bottom: 4px;
            }
            .material-code {
                color: #7f8c8d;
                font-size: 0.9em;
            }
            .status-badge {
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 0.85em;
                font-weight: 500;
            }
            .status-new {
                background-color: #00c3ff;
                color: white;
            }
            .status-used {
                background-color: #4169e1;
                color: white;
            }
            .status-damaged {
                background-color: #ffd700;
                color: black;
            }
            .material-price {
                font-weight: 600;
                color: #2c3e50;
                min-width: 120px;
                text-align: right;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .action-buttons {
                display: flex;
                gap: 8px;
            }
        </style>
    </head>
    <body class="bg-light">
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container">
                <a class="navbar-brand" href="#">Material Management Dashboard</a>
            </div>
        </nav>

        <div class="container dashboard-container">
            <div class="row mb-4">
                <div class="col">
                    <h2>Material Inventory</h2>
                </div>
                <div class="col text-end">
                    <button class="btn btn-primary" onclick="location.href='addMaterial'">
                        <i class="fas fa-plus"></i> Add New Material
                    </button>
                </div>
            </div>

            <div class="materials-list">
                <c:forEach items="${materials}" var="material">
                    <div class="material-item">
                        <div class="material-info">
                            <div class="material-avatar">
                                <img src="${material.materialsUrl}" 
                                     alt="${material.materialName}"
                                     onerror="this.src='https://placehold.co/48x48?text=${material.materialCode}'">
                            </div>
                            <div class="material-details">
                                <div class="material-name">${material.materialName}</div>
                                <div class="material-code">${material.materialCode}</div>
                            </div>
                            <div class="material-meta">
                                <span class="status-badge status-${material.materialStatus.toLowerCase()}">
                                    ${material.materialStatus}
                                </span>
                            </div>
                            <div class="material-price">
                                <fmt:formatNumber value="${material.price}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                            </div>
                            <div class="action-buttons">
                                <a href="editMaterial?id=${material.materialId}" class="btn btn-sm btn-primary">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <a href="deleteMaterial?id=${material.materialId}" 
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Are you sure you want to delete this material?')">
                                    <i class="fas fa-trash"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation">
                    <ul class="pagination">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="dashboard?page=${currentPage - 1}" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="dashboard?page=${i}">${i}</a>
                            </li>
                        </c:forEach>
                        
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="dashboard?page=${currentPage + 1}" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 