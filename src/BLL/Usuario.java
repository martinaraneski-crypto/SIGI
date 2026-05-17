package BLL;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contrasenia;
    private RolUsuario rol;
    private String nombreCompleto;
    private boolean activo;
    
    public Usuario() {}
    
    public Usuario(int id, String nombreUsuario, String contrasenia, 
                   RolUsuario rol, String nombreCompleto, boolean activo) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.activo = activo;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return "ID: " + id + " | Usuario: " + nombreUsuario + " | Rol: " + rol;
    }
}