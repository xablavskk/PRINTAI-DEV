package com.printai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AprovarMakerRequestDTO {

    @NotNull(message = "O campo 'aprovado' é obrigatório")
    private Boolean aprovado;

    private String motivoRejeicao;
}
