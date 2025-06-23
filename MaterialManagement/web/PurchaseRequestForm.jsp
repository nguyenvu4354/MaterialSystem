<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Purchase Request</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap & Fonts -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        .purchase-form .form-control, .purchase-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .purchase-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .purchase-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .purchase-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
    </style>
</head>
<body>
    <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover; min-height: 100vh;">
        <div class="container">
            <div class="row my-5 py-5">
                <div class="col-12 bg-white p-4 rounded shadow purchase-form">
                                <h2 class="display-5 fw-normal text-center mb-4">Create <span class="text-primary">Purchase Request</span></h2>
                                
                                <!-- Display error message if any -->
                                <% Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                                   if (errors != null && !errors.isEmpty()) { %>
                                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                                        <ul style="margin-bottom: 0;">
                                        <% for (String err : errors.values()) { %>
                                            <li><%= err %></li>
                                        <% } %>
                                        </ul>
                                    </div>
                                <% } %>
                                
                    <form action="CreatePurchaseRequest" method="post">
                                    <h4 class="fw-normal mt-3 mb-3">Material List</h4>
                        <div id="materialList">
                            <div class="row material-row">
                                <div class="col-md-3 mb-2">
                                                <label class="form-label text-muted">Material Name</label>
                                    <input type="text" class="form-control" name="materialName">
                                </div>
                                <div class="col-md-2 mb-2">
                                                <label class="form-label text-muted">Quantity</label>
                                    <input type="number" class="form-control" name="quantity">
                                </div>
                                <div class="col-md-3 mb-2">
                                                <label class="form-label text-muted">Category</label>
                                    <select class="form-select" name="categoryId">
                                        <c:forEach var="category" items="${categories}">
                                            <option value="${category.category_id}">${category.category_name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-3 mb-2">
                                                <label class="form-label text-muted">Notes</label>
                                    <input type="text" class="form-control" name="note">
                                </div>
                                <div class="col-md-1 d-flex align-items-end mb-2">
                                                <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                </div>
                            </div>
                        </div>
                        <div class="mt-3">
                                        <button type="button" class="btn btn-outline-secondary" id="addMaterial">Add Material Row</button>
                        </div>
                        <div class="row mt-4">
                            <div class="col-md-6 mb-3">
                                            <label for="estimatedPrice" class="form-label text-muted">Total Estimated Price ($)</label>
                                <input type="number" class="form-control" name="estimatedPrice" id="estimatedPrice" step="0.01">
                            </div>
                            <div class="col-md-6 mb-3">
                                            <label for="reason" class="form-label text-muted">Purchase Reason</label>
                                <textarea class="form-control" name="reason" id="reason" rows="3"></textarea>
                            </div>
                        </div>
                        <div class="mt-5 d-grid gap-2">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('addMaterial').addEventListener('click', function () {
        const materialList = document.getElementById('materialList');
        const newRow = materialList.querySelector('.material-row').cloneNode(true);
        newRow.querySelector('input[name="materialName"]').value = '';
        newRow.querySelector('input[name="quantity"]').value = '';
        // Reset category to the first option or a default empty/placeholder option
        newRow.querySelector('select[name="categoryId"]').selectedIndex = 0;
        newRow.querySelector('input[name="note"]').value = '';
        materialList.appendChild(newRow);
    });
        
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('remove-material')) {
            const materialRows = document.querySelectorAll('.material-row');
            if (materialRows.length > 1) {
                e.target.closest('.material-row').remove();
            } else {
                alert('You must have at least one material in the request.');
            }
        }
    });
</script>
</body>
</html>
