
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Category Management Dashboard - Computer Accessories</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .table-responsive {
            margin: 20px 0;
        }
        .search-box {
            margin-bottom: 20px;
        }
        .pagination {
            justify-content: center;
            margin-top: 20px;
        }
        .btn-action {
            width: 50px;
            height: 32px;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 2px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .custom-search {
            max-width: 400px;
        }
        /* Ensure button colors */
        .btn-custom {
            background-color: #f0ad4e !important;
            color: white !important;
            border: none !important;
            padding: 5px 15px !important;
            border-radius: 5px !important;
        }
        .btn-custom:hover {
            background-color: #d9942e !important;
            color: white !important;
        }
        .btn-danger {
            background-color: #dc3545 !important;
            color: white !important;
            border: none !important;
            padding: 5px 10px !important;
            border-radius: 5px !important;
        }
        .btn-danger:hover {
            background-color: #c82333 !important;
            color: white !important;
        }
        .btn-info {
            background-color: #17a2b8 !important;
            color: white !important;
            border: none !important;
            padding: 5px 15px !important;
            border-radius: 5px !important;
        }
        .btn-info:hover {
            background-color: #138496 !important;
            color: white !important;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="container py-2">
            <div class="row py-4 pb-0 pb-sm-4 align-items-center">
                <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                    <a href="HomePage.jsp" class="text-decoration-none">
                        <h2 class="mb-0 text-primary">
                            <i class="fas fa-boxes"></i> Category Management
                        </h2>
                    </a>
                </div>
                <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                    <div class="text-end d-none d-xl-block">
                        <span class="fs-6 text-muted">Administrator</span>
                        <h5 class="mb-0">admin@accessories.com</h5>
                    </div>
                    <a href="logout" class="btn btn-outline-dark btn-lg ms-4">Logout</a>
                </div>
            </div>
        </div>
    </header>

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
                    <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="bi bi-person-fill-up"></i> Category List</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/CategoryServlet?service=addCategory" class="btn btn-custom me-2">
                            <i class="fas fa-plus me-1"></i> Create Category
                        </a>
                       
                    </div>
                </div>

                <!-- Search and Filter Section -->
                <div class="row search-box">
                    <div class="col-md-8">
                        <form action="Category.jsp" method="GET" class="d-flex gap-2">
                            <input type="number" name="categoryId" class="form-control" 
                                   placeholder="Search by ID..." 
                                   value="${param.categoryId != null ? param.categoryId : ''}" 
                                   style="width: 200px; height: 50px" min="1"/>
                            <input type="text" name="categoryName" class="form-control" 
                                   placeholder="Search by Name..." 
                                   value="${param.categoryName != null ? param.categoryName : ''}" 
                                   style="width: 200px; height: 50px"/>
                            <select name="status" class="form-select" style="width: 150px; height: 50px">
                                <option value="">Select Status</option>
                                <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option>
                                <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                            <select name="sortBy" class="form-select" style="width: 150px; height: 50px">
                                <option value="">Sort By</option>
                                <option value="name_asc" ${param.sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="name_desc" ${param.sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="status_asc" ${param.sortBy == 'status_asc' ? 'selected' : ''}>Status (A-Z)</option>
                                <option value="status_desc" ${param.sortBy == 'status_desc' ? 'selected' : ''}>Status (Z-A)</option>
                            </select>
                            <button type="submit" class="btn btn-custom d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="Category.jsp" class="btn btn-secondary" style="width: 150px; height: 50px">Clear </a>
                        </form>
                    </div>
                </div>

                <!-- Category Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col" style="width: 150px">Name</th>
                                <th scope="col" style="width: 150px">Parent ID</th>
                                <th scope="col" style="width: 150px">Created Date</th>
                                <th scope="col" style="width: 150px">Disabled</th>
                                <th scope="col" style="width: 150px">Status</th>
                                <th scope="col" style="width: 200px">Description</th>
                                <th scope="col" style="width: 150px">Priority</th>
                                <th scope="col">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                CategoryDAO dao = new CategoryDAO();
                                List<Category> fullData = null;
                                String categoryIdRaw = request.getParameter("categoryId");
                                String keyword = request.getParameter("categoryName");
                                String status = request.getParameter("status");
                                String sortBy = request.getParameter("sortBy");
                                String pageRaw = request.getParameter("page");

                                // Debugging: Print received parameters
                                System.out.println("DEBUG: categoryIdRaw = " + categoryIdRaw + ", keyword = " + keyword + 
                                    ", status = " + status + ", sortBy = " + sortBy + ", page = " + pageRaw);

                                final int PAGE_SIZE = 5;
                                int currentPage = 1;

                                // Handle current page
                                try {
                                    if (pageRaw != null && !pageRaw.trim().isEmpty()) {
                                        currentPage = Integer.parseInt(pageRaw);
                                        if (currentPage < 1) currentPage = 1;
                                    }
                                } catch (NumberFormatException e) {
                                    currentPage = 1;
                                    System.out.println("❌ Invalid page number: " + pageRaw);
                                }

                                // Determine search conditions
                                boolean hasId = categoryIdRaw != null && !categoryIdRaw.trim().isEmpty();
                                boolean hasName = keyword != null && !keyword.trim().isEmpty();
                                boolean hasStatus = status != null && !status.trim().isEmpty();

                                // Determine sort direction
                                String sortDirection = (sortBy != null && sortBy.endsWith("_desc")) ? "desc" : "asc";
                                String sortField = (sortBy != null && sortBy.startsWith("name")) ? "name" : 
                                                 (sortBy != null && sortBy.startsWith("status")) ? "status" : null;

                                // Fetch data with search and sort conditions
                                if (hasId && hasName && hasStatus) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        fullData = dao.searchCategoriesByIdNameAndStatus(categoryId, keyword.trim(), status.trim());
                                        System.out.println("DEBUG: Search by ID, Name, Status - ID = " + categoryId + ", size = " + fullData.size());
                                    } catch (NumberFormatException e) {
                                        fullData = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                } else if (hasId && hasName) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        fullData = dao.searchCategoriesByIdAndName(categoryId, keyword.trim());
                                        System.out.println("DEBUG: Search by ID and Name - ID = " + categoryId + ", size = " + fullData.size());
                                    } catch (NumberFormatException e) {
                                        fullData = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                } else if (hasId && hasStatus) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        fullData = dao.searchCategoriesByIdAndStatus(categoryId, status.trim());
                                        System.out.println("DEBUG: Search by ID and Status - ID = " + categoryId + ", size = " + fullData.size());
                                    } catch (NumberFormatException e) {
                                        fullData = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                } else if (hasName && hasStatus) {
                                    fullData = dao.searchCategoriesByNameAndStatus(keyword.trim(), status.trim());
                                    System.out.println("DEBUG: Search by Name and Status - size = " + fullData.size());
                                } else if (hasId) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        fullData = dao.searchCategoriesById(categoryId);
                                        System.out.println("DEBUG: Search by ID - ID = " + categoryId + ", size = " + fullData.size());
                                    } catch (NumberFormatException e) {
                                        fullData = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                } else if (hasStatus) {
                                    fullData = dao.searchCategoriesByStatusSorted(status.trim(), sortDirection);
                                    System.out.println("DEBUG: Search by Status - size = " + fullData.size() + ", sort = " + sortDirection);
                                } else if (hasName) {
                                    fullData = dao.searchCategoriesByNameSorted(keyword.trim(), sortDirection);
                                    System.out.println("DEBUG: Search by Name - size = " + fullData.size() + ", sort = " + sortDirection);
                                } else {
                                    // No search conditions, fetch all with sorting
                                    if (sortField != null) {
                                        if (sortField.equals("name")) {
                                            fullData = dao.getAllCategoriesSortedByName(sortDirection);
                                            System.out.println("DEBUG: Sort by Name (" + sortDirection + ") - size = " + fullData.size());
                                        } else if (sortField.equals("status")) {
                                            fullData = dao.getAllCategoriesSortedByStatus(sortDirection);
                                            System.out.println("DEBUG: Sort by Status (" + sortDirection + ") - size = " + fullData.size());
                                        }
                                    } else {
                                        fullData = dao.getAllCategories();
                                        System.out.println("DEBUG: Fetch all categories - size = " + fullData.size());
                                    }
                                }

                                // Debugging: Print first 3 entries after sorting
                                if (fullData != null && !fullData.isEmpty()) {
                                    System.out.println("DEBUG: First 3 entries after sorting: " + 
                                        fullData.subList(0, Math.min(3, fullData.size())).stream()
                                        .map(cat -> "Name: " + cat.getCategory_name() + ", Status: " + cat.getStatus())
                                        .collect(java.util.stream.Collectors.joining("; ")));
                                }

                                // Pagination
                                int totalCategories = (fullData != null) ? fullData.size() : 0;
                                int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);
                                if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

                                int start = (currentPage - 1) * PAGE_SIZE;
                                int end = Math.min(start + PAGE_SIZE, totalCategories);
                                List<Category> data = (start < totalCategories) ? fullData.subList(start, end) : new ArrayList<>();

                                // Set attributes for JSTL
                                pageContext.setAttribute("data", data);
                                pageContext.setAttribute("currentPage", currentPage);
                                pageContext.setAttribute("totalPages", totalPages);
                                pageContext.setAttribute("totalCategories", totalCategories);
                                pageContext.setAttribute("categoryId", categoryIdRaw);
                                pageContext.setAttribute("categoryName", keyword);
                                pageContext.setAttribute("status", status);
                                pageContext.setAttribute("sortBy", sortBy);
                            %>
                            <%-- JSTL for table rows --%>
                            <c:choose>
                                <c:when test="${not empty data}">
                                    <c:forEach var="cat" items="${data}">
                                        <tr>
                                            <td>${cat.category_id}</td>
                                            <td>${cat.category_name}</td>
                                            <td>${cat.parent_id != null ? cat.parent_id : 'None'}</td>
                                            <td><fmt:formatDate value="${cat.created_at}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                            <td>${cat.disable}</td>
                                            <td>${cat.status}</td>
                                            <td>${cat.description}</td>
                                            <td>${cat.priority}</td>
                                            <td>
                                                <div class="d-flex justify-content-center">
                                                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=updateCategory&categoryID=${cat.category_id}" 
                                                       class="btn btn-custom btn-action" 
                                                       title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=deleteCategory&categoryID=${cat.category_id}" 
                                                       class="btn btn-danger btn-action" 
                                                       onclick="return confirm('Are you sure you want to soft delete this category?');" 
                                                       title="Delete">
                                                        <i class="fas fa-trash"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="9" class="text-center text-muted">No categories found.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 0}">
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div>
                            <span>Page ${currentPage} / ${totalPages}</span>
                        </div>
                        <nav>
                            <ul class="pagination">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="Category.jsp?categoryId=${categoryId}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}&page=${currentPage - 1}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="Category.jsp?categoryId=${categoryId}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}&page=${i}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="Category.jsp?categoryId=${categoryId}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}&page=${currentPage + 1}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </c:if>

                <!-- Debug Message -->
                <c:if test="${empty data}">
                    <p style="color:red;">Debug: No data received. Total categories: ${totalCategories}, Total pages: ${totalPages}, Sort: ${sortBy}</p>
                </c:if>
            </div> <!-- end content -->
        </div> <!-- end row -->
    </div> <!-- end container-fluid -->

    <!-- Footer -->
    <footer class="footer py-4 bg-light mt-auto">
        <div class="container text-center">
            <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
</body>
</html>
