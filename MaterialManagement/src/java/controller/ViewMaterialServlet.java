package controller;

import dal.MaterialDAO;
import entity.Material;
import entity.MaterialDetails;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ViewMaterialServlet", urlPatterns = {"/viewMaterial"})
public class ViewMaterialServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));
                
                if (materialDetails != null) {
                    request.setAttribute("details", materialDetails);
                    request.getRequestDispatcher("viewMaterial.jsp").forward(request, response);
                } else {
                    response.sendRedirect("dashboard");
                }
            } else {
                response.sendRedirect("dashboard");
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
} 