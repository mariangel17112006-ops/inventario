package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PrioridadPedido;
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

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        try {
            Pedido creado = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido confirmado = pedidoService.confirmarPedido(id);
            if (confirmado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(confirmado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido cancelado = pedidoService.cancelarPedido(id);
            if (cancelado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(cancelado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/despachar")
    public ResponseEntity<?> despacharPedido(@PathVariable Long id) {
        try {
            Pedido despachado = pedidoService.despacharPedido(id);
            if (despachado == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(despachado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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

    @GetMapping("/estado/{estado}")
    public List<Pedido> obtenerPorEstado(@PathVariable EstadoPedido estado) {
        return pedidoService.obtenerPorEstado(estado);
    }

    @GetMapping("/resumen")
    public Map<String, Object> obtenerResumen() {
        return pedidoService.obtenerResumen();
    }

    @GetMapping("/siguiente")
    public ResponseEntity<Pedido> obtenerSiguiente() {
        Pedido siguiente = pedidoService.obtenerSiguiente();
        if (siguiente == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(siguiente);
    }

    @GetMapping("/en-riesgo")
    public List<Pedido> obtenerEnRiesgo() {
        return pedidoService.obtenerEnRiesgo();
    }

    // --- ENDPOINTS BOSS 2 ---
    @GetMapping("/prioridad")
    public List<Pedido> obtenerPorPrioridad(@RequestParam PrioridadPedido prioridad) {
        return pedidoService.obtenerPorPrioridad(prioridad);
    }

    @GetMapping("/buscar-cliente")
    public List<Pedido> buscarPorCliente(@RequestParam String cliente) {
        return pedidoService.buscarPorCliente(cliente);
    }
}