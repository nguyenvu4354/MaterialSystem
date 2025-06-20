package controller;

import dal.InventoryDAO;
import dal.UserDAO;
import entity.Inventory;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaticInventoryServlet", urlPatterns = {"/StaticInventory"})
public class StaticInventoryServlet extends HttpServlet {
    private InventoryDAO inventoryDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        inventoryDAO = new InventoryDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Inventory> inventoryList = inventoryDAO.getAllInventory();
            Map<Integer, User> userMap = new HashMap<>();
            
            for (Inventory inv : inventoryList) {
                if (inv.getUpdatedBy() != null && inv.getUpdatedBy() > 0 && !userMap.containsKey(inv.getUpdatedBy())) {
                    User user = userDAO.getUserById(inv.getUpdatedBy());
                    if (user != null) {
                        userMap.put(inv.getUpdatedBy(), user);
                    }
                }
            }
            
            request.setAttribute("inventoryList", inventoryList);
            request.setAttribute("userMap", userMap);
            
        } catch (SQLException e) {
            request.setAttribute("error", "Error loading inventory data: " + e.getMessage());
        }
        request.getRequestDispatcher("/StaticInventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to GET method for POST requests
        doGet(request, response);
    }
} 