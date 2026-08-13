package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final List<Producto> productos = List.of(
        new Producto(1L, "Papa pastusa", 2500.0, 50),
        new Producto(2L, "Tomate", 3200.0, 30),
        new Producto(3L, "Fresa", 8500.0, 20)
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