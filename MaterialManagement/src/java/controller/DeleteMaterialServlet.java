package controller;

import dal.MaterialDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteMaterialServlet", urlPatterns = {"/deletematerial"})
public class DeleteMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String materialId = request.getParameter("id");
            if (materialId == null || materialId.trim().isEmpty()) {
                materialId = request.getParameter("materialId");
            }
            System.out.println("DeleteMaterialServlet: materialId = " + materialId); // Log để debug
            if (materialId != null && !materialId.trim().isEmpty()) {
                MaterialDAO materialDAO = new MaterialDAO();
                boolean success = materialDAO.deleteMaterial(Integer.parseInt(materialId));
                
                if (success) {
                    response.sendRedirect("dashboardmaterial?success=Material disabled successfully");
                } else {
                    response.sendRedirect("dashboardmaterial?error=Failed to disable material");
                }
            } else {
                response.sendRedirect("dashboardmaterial");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("dashboardmaterial?error=Error occurred while deleting material");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
} 