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
    private String condicoesServico;
    private String volumeImpressao;
    private UsuarioRespostaDTO maker;
    
    // Detalhes estendidos para o modal
    private java.util.List<AvaliacaoDTO> avaliacoes;
    private Double mediaAvaliacao;
    private Integer totalAvaliacoes;
}
