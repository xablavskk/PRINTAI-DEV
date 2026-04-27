package com.printai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRespostaDTO {
    private Long id;
    private String nome;
    private String tipo; // CLIENTE, MAKER, ADMIN
    private Double latitude;
    private Double longitude;
    private String telefone;
}
