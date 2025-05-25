<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard - Category List</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        .btn-custom {
            background-color: #f0ad4e;
            color: white;
            border: none;
            padding: 5px 15px;
            border-radius: 5px;
        }
        .btn-custom:hover {
            background-color: #d9942e;
            color: white;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 5px;
        }
        .btn-danger:hover {
            background-color: #c82333;
            color: white;
        }
    </style>
</head>
<body>

    <header>
        <div class="container py-2">
            <div class="row py-4 pb-0 pb-sm-4 align-items-center">
                <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                    <a href="HomePage.jsp">
                        <img src="images/logo.png" alt="logo" class="img-fluid" width="300px">
                    </a>
                </div>
                <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
                    <div class="text-end d-none d-xl-block">
                        <span class="fs-6 text-muted">Admin</span>
                        <h5 class="mb-0">admin@accessories.com</h5>
                    </div>
                    <a href="logout" class="btn btn-outline-dark btn-lg ms-4">
                        Logout
                    </a>
                </div>
            </div>
        </div>
    </header>

    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <%@ include file="Sidebar.jsp" %>
            </div>

            <div class="col-md-9 col-lg-10 px-md-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h2 class="text-primary fw-bold display-6 border-bottom pb-2">📋 Category List</h2>
                    <a href="${pageContext.request.contextPath}/CategoryServlet?service=addCategory" class="btn btn-custom">Create Category</a>
                </div>

                <form method="get" action="Category.jsp" class="mb-3 d-flex flex-wrap gap-2 align-items-center">
                    <input type="number" name="categoryId" class="form-control" placeholder="Search by ID" value="${param.categoryId != null ? param.categoryId : ''}" style="max-width: 150px;" min="1"/>
                    <input type="text" name="categoryName" class="form-control" placeholder="Search by Name" value="${param.categoryName != null ? param.categoryName : ''}" style="max-width: 200px;"/>
                    <select name="status" class="form-select" style="max-width: 150px;">
                        <option value="">Select Status</option>
                        <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option>
                        <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                    </select>
                    <button type="submit" class="btn btn-custom">Search</button>
                    <a href="Category.jsp" class="btn btn-secondary">Clear</a>
                </form>

                <!-- Category Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Parent ID</th>
                                <th>Created At</th>
                                <th>Disable</th>
                                <th>Status</th>
                                <th>Description</th>
                                <th>Priority</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                CategoryDAO dao = new CategoryDAO();
                                List<Category> data = null;
                                String categoryIdRaw = request.getParameter("categoryId");
                                String keyword = request.getParameter("categoryName");
                                String status = request.getParameter("status");

                                boolean hasId = categoryIdRaw != null && !categoryIdRaw.trim().isEmpty();
                                boolean hasName = keyword != null && !keyword.trim().isEmpty();
                                boolean hasStatus = status != null && !status.trim().isEmpty();

                                if (hasId && hasName && hasStatus) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        data = dao.searchCategoriesByIdNameAndStatus(categoryId, keyword.trim(), status.trim());
                                    } catch (NumberFormatException e) {
                                        data = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                }
                
                                else if (hasId && hasName) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        data = dao.searchCategoriesByIdAndName(categoryId, keyword.trim());
                                    } catch (NumberFormatException e) {
                                        data = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                }
                              
                                else if (hasId && hasStatus) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        data = dao.searchCategoriesByIdAndStatus(categoryId, status.trim());
                                    } catch (NumberFormatException e) {
                                        data = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                }
                         
                                else if (hasName && hasStatus) {
                                    data = dao.searchCategoriesByNameAndStatus(keyword.trim(), status.trim());
                                }
                              
                                else if (hasId) {
                                    try {
                                        int categoryId = Integer.parseInt(categoryIdRaw.trim());
                                        data = dao.searchCategoriesById(categoryId);
                                    } catch (NumberFormatException e) {
                                        data = new ArrayList<>();
                                        System.out.println("❌ Invalid category ID format: " + categoryIdRaw);
                                    }
                                }
                             
                                else if (hasStatus) {
                                    data = dao.searchCategoriesByStatus(status.trim());
                                }
                          
                                else if (hasName) {
                                    data = dao.searchCategoriesByName(keyword.trim());
                                }
                           
                                else {
                                    data = dao.getAllCategories();
                                }
                                pageContext.setAttribute("data", data);
                            %>
                            <c:choose>
                                <c:when test="${not empty data}">
                                    <c:forEach var="cat" items="${data}">
                                        <tr>
                                            <td>${cat.category_id}</td>
                                            <td>${cat.category_name}</td>
                                            <td>${cat.parent_id != null ? cat.parent_id : 'None'}</td>
                                            <td>${cat.created_at}</td>
                                            <td>${cat.disable}</td>
                                            <td>${cat.status}</td>
                                            <td>${cat.description}</td>
                                            <td>${cat.priority}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/CategoryServlet?service=updateCategory&categoryID=${cat.category_id}" class="btn btn-custom btn-sm">Edit</a>
                                                <a href="${pageContext.request.contextPath}/CategoryServlet?service=deleteCategory&categoryID=${cat.category_id}" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete?')">Delete</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="9">No categories found.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
         
                <c:if test="${empty data}">
                    <p style="color:red;">Debug: No data received.</p>
                </c:if>
            </div> 
        </div>
    </div> 

    
    <footer class="footer py-4 bg-light mt-auto">
        <div class="container text-center">
            <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
        </div>
    </footer>

    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>