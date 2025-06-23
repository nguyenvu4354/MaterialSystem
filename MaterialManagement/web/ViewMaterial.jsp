<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>View Material Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            .material-details {
                padding: 30px;
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            .material-image {
                width: 200px;
                height: 200px;
                border-radius: 8px;
                overflow: hidden;
                margin-bottom: 20px;
                border: 1px solid #eee;
            }
            .material-image img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }
            .detail-row {
                margin-bottom: 15px;
                border-bottom: 1px solid #eee;
                padding-bottom: 15px;
            }
            .detail-label {
                font-weight: 600;
                color: #666;
            }
            .status-badge {
                padding: 6px 15px;
                border-radius: 20px;
                font-size: 0.9em;
                font-weight: 500;
                display: inline-block;
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
            .condition-bar {
                height: 8px;
                border-radius: 4px;
                margin-top: 8px;
            }
            .info-section {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
            }
            .info-section h3 {
                color: #2c3e50;
                font-size: 1.2rem;
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body class="bg-light">
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container">
                <a class="navbar-brand" href="#">Material Management</a>
                <a href="dashboardmaterial" class="btn btn-outline-light">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </nav>
        <div class="container py-4">
            <div class="material-details">
                <div class="row">
                    <div class="col-md-4">
                        <div class="material-image">
                            <img src="${pageContext.request.contextPath}/${m.materialsUrl}" alt="${m.materialName}" style="width:200px;height:200px;object-fit:cover;" onerror="this.onerror=null;this.src='data:image/png;base64,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';">
                        </div>
                    </div>
                    <div class="col-md-8">
                        <h2 class="mb-4">${empty m.materialName ? 'Không có thông tin' : m.materialName}</h2>
                        <div class="detail-row">
                            <div class="detail-label">Material Code</div>
                            <div>${empty m.materialCode ? 'Không có thông tin' : m.materialCode}</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Status</div>
                            <div>
                                <span class="status-badge status-${empty m.materialStatus ? 'unknown' : m.materialStatus.toLowerCase()}">
                                    ${empty m.materialStatus ? 'Không có thông tin' : m.materialStatus}
                                </span>
                            </div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Condition</div>
                            <div>
                                ${m.conditionPercentage != null ? m.conditionPercentage : 'Không có thông tin'}%

                                <div class="progress condition-bar">
                                    <div class="progress-bar ${m.conditionPercentage >= 70 ? 'bg-success' : m.conditionPercentage >= 40 ? 'bg-warning' : 'bg-danger'}" role="progressbar" style="width: ${m.conditionPercentage != null ? m.conditionPercentage : 0}%" aria-valuenow="${m.conditionPercentage != null ? m.conditionPercentage : 0}" aria-valuemin="0" aria-valuemax="100"></div>
                                </div>
                            </div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Price</div>
                            <div class="h4">
                                <fmt:formatNumber value="${m.price}" type="currency" currencySymbol="VNĐ"/>
                            </div>
                        </div>
                        <div class="row mb-2">
                            <div class="col-sm-4 fw-bold">Last Updated:</div>
                            <div class="col-sm-8"><fmt:formatDate value="${m.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
                        </div>
                        <div class="row mb-2">
                            <div class="col-sm-4 fw-bold">Created At:</div>
                            <div class="col-sm-8"><fmt:formatDate value="${m.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
                        </div>
                    </div>
                </div>
                <!-- Category Information -->
                <div class="info-section mt-4">
                    <h3><i class="fas fa-folder me-2"></i>Category Information</h3>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="detail-row">
                                <div class="detail-label">Category Name</div>
                                <div>${empty m.category.category_name ? 'Không có thông tin' : m.category.category_name}</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">Description</div>
                                <div>${empty m.category.description ? 'Không có thông tin' : m.category.description}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Unit Information -->
                <div class="info-section">
                    <h3><i class="fas fa-ruler me-2"></i>Unit Information</h3>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="detail-row">
                                <div class="detail-label">Unit Name</div>
                                <div>${empty m.unit.unitName ? 'Không có thông tin' : m.unit.unitName}</div>
                            </div>
                            <div class="detail-row">
                                <div class="detail-label">Symbol</div>
                                <div>${empty m.unit.symbol ? 'Không có thông tin' : m.unit.symbol}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mt-4">
                    <a href="editmaterial?materialId=${m.materialId}" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Edit Material
                    </a>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 