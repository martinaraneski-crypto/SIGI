package GUI;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConexionSimple {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL encontrado");
            
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sigi", "root", "");
            System.out.println("✅ Conexión exitosa a la base de datos 'sigi'");
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ ERROR: No se encontró el driver MySQL");
            System.out.println("   Verificá que el JAR esté en Referenced Libraries");
        } catch (Exception e) {
            System.out.println("❌ ERROR de conexión: " + e.getMessage());
        }
    }
}