package controller;

import dal.UnitDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteUnitServlet", urlPatterns = {"/DeleteUnit"})
public class DeleteUnitServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnitDAO unitDAO = new UnitDAO();
        int id = Integer.parseInt(request.getParameter("id"));
        unitDAO.deleteUnit(id);
        response.sendRedirect("UnitList");
    }
}
