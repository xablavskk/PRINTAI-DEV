package com.printai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoDTO {
    private Long id;
    private String clienteNome;
    private int nota;
    private String comentario;
}
