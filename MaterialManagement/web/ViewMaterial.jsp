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
        .status-new { background-color: #00c3ff; color: white; }
        .status-used { background-color: #4169e1; color: white; }
        .status-damaged { background-color: #ffd700; color: black; }
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
                        <img src="${details.material.materialsUrl}" 
                             alt="${details.material.materialName}"
                             onerror="this.src='https://placehold.co/200x200?text=${details.material.materialCode}'">
                    </div>
                </div>
                <div class="col-md-8">
                    <h2 class="mb-4">${details.material.materialName}</h2>
                    
                    <div class="detail-row">
                        <div class="detail-label">Material Code</div>
                        <div>${details.material.materialCode}</div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">Status</div>
                        <div>
                            <span class="status-badge status-${details.material.materialStatus.toLowerCase()}">
                                ${details.material.materialStatus}
                            </span>
                        </div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">Condition</div>
                        <div>
                            ${details.material.conditionPercentage}%
                            <div class="progress condition-bar">
                                <div class="progress-bar ${details.material.conditionPercentage >= 70 ? 'bg-success' : 
                                                         details.material.conditionPercentage >= 40 ? 'bg-warning' : 'bg-danger'}" 
                                     role="progressbar" 
                                     style="width: ${details.material.conditionPercentage}%" 
                                     aria-valuenow="${details.material.conditionPercentage}" 
                                     aria-valuemin="0" 
                                     aria-valuemax="100">
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">Price</div>
                        <div class="h4">
                            <fmt:formatNumber value="${details.material.price}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
                        </div>
                    </div>

                    <div class="detail-row">
                        <div class="detail-label">Stock Quantity</div>
                        <div>
                            <span class="badge bg-${details.material.quantity > 0 ? 'success' : 'danger'} fs-6">
                                ${details.material.quantity} units
                            </span>
                        </div>
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
                            <div>${details.categoryName}</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-row">
                            <div class="detail-label">Description</div>
                            <div>${details.categoryDescription}</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Supplier Information -->
            <div class="info-section">
                <h3><i class="fas fa-truck me-2"></i>Supplier Information</h3>
                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-row">
                            <div class="detail-label">Supplier Name</div>
                            <div>${details.supplierName}</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Contact Person</div>
                            <div>${details.supplierContact}</div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Address</div>
                            <div>${details.supplierAddress}</div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-row">
                            <div class="detail-label">Phone Number</div>
                            <div>
                                <a href="tel:${details.supplierPhone}" class="text-decoration-none">
                                    <i class="fas fa-phone me-2"></i>${details.supplierPhone}
                                </a>
                            </div>
                        </div>
                        <div class="detail-row">
                            <div class="detail-label">Email</div>
                            <div>
                                <a href="mailto:${details.supplierEmail}" class="text-decoration-none">
                                    <i class="fas fa-envelope me-2"></i>${details.supplierEmail}
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                <a href="editmaterial?id=${details.material.materialId}" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Edit Material
                </a>
                <a href="deleteMaterial?id=${details.material.materialId}" 
                   class="btn btn-danger ms-2"
                   onclick="return confirm('Are you sure you want to delete this material?')">
                    <i class="fas fa-trash"></i> Delete Material
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 