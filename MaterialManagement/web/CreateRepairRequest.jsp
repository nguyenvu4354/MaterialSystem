<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="Login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Create Repair Request</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

        <style>
            .repair-form .form-control, .repair-form .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .repair-form .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .repair-form .btn {
                font-size: 1rem;
                padding: 0.75rem 1.25rem;
            }
            .repair-form .material-row {
                margin-bottom: 1rem;
                border-bottom: 1px solid #dee2e6;
                padding-bottom: 1rem;
                align-items: center; /* Căn giữa theo chiều dọc */
            }
            .repair-form .remove-material {
                height: 48px; /* Cân bằng chiều cao với input */
                width: 48px; /* Đảm bảo nút vuông vức và cân đối */
                padding: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.2rem; /* Tăng kích thước chữ X */
                margin-left: 0.5rem; /* Khoảng cách giữa nút và ô input */
                border: 1px solid #dc3545; /* Giữ đường viền đỏ để đồng bộ với btn-outline-danger */
            }
            .repair-cost-container {
                display: flex;
                align-items: center;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp"/>

        <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow repair-form">
                        <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Repair Request</span></h2>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>

                        <form action="repairrequest" method="post" id="repairForm">
                            <h3 class="fw-normal mt-4 mb-3">Materials for Repair</h3>
                            <div id="materialList">
                                <!-- Initial Material Row -->
                                <div class="row material-row align-items-end gy-2">
                                    <div class="col-md-3">
                                        <label class="form-label text-muted">Material Name</label>
                                        <input type="text" class="form-control material-autocomplete" name="materialName" required>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label text-muted">Quantity</label>
                                        <input type="number" class="form-control" name="quantity" required min="1" value="1">
                                    </div>
                                    <div class="col-md-5">
                                        <label class="form-label text-muted">Description of Damage</label>
                                        <input type="text" class="form-control" name="damageDescription" required>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label text-muted">Repair Cost (€)</label>
                                        <div class="repair-cost-container">
                                            <input type="number" class="form-control" name="repairCost" step="0.01" min="0" value="0">
                                            <button type="button" class="btn btn-sm btn-outline-danger remove-material">X</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mt-2 mb-4">
                                <button type="button" class="btn btn-outline-secondary" id="addMaterial">+ Add Material</button>
                            </div>

                            <h3 class="fw-normal mt-5 mb-3">Repairer and Schedule Information</h3>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Repairer's Phone Number</label>
                                    <input type="tel" class="form-control" name="repairPersonPhoneNumber" required pattern="[0-9]{10,15}" title="Phone number must be 10-15 digits">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Repairer's Email</label>
                                    <input type="email" class="form-control" name="repairPersonEmail" required pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$" title="Please enter a valid email address">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Repair Location</label>
                                    <input type="text" class="form-control" name="repairLocation" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Estimated Return Date</label>
                                    <input type="date" class="form-control" name="estimatedReturnDate" id="returnDate" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label text-muted">Reason for Repair</label>
                                    <textarea name="reason" rows="3" class="form-control" required></textarea>
                                </div>
                            </div>

                            <div class="mt-5 d-grid gap-2">
                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                                <a href="HomePage.jsp" class="btn btn-outline-secondary btn-lg rounded-1">Back to Home</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Set minimum date to today
            document.getElementById('returnDate').setAttribute('min', new Date().toISOString().split('T')[0]);

            // Autocomplete for material names
            const availableMaterials = [
            <c:forEach var="m" items="${materialList}" varStatus="loop">
            "${m.materialName}"${loop.last ? '' : ','}
            </c:forEach>
            ];

            function setupAutocomplete(input) {
                $(input).autocomplete({
                    source: availableMaterials,
                    minLength: 1
                });
            }

            // Apply autocomplete to existing material inputs
            document.querySelectorAll('.material-autocomplete').forEach(setupAutocomplete);

            // Add new material row
            document.getElementById('addMaterial').addEventListener('click', function () {
                const materialList = document.getElementById('materialList');
                const firstRow = materialList.querySelector('.material-row');
                const newRow = firstRow.cloneNode(true);

                newRow.querySelectorAll('input').forEach(input => {
                    input.value = (input.name === 'repairCost') ? '0' : (input.name === 'quantity' ? '1' : '');
                });

                // Cập nhật nút X trong hàng mới
                const repairCostContainer = newRow.querySelector('.repair-cost-container');
                const removeButton = repairCostContainer.querySelector('.remove-material');
                repairCostContainer.appendChild(removeButton);

                materialList.appendChild(newRow);
                setupAutocomplete(newRow.querySelector('.material-autocomplete'));
            });

            // Remove material row
            document.addEventListener('click', function (e) {
                const removeBtn = e.target.closest('.remove-material');
                if (removeBtn) {
                    const rows = document.querySelectorAll('.material-row');
                    if (rows.length > 1) {
                        removeBtn.closest('.material-row').remove();
                    } else {
                        alert("At least one material is required.");
                    }
                }
            });

            // Form validation
            document.getElementById('repairForm').addEventListener('submit', function (e) {
                const returnDate = new Date(document.getElementById('returnDate').value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);

                if (returnDate < today) {
                    e.preventDefault();
                    alert('Estimated return date cannot be in the past.');
                    return;
                }

                const phoneNumber = document.querySelector('input[name="repairPersonPhoneNumber"]').value;
                if (!/^[0-9]{10,15}$/.test(phoneNumber)) {
                    e.preventDefault();
                    alert('Phone number must be 10-15 digits.');
                    return;
                }

                const email = document.querySelector('input[name="repairPersonEmail"]').value;
                const emailPattern = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i;
                if (!emailPattern.test(email)) {
                    e.preventDefault();
                    alert('Please enter a valid email address.');
                    return;
                }

                const materialInputs = document.querySelectorAll('.material-autocomplete');
                for (let input of materialInputs) {
                    if (!availableMaterials.includes(input.value)) {
                        e.preventDefault();
                        alert('Please select valid material names from the suggestions.');
                        return;
                    }
                }
            });
        </script>
    </body>
</html>