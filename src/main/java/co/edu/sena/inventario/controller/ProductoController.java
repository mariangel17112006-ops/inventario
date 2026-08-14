package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private List<Producto> productos = new ArrayList<>();

    public ProductoController() {
        // Cargar los 5 productos iniciales
        productos.add(new Producto(1L, "Lechuga crespa", 1800.0, 40));
        productos.add(new Producto(2L, "Zanahoria", 2200.0, 60));
        productos.add(new Producto(3L, "Cebolla cabezona", 3500.0, 25));
        productos.add(new Producto(4L, "Papa pastusa", 2500.0, 50));
        productos.add(new Producto(5L, "Tomate chonto", 3000.0, 35));
    }

    // 1. GET -> READ (Obtener todos los productos)
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productos;
    }

    // 2. GET por ID -> READ (Buscar un producto específico)
    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 3. POST -> CREATE (Crear un nuevo producto)
    @PostMapping
    public Producto crearProducto(@RequestBody Producto nuevoProducto) {
        productos.add(nuevoProducto);
        return nuevoProducto;
    }

    // 4. PUT -> UPDATE (Actualizar un producto existente)
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(id)) {
                productos.set(i, productoActualizado);
                return productoActualizado;
            }
        }
        return null;
    }

    // 5. DELETE -> DELETE (Eliminar un producto por ID)
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));
        if (eliminado) {
            return "Producto con ID " + id + " eliminado correctamente.";
        } else {
            return "Producto no encontrado.";
        }
    }
}