package com.printai.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroClienteRespostaDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private String mensagem;
}
