package controller;

import dal.SupplierDAO;
import entity.Supplier;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "SupplierServlet", urlPatterns = {"/SupplierServlet"})
public class SupplierServlet extends HttpServlet {
    private SupplierDAO supplierDAO;
    private static final int PAGE_SIZE = 5; // Number of suppliers per page

    @Override
    public void init() throws ServletException {
        supplierDAO = new SupplierDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                listSuppliers(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteSupplier(request, response);
                break;
            default:
                listSuppliers(request, response);
                break;
        }
    }

    private void listSuppliers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String idStr = request.getParameter("id");
        String sortBy = request.getParameter("sortBy");
        String pageStr = request.getParameter("page");
        int currentPage = pageStr != null ? Integer.parseInt(pageStr) : 1;
        if (currentPage < 1) currentPage = 1;

        List<Supplier> list;
        int totalSuppliers;

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Supplier supplier = supplierDAO.getSupplierByID(id);
                if (supplier != null) {
                    list = Collections.singletonList(supplier);
                    totalSuppliers = 1;
                } else {
                    list = Collections.emptyList();
                    totalSuppliers = 0;
                    request.setAttribute("error", "Không tìm thấy nhà cung cấp với ID: " + id);
                }
            } catch (NumberFormatException e) {
                list = Collections.emptyList();
                totalSuppliers = 0;
                request.setAttribute("error", "ID không hợp lệ. Vui lòng nhập số.");
            }
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            list = supplierDAO.searchSuppliers(keyword);
            totalSuppliers = list.size();
            request.setAttribute("keyword", keyword);
        } else {
            list = supplierDAO.getAllSuppliers();
            totalSuppliers = list.size();
        }

        // Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "name_asc":
                    Collections.sort(list, Comparator.comparing(Supplier::getSupplierName));
                    break;
                case "name_desc":
                    Collections.sort(list, Comparator.comparing(Supplier::getSupplierName).reversed());
                    break;
            }
            request.setAttribute("sortBy", sortBy);
        }

        // Pagination
        int totalPages = (int) Math.ceil((double) totalSuppliers / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = Collections.emptyList();
        }

        request.setAttribute("supplierList", list);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("id", idStr);
        request.getRequestDispatcher("/SupplierList.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Supplier supplier = supplierDAO.getSupplierByID(id);
                request.setAttribute("supplier", supplier);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID không hợp lệ.");
            }
        }
        request.getRequestDispatcher("/SupplierEdit.jsp").forward(request, response);
    }

    private void deleteSupplier(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                supplierDAO.deleteSupplier(id);
            } catch (NumberFormatException e) {
                // Log error if needed
            }
        }
        response.sendRedirect("SupplierServlet?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("supplier_id");
        String name = request.getParameter("supplier_name");
        String contactInfo = request.getParameter("contact_info");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone_number");
        String email = request.getParameter("email");
        String desc = request.getParameter("description");
        String taxId = request.getParameter("tax_id");

        Supplier supplier = new Supplier();
        supplier.setSupplierName(name);
        supplier.setContactInfo(contactInfo);
        supplier.setAddress(address);
        supplier.setPhoneNumber(phone);
        supplier.setEmail(email);
        supplier.setDescription(desc);
        supplier.setTaxId(taxId);

        if (idStr == null || idStr.isEmpty()) {
            supplierDAO.addSupplier(supplier);
        } else {
            try {
                int id = Integer.parseInt(idStr);
                supplier.setSupplierId(id);
                supplierDAO.updateSupplier(supplier);
            } catch (NumberFormatException e) {
                // Log error if needed
            }
        }
        response.sendRedirect("SupplierServlet?action=list");
    }

    @Override
    public String getServletInfo() {
        return "SupplierServlet handles supplier management operations";
    }
}