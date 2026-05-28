package com.printai.dto;

import com.printai.model.TecnologiaTipo;
import lombok.Data;

@Data
public class BuscaServicoRequestDTO {
    private TecnologiaTipo tecnologia;
    private String material;
    private String modelo;
    private String buscaSimplificada;
    private Double volumeMaximo;
}
