package GUI;

import javax.swing.JOptionPane;
import java.util.List;
import DLL.ControllerUsuario;
import DLL.GestionInsumos;
import DLL.GestionMovimientos;
import BLL.Insumo;
import BLL.Movimiento;
import BLL.RolUsuario;
import BLL.Usuario;
import BLL.Lote;


public class Maqueta {

	public static void main(String[] args) {
	    
	    String[] menuPrincipal = {"Ingresar", "Registrar", "Salir"};
	    ControllerUsuario controller = new ControllerUsuario();
	    
	    
	    javax.swing.ImageIcon logo = new javax.swing.ImageIcon("src/img/sigi_logo.jpg");
	    
	    int opcion;
	    do {
	        opcion = JOptionPane.showOptionDialog(null, 
	            "Sistema de Gestión de Insumos - SIGI\n\nElija una opción:", 
	            "SIGI", 
	            0, 
	            JOptionPane.INFORMATION_MESSAGE, 
	            logo,  
	            menuPrincipal, 
	            menuPrincipal[0]);
	        
	        if (opcion == 0) {
	            String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
	            String contrasenia = JOptionPane.showInputDialog("Ingrese contraseña:");
	            
	            Usuario usuarioLogueado = controller.login(nombreUsuario, contrasenia);
	            
	            if (usuarioLogueado != null) {
	                JOptionPane.showMessageDialog(null, "Bienvenido " + usuarioLogueado.getNombreCompleto());
	                
	                if (usuarioLogueado.getRol() == RolUsuario.ADMIN) {
	                    menuAdministrador(usuarioLogueado);
	                } else {
	                    menuOperador(usuarioLogueado);
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
            JOptionPane.showMessageDialog(null, "✅ Usuario registrado con éxito.");
        } else {
            JOptionPane.showMessageDialog(null, "❌ Error: El nombre de usuario ya existe.");
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
        
        StringBuilder mensaje = new StringBuilder("⚠️ STOCK CRÍTICO:\n\n");
        mensaje.append("ID | NOMBRE | ACTUAL | MÍNIMO\n");
        mensaje.append("----------------------------------------\n");
        boolean hayCriticos = false;
        
        for (Insumo i : insumos) {
            if (i.getStockActual() < i.getStockMinimo()) {
                hayCriticos = true;
                mensaje.append(String.format("%-3d | %-20s | %-6d | %-6d\n", 
                    i.getId(),
                    i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                    i.getStockActual(), 
                    i.getStockMinimo()));
            }
        }
        
        if (!hayCriticos) {
            JOptionPane.showMessageDialog(null, "✅ No hay insumos con stock crítico.");
        } else {
            JOptionPane.showMessageDialog(null, mensaje.toString());
        }
    }
    
    public static void pedidoSugerido() {
        GestionInsumos gestion = new GestionInsumos();
        List<Insumo> insumos = gestion.listar();
        
        if (insumos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay insumos cargados.");
            return;
        }
        
        StringBuilder sb = new StringBuilder("📋 PEDIDO SUGERIDO:\n\n");
        sb.append("ID | NOMBRE | STOCK ACTUAL | SUGERENCIA\n");
        sb.append("--------------------------------------------------\n");
        
        boolean haySugerencias = false;
        
        for (Insumo i : insumos) {
            int sugerencia = i.getStockDeseado() - i.getStockActual();
            if (sugerencia > 0) {
                haySugerencias = true;
                sb.append(String.format("%-3d | %-20s | %-6d | %-6d\n", 
                    i.getId(), 
                    i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                    i.getStockActual(), 
                    sugerencia));
            }
        }
        
        if (!haySugerencias) {
            JOptionPane.showMessageDialog(null, "✅ No hay insumos que necesitan pedido.\nTodos tienen stock suficiente.");
        } else {
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }
    
    
    
    public static void registrarConsumo(Usuario usuarioLogueado) {
        try {
            String[] areas = {"Hematología", "Hemostasia", "Cancelar"};
            int areaSeleccionada = JOptionPane.showOptionDialog(null, 
                "Seleccione el área:", "Registrar Consumo", 0, 
                JOptionPane.QUESTION_MESSAGE, null, areas, areas[0]);
            
            if (areaSeleccionada == 2 || areaSeleccionada == JOptionPane.CLOSED_OPTION) return;
            
            int idCategoria = 0;
            
            if (areaSeleccionada == 0) {
                idCategoria = 6;
            } else if (areaSeleccionada == 1) {
                String[] subcategorias = {"Rutina", "Especiales", "Controles y Calibradores", "Consumibles", "Volver"};
                int subSeleccion = JOptionPane.showOptionDialog(null, 
                    "Seleccione la subcategoría:", "Registrar Consumo", 0, 
                    JOptionPane.QUESTION_MESSAGE, null, subcategorias, subcategorias[0]);
                
                if (subSeleccion == 4 || subSeleccion == JOptionPane.CLOSED_OPTION) return;
                idCategoria = subSeleccion + 2;
            }
            
            GestionInsumos gestion = new GestionInsumos();
            List<Insumo> insumos = gestion.listarPorCategoria(idCategoria);
            
            if (insumos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay insumos en esta categoría.");
                return;
            }
            
            String[] nombres = new String[insumos.size()];
            for (int i = 0; i < insumos.size(); i++) {
                Insumo obj = insumos.get(i);
                nombres[i] = obj.getId() + " - " + obj.getNombre() + " | Stock: " + obj.getStockActual();
            }
            
            int seleccion = JOptionPane.showOptionDialog(null, "Seleccione el insumo:", 
                "Registrar Consumo", 0, JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
            if (seleccion == JOptionPane.CLOSED_OPTION) return;
            
            Insumo insumo = insumos.get(seleccion);
            
           
            List<Lote> lotes = gestion.obtenerLotesPorInsumo(insumo.getId());

            if (lotes.isEmpty()) {
                
                String cantStr = JOptionPane.showInputDialog("Cantidad a consumir (Stock: " + insumo.getStockActual() + "):");
                if (cantStr == null) return;
                int cantidad = Integer.parseInt(cantStr);
                
                String obs = JOptionPane.showInputDialog("Observación (opcional):");
                if (obs == null) obs = "";
                
                gestion.actualizarStock(insumo.getId(), cantidad, "CONSUMO", usuarioLogueado.getId(), obs);
                return;
            }
            
       
            StringBuilder sb = new StringBuilder("📦 LOTES DISPONIBLES:\n\n");
            for (int i = 0; i < lotes.size(); i++) {
                Lote l = lotes.get(i);
                sb.append((i+1) + ". Lote: ").append(l.getNumeroLote());
                if (l.getFechaVencimiento() != null) {
                    sb.append(" | Vence: ").append(l.getFechaVencimiento());
                 
                    java.time.LocalDate hoy = java.time.LocalDate.now();
                    if (l.getFechaVencimiento().isBefore(hoy)) {
                        sb.append(" ⚠️ VENCIDO");
                    } else if (l.getFechaVencimiento().minusDays(30).isBefore(hoy)) {
                        sb.append(" ⚠️ PRÓXIMO A VENCER (menos de 30 días)");
                    }
                } else {
                    sb.append(" | Sin vencimiento");
                }
                sb.append("\n");
            }
            
           
            Lote sugerido = lotes.get(0);
            sb.append("\n💡 SUGERENCIA: Use el lote ").append(sugerido.getNumeroLote());
            if (sugerido.getFechaVencimiento() != null) {
                sb.append(" (vence ").append(sugerido.getFechaVencimiento()).append(")");
            }
            
       
            int loteSeleccionado = JOptionPane.showOptionDialog(null, sb.toString(), 
                "Seleccione el lote a consumir", 0, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Usar lote sugerido", "Elegir otro lote", "Cancelar"}, "Usar lote sugerido");
            
            Lote loteConsumir = null;
            
            if (loteSeleccionado == 0) {
                loteConsumir = sugerido;
            } else if (loteSeleccionado == 1) {
                String[] opcionesLote = new String[lotes.size()];
                for (int i = 0; i < lotes.size(); i++) {
                    opcionesLote[i] = lotes.get(i).getNumeroLote();
                }
                int elegido = JOptionPane.showOptionDialog(null, "Seleccione el lote:", 
                    "Elegir lote", 0, JOptionPane.QUESTION_MESSAGE, null, opcionesLote, opcionesLote[0]);
                if (elegido == JOptionPane.CLOSED_OPTION) return;
                loteConsumir = lotes.get(elegido);
            } else {
                return;
            }
            
            String cantStr = JOptionPane.showInputDialog("Cantidad a consumir (Stock disponible de este lote: " + loteConsumir.getStockLote() + "):");
            if (cantStr == null) return;
            int cantidad = Integer.parseInt(cantStr);
            
            String obs = JOptionPane.showInputDialog("Observación (opcional):");
            if (obs == null) obs = "";
            
            boolean ok = gestion.descontarDeLote(loteConsumir.getId(), cantidad);

            if (ok) {
                gestion.actualizarStock(insumo.getId(), cantidad, "CONSUMO", usuarioLogueado.getId(), obs);
                JOptionPane.showMessageDialog(null, "✅ Consumo registrado del lote " + loteConsumir.getNumeroLote());
            } else {
                JOptionPane.showMessageDialog(null, "❌ No hay suficiente stock en el lote " + loteConsumir.getNumeroLote());
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Número inválido.");
        }
    }  
                        
    
    public static void registrarIngreso(Usuario usuarioLogueado) {
        try {
            String[] areas = {"Hematología", "Hemostasia", "Cancelar"};
            int areaSeleccionada = JOptionPane.showOptionDialog(null, 
                "Seleccione el área:", "Registrar Ingreso", 0, 
                JOptionPane.QUESTION_MESSAGE, null, areas, areas[0]);
            
            if (areaSeleccionada == 2 || areaSeleccionada == JOptionPane.CLOSED_OPTION) return;
            
            int idCategoria = 0;
            
            if (areaSeleccionada == 0) {
                idCategoria = 6;
            } else if (areaSeleccionada == 1) {
                String[] subcategorias = {"Rutina", "Especiales", "Controles y Calibradores", "Consumibles", "Volver"};
                int subSeleccion = JOptionPane.showOptionDialog(null, 
                    "Seleccione la subcategoría:", "Registrar Ingreso", 0, 
                    JOptionPane.QUESTION_MESSAGE, null, subcategorias, subcategorias[0]);
                
                if (subSeleccion == 4 || subSeleccion == JOptionPane.CLOSED_OPTION) return;
                idCategoria = subSeleccion + 2;
            }
            
            GestionInsumos gestion = new GestionInsumos();
            List<Insumo> insumos = gestion.listarPorCategoria(idCategoria);
            
            if (insumos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay insumos en esta categoría.");
                return;
            }
            
            String[] nombres = new String[insumos.size()];
            for (int i = 0; i < insumos.size(); i++) {
                Insumo obj = insumos.get(i);
                nombres[i] = obj.getId() + " - " + obj.getNombre() + " | Stock: " + obj.getStockActual();
            }
            
            int seleccion = JOptionPane.showOptionDialog(null, "Seleccione el insumo:", 
                "Registrar Ingreso", 0, JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
            if (seleccion == JOptionPane.CLOSED_OPTION) return;
            
            Insumo insumo = insumos.get(seleccion);
            String cantStr = JOptionPane.showInputDialog("Cantidad a ingresar:");
            if (cantStr == null) return;
            int cantidad = Integer.parseInt(cantStr);
         
            String numeroLote = JOptionPane.showInputDialog("Número de lote (opcional):");
            if (numeroLote == null) numeroLote = "";

            String fechaVencimientoStr = JOptionPane.showInputDialog("Fecha de vencimiento (opcional, formato dd-MM-yyyy):");
            java.time.LocalDate fechaVencimiento = null;
            if (fechaVencimientoStr != null && !fechaVencimientoStr.trim().isEmpty()) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    fechaVencimiento = java.time.LocalDate.parse(fechaVencimientoStr, formatter);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "❌ Formato de fecha incorrecto.");
                }
            }
          
                                  
            String obs = JOptionPane.showInputDialog("Observación (opcional):");
            if (obs == null) obs = "";
            
           
            gestion.actualizarStock(insumo.getId(), cantidad, "INGRESO", usuarioLogueado.getId(), obs);
            
            
            if (numeroLote != null && !numeroLote.trim().isEmpty()) {
            	gestion.guardarLote(insumo.getId(), numeroLote, fechaVencimiento, cantidad);
            }
            
            JOptionPane.showMessageDialog(null, "✅ Ingreso registrado correctamente.");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Número inválido.");
        }
    }
    public static void verHistorialCompleto() {
        GestionMovimientos gm = new GestionMovimientos();
        List<Movimiento> movimientos = gm.listarTodos();
        
        if (movimientos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay movimientos registrados.");
            return;
        }
        
        GestionInsumos gi = new GestionInsumos();
        ControllerUsuario controller = new ControllerUsuario();
        
        StringBuilder sb = new StringBuilder("📜 HISTORIAL COMPLETO DE MOVIMIENTOS:\n\n");
        sb.append("FECHA | TIPO | INSUMO | CANTIDAD | USUARIO | OBSERVACIÓN\n");
        sb.append("--------------------------------------------------------------------------------\n");
        
        for (Movimiento m : movimientos) {
            Insumo insumo = gi.buscarPorId(m.getIdInsumo());
            String nombreInsumo = (insumo != null) ? insumo.getNombre() : "ID: " + m.getIdInsumo();
            
            Usuario usuario = controller.buscarPorId(m.getIdUsuario());
            String nombreUsuario = (usuario != null) ? usuario.getNombreUsuario() : "ID: " + m.getIdUsuario();
            
            sb.append(m.getFecha()).append(" | ")
              .append(m.getTipo()).append(" | ")
              .append(nombreInsumo).append(" | ")
              .append(m.getCantidad()).append(" | ")
              .append(nombreUsuario).append(" | ")
              .append(m.getObservacion()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
    
    public static void verMisMovimientos(Usuario usuarioLogueado) {
        GestionMovimientos gm = new GestionMovimientos();
        List<Movimiento> movimientos = gm.listarPorUsuario(usuarioLogueado.getId());
        
        if (movimientos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tiene movimientos registrados.");
            return;
        }
        
        GestionInsumos gi = new GestionInsumos();
        
        StringBuilder sb = new StringBuilder("📜 MIS MOVIMIENTOS:\n\n");
        sb.append("FECHA | TIPO | INSUMO | CANTIDAD | OBSERVACIÓN\n");
        sb.append("----------------------------------------------------------\n");
        
        for (Movimiento m : movimientos) {
            Insumo insumo = gi.buscarPorId(m.getIdInsumo());
            String nombreInsumo = (insumo != null) ? insumo.getNombre() : "ID: " + m.getIdInsumo();
            
            sb.append(m.getFecha()).append(" | ")
              .append(m.getTipo()).append(" | ")
              .append(nombreInsumo).append(" | ")
              .append(m.getCantidad()).append(" | ")
              .append(m.getObservacion()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
    
    
    
    public static void listarUsuarios() {
        ControllerUsuario controller = new ControllerUsuario();
        List<Usuario> usuarios = controller.listarTodos();
        
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay usuarios registrados.");
            return;
        }
        
        StringBuilder sb = new StringBuilder("👥 LISTA DE USUARIOS:\n\n");
        sb.append("ID | USUARIO | ROL | ACTIVO\n");
        sb.append("--------------------------------\n");
        for (Usuario u : usuarios) {
            sb.append(u.getId()).append(" | ")
              .append(u.getNombreUsuario()).append(" | ")
              .append(u.getRol()).append(" | ")
              .append(u.isActivo() ? "Sí" : "No").append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
    
    public static void cambiarRolUsuario() {
        try {
            String idStr = JOptionPane.showInputDialog("ID del usuario:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);
            
            ControllerUsuario controller = new ControllerUsuario();
            Usuario u = controller.buscarPorId(id);
            if (u == null) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            
            String[] roles = {"ADMIN", "OPERADOR"};
            int nuevo = JOptionPane.showOptionDialog(null, 
                "Usuario: " + u.getNombreUsuario() + "\nRol actual: " + u.getRol(),
                "Cambiar Rol", 0, JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
            
            if (nuevo == 0) controller.cambiarRol(id, "ADMIN");
            else if (nuevo == 1) controller.cambiarRol(id, "OPERADOR");
            JOptionPane.showMessageDialog(null, "✅ Rol actualizado.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
        }
    }
    
    public static void activarDesactivarUsuario() {
        try {
            String idStr = JOptionPane.showInputDialog("ID del usuario:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);
            
            ControllerUsuario controller = new ControllerUsuario();
            Usuario u = controller.buscarPorId(id);
            if (u == null) {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Usuario: " + u.getNombreUsuario() + "\nEstado: " + (u.isActivo() ? "ACTIVO" : "INACTIVO"),
                "Cambiar estado", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                controller.activarDesactivar(id, !u.isActivo());
                JOptionPane.showMessageDialog(null, "✅ Estado actualizado.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
        }
    }
    
    
    
    public static void agregarInsumo() {
        try {
            String nombre = JOptionPane.showInputDialog("Nombre:");
            if (nombre == null) return;
            String desc = JOptionPane.showInputDialog("Descripción:");
            int actual = Integer.parseInt(JOptionPane.showInputDialog("Stock actual:"));
            int minimo = Integer.parseInt(JOptionPane.showInputDialog("Stock mínimo:"));
            int deseado = Integer.parseInt(JOptionPane.showInputDialog("Stock deseado:"));
            String unidad = JOptionPane.showInputDialog("Unidad de medida:");
            int idCat = Integer.parseInt(JOptionPane.showInputDialog("ID categoría:"));
            
            Insumo nuevo = new Insumo(0, nombre, desc, actual, minimo, deseado, unidad, idCat);
            GestionInsumos gestion = new GestionInsumos();
            if (gestion.agregar(nuevo)) JOptionPane.showMessageDialog(null, "✅ Agregado.");
            else JOptionPane.showMessageDialog(null, "❌ Error.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Número inválido.");
        }
    }
    
    public static void modificarInsumo() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID del insumo:"));
            GestionInsumos gestion = new GestionInsumos();
            Insumo i = gestion.buscarPorId(id);
            if (i == null) {
                JOptionPane.showMessageDialog(null, "No encontrado.");
                return;
            }
            
            String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", i.getNombre());
            if (nuevoNombre != null) i.setNombre(nuevoNombre);
            String nuevaDesc = JOptionPane.showInputDialog("Nueva descripción:", i.getDescripcion());
            if (nuevaDesc != null) i.setDescripcion(nuevaDesc);
            int nuevoActual = Integer.parseInt(JOptionPane.showInputDialog("Nuevo stock actual:", i.getStockActual()));
            i.setStockActual(nuevoActual);
            int nuevoMin = Integer.parseInt(JOptionPane.showInputDialog("Nuevo stock mínimo:", i.getStockMinimo()));
            i.setStockMinimo(nuevoMin);
            int nuevoDes = Integer.parseInt(JOptionPane.showInputDialog("Nuevo stock deseado:", i.getStockDeseado()));
            i.setStockDeseado(nuevoDes);
            
            if (gestion.modificar(i)) JOptionPane.showMessageDialog(null, "✅ Modificado.");
            else JOptionPane.showMessageDialog(null, "❌ Error.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Número inválido.");
        }
    }
    
    public static void eliminarInsumo() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID del insumo:"));
            int confirm = JOptionPane.showConfirmDialog(null, "¿Eliminar insumo ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                GestionInsumos gestion = new GestionInsumos();
                if (gestion.eliminar(id)) JOptionPane.showMessageDialog(null, "✅ Eliminado.");
                else JOptionPane.showMessageDialog(null, "❌ Error.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ ID inválido.");
        }
    }
    
    public static void buscarInsumo() {
        String nombre = JOptionPane.showInputDialog("🔍 Ingrese el nombre del insumo a buscar:");
        if (nombre == null) return;
        
        GestionInsumos gestion = new GestionInsumos();
        List<Insumo> resultados = gestion.buscarPorNombre(nombre);
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontraron insumos con ese nombre.");
        } else {
            StringBuilder sb = new StringBuilder("🔍 RESULTADOS DE BÚSQUEDA:\n\n");
            sb.append("ID | NOMBRE | STOCK | UNIDAD\n");
            sb.append("----------------------------------------\n");
            for (Insumo i : resultados) {
                sb.append(String.format("%-3d | %-20s | %-6d | %-10s\n", 
                    i.getId(), 
                    i.getNombre().length() > 18 ? i.getNombre().substring(0, 18) : i.getNombre(),
                    i.getStockActual(),
                    i.getUnidadMedida()));
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }
   
    
    
    public static void menuGestionInsumos() {
        String[] opciones = {"Listar", "Agregar", "Modificar", "Eliminar", "Buscar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Gestión de Insumos", "ADMIN", 
                0, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            switch (op) {
                case 0: verStockActual(); break;
                case 1: agregarInsumo(); break;
                case 2: modificarInsumo(); break;
                case 3: eliminarInsumo(); break;
                case 4: buscarInsumo(); break;
            }
        } while (op != 5);
    }
    
    public static void menuEstadisticas() {
        String[] opciones = {"Stock", "Stock Crítico", "Pedido Sugerido", "Historial Completo", "Consumo por período", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Estadísticas", "ADMIN", 
                0, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            switch (op) {
                case 0: verStockActual(); break;
                case 1: stockCritico(); break;
                case 2: pedidoSugerido(); break;
                case 3: verHistorialCompleto(); break;
                case 4: consumoPorPeriodo(); break;
            }
        } while (op != 5);
    }
    
    public static void consumoPorPeriodo() {
        
        String desdeStr = JOptionPane.showInputDialog("📅 Fecha DESDE (dd-MM-yyyy):");
        if (desdeStr == null) return;
        String hastaStr = JOptionPane.showInputDialog("📅 Fecha HASTA (dd-MM-yyyy):");
        if (hastaStr == null) return;
        
        try {
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            java.time.LocalDate desde = java.time.LocalDate.parse(desdeStr, formatter);
            java.time.LocalDate hasta = java.time.LocalDate.parse(hastaStr, formatter);
            
            GestionMovimientos gm = new GestionMovimientos();
            List<Movimiento> movimientos = gm.listarPorRangoFechas(desde, hasta);
            
            if (movimientos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay movimientos en ese período.");
                return;
            }
            
            long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
            
            GestionInsumos gi = new GestionInsumos();
            
            StringBuilder sb = new StringBuilder();
            sb.append("📊 CONSUMO POR PERÍODO\n");
            sb.append("Desde: ").append(desdeStr).append("  Hasta: ").append(hastaStr).append("\n");
            sb.append("Días: ").append(dias).append("\n\n");
            sb.append("INSUMO                | CANTIDAD | PROMEDIO/DÍA\n");
            sb.append("-----------------------------------------------\n");
            
            List<Insumo> todosInsumos = gi.listar();
            
            for (Insumo insumo : todosInsumos) {
                int cantidadTotal = 0;
                
                for (Movimiento m : movimientos) {
                    if (m.getTipo().equals("CONSUMO") && m.getIdInsumo() == insumo.getId()) {
                        cantidadTotal += m.getCantidad();
                    }
                }
                
                if (cantidadTotal > 0) {
                    double promedio = (double) cantidadTotal / dias;
                    String nombre = insumo.getNombre();
                    if (nombre.length() > 20) {
                        nombre = nombre.substring(0, 20);
                    }
                    sb.append(String.format("%-20s | %-8d | %.2f\n", nombre, cantidadTotal, promedio));
                }
            }
            
            JOptionPane.showMessageDialog(null, sb.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Error: Formato de fecha incorrecto. Use dd-MM-yyyy (ej: 15-03-2026)");
        }
    }
    
    public static void menuGestionUsuarios() {
        String[] opciones = {"Listar", "Cambiar Rol", "Activar/Desactivar", "Volver"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Gestión de Usuarios", "ADMIN", 
                0, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            switch (op) {
                case 0: listarUsuarios(); break;
                case 1: cambiarRolUsuario(); break;
                case 2: activarDesactivarUsuario(); break;
            }
        } while (op != 3);
    }
    
    public static void menuAdministrador(Usuario usuarioLogueado) {
        String[] opciones = {
            "📦 Gestión de Insumos", 
            "📊 Estadísticas", 
            "👥 Gestión de Usuarios",
            "📝 Registrar Consumo",
            "📥 Registrar Ingreso",
            "❌ Salir"
        };
        
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Menú Administrador", "ADMIN", 
                0, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            switch (op) {
                case 0: menuGestionInsumos(); break;
                case 1: menuEstadisticas(); break;
                case 2: menuGestionUsuarios(); break;
                case 3: registrarConsumo(usuarioLogueado); break;
                case 4: registrarIngreso(usuarioLogueado); break;
            }
        } while (op != 5);
    }
    
    public static void menuOperador(Usuario usuarioLogueado) {
        String[] opciones = {"Registrar Consumo", "Registrar Ingreso", "Ver Stock", "Mis Movimientos", "Salir"};
        int op;
        do {
            op = JOptionPane.showOptionDialog(null, "Menú Operador", "OPERADOR", 
                0, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            switch (op) {
                case 0: registrarConsumo(usuarioLogueado); break;
                case 1: registrarIngreso(usuarioLogueado); break;
                case 2: verStockActual(); break;
                case 3: verMisMovimientos(usuarioLogueado); break;
            }
        } while (op != 4);
    }
}       