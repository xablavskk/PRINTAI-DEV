package com.printai.repository;

import com.printai.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente_Id(Long clienteId);
    List<Pedido> findByServico_Maker_Id(Long makerId);
    List<Pedido> findByServico_Maker_IdAndStatus(Long makerId, String status);
    Optional<Pedido> findByIdAndServico_Maker_Id(Long id, Long makerId);
    boolean existsByCliente_IdAndServico_Maker_IdAndStatus(Long clienteId, Long makerId, String status);
}
