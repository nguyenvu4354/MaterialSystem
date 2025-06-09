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
           
            if (materialId == null || materialId.isEmpty()) {
                
                return;
            }

            int id = Integer.parseInt(materialId);
            MaterialDAO md = new MaterialDAO();
            Material m = md.getInformation(id);

            if (m == null) {
                
                return;
            }

            request.setAttribute("m", m);
            request.getRequestDispatcher("ViewMaterial.jsp").forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

}
