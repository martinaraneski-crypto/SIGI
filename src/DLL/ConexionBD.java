package DLL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private static ConexionBD instancia;
    private Connection conexion;
    
    private static final String URL = "jdbc:mysql://localhost:3306/sigi";
    private static final String USUARIO = "root";
    private static final String CONTRASENIA = "";
    
    private ConexionBD() {
        try {
          
        	Class.forName("com.mysql.jdbc.Driver");
           
            this.conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
            System.out.println("✅ Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL no encontrado");
            System.err.println("Verificá que el JAR esté en Referenced Libraries");
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
        }
    }
    
    public static ConexionBD getInstance() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }
    
    public Connection getConexion() {
        return conexion;
    }
}