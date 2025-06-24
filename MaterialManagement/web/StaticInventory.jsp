<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="entity.User" %>
  
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Static Inventory - Computer Accessories</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!-- Gộp các link CSS lại cho gọn -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
    <link rel="stylesheet" type="text/css" href="css/vendor.css" />
    <link rel="stylesheet" type="text/css" href="style.css" />
    <style>
      body { background-color: #f8f9fa; padding: 20px; }
      .content { padding-left: 20px; font-family: "Roboto", sans-serif; }
      .stats-card { border-radius: 10px; border: 1px solid #bdbdbd; background: #f8f9fa; padding: 18px 0; margin-bottom: 18px; box-shadow: 0 2px 8px rgba(222, 173, 111, 0.08); text-align: center; }
      .stats-number { font-size: 2.1rem; font-weight: 700; color: black; }
      .stats-label { font-size: 1.1rem; opacity: 0.95; color: black; }
      .table-responsive { margin: 20px 0; }
      .search-box { margin-bottom: 20px; }
      .badge, .unit-badge, .category-badge, .location-badge { border: 1px solid #bdbdbd; padding: 7px 16px; font-size: 15px; font-weight: 600; background: #f8f9fa; color: black; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04); }
      .stock-high { background: #d4edda !important; color: #155724 !important; }
      .stock-medium { background: #fff3cd !important; color: #856404 !important; }
      .stock-low { background: #f8d7da !important; color: #721c24 !important; }
      .stock-zero { background: #f8d7da !important; color: #721c24 !important; }
    </style>
  </head>
  <body>
    <!-- Header -->
    <jsp:include page="HeaderAdmin.jsp" />
    <!-- Main content -->
    <div class="container-fluid">
      <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 bg-light p-0">
          <jsp:include page="Sidebar.jsp" />
        </div>
        <!-- Page Content -->
        <div class="col-md-9 col-lg-10 content px-md-4">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="text-primary fw-bold display-6 border-bottom pb-2">
              <i class="fas fa-boxes me-2"></i>Static Inventory
            </h2>
          </div>
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
              <div class="stats-card" style="background: linear-gradient(45deg, #007bff, #0056b3)">
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
              <div class="stats-card" style="background: linear-gradient(45deg, #ffc107, #e0a800)">
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
              <div class="stats-card" style="background: linear-gradient(45deg, #dc3545, #c82333)">
                <div class="stats-number">
                  <c:set var="outOfStockCount" value="0" />
                  <c:forEach var="inv" items="${inventoryList}">
                    <c:if test="${inv.stock == 0}">
                      <c:set var="outOfStockCount" value="${outOfStockCount + 1}" />
                    </c:if>
                  </c:forEach>
                  ${outOfStockCount}
                </div>
                <div class="stats-label">Out of Stock</div>
              </div>
            </div>
          </div>
          <!-- Search and Filter Section -->
          <div class="row search-box">
            <div class="col-md-8">
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-search"></i>
                </span>
                <input type="text" id="searchInput" class="form-control" placeholder="Search by material name, code, or category..." style="height: 50px; border: 2px solid gray" />
              </div>
            </div>
            <div class="col-md-4">
              <select id="stockFilter" class="form-select" style="height: 50px; border: 2px solid gray">
                <option value="">All Stock Levels</option>
                <option value="high">High Stock (&gt;50)</option>
                <option value="medium">Medium Stock (10-50)</option>
                <option value="low">Low Stock (1-9)</option>
                <option value="zero">Out of Stock (0)</option>
              </select>
            </div>
          </div>
          <!-- Inventory Table -->
          <div class="card shadow">
            <div class="card-header bg-primary text-white">
              <h5 class="card-title mb-0">
                <i class="fas fa-list me-2"></i>Inventory List
                <span class="badge bg-light text-dark ms-2">${inventoryList.size()}</span>
              </h5>
            </div>
            <div class="card-body">
              <c:if test="${not empty inventoryList}">
                <div class="table-responsive">
                  <table class="table table-hover table-bordered align-middle text-center" id="inventoryTable">
                    <thead class="table-light">
                      <tr>
                        <th>#</th>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Unit</th>
                        <th>Category</th>
                        <th>Stock</th>
                        <th>Location</th>
                        <th>Note</th>
                        <th>Last Updated</th>
                        <th>Updated By</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="inv" items="${inventoryList}" varStatus="loop">
                        <tr class="inventory-row" data-material-name="${inv.materialName.toLowerCase()}" data-material-code="${inv.materialCode.toLowerCase()}" data-category="${inv.categoryName.toLowerCase()}" data-stock="${inv.stock}">
                          <td><strong>${loop.index + 1}</strong></td>
                          <td><span class="material-code">${inv.materialCode}</span></td>
                          <td><div class="material-name">${inv.materialName}</div></td>
                          <td><div class="material-name">${inv.unitName}</div></td>
                          <td><div class="material-name">${inv.categoryName}</div></td>
                          <td>
                            <c:choose>
                              <c:when test="${inv.stock > 50}"><span class="badge stock-high">${inv.stock}</span></c:when>
                              <c:when test="${inv.stock >= 10}"><span class="badge stock-medium">${inv.stock}</span></c:when>
                              <c:when test="${inv.stock > 0}"><span class="badge stock-low">${inv.stock}</span></c:when>
                              <c:otherwise><span class="badge stock-zero">${inv.stock}</span></c:otherwise>
                            </c:choose>
                          </td>
                          <td><div class="material-name">${inv.location}</div></td>
                          <td>
                            <c:choose>
                              <c:when test="${not empty inv.note}"><span title="${inv.note}">${inv.note}</span></c:when>
                              <c:otherwise><span class="text-muted">-</span></c:otherwise>
                            </c:choose>
                          </td>
                          <td><small class="text-muted">${inv.lastUpdated}</small></td>
                          <td>
                            <c:choose>
                              <c:when test="${not empty userMap[inv.updatedBy]}"><span style="color: black; font-weight: 500">${userMap[inv.updatedBy].fullName}</span></c:when>
                              <c:otherwise><span class="text-muted">-</span></c:otherwise>
                            </c:choose>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
              </c:if>
              <c:if test="${empty inventoryList}">
                <div class="text-center py-5">
                  <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                  <h4 class="text-muted">No inventory data available</h4>
                  <p class="text-muted">Start by adding materials to your inventory system.</p>
                </div>
              </c:if>
            </div>
          </div>
          <!-- Pagination -->
          <c:if test="${totalPages > 1}">
            <div class="d-flex justify-content-between align-items-center mt-4">
              <div class="text-muted">Showing ${(currentPage - 1) * pageSize + 1} to ${Math.min(currentPage * pageSize, totalItems)} of ${totalItems} items</div>
              <nav>
                <ul class="pagination">
                  <li class="page-item ${currentPage == 1 ? 'disabled' : ''}"><a class="page-link" href="StaticInventory?page=${currentPage - 1}">Previous</a></li>
                  <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}"><a class="page-link" href="StaticInventory?page=${i}">${i}</a></li>
                  </c:forEach>
                  <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}"><a class="page-link" href="StaticInventory?page=${currentPage + 1}">Next</a></li>
                </ul>
              </nav>
            </div>
          </c:if>
          <!-- Nút và section báo cáo -->
          <div class="d-flex gap-2 mb-3">
            <button class="btn btn-primary" id="viewReportBtn">View Report</button>
            <c:if test="${not empty sessionScope.user and sessionScope.user.roleId == 3}">
              <a href="ImportMaterial" class="btn btn-success">Import Material</a>
              <a href="ExportMaterial" class="btn btn-info">Export Material</a>
            </c:if>
          </div>
          <div id="reportSection" style="display: none">
            <div class="row mb-4">
              <div class="col-md-6 mb-3"><canvas id="barChart"></canvas></div>
              <div class="col-md-6 mb-3"><canvas id="lineChart"></canvas></div>
            </div>
          </div>
        </div>
        <!-- end content -->
      </div>
      <!-- end row -->
    </div>
    <!-- end container-fluid -->
    <!-- Footer -->
    <jsp:include page="Footer.jsp" />
    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
      document.getElementById("searchInput").addEventListener("keyup", function () {
        const searchTerm = this.value.toLowerCase();
        const rows = document.querySelectorAll(".inventory-row");
        rows.forEach((row) => {
          const materialName = row.getAttribute("data-material-name");
          const materialCode = row.getAttribute("data-material-code");
          const category = row.getAttribute("data-category");
          if (materialName.includes(searchTerm) || materialCode.includes(searchTerm) || category.includes(searchTerm)) {
            row.style.display = "";
          } else {
            row.style.display = "none";
          }
        });
      });
      document.getElementById("stockFilter").addEventListener("change", function () {
        const filterValue = this.value;
        const rows = document.querySelectorAll(".inventory-row");
        rows.forEach((row) => {
          const stock = parseInt(row.getAttribute("data-stock"));
          let show = true;
          switch (filterValue) {
            case "high": show = stock > 50; break;
            case "medium": show = stock >= 10 && stock <= 50; break;
            case "low": show = stock >= 1 && stock < 10; break;
            case "zero": show = stock === 0; break;
            default: show = true;
          }
          row.style.display = show ? "" : "none";
        });
      });
      function addSearchParamsToLinks() {
        const searchTerm = document.getElementById("searchInput").value;
        const filterValue = document.getElementById("stockFilter").value;
        const paginationLinks = document.querySelectorAll('.pagination a');
        paginationLinks.forEach(link => {
          const url = new URL(link.href);
          if (searchTerm) url.searchParams.set('search', searchTerm);
          if (filterValue) url.searchParams.set('filter', filterValue);
          link.href = url.toString();
        });
      }
      document.getElementById("searchInput").addEventListener("input", addSearchParamsToLinks);
      document.getElementById("stockFilter").addEventListener("change", addSearchParamsToLinks);
      document.addEventListener('DOMContentLoaded', addSearchParamsToLinks);
      let chartRendered = false;
      document.getElementById('viewReportBtn').addEventListener('click', function() {
        const section = document.getElementById('reportSection');
        if (section.style.display === 'none') {
          section.style.display = 'block';
          if (!chartRendered) {
            renderCharts();
            chartRendered = true;
          }
        } else {
          section.style.display = 'none';
        }
      });
      function renderCharts() {
        var inventoryData = [];
        <c:forEach var="inv" items="${inventoryList}">
          inventoryData.push({
            name: "${inv.materialName}",
            category: "${inv.categoryName}",
            stock: ${inv.stock}
          });
        </c:forEach>
        var categoryMap = {};
        inventoryData.forEach(function(item) {
          if (!categoryMap[item.category]) categoryMap[item.category] = 0;
          categoryMap[item.category] += item.stock;
        });
        var barLabels = Object.keys(categoryMap);
        var barData = Object.values(categoryMap);
        var lineLabels = inventoryData.map(function(item) { return item.name; });
        var lineData = inventoryData.map(function(item) { return item.stock; });
        var barCtx = document.getElementById('barChart').getContext('2d');
        new Chart(barCtx, {
          type: 'bar',
          data: {
            labels: barLabels,
            datasets: [{
              label: 'Total Stock by Category',
              data: barData,
              backgroundColor: 'rgba(54, 162, 235, 0.7)'
            }]
          },
          options: {
            responsive: true,
            plugins: { legend: { display: false } }
          }
        });
        var lineCtx = document.getElementById('lineChart').getContext('2d');
        new Chart(lineCtx, {
          type: 'line',
          data: {
            labels: lineLabels,
            datasets: [{
              label: 'Stock by Material',
              data: lineData,
              fill: false,
              borderColor: 'rgba(255, 99, 132, 0.7)',
              tension: 0.1
            }]
          },
          options: {
            responsive: true
          }
        });
      }
    </script>
  </body>
</html>