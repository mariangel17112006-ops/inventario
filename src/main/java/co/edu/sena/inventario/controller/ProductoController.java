package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Reto 5 y 7: POST con validaciones y código 201 Created
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto nuevo) {
        if (nuevo.getNombre() == null || nuevo.getNombre().trim().isEmpty() ||
            nuevo.getPrecio() == null || nuevo.getPrecio() <= 0 ||
            nuevo.getCantidad() == null || nuevo.getCantidad() < 0) {
            return ResponseEntity.badRequest().body("Datos del producto inválidos. Revisa el nombre, precio y cantidad.");
        }
        productos.add(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Reto 5: PUT con validaciones
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto actualizado) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                if (actualizado.getNombre() == null || actualizado.getNombre().trim().isEmpty() ||
                    actualizado.getPrecio() == null || actualizado.getPrecio() <= 0 ||
                    actualizado.getCantidad() == null || actualizado.getCantidad() < 0) {
                    return ResponseEntity.badRequest().body("Datos del producto inválidos para actualizar.");
                }
                p.setNombre(actualizado.getNombre());
                p.setPrecio(actualizado.getPrecio());
                p.setCantidad(actualizado.getCantidad());
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Reto 4: GET /productos/precio?maximo=5000
    @GetMapping("/precio")
    public List<Producto> buscarPorPrecioMaximo(@RequestParam Double maximo) {
        return productos.stream()
                .filter(p -> p.getPrecio() <= maximo)
                .collect(Collectors.toList());
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

    // 7. FILTRAR POR CATEGORÍA (Reto 3)
    @GetMapping("/categoria")
    public List<Producto> buscarPorCategoria(@RequestParam String nombre) {
        return productos.stream()
                .filter(p -> {
                    if (nombre.equalsIgnoreCase("Hortalizas")) {
                        return p.getNombre().contains("Lechuga") || p.getNombre().contains("Cebolla");
                    } else if (nombre.equalsIgnoreCase("Tubérculos")) {
                        return p.getNombre().contains("Zanahoria") || p.getNombre().contains("Papa");
                    } else if (nombre.equalsIgnoreCase("Frutas")) {
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