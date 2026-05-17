
public class StockInsuficienteException extends Exception {
    
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
    public StockInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}