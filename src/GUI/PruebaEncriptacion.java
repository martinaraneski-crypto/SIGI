package GUI;

import javax.swing.JOptionPane;
import repository.Encriptador;

public class PruebaEncriptacion {
    public static void main(String[] args) {
        String miContrasenia = "admin123";
        
      
        String hashGenerado = Encriptador.hash(miContrasenia);
        
       
        JOptionPane.showMessageDialog(null, "Hash creado: " + hashGenerado);
        
       
        boolean esValida = Encriptador.verificar(miContrasenia, hashGenerado);
        
        if (esValida) {
            JOptionPane.showMessageDialog(null, "✅ ÉXITO: BCrypt funciona perfectamente.");
        } else {
            JOptionPane.showMessageDialog(null, "❌ ERROR: No funciona.");
        }
    }
}