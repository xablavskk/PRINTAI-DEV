package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "avaliacoes")

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int nota;

    @Column(length = 1000)
    private String comentario;

    @Column(name = "data_avaliacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAvaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    public void operation0() {
        System.out.println("Executando operação genérica de avaliação.");
    }
}
