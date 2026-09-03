package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoService.obtenerPendientes(); // O la llamada a obtenerTodos() de tu servicio
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido nuevo = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            if ("PRODUCTO_NO_EXISTE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto solicitado no existe.");
            }
            return ResponseEntity.badRequest().body("Datos del pedido inválidos.");
        }
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(id);
            if (pedido == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(pedido);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            if (pedido == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(pedido);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/despachar")
    public ResponseEntity<?> despacharPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.despacharPedido(id);
            if (pedido == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok(pedido);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pendientes")
    public List<Pedido> obtenerPendientes() {
        return pedidoService.obtenerPendientes();
    }

    @GetMapping("/urgentes")
    public List<Pedido> obtenerUrgentes() {
        return pedidoService.obtenerUrgentes();
    }

    @GetMapping("/estado")
    public List<Pedido> obtenerPorEstado(@RequestParam EstadoPedido estado) {
        return pedidoService.obtenerPorEstado(estado);
    }

    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen() {
        return pedidoService.obtenerResumen();
    }

    @GetMapping("/siguiente")
    public ResponseEntity<?> obtenerSiguiente() {
        Pedido siguiente = pedidoService.obtenerSiguiente();
        if (siguiente == null) {
            return ResponseEntity.ok("No hay pedidos pendientes por atender.");
        }
        return ResponseEntity.ok(siguiente);
    }

    @GetMapping("/en-riesgo")
    public List<Pedido> obtenerEnRiesgo() {
        return pedidoService.obtenerEnRiesgo();
    }
}