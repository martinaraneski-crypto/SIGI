
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    
    
    public void registrarConsumo(int id, int cantidad) throws StockInsuficienteException {
        Insumo insumo = buscarPorId(id);
        
        if (insumo != null) {
            if (cantidad > insumo.getStockActual()) {
                throw new StockInsuficienteException("Stock insuficiente. Actual: " + insumo.getStockActual() + ", Solicitado: " + cantidad);
            }
            
            int nuevoStock = insumo.getStockActual() - cantidad;
            insumo.setStockActual(nuevoStock);
            modificar(insumo);
        }
    }
    
   
    public void registrarIngreso(int id, int cantidad) {
        Insumo insumo = buscarPorId(id);
        
        if (insumo != null) {
            int nuevoStock = insumo.getStockActual() + cantidad;
            insumo.setStockActual(nuevoStock);
            modificar(insumo);
        }
    }
}