package controller;

import dal.SupplierDAO;
import entity.Supplier;
import utils.SupplierValidator;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "SupplierServlet", urlPatterns = {"/Supplier"})
public class SupplierServlet extends HttpServlet {
    private SupplierDAO supplierDAO;
    private static final int PAGE_SIZE = 5; 

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
        String code = request.getParameter("code");
        String sortBy = request.getParameter("sortBy");
        String pageStr = request.getParameter("page");
        int currentPage = pageStr != null ? Integer.parseInt(pageStr) : 1;
        if (currentPage < 1) currentPage = 1;

        List<Supplier> list;
        int totalSuppliers;

        if (code != null && !code.trim().isEmpty()) {
            // Search by code
            list = supplierDAO.searchSuppliersByCode(code);
            totalSuppliers = list.size();
            request.setAttribute("code", code);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by name, contact info, phone
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
        request.setAttribute("code", code);
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
                request.setAttribute("error", "ID erorr.");
            }
        } else {
            // Generate new supplier code for adding new supplier
            String newSupplierCode = supplierDAO.generateNextSupplierCode();
            request.setAttribute("newSupplierCode", newSupplierCode);
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
                
            }
        }
        response.sendRedirect("Supplier?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("supplier_id");
        String code = request.getParameter("supplier_code");
        String name = request.getParameter("supplier_name");
        String contactInfo = request.getParameter("contact_info");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone_number");
        String email = request.getParameter("email");
        String desc = request.getParameter("description");
        String taxId = request.getParameter("tax_id");

        // Use SupplierValidator for validation
        Map<String, String> errors = SupplierValidator.validateSupplierFormData(
            code, name, contactInfo, address, phone, email, taxId, desc
        );

        // If validation fails, redirect back to form with error
        if (!errors.isEmpty()) {
            Supplier supplier = new Supplier();
            supplier.setSupplierCode(code != null ? code : "");
            supplier.setSupplierName(name != null ? name : "");
            supplier.setContactInfo(contactInfo != null ? contactInfo : "");
            supplier.setAddress(address != null ? address : "");
            supplier.setPhoneNumber(phone != null ? phone : "");
            supplier.setEmail(email != null ? email : "");
            supplier.setDescription(desc != null ? desc : "");
            supplier.setTaxId(taxId != null ? taxId : "");
            
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    supplier.setSupplierId(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    // Handle error
                }
            }
            
            // Combine all error messages
            StringBuilder errorMessage = new StringBuilder();
            for (String error : errors.values()) {
                errorMessage.append(error).append(" ");
            }
            
            request.setAttribute("supplier", supplier);
            request.setAttribute("error", errorMessage.toString().trim());
            request.getRequestDispatcher("/SupplierEdit.jsp").forward(request, response);
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(code);
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
                
            }
        }
        response.sendRedirect("Supplier?action=list");
    }

    @Override
    public String getServletInfo() {
        return "SupplierServlet handles supplier management operations";
    }
}