package controller;

import dal.UnitDAO;
import entity.Unit;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DashboardUnitServlet", urlPatterns = {"/UnitList"})
public class DashboardUnitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnitDAO unitDAO = new UnitDAO();
        List<Unit> units = unitDAO.getAllUnits();
        request.setAttribute("units", units);
        request.getRequestDispatcher("DashboardUnit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
