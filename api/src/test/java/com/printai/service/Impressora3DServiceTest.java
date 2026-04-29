package com.printai.service;

import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ImpressoraRespostaDTO;
import com.printai.model.Impressora3D;
import com.printai.model.Maker;
import com.printai.repository.Impressora3DRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Impressora3DServiceTest {

    @Mock
    private Impressora3DRepository impressora3DRepository;

    @InjectMocks
    private Impressora3DService impressora3DService;

    private Impressora3D impressoraFDM;
    private Impressora3D impressoraSLA;

    @BeforeEach
    void setUp() {
        // Criando um Maker de mentira (Mock) para as impressoras
        Maker maker = new Maker();
        maker.setId(1L);
        maker.setNome("Maker Teste");
        maker.setLatitude(-23.5505);
        maker.setLongitude(-46.6333);

        impressoraFDM = Impressora3D.builder()
                .id(1L)
                .modelo("Ender 3")
                .tecnologia("FDM")
                .material("PLA")
                .disponibilidade(true)
                .maker(maker)
                .build();

        impressoraSLA = Impressora3D.builder()
                .id(2L)
                .modelo("Anycubic Photon")
                .tecnologia("SLA")
                .material("Resina")
                .disponibilidade(true)
                .maker(maker)
                .build();
    }

    @Test
    @DisplayName("CT-04: Buscar sem informar filtros (Retorna todas)")
    void buscarSemFiltros() {
        // Quando o serviço for no banco buscar tudo, ele vai receber a nossa lista mockada
        when(impressora3DRepository.findAll()).thenReturn(Arrays.asList(impressoraFDM, impressoraSLA));

        BuscaServicoRequestDTO buscaDTO = new BuscaServicoRequestDTO();
        
        List<ImpressoraRespostaDTO> resultado = impressora3DService.buscarImpressoras(buscaDTO, null, null, null);

        assertEquals(2, resultado.size());
        assertEquals("Ender 3", resultado.get(0).getModelo());
        assertEquals("Anycubic Photon", resultado.get(1).getModelo());
    }

    @Test
    @DisplayName("CT-02: Filtrar impressoras por tecnologia (FDM)")
    void filtrarImpressorasPorTecnologia() {
        when(impressora3DRepository.findAll()).thenReturn(Arrays.asList(impressoraFDM, impressoraSLA));

        BuscaServicoRequestDTO buscaDTO = new BuscaServicoRequestDTO();
        buscaDTO.setTecnologia("FDM");
        
        List<ImpressoraRespostaDTO> resultado = impressora3DService.buscarImpressoras(buscaDTO, null, null, null);

        // Deve filtrar e trazer apenas 1 impressora
        assertEquals(1, resultado.size());
        assertEquals("FDM", resultado.get(0).getTecnologia());
        assertEquals("Ender 3", resultado.get(0).getModelo());
    }

    @Test
    @DisplayName("CT-05: Filtrar por tecnologia inexistente (SLA-X)")
    void filtrarPorTecnologiaInexistente() {
        when(impressora3DRepository.findAll()).thenReturn(Arrays.asList(impressoraFDM, impressoraSLA));

        BuscaServicoRequestDTO buscaDTO = new BuscaServicoRequestDTO();
        buscaDTO.setTecnologia("SLA-X"); // Tecnologia que não está na lista
        
        List<ImpressoraRespostaDTO> resultado = impressora3DService.buscarImpressoras(buscaDTO, null, null, null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CT-01: Consultar disponibilidade por material (PLA)")
    void consultarPorMaterial() {
        when(impressora3DRepository.findAll()).thenReturn(Arrays.asList(impressoraFDM, impressoraSLA));

        BuscaServicoRequestDTO buscaDTO = new BuscaServicoRequestDTO();
        buscaDTO.setMaterial("PLA");
        
        List<ImpressoraRespostaDTO> resultado = impressora3DService.buscarImpressoras(buscaDTO, null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("PLA", resultado.get(0).getMaterial());
        assertEquals("Ender 3", resultado.get(0).getModelo());
    }
}
