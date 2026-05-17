package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
