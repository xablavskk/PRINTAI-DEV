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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pedido", nullable = false)
    private TipoPedido tipoPedido;

    /** Preenchido quando tipoPedido = COM_MODELO (nome/URL do arquivo STL/OBJ) */
    @Column(name = "arquivo_3d")
    private String arquivo3D;

    /** Preenchido quando tipoPedido = SEM_MODELO */
    @Column(name = "descricao_necessidade", length = 2000)
    private String descricaoNecessidade;

    @Column(length = 100)
    private String material;

    @Column(nullable = false)
    @Builder.Default
    private int quantidade = 1;

    @Column(length = 1000)
    private String observacoes;

    @Column(name = "valor_total")
    private double valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    /** Serviço de impressão para o qual o pedido foi solicitado */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoImpressao servico;

    /**
     * Impressora definida pelo Maker após análise — pode ficar null até o Maker aceitar o pedido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "impressora_id")
    private Impressora3D impressora;
}
