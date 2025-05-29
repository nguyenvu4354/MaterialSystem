package dal;

import entity.Supplier;
import entity.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends DBContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setContactInfo(rs.getString("contact_info"));
                s.setAddress(rs.getString("address"));
                s.setPhoneNumber(rs.getString("phone_number"));
                s.setEmail(rs.getString("email"));
                s.setDescription(rs.getString("description"));
                s.setTaxId(rs.getString("tax_id"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Supplier getSupplierByID(int id) {
        String sql = "SELECT * FROM Suppliers WHERE supplier_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (supplier_name, contact_info, address, phone_number, email, description, tax_id, disable) VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getContactInfo());
            ps.setString(3, supplier.getAddress());
            ps.setString(4, supplier.getPhoneNumber());
            ps.setString(5, supplier.getEmail());
            ps.setString(6, supplier.getDescription());
            ps.setString(7, supplier.getTaxId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET supplier_name=?, contact_info=?, address=?, phone_number=?, email=?, description=?, tax_id=? WHERE supplier_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getContactInfo());
            ps.setString(3, supplier.getAddress());
            ps.setString(4, supplier.getPhoneNumber());
            ps.setString(5, supplier.getEmail());
            ps.setString(6, supplier.getDescription());
            ps.setString(7, supplier.getTaxId());
            ps.setInt(8, supplier.getSupplierId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(int id) {
        String sql = "UPDATE Suppliers SET disable=1 WHERE supplier_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE disable=0 AND (supplier_name LIKE ? OR contact_info LIKE ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
<<<<<<< HEAD
    
=======
>>>>>>> 78d44fa33834937ce32290a1c7387de14198d5aa
    public Supplier getSupplierByPhone(String phone) {
        String sql = "SELECT * FROM Suppliers WHERE phone_number = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Hàm main để test
    public static void main(String[] args) {
        SupplierDAO dao = new SupplierDAO();
        List<Supplier> list = dao.getAllSuppliers();
        System.out.println("===== Danh sách nhà cung cấp =====");
        for (Supplier s : list) {
            System.out.println("ID: " + s.getSupplierId());
            System.out.println("Tên: " + s.getSupplierName());
            System.out.println("Liên hệ: " + s.getContactInfo());
            System.out.println("Địa chỉ: " + s.getAddress());
            System.out.println("SĐT: " + s.getPhoneNumber());
            System.out.println("Email: " + s.getEmail());
            System.out.println("Mô tả: " + s.getDescription());
            System.out.println("Mã số thuế: " + s.getTaxId());
            System.out.println("-----------------------------------");
        }
    }
}
