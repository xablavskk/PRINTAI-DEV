package com.printai.dto;

import com.printai.model.MaterialTipo;
import com.printai.model.TecnologiaTipo;
import lombok.Data;

@Data
public class BuscaServicoRequestDTO {
    private TecnologiaTipo tecnologia;
    // Filtro por material usando o enum MaterialTipo, alinhado com o diagrama de classes
    private MaterialTipo material;
    private String modelo;
    private String buscaSimplificada;
    private Double volumeMaximo;
}
