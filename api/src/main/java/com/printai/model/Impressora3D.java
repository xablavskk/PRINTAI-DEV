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

    @Column
    private String material;

    @Column(length = 500)
    private String descricao;

    @Column
    private String tecnologia; // FDM, SLA, etc.

    @Column
    private boolean disponibilidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    public boolean verificarDisponibilidade() {
        // Lógica para verificar se a impressora está disponível
        return this.disponibilidade;
    }
}
