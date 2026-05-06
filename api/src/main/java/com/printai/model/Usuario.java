package com.printai.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private String telefone;

    @Column
    private String cidade;

    @Column
    private String estado;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade")),
        @AttributeOverride(name = "estado", column = @Column(name = "endereco_estado")),
        @AttributeOverride(name = "cep",    column = @Column(name = "endereco_cep", length = 9)),
        @AttributeOverride(name = "pais",   column = @Column(name = "endereco_pais", length = 50))
    })
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(name = "status_aprovacao")
    private Boolean statusAprovacao;

    @Column(name = "documento_cpf_cnpj")
    private String documentoCpfCnpj;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<ServicoImpressao> servicos;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<Impressora3D> impressoras;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<AvaliacaoMaker> avaliacoesRecebidas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<AvaliacaoMaker> avaliacoesFeitas;
}
