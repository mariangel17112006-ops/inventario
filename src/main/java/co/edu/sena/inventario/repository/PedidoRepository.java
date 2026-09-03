package co.edu.sena.inventario.repository;

import co.edu.sena.inventario.model.EstadoPedido;
import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.PrioridadPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Consulta derivada 1: Buscar por prioridad
    List<Pedido> findByPrioridad(PrioridadPedido prioridad);

    // Consulta derivada 2: Buscar por cliente ignorando mayúsculas/minúsculas
    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

    // Consulta por estado
    List<Pedido> findByEstado(EstadoPedido estado);
}