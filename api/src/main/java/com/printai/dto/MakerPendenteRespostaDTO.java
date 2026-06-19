package com.printai.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerPendenteRespostaDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String documentoCpfCnpj;
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private Boolean statusAprovacao;
    private Integer totalServicos;
    private List<ServicoMakerRespostaDTO> servicos;
}
