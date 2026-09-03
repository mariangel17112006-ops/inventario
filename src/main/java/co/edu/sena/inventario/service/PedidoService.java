package co.edu.sena.inventario.service;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PrioridadPedido;
import co.edu.sena.inventario.model.Producto;
import co.edu.sena.inventario.repository.PedidoRepository;
import co.edu.sena.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    // Nivel 1 - Crear pedido
    public Pedido crearPedido(Pedido nuevoPedido) {
        Long productoId = obtenerProductoIdDePedido(nuevoPedido);
        Producto producto = (productoId != null) ? productoRepository.findById(productoId).orElse(null) : null;

        if (producto == null) {
            throw new IllegalArgumentException("PRODUCTO_NO_EXISTE");
        }

        if (nuevoPedido.getCliente() == null || nuevoPedido.getCliente().trim().isEmpty() ||
                nuevoPedido.getCantidad() == null || nuevoPedido.getCantidad() <= 0 ||
                nuevoPedido.getPrioridad() == null) {
            throw new IllegalArgumentException("DATOS_INVALIDOS");
        }

        // Asigna la relación JPA del producto
        nuevoPedido.setProducto(producto);
        nuevoPedido.setEstado(EstadoPedido.PENDIENTE);

        // Si no hay stock suficiente, entra retenido
        if (nuevoPedido.getCantidad() > producto.getCantidad()) {
            nuevoPedido.setEstado(EstadoPedido.PARCIALMENTE_RETENIDO);
        }

        return pedidoRepository.save(nuevoPedido);
    }

    // Nivel 2 - Confirmar pedido (descuenta del stock)
    public Pedido confirmarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null)
            return null;

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }

        Producto producto = pedido.getProducto();
        if (producto == null || producto.getCantidad() < pedido.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para confirmar el pedido.");
        }

        producto.setCantidad(producto.getCantidad() - pedido.getCantidad());
        productoRepository.save(producto);

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    // Nivel 3 - Cancelar pedido (repone stock si ya estaba confirmado)
    public Pedido cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null)
            return null;

        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un pedido DESPACHADO o CANCELADO.");
        }

        if (pedido.getEstado() == EstadoPedido.CONFIRMADO) {
            Producto producto = pedido.getProducto();
            if (producto != null) {
                producto.setCantidad(producto.getCantidad() + pedido.getCantidad());
                productoRepository.save(producto);
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    // Nivel 4 - Despachar pedido
    public Pedido despacharPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null)
            return null;

        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden despachar pedidos CONFIRMADOS.");
        }

        pedido.setEstado(EstadoPedido.DESPACHADO);
        return pedidoRepository.save(pedido);
    }

    // Nivel 5 - Consultas
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE);
    }

    public List<Pedido> obtenerUrgentes() {
        return pedidoRepository.findAll().stream()
                .filter(p -> p.getPrioridad() == PrioridadPedido.URGENTE)
                .collect(Collectors.toList());
    }

    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Map<String, Object> obtenerResumen() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalPedidos", pedidos.size());
        resumen.put("pendientes", contarPorEstado(pedidos, EstadoPedido.PENDIENTE));
        resumen.put("confirmados", contarPorEstado(pedidos, EstadoPedido.CONFIRMADO));
        resumen.put("despachados", contarPorEstado(pedidos, EstadoPedido.DESPACHADO));
        resumen.put("cancelados", contarPorEstado(pedidos, EstadoPedido.CANCELADO));
        resumen.put("parcialmenteRetenidos", contarPorEstado(pedidos, EstadoPedido.PARCIALMENTE_RETENIDO));
        resumen.put("urgentes", pedidos.stream().filter(p -> p.getPrioridad() == PrioridadPedido.URGENTE).count());
        return resumen;
    }

    // Nivel 6 - Algoritmo de prioridad
    public Pedido obtenerSiguiente() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE).stream()
                .min(Comparator
                        .comparing(Pedido::getPrioridad, Comparator.comparingInt(this::getPesoPrioridad))
                        .thenComparing(Pedido::getId))
                .orElse(null);
    }

    // Nivel 7 - Endpoint en riesgo
    public List<Pedido> obtenerEnRiesgo() {
        return pedidoRepository.findAll().stream()
                .filter(p -> {
                    if (p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.DESPACHADO)
                        return false;
                    Producto producto = p.getProducto();
                    return producto == null || producto.getCantidad() < p.getCantidad()
                            || p.getEstado() == EstadoPedido.PARCIALMENTE_RETENIDO;
                })
                .collect(Collectors.toList());
    }

    private Long obtenerProductoIdDePedido(Pedido pedido) {
        if (pedido.getProducto() != null && pedido.getProducto().getId() != null) {
            return pedido.getProducto().getId();
        }
        return null;
    }

    private long contarPorEstado(List<Pedido> pedidos, EstadoPedido estado) {
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