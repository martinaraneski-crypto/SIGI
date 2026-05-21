package GUI;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConexionSimple {
    public static void main(String[] args) {
        System.out.println("Buscando el JAR...");
        
        String[] jars = System.getProperty("java.class.path").split(";");
        for (String jar : jars) {
            if (jar.contains("mysql")) {
                System.out.println("Encontrado: " + jar);
            }
        }
        
        try {
            Class.forName("com.mysql.jdbc.Driver");   
            System.out.println("Driver encontrado");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sigi", "root", "");
            System.out.println("✅ Conexión exitosa");
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver NO encontrado");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}