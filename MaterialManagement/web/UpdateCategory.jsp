<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Update Category</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIV pyrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
        <style>
            .card-header {
                background-color: #DEAD6F;
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
                    <c:if test="${not empty error}">
                        <p class="alert alert-danger">${error}</p>
                    </c:if>
                    <c:if test="${not empty success}">
                        <p class="alert alert-success">${success}</p>
                    </c:if>
                    <c:if test="${empty c}">
                        <p class="alert alert-danger">Category not found.</p>
                        <a href="${pageContext.request.contextPath}/Category?service=listCategory" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to Category List
                        </a>
                    </c:if>
                    <c:if test="${not empty c}">
                        <form action="${pageContext.request.contextPath}/Category?service=updateCategory" method="post">
                            <input type="hidden" name="categoryID" value="${c.category_id}">
                            <input type="hidden" name="disable" value="${c.disable}">

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="categoryName">Category Name</label>
                                        <input type="text" id="categoryName" name="categoryName" class="form-control" 
                                               value="${c.category_name}" readonly style="background-color: #f8f9fa;">
                                        <small class="text-muted">Category name cannot be changed</small>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="code">Code</label>
                                        <input type="text" id="code" name="code" class="form-control" 
                                               value="${c.code}" required>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-3">

                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="status">Status</label>
                                        <select id="status" name="status" class="form-control" required>
                                            <option value="active" ${c.status == 'active' ? 'selected' : ''}>Active</option>
                                            <option value="inactive" ${c.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="priority">Priority</label>
                                        <select id="priority" name="priority" class="form-control" required>
                                            <option value="high" ${c.priority == 'high' ? 'selected' : ''}>High</option>
                                            <option value="medium" ${c.priority == 'medium' ? 'selected' : ''}>Medium</option>
                                            <option value="low" ${c.priority == 'low' ? 'selected' : ''}>Low</option>
                                        </select>
                                    </div>
                                </div>

                            </div>
                                                                 <div class="row mb-3">
                                                                 <div class="col-md-6">
                                                            <label for="parentID">Parent Category</label>
                                                            <div class="form-group">
                                                                <select id="parentID" name="parentID" class="form-control">
                                                                    <option value="">None</option>
                            <%
                                CategoryDAO dao = new CategoryDAO();
                                List<Category> categories = dao.getAllCategories();
                                Category currentCategory = (Category) request.getAttribute("c");
                                if (categories != null) {
                                    for (Category category : categories) {
                                        if (category.getCategory_id() != currentCategory.getCategory_id()) {
                                            String selected = (currentCategory.getParent_id() != null && 
                                                              category.getCategory_id() == currentCategory.getParent_id()) ? "selected" : "";
                            %>
                            <option value="<%= category.getCategory_id() %>" <%= selected %>>
                            <%= category.getCategory_id() %> - <%= category.getCategory_name() %>
                        </option>
                            <%      }
                                    }
                                } else {
                                    System.out.println("UpdateCategory.jsp - Failed to retrieve categories for parentID dropdown.");
                                }
                            %>
                        </select>
                    </div>
                </div>
</div>


                            <div class="form-group">
                                <label for="description">Description</label>
                                <textarea id="description" name="description" class="form-control" required>${c.description}</textarea>
                            </div>

                            <div class="mt-3">
                                <button type="submit" name="submit" value="Update Category" class="btn" style="background-color: #DEAD6F; color: white;">
                                    <i class="fas fa-save"></i> Update Category
                                </button>
                                <a href="${pageContext.request.contextPath}/Category?service=listCategory" class="btn btn-secondary ms-2">
                                    <i class="fas fa-times"></i> Cancel
                                </a>
                            </div>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3 WysRmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
    </body>
</html>