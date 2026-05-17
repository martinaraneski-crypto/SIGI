package BLL;

public class Insumo {
    private int id;
    private String codigoPropio;
    private String nombre;
    private String descripcion;
    private int stockActual;
    private int stockMinimo;
    private int stockDeseado;
    private String unidadMedida;
    private int idCategoria;
    
    public Insumo() {}
    
    public Insumo(String nombre, String descripcion, int stockActual, 
                  int stockMinimo, int stockDeseado, String unidadMedida, int idCategoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockDeseado = stockDeseado;
        this.unidadMedida = unidadMedida;
        this.idCategoria = idCategoria;
    }
    
    public Insumo(int id, String nombre, String descripcion, int stockActual, 
                  int stockMinimo, int stockDeseado, String unidadMedida, int idCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockDeseado = stockDeseado;
        this.unidadMedida = unidadMedida;
        this.idCategoria = idCategoria;
    }
    
   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCodigoPropio() { return codigoPropio; }
    public void setCodigoPropio(String codigoPropio) { this.codigoPropio = codigoPropio; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public int getStockDeseado() { return stockDeseado; }
    public void setStockDeseado(int stockDeseado) { this.stockDeseado = stockDeseado; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    @Override
    public String toString() {
        return id + " - " + nombre + " | Stock: " + stockActual + " " + unidadMedida;
    }
}