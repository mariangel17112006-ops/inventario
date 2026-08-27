package co.edu.sena.inventario.service;

import co.edu.sena.inventario.controller.ProductoController;
import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PrioridadPedido;
import co.edu.sena.inventario.model.Producto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final ProductoController productoController;
    private Long secuencialId = 1L;

    public PedidoService(ProductoController productoController) {
        this.productoController = productoController;
    }

    // Nivel 1 - Crear pedido
    public Pedido crearPedido(Pedido nuevoPedido) {
        Producto producto = obtenerProducto(nuevoPedido.getProductoId());
        if (producto == null) {
            throw new IllegalArgumentException("PRODUCTO_NO_EXISTE");
        }

        if (nuevoPedido.getCliente() == null || nuevoPedido.getCliente().trim().isEmpty() ||
            nuevoPedido.getCantidad() == null || nuevoPedido.getCantidad() <= 0 ||
            nuevoPedido.getPrioridad() == null) {
            throw new IllegalArgumentException("DATOS_INVALIDOS");
        }

        nuevoPedido.setId(secuencialId++);
        nuevoPedido.setEstado(EstadoPedido.PENDIENTE);

        // Solución Boss Final: Si no hay stock suficiente, entra retenido
        if (nuevoPedido.getCantidad() > producto.getCantidad()) {
            nuevoPedido.setEstado(EstadoPedido.PARCIALMENTE_RETENIDO);
        }

        pedidos.add(nuevoPedido);
        return nuevoPedido;
    }

    // Nivel 2 - Confirmar pedido (descuenta del stock)
    public Pedido confirmarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido == null) return null;

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }

        Producto producto = obtenerProducto(pedido.getProductoId());
        if (producto == null || producto.getCantidad() < pedido.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para confirmar el pedido.");
        }

        producto.setCantidad(producto.getCantidad() - pedido.getCantidad());
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedido;
    }

    // Nivel 3 - Cancelar pedido (repone stock si ya estaba confirmado)
    public Pedido cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido == null) return null;

        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido DESPACHADO o CANCELADO.");
        }

        if (pedido.getEstado() == EstadoPedido.CONFIRMADO) {
            Producto producto = obtenerProducto(pedido.getProductoId());
            if (producto != null) {
                producto.setCantidad(producto.getCantidad() + pedido.getCantidad());
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedido;
    }

    // Nivel 4 - Despachar pedido
    public Pedido despacharPedido(Long id) {
        Pedido pedido = buscarPorId(id);
        if (pedido == null) return null;

        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden despachar pedidos CONFIRMADOS.");
        }

        pedido.setEstado(EstadoPedido.DESPACHADO);
        return pedido;
    }

    // Nivel 5 - Consultas
    public List<Pedido> obtenerPendientes() {
        return pedidos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).collect(Collectors.toList());
    }

    public List<Pedido> obtenerUrgentes() {
        return pedidos.stream().filter(p -> p.getPrioridad() == PrioridadPedido.URGENTE).collect(Collectors.toList());
    }

    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidos.stream().filter(p -> p.getEstado() == estado).collect(Collectors.toList());
    }

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalPedidos", pedidos.size());
        resumen.put("pendientes", contarPorEstado(EstadoPedido.PENDIENTE));
        resumen.put("confirmados", contarPorEstado(EstadoPedido.CONFIRMADO));
        resumen.put("despachados", contarPorEstado(EstadoPedido.DESPACHADO));
        resumen.put("cancelados", contarPorEstado(EstadoPedido.CANCELADO));
        resumen.put("parcialmenteRetenidos", contarPorEstado(EstadoPedido.PARCIALMENTE_RETENIDO));
        resumen.put("urgentes", pedidos.stream().filter(p -> p.getPrioridad() == PrioridadPedido.URGENTE).count());
        return resumen;
    }

    // Nivel 6 - Algoritmo de prioridad
    public Pedido obtenerSiguiente() {
        return pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .min(Comparator
                        .comparing(Pedido::getPrioridad, Comparator.comparingInt(this::getPesoPrioridad))
                        .thenComparing(Pedido::getId))
                .orElse(null);
    }

    // Nivel 7 - Endpoint en riesgo
    public List<Pedido> obtenerEnRiesgo() {
        return pedidos.stream()
                .filter(p -> {
                    if (p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.DESPACHADO) return false;
                    Producto producto = obtenerProducto(p.getProductoId());
                    return producto == null || producto.getCantidad() < p.getCantidad() || p.getEstado() == EstadoPedido.PARCIALMENTE_RETENIDO;
                })
                .collect(Collectors.toList());
    }

    private Pedido buscarPorId(Long id) {
        return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    private Producto obtenerProducto(Long productoId) {
        return productoController.obtenerTodos().stream()
                .filter(p -> p.getId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    private long contarPorEstado(EstadoPedido estado) {
        return pedidos.stream().filter(p -> p.getEstado() == estado).count();
    }

    private int getPesoPrioridad(PrioridadPedido prioridad) {
        return switch (prioridad) {
            case URGENTE -> 1;
            case ALTA -> 2;
            case MEDIA -> 3;
            case BAJA -> 4;
        };
    }
}