package com.printai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoImpressaoRequestDTO {

    @NotBlank(message = "Nome do serviço é obrigatório")
    private String nome;

    @Positive(message = "Preço base deve ser maior que zero")
    private double precoBase;

    private String descricao;
    private String condicoesServico;

    private Long tipoId;

    private String tecnologia;
    private String material;

    private boolean suportaPecasPequenas;
    private boolean suportaDecorativos;
    private boolean suportaPrototipos;
}
