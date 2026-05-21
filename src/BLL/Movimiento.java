package BLL;

import java.time.LocalDate;

public class Movimiento {
    private int id;
    private LocalDate fecha;
    private String tipo;  
    private int cantidad;
    private String observacion;
    private int idInsumo;
    private int idUsuario;
    
    
    public Movimiento() {}
    
    
    public Movimiento(LocalDate fecha, String tipo, int cantidad, 
                      String observacion, int idInsumo, int idUsuario) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.idInsumo = idInsumo;
        this.idUsuario = idUsuario;
    }
    
  
    public Movimiento(int id, LocalDate fecha, String tipo, int cantidad, 
                      String observacion, int idInsumo, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
        this.idInsumo = idInsumo;
        this.idUsuario = idUsuario;
    }
    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    
    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    @Override
    public String toString() {
        return fecha + " - " + tipo + " - " + cantidad + " unidades";
    }
}