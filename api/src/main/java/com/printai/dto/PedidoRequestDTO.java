package com.printai.dto;

import com.printai.model.TipoPedido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {
    @NotNull(message = "O ID do serviço de impressão é obrigatório")
    private Long servicoId;

    @NotNull(message = "O tipo do pedido é obrigatório")
    private TipoPedido tipoPedido;

    private String arquivo3D;
    private String descricaoNecessidade;
    private String material;

    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private int quantidade = 1;

    private String observacoes;
}
