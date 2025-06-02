<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String userAgent = request.getHeader("User-Agent");
    if (userAgent != null && !userAgent.contains("Edg")) {
%>
    <script>
        alert("Trang này chỉ hỗ trợ trên Microsoft Edge!");
        window.location.href = "https://www.microsoft.com/edge"; 
    </script>
<%
        return; 
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Category</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
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
        .error-message {
            color: #dc3545;
            font-weight: bold;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header">
                <h3><i class="fas fa-plus"></i> Create Category</h3>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger error-message">${error}</div>
                </c:if>
                <form action="${pageContext.request.contextPath}/CategoryServlet?service=addCategory" method="post">
                    <input type="hidden" name="disable" value="0">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="categoryName">Category Name <span class="text-danger">*</span></label>
                                <input type="text" id="categoryName" name="categoryName" class="form-control" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="code">Code <span class="text-danger">*</span></label>
                                <input type="text" id="code" name="code" class="form-control" required>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="parentID">Parent Category </label>
                                <select id="parentID" name="parentID" class="form-control">
                                    <option value="">None</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.category_id}">
                                            ${category.category_id} - ${category.category_name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="status">Status <span class="text-danger">*</span></label>
                                <select id="status" name="status" class="form-control" required>
                                    <option value="active">Active</option>
                                    <option value="inactive">Inactive</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="priority">Priority <span class="text-danger">*</span></label>
                                <select id="priority" name="priority" class="form-control" required>
                                    <option value="high">High</option>
                                    <option value="medium">Medium</option>
                                    <option value="low">Low</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="createdAt">Created At <span class="text-danger">*</span></label>
                                <input type="datetime-local" id="createdAt" name="createdAt" class="form-control" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control"></textarea>
                    </div>

                    <div class="mt-3">
                        <button type="submit" name="submit" value="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Create Category
                        </button>
                        <a href="${pageContext.request.contextPath}/Category.jsp" class="btn btn-secondary ms-2">
                            <i class="fas fa-times"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF7qD9jIH/wu1b3X9g8a8wXg8q3W0Xb2F5w5D7vX6D9z5j7mX1Z8a3X3z9z5j7mX1Z8a3X3z9z5j7mX1Z8a" crossorigin="anonymous"></script>
</body>
</html>