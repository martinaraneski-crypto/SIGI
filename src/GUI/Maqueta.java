package GUI;

import javax.swing.JOptionPane;
import java.util.List;
import DLL.ControllerUsuario;
import DLL.GestionInsumos;
import BLL.Insumo;
import BLL.RolUsuario;
import BLL.Usuario;

public class Maqueta {

    public static void main(String[] args) {
        
        String[] menuPrincipal = {"Ingresar", "Salir"};
        ControllerUsuario controller = new ControllerUsuario();
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "Sistema de Gestión de Insumos - SIGI\n\nElija una opción:", 
                "SIGI", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                menuPrincipal, 
                menuPrincipal[0]);
            
            if (opcion == 0) {
               
                String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
                String contrasenia = JOptionPane.showInputDialog("Ingrese contraseña:");
                
                Usuario usuarioLogueado = controller.login(nombreUsuario, contrasenia);
                
                if (usuarioLogueado != null) {
                    JOptionPane.showMessageDialog(null, "Bienvenido " + usuarioLogueado.getNombreCompleto());
                    
                    if (usuarioLogueado.getRol() == RolUsuario.ADMIN) {
                        menuAdministrador();
                    } else {
                        menuOperador();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                }
            }
        } while (opcion != 1);
    }
    
   
    public static void menuAdministrador() {
        String[] opciones = {"Listar Insumos", "Buscar Insumo", "Ver Stock", "Salir"};
        
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
                    GestionInsumos gestion = new GestionInsumos();
                    List<Insumo> insumos = gestion.listar();
                    if (insumos.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
                    } else {
                        StringBuilder mensaje = new StringBuilder("📋 LISTA DE INSUMOS:\n\n");
                        for (Insumo i : insumos) {
                            mensaje.append(i.getId()).append(" - ")
                                   .append(i.getNombre()).append(" | Stock: ")
                                   .append(i.getStockActual()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, mensaje.toString());
                    }
                    break;
                    
                case 1: 
                    String nombreBuscado = JOptionPane.showInputDialog("🔍 Ingrese el nombre del insumo a buscar:");
                    if (nombreBuscado != null && !nombreBuscado.trim().isEmpty()) {
                        GestionInsumos gestionBusqueda = new GestionInsumos();
                        List<Insumo> resultados = gestionBusqueda.buscarPorNombre(nombreBuscado);
                        if (resultados.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No se encontraron insumos con ese nombre.");
                        } else {
                            StringBuilder mensaje = new StringBuilder("🔍 RESULTADOS DE BÚSQUEDA:\n\n");
                            for (Insumo i : resultados) {
                                mensaje.append(i.getId()).append(" - ")
                                       .append(i.getNombre()).append(" | Stock: ")
                                       .append(i.getStockActual()).append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensaje.toString());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Búsqueda cancelada o nombre vacío.");
                    }
                    break;
                    
                case 2: 
                    GestionInsumos gestionStock = new GestionInsumos();
                    List<Insumo> stock = gestionStock.listar();
                    if (stock.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
                    } else {
                        StringBuilder mensaje = new StringBuilder("📊 STOCK ACTUAL:\n\n");
                        mensaje.append("ID - NOMBRE | ACTUAL | MÍNIMO | DESEADO\n");
                        mensaje.append("------------------------------------\n");
                        for (Insumo i : stock) {
                            mensaje.append(i.getId()).append(" - ")
                                   .append(i.getNombre()).append(" | ")
                                   .append(i.getStockActual()).append(" | ")
                                   .append(i.getStockMinimo()).append(" | ")
                                   .append(i.getStockDeseado()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, mensaje.toString());
                    }
                    break;
            }
        } while (opcion != 3);
    }
    
   
    public static void menuOperador() {
        String[] opciones = {"Registrar Consumo", "Ver Stock", "Salir"};
        
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
                    JOptionPane.showMessageDialog(null, "REGISTRAR CONSUMO\n\nFunción en desarrollo");
                    break;
                case 1:
                    GestionInsumos gestionStock = new GestionInsumos();
                    List<Insumo> stock = gestionStock.listar();
                    if (stock.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
                    } else {
                        StringBuilder mensaje = new StringBuilder("📊 STOCK ACTUAL:\n\n");
                        mensaje.append("ID - NOMBRE | ACTUAL | MÍNIMO | DESEADO\n");
                        mensaje.append("------------------------------------\n");
                        for (Insumo i : stock) {
                            mensaje.append(i.getId()).append(" - ")
                                   .append(i.getNombre()).append(" | ")
                                   .append(i.getStockActual()).append(" | ")
                                   .append(i.getStockMinimo()).append(" | ")
                                   .append(i.getStockDeseado()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, mensaje.toString());
                    }
                    break;
            }
        } while (opcion != 2);
    }
}