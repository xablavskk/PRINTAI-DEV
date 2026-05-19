package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicos_impressao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoImpressao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "preco_base")
    private double precoBase;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "condicoes_servico", length = 500)
    private String condicoesServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id")
    private Tipo tipo;

    @Column
    private String material;

    @Column(name = "suporta_pecas_pequenas")
    private boolean suportaPecasPequenas;

    @Column(name = "suporta_decorativos")
    private boolean suportaDecorativos;

    @Column(name = "suporta_prototipos")
    private boolean suportaPrototipos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Usuario maker;
}
