import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Maqueta {

    public static void main(String[] args) {
        
       
        String[] menuPrincipal = {"Ingresar", "Salir"};
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "Sistema de Gestión de Insumos\n\nElija una opción:", 
                "SIGI", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                new ImageIcon(Maqueta.class.getResource("img/sigi_logo.jpg")),  
                menuPrincipal, 
                menuPrincipal[0]);
            
            if (opcion == 0) {
             
                String usuario = JOptionPane.showInputDialog("Ingrese usuario:");
                String contrasenia = JOptionPane.showInputDialog("Ingrese contraseña:");
                
                if (usuario.equals("admin") && contrasenia.equals("1234")) {
                    menuAdministrador();
                } else if (usuario.equals("operador") && contrasenia.equals("1234")) {
                    menuOperador();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                }
            }
        } while (opcion != 1);
    }
    
   
    public static void menuAdministrador() {
        String[] opciones = {"Registrar", "Gestionar Insumos", "Ver Stock", "Ver Historial", "Salir"};
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "Menú Administrador\n\nSeleccione una opción:", 
                "ADMIN", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                opciones, 
                opciones[0]);
            
            switch (opcion) {
                case 0:
                    JOptionPane.showMessageDialog(null, "REGISTRAR\n\nFunción en desarrollo");
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "GESTIONAR INSUMOS\n\nFunción en desarrollo");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "VER STOCK\n\nFunción en desarrollo");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "VER HISTORIAL\n\nFunción en desarrollo");
                    break;
            }
        } while (opcion != 4);
    }
    
   
    public static void menuOperador() {
        String[] opciones = {"Registrar", "Ver Stock", "Mi Historial", "Salir"};
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "Menú Operador\n\nSeleccione una opción:", 
                "OPERADOR", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                opciones, 
                opciones[0]);
            
            switch (opcion) {
                case 0:
                    JOptionPane.showMessageDialog(null, "REGISTRAR\n\nFunción en desarrollo");
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "VER STOCK\n\nFunción en desarrollo");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "MI HISTORIAL\n\nFunción en desarrollo");
                    break;
            }
        } while (opcion != 3);
    }
}