package controller;

import dal.UnitDAO;
import entity.Unit;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AddUnitServlet", urlPatterns = {"/AddUnit"})
public class AddUnitServlet extends HttpServlet {
    private UnitDAO unitDAO;

    @Override
    public void init() throws ServletException {
        unitDAO = new UnitDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String description = request.getParameter("description");
        Unit unit = new Unit();
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setDescription(description);
        unitDAO.addUnit(unit);
        response.sendRedirect("UnitList");
    }
} 