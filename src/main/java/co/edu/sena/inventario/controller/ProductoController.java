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
        // Cargar 5 productos
        productos.add(new Producto(1L, "Lechuga crespa", 1800.0, 30));
        productos.add(new Producto(2L, "Zanahoria", 2200.0, 60));
        productos.add(new Producto(3L, "Cebolla cabezona", 3500.0, 25));
        productos.add(new Producto(4L, "Papa pastusa", 2500.0, 50));
        productos.add(new Producto(5L, "Tomate chonto", 3000.0, 35));
    }

    // GET general
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productos;
    }

    // 1. FILTRAR POR NOMBRE
    @GetMapping("/buscar/nombre/{nombre}")
    public List<Producto> buscarPorNombre(@PathVariable String nombre) {
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 2. FILTRAR POR CATEGORÍA
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

    // 3. OPERACIÓN MATEMÁTICA (RESTA EN VENTA)
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