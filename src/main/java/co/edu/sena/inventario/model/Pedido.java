package co.edu.sena.inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private PrioridadPedido prioridad;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    // Constructor vacío obligatorio para JPA
    public Pedido() {
    }

    // Constructor con parámetros
    public Pedido(Long id, String cliente, Producto producto, Integer cantidad, PrioridadPedido prioridad) {
        this.id = id;
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.prioridad = prioridad;
        this.estado = EstadoPedido.PENDIENTE; // Estado por defecto
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    // Helper para obtener el ID del producto si se requiere
    public Long getProductoId() {
        return producto != null ? producto.getId() : null;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public PrioridadPedido getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadPedido prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}