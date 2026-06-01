package com.printai.controller;

import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ImpressoraRespostaDTO;
import com.printai.dto.ServicoRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.Perfil;
import com.printai.model.TecnologiaTipo;
import com.printai.service.Impressora3DService;
import com.printai.service.ServicoImpressaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuscaController.class)
class BuscaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicoImpressaoService servicoImpressaoService;

    @MockitoBean
    private Impressora3DService impressora3DService;

    // ===================== /busca/servicos =====================

    @Test
    @DisplayName("Buscar servicos sem filtros deve retornar lista")
    void buscarServicos_semFiltros_retornaLista() throws Exception {
        ServicoRespostaDTO servico = ServicoRespostaDTO.builder()
                .id(1L)
                .nome("Impressão FDM de Alta Precisão")
                .descricao("Serviço ideal para protótipos.")
                .material("PLA")
                .precoBase(50.0)
                .build();

        when(servicoImpressaoService.buscarServicos(any(BuscaServicoRequestDTO.class), any(), any()))
                .thenReturn(List.of(servico));

        mockMvc.perform(get("/api/busca/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Impressão FDM de Alta Precisão"))
                .andExpect(jsonPath("$[0].material").value("PLA"));
    }

    @Test
    @DisplayName("Buscar servicos sem resultados deve retornar lista vazia")
    void buscarServicos_semResultados_retornaListaVazia() throws Exception {
        when(servicoImpressaoService.buscarServicos(any(BuscaServicoRequestDTO.class), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/busca/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ===================== /busca/detalhe/{id} =====================

    @Test
    @DisplayName("Buscar detalhe por ID existente deve retornar servico")
    void buscarDetalhe_idExistente_retornaServico() throws Exception {
        UsuarioRespostaDTO maker = UsuarioRespostaDTO.builder()
                .id(1L)
                .nome("Adriano Maker")
                .perfil(Perfil.MAKER)
                .cidade("São Paulo")
                .estado("SP")
                .build();

        ServicoRespostaDTO servico = ServicoRespostaDTO.builder()
                .id(1L)
                .nome("Impressão FDM de Alta Precisão")
                .descricao("Serviço ideal para protótipos.")
                .material("PLA")
                .precoBase(50.0)
                .maker(maker)
                .mediaAvaliacao(4.5)
                .totalAvaliacoes(2)
                .build();

        when(servicoImpressaoService.buscarPorId(1L)).thenReturn(servico);

        mockMvc.perform(get("/api/busca/detalhe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Impressão FDM de Alta Precisão"))
                .andExpect(jsonPath("$.maker.nome").value("Adriano Maker"))
                .andExpect(jsonPath("$.maker.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.mediaAvaliacao").value(4.5));
    }

    @Test
    @DisplayName("Buscar detalhe com ID inexistente deve retornar 500 com mensagem de erro")
    void buscarDetalhe_idInexistente_retornaErro() throws Exception {
        when(servicoImpressaoService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Serviço não encontrado"));

        mockMvc.perform(get("/api/busca/detalhe/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro").exists());
    }

    // ===================== /busca/impressoras =====================

    @Test
    @DisplayName("Buscar impressoras sem filtros deve retornar lista")
    void buscarImpressoras_semFiltros_retornaLista() throws Exception {
        UsuarioRespostaDTO maker = UsuarioRespostaDTO.builder()
                .id(1L).nome("Adriano Maker").perfil(Perfil.MAKER)
                .cidade("São Paulo").estado("SP")
                .latitude(-23.550520).longitude(-46.633308)
                .build();

        ImpressoraRespostaDTO impressora = ImpressoraRespostaDTO.builder()
                .id(1L)
                .modelo("Ender 3")
                .material("PLA")
                .tecnologias(List.of(TecnologiaTipo.FDM))
                .disponibilidade(true)
                .maker(maker)
                .build();

        when(impressora3DService.buscarImpressoras(any(), nullable(Double.class), nullable(Double.class), nullable(Double.class)))
                .thenReturn(List.of(impressora));

        mockMvc.perform(get("/api/busca/impressoras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modelo").value("Ender 3"))
                .andExpect(jsonPath("$[0].material").value("PLA"))
                .andExpect(jsonPath("$[0].maker.cidade").value("São Paulo"));
    }

    @Test
    @DisplayName("Filtrar impressoras por tecnologia FDM deve retornar apenas FDM")
    void buscarImpressoras_filtroTecnologiaFDM_retornaApenasCompativel() throws Exception {
        ImpressoraRespostaDTO impressoraFDM = ImpressoraRespostaDTO.builder()
                .id(1L)
                .modelo("Ender 3")
                .material("PLA")
                .tecnologias(List.of(TecnologiaTipo.FDM))
                .disponibilidade(true)
                .build();

        when(impressora3DService.buscarImpressoras(any(), nullable(Double.class), nullable(Double.class), nullable(Double.class)))
                .thenReturn(List.of(impressoraFDM));

        mockMvc.perform(get("/api/busca/impressoras").param("tecnologia", "FDM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tecnologias[0]").value("FDM"));
    }

    @Test
    @DisplayName("Filtrar impressoras por tecnologia inexistente deve retornar lista vazia")
    void buscarImpressoras_filtroTecnologiaInexistente_retornaVazio() throws Exception {
        when(impressora3DService.buscarImpressoras(any(), nullable(Double.class), nullable(Double.class), nullable(Double.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/busca/impressoras").param("tecnologia", "SLA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Buscar impressoras com dados incompletos nao deve quebrar")
    void buscarImpressoras_dadosIncompletos_retornaSemErro() throws Exception {
        ImpressoraRespostaDTO impressoraSemMaterial = ImpressoraRespostaDTO.builder()
                .id(2L)
                .modelo("Anycubic Photon")
                .material(null)
                .disponibilidade(true)
                .build();

        when(impressora3DService.buscarImpressoras(any(), nullable(Double.class), nullable(Double.class), nullable(Double.class)))
                .thenReturn(List.of(impressoraSemMaterial));

        mockMvc.perform(get("/api/busca/impressoras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modelo").value("Anycubic Photon"))
                .andExpect(jsonPath("$[0].material").doesNotExist());
    }
}
