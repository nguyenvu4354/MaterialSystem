package controller;

import dal.ExportRequestDAO;
import entity.ExportRequest;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
        List<ExportRequest> exportRequests = exportRequestDAO.getAll();
        
        request.setAttribute("exportRequests", exportRequests);
        request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
    }
} 