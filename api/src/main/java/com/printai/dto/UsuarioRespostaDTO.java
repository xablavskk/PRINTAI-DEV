package com.printai.dto;

import com.printai.model.Perfil;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRespostaDTO {
    private Long id;
    private String nome;
    private Perfil perfil;
    private Double latitude;
    private Double longitude;
    private String telefone;
}
