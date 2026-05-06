package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "impressoras_3d")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Impressora3D {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo tipo;

    @Column
    private String tecnologia;

    @Column(name = "volume_impressao")
    private String volumeImpressao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(length = 500)
    private String descricao;

    @Column
    private boolean disponibilidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Usuario maker;
}
