package com.printai.dto;

import lombok.Data;


@Data
public class BuscaServicoRequestDTO {
    private String tecnologia;
    private String material;
    private String modelo;

    private String buscaSimplificada;
}
