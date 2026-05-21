package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import BLL.Movimiento;

public class GestionMovimientos {
    
    private Connection conexion;
    
    public GestionMovimientos() {
        this.conexion = ConexionBD.getInstance().getConexion();
    }
    
    
    public boolean guardarMovimiento(Movimiento movimiento) {
        String sql = "INSERT INTO movimiento (fecha, tipo, cantidad, observacion, id_insumo, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(movimiento.getFecha()));
            ps.setString(2, movimiento.getTipo());
            ps.setInt(3, movimiento.getCantidad());
            ps.setString(4, movimiento.getObservacion());
            ps.setInt(5, movimiento.getIdInsumo());
            ps.setInt(6, movimiento.getIdUsuario());
            
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar movimiento: " + e.getMessage());
            return false;
        }
    }
    
    
    public List<Movimiento> listarTodos() {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimiento ORDER BY fecha DESC";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id_movimiento"));
                m.setFecha(rs.getDate("fecha").toLocalDate());
                m.setTipo(rs.getString("tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setObservacion(rs.getString("observacion"));
                m.setIdInsumo(rs.getInt("id_insumo"));
                m.setIdUsuario(rs.getInt("id_usuario"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar movimientos: " + e.getMessage());
        }
        return lista;
    }
    
    
    public List<Movimiento> listarPorUsuario(int idUsuario) {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimiento WHERE id_usuario = ? ORDER BY fecha DESC";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id_movimiento"));
                m.setFecha(rs.getDate("fecha").toLocalDate());
                m.setTipo(rs.getString("tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setObservacion(rs.getString("observacion"));
                m.setIdInsumo(rs.getInt("id_insumo"));
                m.setIdUsuario(rs.getInt("id_usuario"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar movimientos por usuario: " + e.getMessage());
        }
        return lista;
    }
}