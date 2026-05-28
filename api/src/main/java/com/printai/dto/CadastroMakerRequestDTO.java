package com.printai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroMakerRequestDTO {

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

    @NotBlank(message = "CPF ou CNPJ é obrigatório")
    private String documentoCpfCnpj;

    // Endereço para geocodificação via Nominatim
    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

    private String numero;
    private String complemento;
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (ex: SP)")
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    private String cep;

    private String pais = "Brasil";

    // Serviços de impressão iniciais (opcional)
    private java.util.List<ServicoImpressaoRequestDTO> servicos;
}
