<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Category</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .card-header {
            background-color: #0d6efd;
            color: white;
            padding: 0.75rem 1.25rem;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            font-weight: bold;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header">
                <h3>Create Category</h3>
            </div>
            <div class="card-body">
         
                <% if (request.getAttribute("error") != null) { %>
                    <p class="text-danger"><%= request.getAttribute("error") %></p>
                <% } %>
                <form action="${pageContext.request.contextPath}/CategoryServlet?service=addCategory" method="post" class="form">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="categoryName">Category Name</label>
                                <input type="text" id="categoryName" name="categoryName" class="form-control" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="parentID">Parent Category (Optional)</label>
                                <select id="parentID" name="parentID" class="form-control">
                                    <option value="">None</option>
                                    <%
                                        CategoryDAO dao = new CategoryDAO();
                                        List<Category> categories = dao.getAllCategories();
                                        if (categories != null) {
                                            for (Category category : categories) {
                                    %>
                                        <option value="<%= category.getCategory_id() %>"><%= category.getCategory_id() %> - <%= category.getCategory_name() %></option>
                                    <%      }
                                        } else {
                                            System.out.println("Failed to retrieve categories.");
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="status">Status</label>
                                <select id="status" name="status" class="form-control" required>
  
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="priority">Priority</label>
                                <input type="number" id="priority" name="priority" class="form-control" min="1" required>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="disable">Disable </label>
                                <select id="disable" name="disable" class="form-control" required>
                  
                                    <option value="0" ${c.disable == '0' ? 'selected' : ''}>0</option>
                                    <option value="1" ${c.disable == '1' ? 'selected' : ''}>1</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="createdAt">Created At</label>
                                <input type="datetime-local" id="createdAt" name="createdAt" class="form-control" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" required></textarea>
                    </div>

                    <div class="mt-3">
                        <button type="submit" name="submit" value="Add Category" class="btn btn-primary">
                            <i class="fas fa-save"></i> Create Category
                        </button>
                        <a href="${pageContext.request.contextPath}/jsp/Category.jsp" class="btn btn-secondary ms-2">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>