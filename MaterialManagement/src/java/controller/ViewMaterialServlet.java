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

@WebServlet(name = "ViewMaterialServlet", urlPatterns = {"/viewmaterial"})
public class ViewMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                MaterialDetails materialDetails = materialDAO.getMaterialById(Integer.parseInt(materialId));

                // ✅ In ra log để kiểm tra
                System.out.println("MaterialDetails: " + materialDetails);
                if (materialDetails != null) {
                    request.setAttribute("details", materialDetails);
                    System.out.println("materialId = " + materialId);
                    System.out.println("materialDetails = " + materialDetails);
                    request.getRequestDispatcher("/ViewMaterial.jsp").forward(request, response);
                } else {
                    response.sendRedirect("dashboardmaterial");
                }
            } else {
                response.sendRedirect("dashboardmaterial");
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Error occurred: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
