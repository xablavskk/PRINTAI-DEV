package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "makers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Maker extends Usuario {

    @Column(name = "status_aprovacao")
    private boolean statusAprovacao;

    @Column(name = "documento_cpf_cnpj")
    private String documentoCpfCnpj;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<ServicoImpressao> servicos;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<Impressora3D> impressoras;

    @OneToMany(mappedBy = "maker", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes;

    public void solicitarCadastro() {
        System.out.println("Maker " + this.getNome() + " solicitando cadastro na plataforma.");
        this.statusAprovacao = false;
    }

    public void manterServico(ServicoImpressao servico) {
        System.out.println("Atualizando serviço de impressão: " + servico.getNome());
    }

    public List<Avaliacao> consultarAvaliacoes() {
        return this.avaliacoes;
    }
}
