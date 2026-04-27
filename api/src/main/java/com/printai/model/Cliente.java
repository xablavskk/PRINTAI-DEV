package com.printai.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes;

    public List<Impressora3D> buscarImpressoras() {
        System.out.println("Cliente " + this.getNome() + " buscando impressoras próximas.");
        return null; // A busca real é feita via Service/Controller
    }

    public Pedido solicitarPedido(Impressora3D impressora, String arquivo) {
        System.out.println("Solicitando pedido de impressão para: " + arquivo);
        return Pedido.builder()
                .cliente(this)
                .impressora(impressora)
                .arquivo3D(arquivo)
                .dataPedido(new java.util.Date())
                .status("PENDENTE")
                .build();
    }

    public void visualizarDetalhes(Pedido pedido) {
        System.out.println("Visualizando detalhes do pedido: " + pedido.getId());
    }

    public Avaliacao avaliarMaker(Maker maker, int nota, String comentario) {
        System.out.println("Avaliando Maker: " + maker.getNome());
        return Avaliacao.builder()
                .cliente(this)
                .maker(maker)
                .nota(nota)
                .comentario(comentario)
                .dataAvaliacao(new java.util.Date())
                .build();
    }
}
