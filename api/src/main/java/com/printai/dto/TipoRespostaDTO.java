package com.printai.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoRespostaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private List<String> tecnologias;
}
