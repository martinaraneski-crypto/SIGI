package BLL;

import java.time.LocalDate;

public class Lote {
    private int id;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private int idInsumo;
    
    public Lote() {}
    
    public Lote(String numeroLote, LocalDate fechaVencimiento, int idInsumo) {
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.idInsumo = idInsumo;
    }
    
    public Lote(int id, String numeroLote, LocalDate fechaVencimiento, int idInsumo) {
        this.id = id;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.idInsumo = idInsumo;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    
    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }
    
    @Override
    public String toString() {
        return "Lote [id=" + id + ", numeroLote=" + numeroLote + "]";
    }
}