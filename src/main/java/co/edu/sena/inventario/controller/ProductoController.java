package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private List<Producto> productos = new ArrayList<>();

    public ProductoController() {
        // Cargar los 5 productos iniciales
        productos.add(new Producto(1L, "Lechuga crespa", 1800.0, 30));
        productos.add(new Producto(2L, "Zanahoria", 2200.0, 60));
        productos.add(new Producto(3L, "Cebolla cabezona", 3500.0, 25));
        productos.add(new Producto(4L, "Papa pastusa", 2500.0, 50));
        productos.add(new Producto(5L, "Tomate chonto", 3000.0, 35));
    }

    // 1. GET -> Consultar todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productos;
    }

    // 2. GET POR ID -> Consultar un producto
    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    // 3. POST -> Crear un nuevo producto (¡El que nos faltaba!)
    @PostMapping
    public String crearProducto(@RequestBody Producto nuevo) {
        productos.add(nuevo);
        return "Producto '" + nuevo.getNombre() + "' agregado correctamente.";
    }

    // 4. PUT -> Actualizar un producto por ID
    @PutMapping("/{id}")
    public String actualizarProducto(@PathVariable Long id, @RequestBody Producto actualizado) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                p.setNombre(actualizado.getNombre());
                p.setPrecio(actualizado.getPrecio());
                p.setCantidad(actualizado.getCantidad());
                return "Producto con ID " + id + " actualizado correctamente.";
            }
        }
        return "Producto no encontrado.";
    }

    // 5. DELETE -> Eliminar un producto por ID
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productos.removeIf(p -> p.getId().equals(id));
        return "Producto con ID " + id + " eliminado del inventario.";
    }

    // 6. FILTRAR POR NOMBRE (Reto 2)
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 7. FILTRAR POR CATEGORÍA
    @GetMapping("/buscar/categoria/{categoria}")
    public List<Producto> buscarPorCategoria(@PathVariable String categoria) {
        return productos.stream()
                .filter(p -> {
                    if (categoria.equalsIgnoreCase("Hortalizas")) {
                        return p.getNombre().contains("Lechuga") || p.getNombre().contains("Cebolla");
                    } else if (categoria.equalsIgnoreCase("Tubérculos")) {
                        return p.getNombre().contains("Zanahoria") || p.getNombre().contains("Papa");
                    } else if (categoria.equalsIgnoreCase("Frutas")) {
                        return p.getNombre().contains("Tomate");
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // 8. RESTAR STOCK EN VENTA
    @GetMapping("/{id}/vender/{comprados}")
    public String venderProducto(@PathVariable Long id, @PathVariable Integer comprados) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                if (p.getCantidad() >= comprados) {
                    int cantidadAnterior = p.getCantidad();
                    int nuevaCantidad = cantidadAnterior - comprados;
                    p.setCantidad(nuevaCantidad);
                    return "¡Venta exitosa! El producto '" + p.getNombre() + "' tenía " + cantidadAnterior 
                            + " unidades. Se vendieron " + comprados + " y ahora quedan " + nuevaCantidad + " en inventario.";
                } else {
                    return "Sin stock suficiente. Quedan " + p.getCantidad() + " unidades.";
                }
            }
        }
        return "Producto no encontrado.";
    }
}