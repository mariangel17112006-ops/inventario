package co.edu.sena.inventario.repository;

import co.edu.sena.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda por categoría
    List<Producto> findByCategoria(String categoria);

    // Búsqueda parcial por nombre ignorando mayúsculas/minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda por stock menor a un límite
    List<Producto> findByCantidadLessThan(Integer limite);
}