package com.printai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (ex: SP)")
    private String estado;

    private String cep;
    private String pais = "Brasil";
}
