package com.printai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRespostaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String tecnologia;
    private String material;
    private double precoBase;
    private UsuarioRespostaDTO maker;
}
