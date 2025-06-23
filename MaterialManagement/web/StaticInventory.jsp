<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Static Inventory</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
      rel="stylesheet"
    />
    <link rel="stylesheet" href="style.css" />
    <style>
      body {
        background: gainsboro;
        color: black;
      }
      .container {
        margin-top: 32px;
      }
      .card {
        border: 1px solid gray;
        border-radius: 10px;
        padding: 24px;
        margin-top: 32px;
        background: white;
      }
      .page-title {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 24px;
        color: black;
        text-align: center;
      }
      .form-label,
      .form-control,
      .form-select {
        font-size: 16px;
        color: black;
      }
      .form-control,
      .form-select {
        border-radius: 8px;
        border: 1px solid #bdbdbd;
        background: #f8f9fa;
        padding: 10px 14px;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
      }
      .form-control:focus,
      .form-select:focus {
        border-color: #888;
        box-shadow: 0 0 0 2px #bdbdbd33;
      }
      .table {
        background: white;
        color: black;
        border: 1px solid gray;
        border-radius: 10px;
        overflow: hidden;
      }
      .table th,
      .table td {
        color: black;
        font-size: 16px;
        font-weight: 400;
        border: 1px solid #bdbdbd;
        padding: 10px 12px;
        text-align: center;
        background: white;
      }
      .table th {
        font-weight: 700;
        background: lightgray;
        color: black;
        border-top: none;
      }
      .badge,
      .unit-badge,
      .category-badge,
      .location-badge {
        border-radius: 10px;
        border: 1px solid #bdbdbd;
        padding: 7px 16px;
        font-size: 15px;
        font-weight: 600;
        background: #f8f9fa;
        color: black;
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
      }
      .stats-card {
        border-radius: 10px;
        border: 1px solid #bdbdbd;
        background: #f8f9fa;
        padding: 18px 0;
        margin-bottom: 18px;
        box-shadow: 0 2px 8px rgba(222, 173, 111, 0.08);
        text-align: center;
      }
      .stats-number {
        font-size: 2.1rem;
        font-weight: 700;
        color: black;
      }
      .stats-label {
        font-size: 1.1rem;
        opacity: 0.95;
        color: black;
      }
      .btn {
        font-size: 16px;
        padding: 8px 18px;
        color: black;
        background: gainsboro;
        border: 1px solid gray;
        border-radius: 8px;
        font-weight: 600;
      }
      .btn:hover {
        background: gray;
        color: white;
      }
      .alert,
      .stock-info {
        color: black;
        text-align: center;
        font-size: 16px;
        font-weight: 500;
      }
    </style>
  </head>
  <body>
    <jsp:include page="HeaderAdmin.jsp" />
    <div class="main-container">
      <div class="row">
        <div class="col-12">
          <div class="container">
            <div
              class="card"
              style="
                border: 1px solid #dee2e6;
                border-radius: 10px;
                padding: 24px;
                margin-top: 32px;
              "
            >
              <div class="page-title">Static Inventory</div>

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
                        <c:set
                          var="totalStock"
                          value="${totalStock + inv.stock}"
                        />
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
                          <c:set
                            var="lowStockCount"
                            value="${lowStockCount + 1}"
                          />
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
                            <th>
                              <i class="fas fa-box me-2"></i>Material Name
                            </th>
                            <th><i class="fas fa-ruler me-2"></i>Unit</th>
                            <th><i class="fas fa-tags me-2"></i>Category</th>
                            <th>
                              <i class="fas fa-sort-numeric-up me-2"></i>Stock
                            </th>
                            <th>
                              <i class="fas fa-map-marker-alt me-2"></i>Location
                            </th>
                            <th><i class="fas fa-sticky-note me-2"></i>Note</th>
                            <th>
                              <i class="fas fa-clock me-2"></i>Last Updated
                            </th>
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
                                <div class="material-name">
                                  ${inv.materialName}
                                </div>
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
                                <span class="location-badge"
                                  >${inv.location}</span
                                >
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
      </div>
    </div>
    <jsp:include page="Footer.jsp" />
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
