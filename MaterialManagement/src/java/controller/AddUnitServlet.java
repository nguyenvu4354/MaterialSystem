package controller;

import dal.UnitDAO;
import entity.Unit;
import utils.UnitValidator;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AddUnitServlet", urlPatterns = {"/AddUnit"})
public class AddUnitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String description = request.getParameter("description");
        
        // Validate form data
        Map<String, String> errors = UnitValidator.validateUnitFormData(unitName, symbol, description);
        
        if (!errors.isEmpty()) {
            // Set errors and form data back to request
            request.setAttribute("errors", errors);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
            return;
        }
        
        // Create unit object and validate
        Unit unit = new Unit();
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setDescription(description);
        
        UnitDAO unitDAO = new UnitDAO();
        Map<String, String> validationErrors = UnitValidator.validateUnit(unit, unitDAO);
        
        if (!validationErrors.isEmpty()) {
            request.setAttribute("errors", validationErrors);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
            return;
        }
        
        // Add unit to database
        try {
            unitDAO.addUnit(unit);
            response.sendRedirect("UnitList");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to add unit. Please try again.");
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
        }
    }
} 