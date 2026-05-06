package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tecnologias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tecnologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TecnologiaTipo nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo tipo;
}
