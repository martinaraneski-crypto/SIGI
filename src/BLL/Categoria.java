package BLL;

import java.util.ArrayList;
import java.util.List;

public class Categoria {
    private int id;
    private String nombre;
    private String descripcion;
    private Categoria categoriaPadre;
    private List<Categoria> subcategorias;
    
    public Categoria() {
        this.subcategorias = new ArrayList<>();
    }
    
    public Categoria(int id, String nombre, String descripcion, Categoria categoriaPadre) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadre = categoriaPadre;
        this.subcategorias = new ArrayList<>();
    }
    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Categoria getCategoriaPadre() { return categoriaPadre; }
    public void setCategoriaPadre(Categoria categoriaPadre) { this.categoriaPadre = categoriaPadre; }
    
    public List<Categoria> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<Categoria> subcategorias) { this.subcategorias = subcategorias; }
    
    public void agregarSubcategoria(Categoria subcategoria) {
        this.subcategorias.add(subcategoria);
    }
    
    @Override
    public String toString() {
        return "ID: " + id + " | Nombre: " + nombre;
    }
}