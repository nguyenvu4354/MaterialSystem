package controller;

import dal.RepairRequestDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RejectRepairRequestServlet", urlPatterns = {"/reject"})
public class RejectRepairRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Chỉ giám đốc mới được từ chối
        if (user == null || user.getRoleId() != 2) {
            response.sendRedirect("accessDenied.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String reason = request.getParameter("reason");

            RepairRequestDAO dao = new RepairRequestDAO();
            dao.updateStatus(requestId, "reject", user.getUserId(), reason);

            response.sendRedirect("repairrequestlist");

        } catch (SQLException | NumberFormatException ex) {
            Logger.getLogger(RejectRepairRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendRedirect("error.jsp");
        }
    }
}
