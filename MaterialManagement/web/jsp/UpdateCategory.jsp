<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update Category</title>
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
                <h3>Update Category</h3>
            </div>
            <div class="card-body">
              
                <% if (request.getAttribute("error") != null) { %>
                    <p class="text-danger"><%= request.getAttribute("error") %></p>
                <% } %>
        
                <% if (request.getAttribute("success") != null) { %>
                    <p class="text-success"><%= request.getAttribute("success") %></p>
                <% } %>
                
                <form action="${pageContext.request.contextPath}/CategoryServlet?service=updateCategory" method="post" class="form">
                    <input type="hidden" name="categoryID" value="${c.category_id}">

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="categoryName">Category Name</label>
                                <input type="text" id="categoryName" name="categoryName" class="form-control" 
                                       value="${c.category_name}" required>
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
                                        Category currentCategory = (Category) request.getAttribute("c");
                                        if (categories != null) {
                                            for (Category category : categories) {
                                                String selected = "";
                                                if (currentCategory != null && currentCategory.getParent_id() != null && 
                                                    category.getCategory_id() == currentCategory.getParent_id()) {
                                                    selected = "selected";
                                                }
                                    %>
                                        <option value="<%= category.getCategory_id() %>" <%= selected %>>
                                            <%= category.getCategory_id() %> - <%= category.getCategory_name() %>
                                        </option>
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
                          
                                    <option value="Active" ${c.status == 'Active' ? 'selected' : ''}>Active</option>
                                    <option value="Inactive" ${c.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="priority">Priority</label>
                                <input type="number" id="priority" name="priority" class="form-control" 
                                       min="0" value="${c.priority}" required>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="disable">Disable (0/1)</label>
                                 <select id="disable" name="disable" class="form-control" required>
                  
                                    <option value="0" ${c.disable == '0' ? 'selected' : ''}>0</option>
                                    <option value="1" ${c.disable == '1' ? 'selected' : ''}>1</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="createdAt">Created At</label>
                                <fmt:formatDate value="${c.created_at}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedCreatedAt" />
                                <input type="datetime-local" id="createdAt" name="createdAt" class="form-control" 
                                       value="${formattedCreatedAt}" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control">${c.description}</textarea>
                    </div>

                    <div class="mt-3">
                        <button type="submit" name="submit" value="Update Category" class="btn btn-primary">
                            <i class="fas fa-save"></i> Update Category
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