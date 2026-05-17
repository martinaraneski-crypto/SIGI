package GUI;

import DLL.ControllerUsuario;
import BLL.Usuario;

public class TestLogin {
    public static void main(String[] args) {
        System.out.println("--- Iniciando prueba de login ---");
        ControllerUsuario controller = new ControllerUsuario();
        Usuario usuario = controller.login("admin", "1234");
        
        if (usuario != null) {
            System.out.println("✅ ÉXITO: Login funcionó correctamente");
            System.out.println("   Usuario encontrado: " + usuario.getNombreUsuario());
            System.out.println("   Rol del usuario: " + usuario.getRol());
        } else {
            System.out.println("❌ FALLO: El login no funcionó");
            System.out.println("   El usuario 'admin' no fue encontrado o la contraseña es incorrecta.");
        }
    }
}