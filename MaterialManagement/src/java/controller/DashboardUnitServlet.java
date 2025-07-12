package controller;

import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Unit;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DashboardUnitServlet", urlPatterns = {"/UnitList"})
public class DashboardUnitServlet extends HttpServlet {
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            if (user == null || (user.getRoleId() != 1 && user.getRoleId() != 2 && user.getRoleId() != 3 && user.getRoleId() != 4)) {
                request.setAttribute("error", "Vui lòng đăng nhập để truy cập trang này.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra quyền VIEW_LIST_UNIT
            int roleId = user.getRoleId();
            if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_UNIT")) {
                request.setAttribute("error", "Bạn không có quyền xem danh sách đơn vị.");
                request.getRequestDispatcher("DashboardUnit.jsp").forward(request, response);
                return;
            }

            boolean readonly = user.getRoleId() != 1;
            request.setAttribute("readonly", readonly);

            UnitDAO unitDAO = new UnitDAO();
            String keyword = request.getParameter("keyword");
            String pageParam = request.getParameter("page");
            int page = 1;
            int pageSize = 10;
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            int totalUnits = unitDAO.countUnits(keyword);
            int totalPages = (int) Math.ceil((double) totalUnits / pageSize);
            if (page > totalPages && totalPages > 0) page = totalPages;
            int offset = (page - 1) * pageSize;
            List<Unit> units = unitDAO.getUnitsByPage(offset, pageSize, keyword);
            request.setAttribute("units", units);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("DashboardUnit.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
