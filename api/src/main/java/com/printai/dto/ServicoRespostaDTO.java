package com.printai.dto;

import com.printai.model.TecnologiaTipo;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRespostaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String tipoNome;
    private String tipoDescricao;
    private List<TecnologiaTipo> tecnologias;
    private String material;
    private double precoBase;
    private String condicoesServico;
    private String volumeImpressao;
    private Double distanciaKm;
    private UsuarioRespostaDTO maker;
    private List<AvaliacaoDTO> avaliacoes;
    private Double mediaAvaliacao;
    private Integer totalAvaliacoes;
}
