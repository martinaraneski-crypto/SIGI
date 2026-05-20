package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import BLL.Insumo;
import BLL.Movimiento;
import BLL.TipoMovimiento;
import repository.ICRUD;
import repository.StockInsuficienteException;

public class GestionInsumos implements ICRUD<Insumo> {
    
    private Connection conexion;
    
    public GestionInsumos() {
        this.conexion = ConexionBD.getInstance().getConexion();
    }
    
   
    
    @Override
    public boolean agregar(Insumo insumo) {
        String sql = "INSERT INTO insumo (nombre, descripcion, stock_actual, stock_minimo, stock_deseado, unidad_medida, id_categoria) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getDescripcion());
            ps.setInt(3, insumo.getStockActual());
            ps.setInt(4, insumo.getStockMinimo());
            ps.setInt(5, insumo.getStockDeseado());
            ps.setString(6, insumo.getUnidadMedida());
            ps.setInt(7, insumo.getIdCategoria());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al agregar insumo: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Insumo> listar() {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumo";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id_insumo"));
                insumo.setCodigoPropio(rs.getString("codigo_propio"));
                insumo.setNombre(rs.getString("nombre"));
                insumo.setDescripcion(rs.getString("descripcion"));
                insumo.setStockActual(rs.getInt("stock_actual"));
                insumo.setStockMinimo(rs.getInt("stock_minimo"));
                insumo.setStockDeseado(rs.getInt("stock_deseado"));
                insumo.setUnidadMedida(rs.getString("unidad_medida"));
                insumo.setIdCategoria(rs.getInt("id_categoria"));
                lista.add(insumo);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar insumos: " + e.getMessage());
        }
        return lista;
    }
    
    @Override
    public Insumo buscarPorId(int id) {
        String sql = "SELECT * FROM insumo WHERE id_insumo = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id_insumo"));
                insumo.setCodigoPropio(rs.getString("codigo_propio"));
                insumo.setNombre(rs.getString("nombre"));
                insumo.setDescripcion(rs.getString("descripcion"));
                insumo.setStockActual(rs.getInt("stock_actual"));
                insumo.setStockMinimo(rs.getInt("stock_minimo"));
                insumo.setStockDeseado(rs.getInt("stock_deseado"));
                insumo.setUnidadMedida(rs.getString("unidad_medida"));
                insumo.setIdCategoria(rs.getInt("id_categoria"));
                return insumo;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar insumo: " + e.getMessage());
        }
        return null;
    }
    
    public List<Insumo> buscarPorNombre(String nombre) {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumo WHERE nombre LIKE ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id_insumo"));
                insumo.setCodigoPropio(rs.getString("codigo_propio"));
                insumo.setNombre(rs.getString("nombre"));
                insumo.setDescripcion(rs.getString("descripcion"));
                insumo.setStockActual(rs.getInt("stock_actual"));
                insumo.setStockMinimo(rs.getInt("stock_minimo"));
                insumo.setStockDeseado(rs.getInt("stock_deseado"));
                insumo.setUnidadMedida(rs.getString("unidad_medida"));
                insumo.setIdCategoria(rs.getInt("id_categoria"));
                lista.add(insumo);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por nombre: " + e.getMessage());
        }
        return lista;
    }
    
    @Override
    public boolean modificar(Insumo insumo) {
        String sql = "UPDATE insumo SET nombre=?, descripcion=?, stock_actual=?, stock_minimo=?, stock_deseado=?, unidad_medida=?, id_categoria=? WHERE id_insumo=?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getDescripcion());
            ps.setInt(3, insumo.getStockActual());
            ps.setInt(4, insumo.getStockMinimo());
            ps.setInt(5, insumo.getStockDeseado());
            ps.setString(6, insumo.getUnidadMedida());
            ps.setInt(7, insumo.getIdCategoria());
            ps.setInt(8, insumo.getId());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al modificar insumo: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM insumo WHERE id_insumo = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar insumo: " + e.getMessage());
            return false;
        }
    }
    
    
    
    public boolean actualizarStock(int idInsumo, int cantidad, TipoMovimiento tipoMovimiento, 
                                    int idUsuario, String observacion, int idLote) {
        Insumo insumo = buscarPorId(idInsumo);
        if (insumo == null) {
            JOptionPane.showMessageDialog(null, "Insumo no encontrado.");
            return false;
        }
        
       
        if (tipoMovimiento == TipoMovimiento.CONSUMO && cantidad > insumo.getStockActual()) {
            JOptionPane.showMessageDialog(null, "❌ Stock insuficiente. Stock actual: " + insumo.getStockActual());
            return false;
        }
        
       
        int nuevoStock;
        if (tipoMovimiento == TipoMovimiento.INGRESO) {
            nuevoStock = insumo.getStockActual() + cantidad;
        } else {
            nuevoStock = insumo.getStockActual() - cantidad;
        }
        insumo.setStockActual(nuevoStock);
        
       
        GestionMovimientos gm = new GestionMovimientos();
        Movimiento movimiento = new Movimiento(
            LocalDate.now(),
            tipoMovimiento,
            cantidad,
            idInsumo,
            idUsuario,
            idLote
        );
        movimiento.setObservacion(observacion);
        
        if (modificar(insumo) && gm.guardarMovimiento(movimiento)) {
           
            if (insumo.getStockActual() <= insumo.getStockMinimo()) {
                JOptionPane.showMessageDialog(null, 
                    "⚠️ ALERTA: El insumo '" + insumo.getNombre() + "' está en stock crítico!\n" +
                    "Stock actual: " + insumo.getStockActual() + " (Mínimo: " + insumo.getStockMinimo() + ")");
            }
            
           
            int sugerencia = insumo.getStockDeseado() - insumo.getStockActual();
            if (sugerencia > 0) {
                JOptionPane.showMessageDialog(null,
                    "📋 SUGERENCIA DE PEDIDO:\n" +
                    "Insumo: " + insumo.getNombre() + "\n" +
                    "Stock actual: " + insumo.getStockActual() + "\n" +
                    "Stock deseado: " + insumo.getStockDeseado() + "\n" +
                    "Se recomienda pedir: " + sugerencia + " " + insumo.getUnidadMedida());
            }
            
            return true;
        }
        return false;
    }
    
    public int obtenerSugerenciaPedido(int idInsumo) {
        Insumo insumo = buscarPorId(idInsumo);
        if (insumo == null) return 0;
        int sugerencia = insumo.getStockDeseado() - insumo.getStockActual();
        return sugerencia > 0 ? sugerencia : 0;
    }
    
    public boolean isStockCritico(int idInsumo) {
        Insumo insumo = buscarPorId(idInsumo);
        if (insumo == null) return false;
        return insumo.getStockActual() <= insumo.getStockMinimo();
    }
}