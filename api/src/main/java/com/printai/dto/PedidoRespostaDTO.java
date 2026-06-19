package com.printai.dto;

import com.printai.model.TipoPedido;
import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRespostaDTO {
    private Long id;
    private String status;
    private TipoPedido tipoPedido;
    private Date dataPedido;
    private String servicoNome;
    private Long makerId;
    private String makerNome;
    private String mensagem;
}
