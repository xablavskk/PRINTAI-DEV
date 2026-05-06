package com.printai.dto;

import com.printai.model.TecnologiaTipo;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpressoraRespostaDTO {
    private Long id;
    private String modelo;
    private String material;
    private String tipoNome;
    private String tipoDescricao;
    private List<TecnologiaTipo> tecnologias;
    private String descricao;
    private boolean disponibilidade;
    private UsuarioRespostaDTO maker;
}
