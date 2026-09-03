package co.edu.sena.inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private Long productoId;
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private PrioridadPedido prioridad;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    // Constructor vacío obligatorio para JPA
    public Pedido() {}

    // Constructor con parámetros
    public Pedido(Long id, String cliente, Long productoId, Integer cantidad, PrioridadPedido prioridad) {
        this.id = id;
        this.cliente = cliente;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.prioridad = prioridad;
        this.estado = EstadoPedido.PENDIENTE; // Estado por defecto
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public PrioridadPedido getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadPedido prioridad) { this.prioridad = prioridad; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
}