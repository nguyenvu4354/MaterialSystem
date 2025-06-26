package controller;

import dal.InventoryDAO;
import dal.UserDAO;
import dal.ImportDAO;
import dal.ExportDAO;
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
    private ImportDAO importDAO;
    private ExportDAO exportDAO;
    private static final int PAGE_SIZE = 5; 

    @Override
    public void init() throws ServletException {
        inventoryDAO = new InventoryDAO();
        userDAO = new UserDAO();
        importDAO = new ImportDAO();
        exportDAO = new ExportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            
            String pageStr = request.getParameter("page");
            int currentPage = pageStr != null ? Integer.parseInt(pageStr) : 1;
            if (currentPage < 1) currentPage = 1;           
            List<Inventory> allInventoryList = inventoryDAO.getAllInventory();
            int totalItems = allInventoryList.size();
            int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalItems);
            List<Inventory> inventoryList;        
            if (start < totalItems) {
                inventoryList = allInventoryList.subList(start, end);
            } else {
                inventoryList = allInventoryList;
            }
            Map<Integer, User> userMap = new HashMap<>();
            for (Inventory inv : inventoryList) {
                if (inv.getUpdatedBy() != null && inv.getUpdatedBy() > 0 && !userMap.containsKey(inv.getUpdatedBy())) {
                    User user = userDAO.getUserById(inv.getUpdatedBy());
                    if (user != null) {
                        userMap.put(inv.getUpdatedBy(), user);
                    }
                }
            }
            
            // Lấy tổng số lượng nhập, xuất, tồn
            int totalImported = 0;
            int totalExported = 0;
            int totalStock = 0;
            try {
                totalImported = importDAO.getTotalImportedQuantity();
            } catch (Exception ex) { totalImported = 0; }
            try {
                totalExported = exportDAO.getTotalExportedQuantity();
            } catch (Exception ex) { totalExported = 0; }
            try {
                List<Inventory> allInv = inventoryDAO.getAllInventory();
                for (Inventory inv : allInv) totalStock += inv.getStock();
            } catch (Exception ex) { totalStock = 0; }
            request.setAttribute("totalImported", totalImported);
            request.setAttribute("totalExported", totalExported);
            request.setAttribute("totalStock", totalStock);
            
            request.setAttribute("inventoryList", inventoryList);
            request.setAttribute("userMap", userMap);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("pageSize", PAGE_SIZE);
            
        } catch (SQLException e) {
            request.setAttribute("error", "Error loading inventory data: " + e.getMessage());
        }
        request.getRequestDispatcher("/StaticInventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
} 