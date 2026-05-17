package GUI;

import DLL.ConexionBD;

public class TestConexion {
    public static void main(String[] args) {
        ConexionBD conexion = ConexionBD.getInstance();
        if (conexion.getConexion() != null) {
            System.out.println("✅ ÉXITO: Conexión a la base de datos 'sigi' establecida.");
        } else {
            System.out.println("❌ ERROR: No se pudo conectar a la base de datos.");
        }
    }
}