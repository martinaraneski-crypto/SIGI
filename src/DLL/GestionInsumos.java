package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import BLL.Insumo;
import BLL.Lote;
import BLL.Movimiento;
import repository.ICRUD;


public class GestionInsumos implements ICRUD<Insumo> {
    
    private Connection conexion;
    
    public GestionInsumos() {
        this.conexion = ConexionBD.getInstance().getConexion();
    }
    
    
    public void guardarLote(int idInsumo, String numeroLote, java.time.LocalDate fechaVencimiento, int cantidad) {
        String sql = "INSERT INTO lote (numero_lote, fecha_vencimiento, id_insumo, stock_lote) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, numeroLote);
            if (fechaVencimiento != null) {
                ps.setDate(2, java.sql.Date.valueOf(fechaVencimiento));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setInt(3, idInsumo);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
            System.out.println("✅ Lote guardado: " + numeroLote + " | Stock: " + cantidad);
        } catch (SQLException e) {
            System.err.println("Error al guardar lote: " + e.getMessage());
        }
    }
    public boolean descontarDeLote(int idLote, int cantidad) {
        String sql = "UPDATE lote SET stock_lote = stock_lote - ? WHERE id_lote = ? AND stock_lote >= ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idLote);
            ps.setInt(3, cantidad);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al descontar de lote: " + e.getMessage());
            return false;
        }
    }
    
    public List<Lote> obtenerLotesPorInsumo(int idInsumo) {
        List<Lote> lotes = new ArrayList<>();
        String sql = "SELECT * FROM lote WHERE id_insumo = ? AND stock_lote > 0 ORDER BY fecha_vencimiento ASC";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Lote lote = new Lote();
                lote.setId(rs.getInt("id_lote"));
                lote.setNumeroLote(rs.getString("numero_lote"));
                java.sql.Date fechaSQL = rs.getDate("fecha_vencimiento");
                if (fechaSQL != null) {
                    lote.setFechaVencimiento(fechaSQL.toLocalDate());
                }
                lote.setIdInsumo(rs.getInt("id_insumo"));
                lote.setStockLote(rs.getInt("stock_lote"));  // ← AGREGAR ESTO
                lotes.add(lote);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lotes: " + e.getMessage());
        }
        return lotes;
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
            System.err.println("Error al buscar insumo por ID: " + e.getMessage());
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
    
    public List<Insumo> listarPorCategoria(int idCategoria) {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM insumo WHERE id_categoria = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Insumo insumo = new Insumo();
                insumo.setId(rs.getInt("id_insumo"));
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
            System.err.println("Error al listar por categoría: " + e.getMessage());
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
    
    
    
    public boolean actualizarStock(int idInsumo, int cantidad, String tipoMovimiento, int idUsuario, String observacion) {
        Insumo insumo = buscarPorId(idInsumo);
        if (insumo == null) {
            JOptionPane.showMessageDialog(null, "Insumo no encontrado.");
            return false;
        }
        
        if (tipoMovimiento.equals("CONSUMO") && cantidad > insumo.getStockActual()) {
            JOptionPane.showMessageDialog(null, "❌ Stock insuficiente.\nStock actual: " + insumo.getStockActual());
            return false;
        }
        
        int nuevoStock;
        if (tipoMovimiento.equals("INGRESO")) {
            nuevoStock = insumo.getStockActual() + cantidad;
        } else {
            nuevoStock = insumo.getStockActual() - cantidad;
        }
        insumo.setStockActual(nuevoStock);
        
        GestionMovimientos gm = new GestionMovimientos();
        Movimiento movimiento = new Movimiento(
            java.time.LocalDate.now(),
            tipoMovimiento,
            cantidad,
            observacion,
            idInsumo,
            idUsuario
        );
        
        if (modificar(insumo) && gm.guardarMovimiento(movimiento)) {
            if (insumo.getStockActual() <= insumo.getStockMinimo()) {
                JOptionPane.showMessageDialog(null, 
                    "⚠️ ALERTA: El insumo '" + insumo.getNombre() + "' está en stock crítico!\n" +
                    "Stock actual: " + insumo.getStockActual() + " (Mínimo: " + insumo.getStockMinimo() + ")");
            }
            
            JOptionPane.showMessageDialog(null,
                "✅ " + (tipoMovimiento.equals("INGRESO") ? "Ingreso" : "Consumo") + " registrado.\n" +
                "Stock actual de '" + insumo.getNombre() + "': " + insumo.getStockActual() + " " + insumo.getUnidadMedida());
            
            return true;
        }
        return false;
    }
}

