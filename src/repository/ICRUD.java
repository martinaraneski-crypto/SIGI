package repository;

import java.util.List;

public interface ICRUD<T> {
    boolean agregar(T objeto);
    List<T> listar();
    T buscarPorId(int id);
    boolean modificar(T objeto);
    boolean eliminar(int id);
}