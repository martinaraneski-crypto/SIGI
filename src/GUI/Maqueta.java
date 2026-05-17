package GUI;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.stream.Collectors;
import DLL.ControllerUsuario;
import DLL.GestionInsumos;
import BLL.Insumo;
import BLL.RolUsuario;
import BLL.Usuario;

public class Maqueta {

    public static void main(String[] args) {
        
        String[] menuPrincipal = {"Ingresar", "Registrar", "Salir"};
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
            } else if (opcion == 1) {
                
                registrarUsuario();
            }
        } while (opcion != 2);
    }
    
    
    public static void registrarUsuario() {
        String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Registro cancelado.");
            return;
        }
        
        String contrasenia = JOptionPane.showInputDialog("Ingrese contraseña:");
        if (contrasenia == null || contrasenia.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Registro cancelado.");
            return;
        }
        
        String nombreCompleto = JOptionPane.showInputDialog("Ingrese su nombre completo:");
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Registro cancelado.");
            return;
        }
        
        ControllerUsuario controller = new ControllerUsuario();
        boolean registrado = controller.registrar(nombreUsuario, contrasenia, nombreCompleto);
        
        if (registrado) {
            JOptionPane.showMessageDialog(null, "✅ Usuario registrado con éxito. Ya puede iniciar sesión.");
        } else {
            JOptionPane.showMessageDialog(null, "❌ Error: El nombre de usuario ya existe o hubo un problema.");
        }
    }
    
   
    public static void agregarInsumo() {
        try {
            String nombre = JOptionPane.showInputDialog("Ingrese nombre del insumo:");
            if (nombre == null || nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Agregado cancelado.");
                return;
            }
            
            String descripcion = JOptionPane.showInputDialog("Ingrese descripción:");
            if (descripcion == null) descripcion = "";
            
            String stockActualStr = JOptionPane.showInputDialog("Ingrese stock actual:");
            int stockActual = Integer.parseInt(stockActualStr);
            
            String stockMinimoStr = JOptionPane.showInputDialog("Ingrese stock mínimo:");
            int stockMinimo = Integer.parseInt(stockMinimoStr);
            
            String stockDeseadoStr = JOptionPane.showInputDialog("Ingrese stock deseado:");
            int stockDeseado = Integer.parseInt(stockDeseadoStr);
            
            String unidadMedida = JOptionPane.showInputDialog("Ingrese unidad de medida (ml, unidad, vial, etc):");
            if (unidadMedida == null || unidadMedida.trim().isEmpty()) unidadMedida = "unidad";
            
            String idCategoriaStr = JOptionPane.showInputDialog("Ingrese ID de categoría:");
            int idCategoria = Integer.parseInt(idCategoriaStr);
            
            Insumo nuevo = new Insumo(0, nombre, descripcion, stockActual, stockMinimo, stockDeseado, unidadMedida, idCategoria);
            GestionInsumos gestion = new GestionInsumos();
            
            if (gestion.agregar(nuevo)) {
                JOptionPane.showMessageDialog(null, "✅ Insumo agregado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "❌ Error al agregar insumo.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Debe ingresar números válidos para stock.");
        }
    }
    
    
    public static void modificarInsumo() {
        try {
            String idStr = JOptionPane.showInputDialog("Ingrese el ID del insumo a modificar:");
            if (idStr == null || idStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Modificación cancelada.");
                return;
            }
            
            int id = Integer.parseInt(idStr);
            GestionInsumos gestion = new GestionInsumos();
            Insumo insumo = gestion.buscarPorId(id);
            
            if (insumo == null) {
                JOptionPane.showMessageDialog(null, "No se encontró un insumo con ese ID.");
                return;
            }
            
            String nuevoNombre = JOptionPane.showInputDialog("Ingrese nuevo nombre (actual: " + insumo.getNombre() + "):", insumo.getNombre());
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                insumo.setNombre(nuevoNombre);
            }
            
            String nuevaDescripcion = JOptionPane.showInputDialog("Ingrese nueva descripción (actual: " + insumo.getDescripcion() + "):", insumo.getDescripcion());
            if (nuevaDescripcion != null) {
                insumo.setDescripcion(nuevaDescripcion);
            }
            
            String nuevoStockActualStr = JOptionPane.showInputDialog("Ingrese nuevo stock actual (actual: " + insumo.getStockActual() + "):", insumo.getStockActual());
            if (nuevoStockActualStr != null && !nuevoStockActualStr.trim().isEmpty()) {
                insumo.setStockActual(Integer.parseInt(nuevoStockActualStr));
            }
            
            String nuevoStockMinimoStr = JOptionPane.showInputDialog("Ingrese nuevo stock mínimo (actual: " + insumo.getStockMinimo() + "):", insumo.getStockMinimo());
            if (nuevoStockMinimoStr != null && !nuevoStockMinimoStr.trim().isEmpty()) {
                insumo.setStockMinimo(Integer.parseInt(nuevoStockMinimoStr));
            }
            
            String nuevoStockDeseadoStr = JOptionPane.showInputDialog("Ingrese nuevo stock deseado (actual: " + insumo.getStockDeseado() + "):", insumo.getStockDeseado());
            if (nuevoStockDeseadoStr != null && !nuevoStockDeseadoStr.trim().isEmpty()) {
                insumo.setStockDeseado(Integer.parseInt(nuevoStockDeseadoStr));
            }
            
            String nuevaUnidad = JOptionPane.showInputDialog("Ingrese nueva unidad de medida (actual: " + insumo.getUnidadMedida() + "):", insumo.getUnidadMedida());
            if (nuevaUnidad != null && !nuevaUnidad.trim().isEmpty()) {
                insumo.setUnidadMedida(nuevaUnidad);
            }
            
            String nuevaCategoriaStr = JOptionPane.showInputDialog("Ingrese nuevo ID de categoría (actual: " + insumo.getIdCategoria() + "):", insumo.getIdCategoria());
            if (nuevaCategoriaStr != null && !nuevaCategoriaStr.trim().isEmpty()) {
                insumo.setIdCategoria(Integer.parseInt(nuevaCategoriaStr));
            }
            
            if (gestion.modificar(insumo)) {
                JOptionPane.showMessageDialog(null, "✅ Insumo modificado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "❌ Error al modificar insumo.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Debe ingresar números válidos.");
        }
    }
    
   
    public static void eliminarInsumo() {
        try {
            String idStr = JOptionPane.showInputDialog("Ingrese el ID del insumo a eliminar:");
            if (idStr == null || idStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Eliminación cancelada.");
                return;
            }
            
            int id = Integer.parseInt(idStr);
            GestionInsumos gestion = new GestionInsumos();
            Insumo insumo = gestion.buscarPorId(id);
            
            if (insumo == null) {
                JOptionPane.showMessageDialog(null, "No se encontró un insumo con ese ID.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro de eliminar el insumo:\n" + insumo.getId() + " - " + insumo.getNombre() + "?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (gestion.eliminar(id)) {
                    JOptionPane.showMessageDialog(null, "✅ Insumo eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Error al eliminar insumo.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Debe ingresar un ID válido.");
        }
    }
    
   
    public static void verStockActual() {
        GestionInsumos gestion = new GestionInsumos();
        List<Insumo> stock = gestion.listar();
        
        if (stock.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
        } else {
            StringBuilder mensaje = new StringBuilder("📊 STOCK ACTUAL:\n\n");
            mensaje.append("ID | NOMBRE | ACTUAL | MÍNIMO | DESEADO\n");
            mensaje.append("--------------------------------------------------\n");
            for (Insumo i : stock) {
                mensaje.append(String.format("%-3d | %-20s | %-6d | %-6d | %-6d\n", 
                    i.getId(), 
                    i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                    i.getStockActual(), 
                    i.getStockMinimo(), 
                    i.getStockDeseado()));
            }
            JOptionPane.showMessageDialog(null, mensaje.toString());
        }
    }
    
   
    public static void stockCritico() {
        GestionInsumos gestion = new GestionInsumos();
        List<Insumo> insumos = gestion.listar();
        if (insumos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
            return;
        }
        List<Insumo> criticos = insumos.stream()
                .filter(i -> i.getStockActual() < i.getStockMinimo())
                .collect(Collectors.toList());
        if (criticos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "✅ No hay insumos con stock crítico.");
        } else {
            StringBuilder mensaje = new StringBuilder("⚠️ STOCK CRÍTICO (por debajo del mínimo):\n\n");
            mensaje.append("ID | NOMBRE | ACTUAL | MÍNIMO\n");
            mensaje.append("----------------------------------------\n");
            for (Insumo i : criticos) {
                mensaje.append(String.format("%-3d | %-20s | %-6d | %-6d\n", 
                    i.getId(),
                    i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                    i.getStockActual(), 
                    i.getStockMinimo()));
            }
            JOptionPane.showMessageDialog(null, mensaje.toString());
        }
    }
    
    
    public static void stockDeseadoVsActual() {
        GestionInsumos gestion = new GestionInsumos();
        List<Insumo> insumos = gestion.listar();
        
        if (insumos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
            return;
        }
        
        StringBuilder mensaje = new StringBuilder("🎯 STOCK DESEADO vs STOCK ACTUAL:\n\n");
        mensaje.append("ID | NOMBRE | ACTUAL | DESEADO | DIFERENCIA\n");
        mensaje.append("----------------------------------------------------------\n");
        
        for (Insumo i : insumos) {
            int diferencia = i.getStockDeseado() - i.getStockActual();
            String diferenciaStr = diferencia > 0 ? "Faltan " + diferencia : "Sobran " + Math.abs(diferencia);
            mensaje.append(String.format("%-3d | %-20s | %-6d | %-6d | %s\n", 
                i.getId(), 
                i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                i.getStockActual(), 
                i.getStockDeseado(),
                diferenciaStr));
        }
        JOptionPane.showMessageDialog(null, mensaje.toString());
    }
    
    
    
    public static void menuGestionUsuarios() {
        String[] opciones = {"Listar Usuarios", "Cambiar Rol", "Activar/Desactivar", "Volver"};
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "👥 GESTIÓN DE USUARIOS\n\nSeleccione una opción:", 
                "ADMIN", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                opciones, 
                opciones[0]);
            
            switch (opcion) {
                case 0: listarUsuarios(); break;
                case 1: cambiarRolUsuario(); break;
                case 2: activarDesactivarUsuario(); break;
            }
        } while (opcion != 3);
    }
    
    public static void listarUsuarios() {
        ControllerUsuario controller = new ControllerUsuario();
        List<Usuario> usuarios = controller.listarTodos();
        
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios registrados.");
        } else {
            StringBuilder mensaje = new StringBuilder("👥 LISTA DE USUARIOS:\n\n");
            mensaje.append("ID | USUARIO | ROL | ACTIVO\n");
            mensaje.append("--------------------------------\n");
            for (Usuario u : usuarios) {
                mensaje.append(u.getId()).append(" | ")
                       .append(u.getNombreUsuario()).append(" | ")
                       .append(u.getRol()).append(" | ")
                       .append(u.isActivo() ? "Sí" : "No").append("\n");
            }
            JOptionPane.showMessageDialog(null, mensaje.toString());
        }
    }
    
    public static void cambiarRolUsuario() {
        try {
            String idStr = JOptionPane.showInputDialog("Ingrese el ID del usuario:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            
            int id = Integer.parseInt(idStr);
            ControllerUsuario controller = new ControllerUsuario();
            Usuario usuario = controller.buscarPorId(id);
            
            if (usuario == null) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            
            String[] roles = {"ADMIN", "OPERADOR"};
            int nuevoRol = JOptionPane.showOptionDialog(null, 
                "Usuario: " + usuario.getNombreUsuario() + "\nRol actual: " + usuario.getRol() + "\n\nSeleccione nuevo rol:",
                "Cambiar Rol", 0, JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
            
            if (nuevoRol == 0) {
                controller.cambiarRol(id, "ADMIN");
                JOptionPane.showMessageDialog(null, "✅ Usuario ahora es ADMIN.");
            } else if (nuevoRol == 1) {
                controller.cambiarRol(id, "OPERADOR");
                JOptionPane.showMessageDialog(null, "✅ Usuario ahora es OPERADOR.");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
        }
    }
    
    public static void activarDesactivarUsuario() {
        try {
            String idStr = JOptionPane.showInputDialog("Ingrese el ID del usuario:");
            if (idStr == null || idStr.trim().isEmpty()) return;
            
            int id = Integer.parseInt(idStr);
            ControllerUsuario controller = new ControllerUsuario();
            Usuario usuario = controller.buscarPorId(id);
            
            if (usuario == null) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            
            String estadoActual = usuario.isActivo() ? "ACTIVO" : "INACTIVO";
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Usuario: " + usuario.getNombreUsuario() + "\nEstado actual: " + estadoActual +
                "\n\n¿Desea cambiar el estado?", "Activar/Desactivar", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                controller.activarDesactivar(id, !usuario.isActivo());
                JOptionPane.showMessageDialog(null, "✅ Estado actualizado.");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
        }
    }
    
    
    
    public static void menuGestionInsumos() {
        String[] opciones = {
            "Listar Insumos", 
            "Agregar Insumo", 
            "Modificar Insumo", 
            "Eliminar Insumo", 
            "Buscar Insumo", 
            "Volver"
        };
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "📦 GESTIÓN DE INSUMOS\n\nSeleccione una opción:", 
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
                                   .append(i.getStockActual()).append(" ").append(i.getUnidadMedida()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, mensaje.toString());
                    }
                    break;
                    
                case 1:
                    agregarInsumo();
                    break;
                    
                case 2: 
                    modificarInsumo();
                    break;
                    
                case 3: 
                    eliminarInsumo();
                    break;
                    
                case 4: 
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
                                       .append(i.getStockActual()).append(" ").append(i.getUnidadMedida()).append("\n");
                            }
                            JOptionPane.showMessageDialog(null, mensaje.toString());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Búsqueda cancelada.");
                    }
                    break;
            }
        } while (opcion != 5);
    }
    
   
    
    public static void menuEstadisticas() {
        String[] opciones = {
            "📊 Ver Stock Actual",
            "⚠️ Stock Crítico (por debajo del mínimo)",
            "🎯 Stock Deseado vs Actual",
            "📅 Consumo por Período (próxima versión)",
            "📋 Sugerencia de Pedido (próxima versión)",
            "Volver"
        };
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "📊 ESTADÍSTICAS Y REPORTES\n\nSeleccione una opción:", 
                "ADMIN", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                opciones, 
                opciones[0]);
            
            switch (opcion) {
                case 0: 
                    verStockActual();
                    break;
                case 1: 
                    stockCritico();
                    break;
                case 2: 
                    stockDeseadoVsActual();
                    break;
                case 3: 
                    JOptionPane.showMessageDialog(null, 
                        "📅 CONSUMO POR PERÍODO\n\nFunción en desarrollo para la próxima versión.\n" +
                        "Permitirá ver consumo por mes, trimestre o año.");
                    break;
                case 4: 
                    JOptionPane.showMessageDialog(null, 
                        "📋 SUGERENCIA DE PEDIDO\n\nFunción en desarrollo para la próxima versión.\n" +
                        "Calculará: stockDeseado - stockActual = cantidad a pedir.");
                    break;
            }
        } while (opcion != 5);
    }
    
   
    
    public static void menuAdministrador() {
        String[] opcionesPrincipales = {
            "📦 Gestión de Insumos",
            "📊 Estadísticas y Reportes", 
            "👥 Gestión de Usuarios",
            "❌ Salir"
        };
        
        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(null, 
                "Menú Administrador\n\nSeleccione una categoría:", 
                "ADMIN", 
                0, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, 
                opcionesPrincipales, 
                opcionesPrincipales[0]);
            
            switch (opcion) {
                case 0: 
                    menuGestionInsumos();
                    break;
                case 1: 
                    menuEstadisticas();
                    break;
                case 2: 
                    menuGestionUsuarios();
                    break;
            }
        } while (opcion != 3);
    }
    
    
    
    public static void menuOperador() {
        String[] opciones = {"Registrar Consumo", "Ver Stock Actual", "Salir"};
        
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
                    verStockActual();
                    break;
            }
        } while (opcion != 2);
    }
}