<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Create Repair Request</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f6fa;
                padding: 40px;
            }

            h2, h3 {
                color: #2f3640;
            }

            .container {
                max-width: 1000px;
                margin: auto;
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }

            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr 1fr;
                gap: 20px;
            }

            label {
                font-weight: bold;
                margin-bottom: 5px;
                display: block;
            }

            input[type="text"],
            input[type="number"],
            input[type="email"],
            textarea,
            select {
                width: 100%;
                padding: 10px;
                border: 1px solid #dcdde1;
                border-radius: 5px;
                font-size: 14px;
            }

            textarea {
                resize: vertical;
            }

            .form-section {
                margin-top: 30px;
            }

            .form-footer {
                margin-top: 30px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            input[type="submit"], button {
                background-color: #27ae60;
                color: white;
                padding: 10px 25px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-weight: bold;
            }

            button:hover, input[type="submit"]:hover {
                background-color: #219653;
            }

            .table-section {
                margin-top: 20px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                background-color: #f9f9f9;
                border: 1px solid #dcdde1;
            }

            th, td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #dcdde1;
            }

            th {
                background-color: #2f3640;
                color: white;
            }

            .remove-btn {
                background-color: #e74c3c;
                color: white;
                border: none;
                padding: 5px 10px;
                border-radius: 5px;
                font-weight: bold;
                cursor: pointer;
            }

            .remove-btn:hover {
                background-color: #c0392b;
            }

            .edit-btn {
                background-color: #f1c40f;
                color: white;
                border: none;
                padding: 5px 10px;
                border-radius: 5px;
                font-weight: bold;
                cursor: pointer;
                margin-left: 5px;
            }

            .edit-btn:hover {
                background-color: #d4ac0d;
            }

            .add-row-btn {
                margin-top: 10px;
                background-color: #3498db;
            }

            .add-row-btn:hover {
                background-color: #2980b9;
            }

            .error {
                color: red;
                margin-top: 10px;
            }
        </style>

        <script>
            function addRow() {
                const table = document.getElementById("details").getElementsByTagName("tbody")[0];
                const newRow = table.insertRow();
                newRow.innerHTML = `
                    <td><input type="number" name="materialId" required></td>
                    <td><input type="number" name="quantity" required></td>
                    <td><input type="text" name="damageDescription" required></td>
                    <td><input type="number" name="repairCost" step="0.01" min="0"></td>
                    <td>
                        <button type="button" class="remove-btn" onclick="removeRow(this)">X</button>
                        <button type="button" class="edit-btn" onclick="editRow(this)">Save</button>
                    </td>
                `;
            }

            function removeRow(btn) {
                const row = btn.parentNode.parentNode;
                row.parentNode.removeChild(row);
            }

            function editRow(btn) {
                const row = btn.closest('tr');
                const inputs = row.querySelectorAll('input');
                if (btn.textContent === 'Edit') {
                    inputs.forEach(input => input.removeAttribute('readonly'));
                    btn.textContent = 'Save';
                } else {
                    inputs.forEach(input => input.setAttribute('readonly', true));
                    btn.textContent = 'Edit';
                }
            }
        </script>
    </head>
    <body>
        <div class="container">
            <h2>Form Repair Request</h2>

            <form action="repairrequest" method="post">
                <!-- KHÔNG cần userId vì Servlet sẽ tự lấy từ session -->

                <div class="form-section table-section">
                    <h3>List of materials to be repaired</h3>
                    <table id="details">
                        <thead>
                            <tr>
                                <th>Material ID</th>
                                <th>Quantity</th>
                                <th>Description of damage</th>
                                <th>Repair cost</th>
                                <th>Operation</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><input type="number" name="materialId" required></td>
                                <td><input type="number" name="quantity" required></td>
                                <td><input type="text" name="damageDescription" required></td>
                                <td><input type="number" name="repairCost" step="0.01" min="0"></td>
                                <td>
                                    <button type="button" class="remove-btn" onclick="removeRow(this)">X</button>
                                    <button type="button" class="edit-btn" onclick="editRow(this)">Save</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <button type="button" class="add-row-btn" onclick="addRow()">+ Add material</button>
                </div>

                <div class="form-section form-grid">
                   
                    <div>
                        <label>Phone Number of the Repairer</label>
                        <input type="text" name="repairPersonPhoneNumber" required>
                    </div>
                    <div>
                        <label>Email</label>
                        <input type="email" name="repairPersonEmail" required>
                    </div>
                    <div>
                        <label>Repair location</label>
                        <input type="text" name="repairLocation" required>
                    </div>
                    <div>
                        <label>Estimated return date</label>
                        <input type="text" name="estimatedReturnDate" required>
                    </div>
                </div>

                <div class="form-section">
                    <label>Reason for repair</label>
                    <textarea name="reason" rows="3" required></textarea>
                </div>

                <div class="form-footer">
                    <input type="submit" value="Submit">
                    <a href="home"><button type="button">Home</button></a>
                </div>
            </form>

            <c:if test="${not empty errorMessage}">
                <p class="error">${errorMessage}</p>
            </c:if>
        </div>
    </body>
</html>
