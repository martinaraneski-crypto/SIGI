package BLL;

import java.time.LocalDate;

public class Movimiento {
    private int id;
    private LocalDate fecha;
    private TipoMovimiento tipo;
    private int cantidad;
    private String observacion;
    private int idInsumo;
    private int idUsuario;
    private int idLote;
    
    public Movimiento() {}
    
    public Movimiento(LocalDate fecha, TipoMovimiento tipo, int cantidad, 
                      int idInsumo, int idUsuario, int idLote) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.idInsumo = idInsumo;
        this.idUsuario = idUsuario;
        this.idLote = idLote;
    }
    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    
    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public int getIdLote() { return idLote; }
    public void setIdLote(int idLote) { this.idLote = idLote; }
    
    @Override
    public String toString() {
        return "Movimiento [id=" + id + ", fecha=" + fecha + ", tipo=" + tipo + ", cantidad=" + cantidad + "]";
    }
}