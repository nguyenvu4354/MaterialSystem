<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Category Management Dashboard - Computer Accessories</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap & Custom CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
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
                            <i class="fas fa-list"></i> Category Management
                        </h2>
                    </a>
                </div>
                <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                    <div class="text-end d-none d-xl-block">
                        <span class="fs-6 text-muted">Admin</span>
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
                    <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="bi bi-list-ul"></i> Category List</h2>
                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=addCategory" class="btn btn-primary">
                        <i class="fas fa-plus me-1"></i> Add New Category
                    </a>
                </div>

                <!-- Search and Filter Section -->
                <div class="row search-box">
                    <div class="col-md-8">
                        <form method="get" action="Category.jsp" class="d-flex gap-2 align-items-center">
                            <input type="text" name="code" class="form-control" 
                                   placeholder="Search by Code" 
                                   value="${param.code != null ? param.code : ''}" 
                                   style="width: 200px; height: 50px; border: 2px solid gray"/>
                            <input type="text" name="categoryName" class="form-control" 
                                   placeholder="Search by Name" 
                                   value="${param.categoryName != null ? param.categoryName : ''}" 
                                   style="width: 200px; height: 50px; border: 2px solid gray"/>
                            <select name="priority" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">All Priorities</option>
                                <option value="high" ${param.priority == 'high' ? 'selected' : ''}>High</option>
                                <option value="medium" ${param.priority == 'medium' ? 'selected' : ''}>Medium</option>
                                <option value="low" ${param.priority == 'low' ? 'selected' : ''}>Low</option>
                            </select>
                            <select name="status" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">All Status</option>
                                <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active</option>
                                <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                            <select name="sortBy" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                <option value="">Sort By</option>
                                <option value="name_asc" ${param.sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="name_desc" ${param.sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="status_asc" ${param.sortBy == 'status_asc' ? 'selected' : ''}>Status (A-Z)</option>
                                <option value="status_desc" ${param.sortBy == 'status_desc' ? 'selected' : ''}>Status (Z-A)</option>
                                <option value="code_asc" ${param.sortBy == 'code_asc' ? 'selected' : ''}>Code (A-Z)</option>
                                <option value="code_desc" ${param.sortBy == 'code_desc' ? 'selected' : ''}>Code (Z-A)</option>
                            </select>
                            <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                <i class="fas fa-search me-2"></i> Search
                            </button>
                            <a href="Category.jsp" class="btn btn-secondary" style="width: 150px; height: 50px">Clear</a>
                        </form>
                    </div>
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Category Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col" style="width: 150px">Code</th>
                                <th scope="col" style="width: 150px">Name</th>
                                <th scope="col" style="width: 150px">Parent ID</th>
                                <th scope="col" style="width: 150px">Created Date</th>
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
                                String code = request.getParameter("code");
                                String priority = request.getParameter("priority");
                                String keyword = request.getParameter("categoryName");
                                String status = request.getParameter("status");
                                String sortBy = request.getParameter("sortBy");
                                String pageRaw = request.getParameter("page");

                                // Debugging: Log parameters
                                System.out.println("Category.jsp - Parameters: code=" + code + ", categoryName=" + keyword + 
                                    ", priority=" + priority + ", status=" + status + ", sortBy=" + sortBy + ", page=" + pageRaw);

                                final int PAGE_SIZE = 5;
                                int currentPage = 1;

                                // Parse current page
                                try {
                                    if (pageRaw != null && !pageRaw.trim().isEmpty()) {
                                        currentPage = Integer.parseInt(pageRaw);
                                        if (currentPage < 1) currentPage = 1;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("[ERROR] Invalid page number: " + pageRaw);
                                }

                                // Determine search conditions
                                boolean hasCode = code != null && !code.trim().isEmpty();
                                boolean hasPriority = priority != null && !priority.trim().isEmpty();
                                boolean hasName = keyword != null && !keyword.trim().isEmpty();
                                boolean hasStatus = status != null && !status.trim().isEmpty();

                                // Determine sort direction
                                String sortDirection = (sortBy != null && sortBy.endsWith("_desc")) ? "desc" : "asc";
                                String sortField = sortBy != null ? 
                                    (sortBy.startsWith("name") ? "name" : 
                                     sortBy.startsWith("status") ? "status" : 
                                     sortBy.startsWith("code") ? "code" : null) : null;

                                // Fetch data based on conditions
                                try {
                                    if (hasCode) {
                                        fullData = dao.searchCategoriesByCode(code.trim());
                                    } else if (hasPriority && hasName && hasStatus) {
                                        fullData = dao.searchCategoriesByPriorityNameAndStatus(priority.trim(), keyword.trim(), status.trim());
                                    } else if (hasPriority && hasName) {
                                        fullData = dao.searchCategoriesByPriorityAndName(priority.trim(), keyword.trim());
                                    } else if (hasPriority && hasStatus) {
                                        fullData = dao.searchCategoriesByPriorityAndStatus(priority.trim(), status.trim());
                                    } else if (hasName && hasStatus) {
                                        fullData = dao.searchCategoriesByNameAndStatus(keyword.trim(), status.trim());
                                    } else if (hasPriority) {
                                        fullData = dao.searchCategoriesByPriority(priority.trim());
                                    } else if (hasStatus) {
                                        fullData = dao.searchCategoriesByStatusSorted(status.trim(), sortDirection);
                                    } else if (hasName) {
                                        fullData = dao.searchCategoriesByNameSorted(keyword.trim(), sortDirection);
                                    } else {
                                        if (sortField != null) {
                                            if (sortField.equals("name")) {
                                                fullData = dao.getAllCategoriesSortedByName(sortDirection);
                                            } else if (sortField.equals("status")) {
                                                fullData = dao.getAllCategoriesSortedByStatus(sortDirection);
                                            } else if (sortField.equals("code")) {
                                                fullData = dao.getAllCategoriesSortedByCode(sortDirection);
                                            }
                                        } else {
                                            fullData = dao.getAllCategories();
                                        }
                                    }

                                    // Debugging: Log data size
                                    System.out.println("Category.jsp - Data fetched: " + (fullData != null ? fullData.size() : 0) + " categories");

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
                                    pageContext.setAttribute("code", code);
                                    pageContext.setAttribute("priority", priority);
                                    pageContext.setAttribute("categoryName", keyword);
                                    pageContext.setAttribute("status", status);
                                    pageContext.setAttribute("sortBy", sortBy);
                                } catch (Exception e) {
                                    System.out.println("[ERROR] Error fetching data: " + e.getMessage());
                                    e.printStackTrace();
                                    pageContext.setAttribute("error", "Error loading categories: " + e.getMessage());
                                }
                            %>
                            <c:choose>
                                <c:when test="${not empty data}">
                                    <c:forEach var="cat" items="${data}">
                                        <tr>
                                            <td>${cat.category_id}</td>
                                            <td>${cat.code}</td>
                                            <td>${cat.category_name}</td>
                                            <td>${cat.parent_id != null ? cat.parent_id : 'None'}</td>
                                            <td><fmt:formatDate value="${cat.created_at}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                            <td>${cat.status}</td>
                                            <td>${cat.description}</td>
                                            <td>${cat.priority}</td>
                                            <td>
                                                <div class="d-flex justify-content-center">
                                                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=updateCategory&categoryID=${cat.category_id}" 
                                                       class="btn btn-warning btn-action" 
                                                       title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=deleteCategory&categoryID=${cat.category_id}" 
                                                       class="btn btn-danger btn-action" 
                                                       onclick="return confirm('Are you sure you want to delete this category?');" 
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
                <c:if test="${totalPages > 1}">
                    <nav>
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="Category.jsp?page=${currentPage - 1}&code=${code}&priority=${priority}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="Category.jsp?page=${i}&code=${code}&priority=${priority}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="Category.jsp?page=${currentPage + 1}&code=${code}&priority=${priority}&categoryName=${categoryName}&status=${status}&sortBy=${sortBy}">Next</a>
                            </li>
                        </ul>
                    </nav>
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
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
</body>
</html>