package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final List<Producto> productos = List.of(
    new Producto(1L, "Lechuga crespa", 1800.0, 40),
    new Producto(2L, "Zanahoria", 2200.0, 60),
    new Producto(3L, "Cebolla cabezona", 3500.0, 25)
);

    @GetMapping
    public List<Producto> listarProductos() {
        return productos;
    }

    @GetMapping("/{id}")
    public Producto buscarProducto(@PathVariable Long id) {
        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                return producto;
            }
        }
        return null;
    }
}