package com.printai.dto;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoRespostaDTO {
    private Long id;
    private String clienteNome;
    private String makerNome;
    private int nota;
    private String comentario;
    private Date dataAvaliacao;
    private String mensagem;
}
