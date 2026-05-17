package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import BLL.RolUsuario;
import BLL.Usuario;
import repository.Encriptador;

public class ControllerUsuario {

    private Connection conexion;

    public ControllerUsuario() {
        this.conexion = ConexionBD.getInstance().getConexion();
    }

   
    public Usuario login(String nombreUsuario, String contraseniaPlana) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("contrasenia");

                if (Encriptador.verificar(contraseniaPlana, hashAlmacenado)) {
                    int id = rs.getInt("id_usuario");
                    String rolString = rs.getString("rol");
                    String nombreCompleto = rs.getString("nombre_completo");
                    boolean activo = rs.getBoolean("activo");

                    RolUsuario rol = RolUsuario.valueOf(rolString);
                    usuario = new Usuario(id, nombreUsuario, hashAlmacenado, rol, nombreCompleto, activo);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de base de datos: " + e.getMessage());
        }
        return usuario;
    }

    
    public boolean registrar(String nombreUsuario, String contraseniaPlana, String nombreCompleto) {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasenia, rol, nombre_completo, activo) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, Encriptador.hash(contraseniaPlana));
            ps.setString(3, "OPERADOR");
            ps.setString(4, nombreCompleto);
            ps.setInt(5, 1);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    
    
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setContrasenia(rs.getString("contrasenia"));
                
                String rolString = rs.getString("rol");
                if (rolString.equals("ADMIN")) {
                    u.setRol(RolUsuario.ADMIN);
                } else {
                    u.setRol(RolUsuario.OPERADOR);
                }
                
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setContrasenia(rs.getString("contrasenia"));
                
                String rolString = rs.getString("rol");
                if (rolString.equals("ADMIN")) {
                    u.setRol(RolUsuario.ADMIN);
                } else {
                    u.setRol(RolUsuario.OPERADOR);
                }
                
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setActivo(rs.getBoolean("activo"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void cambiarRol(int id, String nuevoRol) {
        String sql = "UPDATE usuario SET rol = ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoRol);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void activarDesactivar(int id, boolean estado) {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, estado ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}