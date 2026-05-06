package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tipos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @OneToMany(mappedBy = "tipo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tecnologia> tecnologias;
}
