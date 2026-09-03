package co.edu.sena.inventario.repository;

import co.edu.sena.inventario.model.Pedido;
import co.edu.sena.inventario.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Consultas derivadas requeridas en el Boss 2
    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteContainingIgnoreCase(String cliente);
}