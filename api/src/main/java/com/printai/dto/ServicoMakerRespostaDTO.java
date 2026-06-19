package com.printai.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoMakerRespostaDTO {
    private Long id;
    private String nome;
    private double precoBase;
    private String descricao;
    private String condicoesServico;
    private Long tipoId;
    private String tipoNome;
    private String tecnologia;
    private String material;
    private boolean suportaPecasPequenas;
    private boolean suportaDecorativos;
    private boolean suportaPrototipos;
}
