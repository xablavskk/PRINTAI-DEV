package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "pedidos")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_pedido")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPedido;

    @Column
    private String status;

    @Column(name = "arquivo_3d")
    private String arquivo3D;

    @Column(name = "valor_total")
    private double valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "impressora_id", nullable = false)
    private Impressora3D impressora;

    public void atualizarStatus(String novoStatus) {
        this.status = novoStatus;
    }
}
