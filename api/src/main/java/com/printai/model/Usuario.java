package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Usuario {

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

    public void realizarCadastro() {
        System.out.println("Iniciando processo de cadastro para o usuário: " + this.nome);
    }

    public boolean realizarLogin() {
        System.out.println("Realizando login para o usuário: " + this.email);
        return true;
    }
}
