package dal;

import entity.DBContext;
import entity.Unit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO extends DBContext {
    
    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        try {
            String sql = "SELECT * FROM units WHERE disable = 0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                units.add(unit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return units;
    }

    public void addUnit(Unit unit) {
        String sql = "INSERT INTO units (unit_name, symbol, description, disable) VALUES (?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getSymbol());
            ps.setString(3, unit.getDescription());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Unit getUnitById(int id) {
        String sql = "SELECT * FROM units WHERE unit_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                return unit;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateUnit(Unit unit) {
        String sql = "UPDATE units SET unit_name = ?, symbol = ?, description = ? WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getSymbol());
            ps.setString(3, unit.getDescription());
            ps.setInt(4, unit.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUnit(int id) {
        String sql = "UPDATE units SET disable = 1 WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
} 