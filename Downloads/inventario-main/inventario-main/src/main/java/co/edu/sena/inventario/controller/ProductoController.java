package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private List<Producto> productos = new ArrayList<>();

    public ProductoController() {
        // Cargar productos con cantidades para probar el Reto 8
        productos.add(new Producto(1L, "Lechuga crespa", 1800.0, 3)); // Aparece (< 10)
        productos.add(new Producto(2L, "Zanahoria", 2200.0, 8)); // Aparece (< 10)
        productos.add(new Producto(3L, "Cebolla cabezona", 3500.0, 15)); // No aparece
        productos.add(new Producto(4L, "Papa pastusa", 2500.0, 30)); // No aparece
        productos.add(new Producto(5L, "Tomate chonto", 3000.0, 35)); // No aparece
    }

    // 1. GET -> Consultar todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productos;
    }

    // Reto 6: GET POR ID -> Devuelve 200 OK o 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Reto 5 y 7: POST con validaciones, control de ID duplicado y código 201
    // Created
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto nuevo) {
        // Validación de datos requeridos
        if (nuevo.getNombre() == null || nuevo.getNombre().trim().isEmpty() ||
                nuevo.getPrecio() == null || nuevo.getPrecio() <= 0 ||
                nuevo.getCantidad() == null || nuevo.getCantidad() < 0) {
            return ResponseEntity.badRequest()
                    .body("Datos del producto inválidos. Revisa el nombre, precio y cantidad.");
        }

        // CORRECCIÓN 3: Validar que no exista un producto con el mismo ID
        if (nuevo.getId() != null) {
            boolean existeId = productos.stream().anyMatch(p -> p.getId().equals(nuevo.getId()));
            if (existeId) {
                return ResponseEntity.badRequest().body("Ya existe un producto con el ID " + nuevo.getId());
            }
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

    // 5. DELETE -> Eliminar un producto por ID con validacion de existencia (404
    // Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        // CORRECCIÓN 2: Verificar si el producto existe antes de eliminar
        boolean existe = productos.stream().anyMatch(p -> p.getId().equals(id));
        if (!existe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto con ID " + id + " no encontrado.");
        }

        productos.removeIf(p -> p.getId().equals(id));
        return ResponseEntity.ok("Producto con ID " + id + " eliminado del inventario.");
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

    // 8. RESTAR STOCK EN VENTA (Con validacion de cantidad positiva)
    @GetMapping("/{id}/vender/{comprados}")
    public ResponseEntity<?> venderProducto(@PathVariable Long id, @PathVariable Integer comprados) {
        // CORRECCIÓN 1: Validar que la cantidad comprada sea estrictamente mayor a 0
        if (comprados == null || comprados <= 0) {
            return ResponseEntity.badRequest().body("La cantidad a vender debe ser mayor a cero.");
        }

        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                if (p.getCantidad() >= comprados) {
                    int cantidadAnterior = p.getCantidad();
                    int nuevaCantidad = cantidadAnterior - comprados;
                    p.setCantidad(nuevaCantidad);
                    return ResponseEntity
                            .ok("¡Venta exitosa! El producto '" + p.getNombre() + "' tenía " + cantidadAnterior
                                    + " unidades. Se vendieron " + comprados + " y ahora quedan " + nuevaCantidad
                                    + " en inventario.");
                } else {
                    return ResponseEntity.badRequest()
                            .body("Sin stock suficiente. Quedan " + p.getCantidad() + " unidades.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
    }

    // Reto 8: GET /productos/stock-bajo?limite=10
    @GetMapping("/stock-bajo")
    public List<Producto> obtenerStockBajo(@RequestParam(defaultValue = "10") Integer limite) {
        return productos.stream()
                .filter(p -> p.getCantidad() < limite)
                .collect(Collectors.toList());
    }

    // Reto 9: GET /productos/resumen
    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen() {
        int totalProductos = productos.size();
        int totalUnidades = productos.stream().mapToInt(Producto::getCantidad).sum();
        double valorTotal = productos.stream().mapToDouble(p -> p.getPrecio() * p.getCantidad()).sum();

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalProductos", totalProductos);
        resumen.put("totalUnidades", totalUnidades);
        resumen.put("valorTotalInventario", valorTotal);

        return resumen;
    }
}