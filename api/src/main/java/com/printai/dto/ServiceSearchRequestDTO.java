package com.printai.dto;

import lombok.Data;

@Data
public class ServiceSearchRequestDTO {
    // Busca Avançada
    private String technology;
    private String material;

    // Busca Simplificada
    private String simplifiedSearch; // Pode ser "peça pequena", "objeto decorativo", "protótipo"
}
