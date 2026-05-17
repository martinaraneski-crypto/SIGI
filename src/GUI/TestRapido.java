package GUI;

import DLL.ControllerUsuario;
import BLL.Usuario;

public class TestRapido {
    public static void main(String[] args) {
        ControllerUsuario controller = new ControllerUsuario();
        Usuario usuario = controller.login("admin", "1234");
        
        if (usuario != null) {
            System.out.println("✅ FUNCIONA - Login exitoso");
            System.out.println("Rol: " + usuario.getRol());
        } else {
            System.out.println("❌ NO FUNCIONA - Usuario no encontrado");
        }
    }
}