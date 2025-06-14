<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Export Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <!-- Header -->
    <header>
        <div class="container py-2">
            <div class="row py-4 pb-0 pb-sm-4 align-items-center">
                <div class="col-sm-4 col-lg-3 text-center text-sm-start">
                    <div class="main-logo">
                        <a href="Home">
                            <img src="images/loogo.jpg" alt="logo" class="img-fluid" width="300px">
                        </a>
                    </div>
                </div>
                <div class="col-sm-6 offset-sm-2 offset-md-0 col-lg-5 d-none d-lg-block">
                    <div class="search-bar border rounded-2 px-3 border-dark-subtle">
                        <form id="search-form" class="text-center d-flex align-items-center" action="search" method="get">
                            <input type="text" name="keyword" class="form-control border-0 bg-transparent"
                                   placeholder="Search" required />
                            <button type="submit" class="btn p-0 border-0 bg-transparent">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                                    <path fill="currentColor"
                                          d="M21.71 20.29L18 16.61A9 9 0 1 0 16.61 18l3.68 3.68a1 1 0 0 0 1.42 0a1 1 0 0 0 0-1.39ZM11 18a7 7 0 1 1 7-7a7 7 0 0 1-7 7Z"/>
                                </svg>
                            </button>
                        </form>
                    </div>
                </div>
                <div class="col-sm-8 col-lg-4 d-flex justify-content-end gap-5 align-items-center mt-4 mt-sm-0">
                    <div class="support-box text-end d-none d-xl-block">
                        <span class="fs-6 secondary-font text-muted">Phone</span>
                        <h5 class="mb-0">+980-34984089</h5>
                    </div>
                    <div class="support-box text-end d-none d-xl-block">
                        <span class="fs-6 secondary-font text-muted">Email</span>
                        <h5 class="mb-0">accessoriess@gmail.com</h5>
                    </div>
                </div>
            </div>
        </div>
        <div class="container-fluid">
            <hr class="m-0">
        </div>
    </header>

    <!-- Main Content -->
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="EmployeeSidebar.jsp"/>
            <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                <h2 class="mt-4">Create Export Request</h2>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="CreateExportRequest" method="post">
                    <div class="mb-3">
                        <label for="requestCode" class="form-label">Request Code</label>
                        <input type="text" class="form-control" id="requestCode" name="requestCode" value="${requestCode}" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="deliveryDate" class="form-label">Delivery Date</label>
                        <input type="date" class="form-control" id="deliveryDate" name="deliveryDate" required>
                    </div>
                    <div class="mb-3">
                        <label for="reason" class="form-label">Reason</label>
                        <textarea class="form-control" id="reason" name="reason" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="recipientId" class="form-label">Recipient</label>
                        <select class="form-select" id="recipientId" name="recipientId">
                            <option value="">Select Recipient</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.userId}">${user.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <h5 class="mt-4">Materials</h5>
                    <div id="materialList">
                        <div class="row mb-3 material-row">
                            <div class="col-md-4">
                                <label class="form-label">Material</label>
                                <select class="form-select" name="materials[]" required>
                                    <option value="">Select Material</option>
                                    <c:forEach var="material" items="${materials}">
                                        <option value="${material.materialId}">${material.materialName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Quantity</label>
                                <input type="number" class="form-control" name="quantities[]" min="1" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Condition</label>
                                <select class="form-select" name="conditions[]" required>
                                    <option value="new">New</option>
                                    <option value="used">Used</option>
                                    <option value="refurbished">Refurbished</option>
                                </select>
                            </div>
                            <div class="col-md-2 d-flex align-items-end">
                                <button type="button" class="btn btn-danger remove-material">Remove</button>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-secondary mb-3" id="addMaterial">Add Material</button>

                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </main>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('addMaterial').addEventListener('click', function() {
            const materialList = document.getElementById('materialList');
            const newRow = materialList.querySelector('.material-row').cloneNode(true);
            newRow.querySelector('select[name="materials[]"]').value = '';
            newRow.querySelector('input[name="quantities[]"]').value = '';
            newRow.querySelector('select[name="conditions[]"]').value = '';
            materialList.appendChild(newRow);
        });

        document.addEventListener('click', function(e) {
            if (e.target.classList.contains('remove-material')) {
                const materialRows = document.querySelectorAll('.material-row');
                if (materialRows.length > 1) {
                    e.target.closest('.material-row').remove();
                }
            }
        });
    </script>
</body>
</html>