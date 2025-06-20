<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Static Inventory</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
      rel="stylesheet"
    />
    <style>
      body {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
      }
      .main-container {
        background: rgba(255, 255, 255, 0.95);
        border-radius: 20px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
        margin: 20px;
        padding: 30px;
      }
      .card {
        border: none;
        border-radius: 15px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        margin-bottom: 30px;
        transition: transform 0.3s ease;
      }
      .card:hover {
        transform: translateY(-5px);
      }
      .card-header {
        border-radius: 15px 15px 0 0 !important;
        border: none;
        padding: 20px 25px;
      }
      .card-body {
        padding: 25px;
      }
      .table {
        border-radius: 10px;
        overflow: hidden;
      }
      .table th {
        background: linear-gradient(45deg, #667eea, #764ba2);
        color: white;
        border: none;
        padding: 15px;
        font-weight: 600;
      }
      .table td {
        padding: 12px 15px;
        vertical-align: middle;
      }
      .page-title {
        color: #2c3e50;
        font-weight: 700;
        margin-bottom: 30px;
        text-align: center;
      }
      .inventory-list {
        max-height: 600px;
        overflow-y: auto;
      }
      .alert {
        border-radius: 10px;
        border: none;
        padding: 15px 20px;
      }
      .badge {
        border-radius: 8px;
        padding: 8px 12px;
      }
      .stock-high {
        background-color: #d4edda;
        color: #155724;
      }
      .stock-medium {
        background-color: #fff3cd;
        color: #856404;
      }
      .stock-low {
        background-color: #f8d7da;
        color: #721c24;
      }
      .stock-zero {
        background-color: #e2e3e5;
        color: #383d41;
      }
      .material-code {
        font-family: "Courier New", monospace;
        font-weight: 600;
        color: #495057;
      }
      .material-name {
        font-weight: 600;
        color: #2c3e50;
      }
      .category-badge {
        background: linear-gradient(45deg, #17a2b8, #20c997);
        color: white;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.85em;
        font-weight: 600;
      }
      .unit-badge {
        background: linear-gradient(45deg, #6c757d, #495057);
        color: white;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.85em;
        font-weight: 600;
      }
      .location-badge {
        background: linear-gradient(45deg, #fd7e14, #e83e8c);
        color: white;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.85em;
        font-weight: 600;
      }
      .stats-card {
        background: linear-gradient(45deg, #28a745, #20c997);
        color: white;
        border-radius: 15px;
        padding: 20px;
        margin-bottom: 20px;
      }
      .stats-number {
        font-size: 2.5rem;
        font-weight: 700;
      }
      .stats-label {
        font-size: 1.1rem;
        opacity: 0.9;
      }
      .empty-state {
        text-align: center;
        padding: 60px 20px;
      }
      .empty-state i {
        font-size: 4rem;
        color: #6c757d;
        margin-bottom: 20px;
      }
      .search-box {
        background: white;
        border-radius: 10px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
      }
    </style>
  </head>
  <body>
    <div class="main-container">
      <div class="row">
        <div class="col-12">
          <h1 class="page-title">
            <i class="fas fa-warehouse me-3"></i>Inventory Management System
          </h1>

          <!-- Alert Messages -->
          <c:if test="${not empty error}">
            <div class="alert alert-danger">
              <i class="fas fa-exclamation-triangle me-2"></i>${error}
            </div>
          </c:if>

          <!-- Statistics Cards -->
          <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="stats-card">
                <div class="stats-number">${inventoryList.size()}</div>
                <div class="stats-label">Total Items</div>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
              <div
                class="stats-card"
                style="background: linear-gradient(45deg, #007bff, #0056b3)"
              >
                <div class="stats-number">
                  <c:set var="totalStock" value="0" />
                  <c:forEach var="inv" items="${inventoryList}">
                    <c:set var="totalStock" value="${totalStock + inv.stock}" />
                  </c:forEach>
                  ${totalStock}
                </div>
                <div class="stats-label">Total Stock</div>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
              <div
                class="stats-card"
                style="background: linear-gradient(45deg, #ffc107, #e0a800)"
              >
                <div class="stats-number">
                  <c:set var="lowStockCount" value="0" />
                  <c:forEach var="inv" items="${inventoryList}">
                    <c:if test="${inv.stock < 10}">
                      <c:set var="lowStockCount" value="${lowStockCount + 1}" />
                    </c:if>
                  </c:forEach>
                  ${lowStockCount}
                </div>
                <div class="stats-label">Low Stock Items</div>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
              <div
                class="stats-card"
                style="background: linear-gradient(45deg, #dc3545, #c82333)"
              >
                <div class="stats-number">
                  <c:set var="outOfStockCount" value="0" />
                  <c:forEach var="inv" items="${inventoryList}">
                    <c:if test="${inv.stock == 0}">
                      <c:set
                        var="outOfStockCount"
                        value="${outOfStockCount + 1}"
                      />
                    </c:if>
                  </c:forEach>
                  ${outOfStockCount}
                </div>
                <div class="stats-label">Out of Stock</div>
              </div>
            </div>
          </div>

          <!-- Search Box -->
          <div class="search-box">
            <div class="row align-items-center">
              <div class="col-md-8">
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-search"></i>
                  </span>
                  <input
                    type="text"
                    id="searchInput"
                    class="form-control"
                    placeholder="Search by material name, code, or category..."
                  />
                </div>
              </div>
              <div class="col-md-4">
                <select id="stockFilter" class="form-select">
                  <option value="">All Stock Levels</option>
                  <option value="high">High Stock (>50)</option>
                  <option value="medium">Medium Stock (10-50)</option>
                  <option value="low">Low Stock (1-9)</option>
                  <option value="zero">Out of Stock (0)</option>
                </select>
              </div>
            </div>
          </div>

          <!-- Inventory Table -->
          <div class="card">
            <div class="card-header bg-secondary text-white">
              <h5 class="card-title mb-0">
                <i class="fas fa-list me-2"></i>Inventory List
                <span class="badge bg-light text-dark ms-2"
                  >${inventoryList.size()}</span
                >
              </h5>
            </div>
            <div class="card-body">
              <c:if test="${not empty inventoryList}">
                <div class="table-responsive inventory-list">
                  <table
                    class="table table-hover table-bordered"
                    id="inventoryTable"
                  >
                    <thead>
                      <tr>
                        <th><i class="fas fa-hashtag me-2"></i>#</th>
                        <th>
                          <i class="fas fa-barcode me-2"></i>Material Code
                        </th>
                        <th><i class="fas fa-box me-2"></i>Material Name</th>
                        <th><i class="fas fa-ruler me-2"></i>Unit</th>
                        <th><i class="fas fa-tags me-2"></i>Category</th>
                        <th>
                          <i class="fas fa-sort-numeric-up me-2"></i>Stock
                        </th>
                        <th>
                          <i class="fas fa-map-marker-alt me-2"></i>Location
                        </th>
                        <th><i class="fas fa-sticky-note me-2"></i>Note</th>
                        <th><i class="fas fa-clock me-2"></i>Last Updated</th>
                        <th><i class="fas fa-user me-2"></i>Updated By</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach
                        var="inv"
                        items="${inventoryList}"
                        varStatus="loop"
                      >
                        <tr
                          class="inventory-row"
                          data-material-name="${inv.materialName.toLowerCase()}"
                          data-material-code="${inv.materialCode.toLowerCase()}"
                          data-category="${inv.categoryName.toLowerCase()}"
                          data-stock="${inv.stock}"
                        >
                          <td><strong>${loop.index + 1}</strong></td>
                          <td>
                            <span class="material-code"
                              >${inv.materialCode}</span
                            >
                          </td>
                          <td>
                            <div class="material-name">${inv.materialName}</div>
                          </td>
                          <td>
                            <span class="unit-badge">${inv.unitName}</span>
                          </td>
                          <td>
                            <span class="category-badge"
                              >${inv.categoryName}</span
                            >
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${inv.stock > 50}">
                                <span class="badge stock-high"
                                  >${inv.stock}</span
                                >
                              </c:when>
                              <c:when test="${inv.stock >= 10}">
                                <span class="badge stock-medium"
                                  >${inv.stock}</span
                                >
                              </c:when>
                              <c:when test="${inv.stock > 0}">
                                <span class="badge stock-low"
                                  >${inv.stock}</span
                                >
                              </c:when>
                              <c:otherwise>
                                <span class="badge stock-zero"
                                  >${inv.stock}</span
                                >
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <span class="location-badge">${inv.location}</span>
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${not empty inv.note}">
                                <span title="${inv.note}">${inv.note}</span>
                              </c:when>
                              <c:otherwise>
                                <span class="text-muted">-</span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <small class="text-muted">
                              <i class="fas fa-calendar-alt me-1"></i>
                              ${inv.lastUpdated}
                            </small>
                          </td>
                          <td>
                            <c:choose>
                              <c:when
                                test="${not empty userMap[inv.updatedBy]}"
                              >
                                <span class="badge bg-info"
                                  >${userMap[inv.updatedBy].fullName}</span
                                >
                              </c:when>
                              <c:otherwise>
                                <span class="text-muted">-</span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
              </c:if>
              <c:if test="${empty inventoryList}">
                <div class="empty-state">
                  <i class="fas fa-box-open"></i>
                  <h4 class="text-muted">No inventory data available</h4>
                  <p class="text-muted">
                    Start by adding materials to your inventory system.
                  </p>
                </div>
              </c:if>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // Search functionality
      document
        .getElementById("searchInput")
        .addEventListener("keyup", function () {
          const searchTerm = this.value.toLowerCase();
          const rows = document.querySelectorAll(".inventory-row");

          rows.forEach((row) => {
            const materialName = row.getAttribute("data-material-name");
            const materialCode = row.getAttribute("data-material-code");
            const category = row.getAttribute("data-category");

            if (
              materialName.includes(searchTerm) ||
              materialCode.includes(searchTerm) ||
              category.includes(searchTerm)
            ) {
              row.style.display = "";
            } else {
              row.style.display = "none";
            }
          });
        });

      // Stock filter functionality
      document
        .getElementById("stockFilter")
        .addEventListener("change", function () {
          const filterValue = this.value;
          const rows = document.querySelectorAll(".inventory-row");

          rows.forEach((row) => {
            const stock = parseInt(row.getAttribute("data-stock"));
            let show = true;

            switch (filterValue) {
              case "high":
                show = stock > 50;
                break;
              case "medium":
                show = stock >= 10 && stock <= 50;
                break;
              case "low":
                show = stock >= 1 && stock < 10;
                break;
              case "zero":
                show = stock === 0;
                break;
              default:
                show = true;
            }

            row.style.display = show ? "" : "none";
          });
        });
    </script>
  </body>
</html>
