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
} 