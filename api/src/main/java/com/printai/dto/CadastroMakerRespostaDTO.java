package com.printai.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroMakerRespostaDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private Boolean statusAprovacao;
    private Integer totalServicos;
    private String mensagem;
}
