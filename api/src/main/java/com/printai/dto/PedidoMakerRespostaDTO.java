package com.printai.dto;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMakerRespostaDTO {
    private Long id;
    private String status;
    private String tipoPedido;
    private Date dataPedido;
    private String clienteNome;
    private String clienteEmail;
    private String clienteTelefone;
    private String servicoNome;
    private String arquivo3D;
    private String descricaoNecessidade;
    private String material;
    private int quantidade;
    private String observacoes;
    private Long impressoraId;
    private String impressoraModelo;
}
