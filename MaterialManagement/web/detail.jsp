<%-- 
    Document   : detaij
    Created on : May 24, 2025, 2:08:28 PM
    Author     : Nhat Anh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Danh sách sản phẩm</h1>
        <table border="1" cellpadding="10">
            <tr>
                <th>ID</th>
                <th>Code</th>
                <th>Name</th>
                <th>Image</th>
                <th>Price</th>
                <th>Description</th>
                <th>Rating</th>
                <th>Category ID</th>
            </tr>
            <%
                List<Product> list = (List<Product>) request.getAttribute("productList");
                for (Product p : list) {
            %>
            <tr>
                <td><%= p.getMaterialId() %></td>
                <td><%= p.getMaterialCode() %></td>
                <td><%= p.getMaterialName() %></td>
                <td><img src="<%= p.getMaterialsUrl() %>" width="100" height="100"/></td>
                <td><%= p.getPrice() %></td>
                <td><%= p.getDescription() %></td>
                <td><%= p.getRating() %></td>
                <td><%= p.getCategoryId() %></td>
            </tr>
            <% } %>
        </table>
    </body>
</html>
