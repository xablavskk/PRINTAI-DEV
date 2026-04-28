package com.printai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpressoraRespostaDTO {
    private Long id;
    private String modelo;
    private String material;
    private String tecnologia;
    private String descricao;
    private boolean disponibilidade;
    private UsuarioRespostaDTO maker;
}
