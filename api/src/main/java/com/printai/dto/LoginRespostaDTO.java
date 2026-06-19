package com.printai.dto;

import com.printai.model.Perfil;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRespostaDTO {
    private Long id;
    private String nome;
    private String email;
    private Perfil perfil;
    private String cidade;
    private String estado;
}
