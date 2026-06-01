package com.printai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtualizarStatusPedidoRequestDTO {

    @NotBlank(message = "Status é obrigatório")
    private String status;

    private Long impressoraId;
}
