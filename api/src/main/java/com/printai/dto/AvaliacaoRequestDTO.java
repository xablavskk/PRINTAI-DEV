package com.printai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoRequestDTO {

    @NotNull(message = "Informe o Maker a ser avaliado")
    private Long makerId;

    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private int nota;

    @NotBlank(message = "Comentário é obrigatório")
    @Size(max = 500, message = "Comentário deve ter no máximo 500 caracteres")
    private String comentario;
}
