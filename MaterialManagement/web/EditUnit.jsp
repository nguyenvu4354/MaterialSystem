<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Unit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        .edit-header {
            background: #e2b77a;
            color: #fff;
            font-size: 2rem;
            font-weight: bold;
            border-radius: 8px 8px 0 0;
            padding: 18px 32px;
        }
        .edit-card {
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
        .edit-btn-cancel {
            background: #6c757d;
            color: #fff;
            border: none;
        }
        .edit-btn-cancel:hover {
            background: #495057;
            color: #fff;
        }
        .edit-btn-delete {
            background: #dc3545;
            color: #fff;
            border: none;
        }
        .edit-btn-delete:hover {
            background: #b52a37;
            color: #fff;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="edit-card">
        <div class="edit-header">Edit Unit</div>
        <div class="p-4">
            <form action="EditUnit" method="post">
                <input type="hidden" name="id" value="${unit.id}" />
                <div class="mb-3">
                    <label class="form-label">Unit Name</label>
                    <input type="text" class="form-control" name="unitName" value="${unit.unitName}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Symbol</label>
                    <input type="text" class="form-control" name="symbol" value="${unit.symbol}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <textarea class="form-control" name="description" rows="3">${unit.description}</textarea>
                </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn edit-btn-save flex-fill">Save Changes</button>
                    <a href="UnitList" class="btn edit-btn-cancel flex-fill">Cancel</a>
                </div>
            </form>
            <form action="DeleteUnit" method="post" onsubmit="return confirm('Are you sure you want to delete this unit?');" class="mt-2">
                <input type="hidden" name="id" value="${unit.id}" />
                <button type="submit" class="btn edit-btn-delete w-100">Delete Unit</button>
            </form>
        </div>
    </div>
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 