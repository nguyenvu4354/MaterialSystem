package controller;

import dal.UnitDAO;
import entity.Unit;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditUnitServlet", urlPatterns = {"/EditUnit"})
public class EditUnitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnitDAO unitDAO = new UnitDAO();
        String idStr = request.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            Unit unit = unitDAO.getUnitById(id);
            request.setAttribute("unit", unit);
        }
        request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnitDAO unitDAO = new UnitDAO();
        int id = Integer.parseInt(request.getParameter("id"));
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String description = request.getParameter("description");
        Unit unit = new Unit();
        unit.setId(id);
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setDescription(description);
        unitDAO.updateUnit(unit);
        response.sendRedirect("UnitList");
    }
}
