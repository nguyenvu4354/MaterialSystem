<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add New Unit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        .add-header {
            background: #e2b77a;
            color: #fff;
            font-size: 2rem;
            font-weight: bold;
            border-radius: 8px 8px 0 0;
            padding: 18px 32px;
        }
        .add-card {
            border-radius: 8px;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08);
            background: #fff;
            max-width: 700px;
            margin: 40px auto;
        }
        .edit-btn-save {
            background: #e2b77a;
            color: #fff;
            border: none;
        }
        .edit-btn-save:hover {
            background: #d1a05a;
            color: #fff;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="add-card">
        <div class="add-header">Add New Unit</div>
        <div class="p-4">
            <form action="AddUnit" method="post">
                <div class="mb-3">
                    <label class="form-label">Unit Name</label>
                    <input type="text" class="form-control" name="unitName" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Symbol</label>
                    <input type="text" class="form-control" name="symbol" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <textarea class="form-control" name="description" rows="3"></textarea>
                </div>
                <div class="d-grid gap-2">
                    <button type="submit" class="btn edit-btn-save">Add Unit</button>
                    <a href="UnitList" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>