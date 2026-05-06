package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materiais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private MaterialTipo nome;

    @Column(length = 500)
    private String descricao;
}
