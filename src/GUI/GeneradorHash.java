package GUI;

import repository.Encriptador;

public class GeneradorHash {
    public static void main(String[] args) {
        String contrasenia = "1234";
        String hash = Encriptador.hash(contrasenia);
        System.out.println("EL HASH PARA LA CONTRASEÑA '1234' ES:");
        System.out.println(hash);
    }
}