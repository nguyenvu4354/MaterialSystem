package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.UserDAO;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ViewExportRequestServlet", urlPatterns = {"/ViewExportRequest"})
public class ViewExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || (user.getRoleId() != 2 && user.getRoleId() != 3 && user.getRoleId() != 4)) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            ExportRequest exportRequest = exportRequestDAO.getById(requestId);
            if (exportRequest == null) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            }
            List<ExportRequestDetail> details = new ExportRequestDetailDAO().getByRequestId(requestId);
            User sender = userDAO.getUserById(exportRequest.getUserId());
            User recipient = null;
            if (exportRequest.getRecipientId() > 0) {
                recipient = userDAO.getUserById(exportRequest.getRecipientId());
            }
            request.setAttribute("exportRequest", exportRequest);
            request.setAttribute("details", details);
            request.setAttribute("sender", sender);
            request.setAttribute("recipient", recipient);
            request.setAttribute("roleId", user.getRoleId());
            request.setAttribute("user", user);
            request.getRequestDispatcher("ViewExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ExportRequestList");
        }
    }
}