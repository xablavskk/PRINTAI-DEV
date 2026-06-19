package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.dto.ServicoMakerRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.service.ManterServicosImpressaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManterServicosImpressaoController.class)
class ManterServicosImpressaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ManterServicosImpressaoService manterServicosImpressaoService;

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ServicoImpressaoRequestDTO dtoServicoPadrao() {
        return ServicoImpressaoRequestDTO.builder()
                .nome("Impressão FDM").precoBase(80.0)
                .tecnologia("FDM").suportaPrototipos(true)
                .build();
    }

    private ServicoMakerRespostaDTO respostaServicoPadrao() {
        return ServicoMakerRespostaDTO.builder()
                .id(10L).nome("Impressão FDM").precoBase(80.0)
                .tecnologia("FDM").suportaPrototipos(true)
                .build();
    }

    // ── GET /api/maker/servicos ────────────────────────────────────────────────

    @Test
    @DisplayName("Listar serviços com header válido deve retornar 200 com lista")
    void listarServicos_headerValido_retorna200() throws Exception {
        when(manterServicosImpressaoService.listarServicos(1L)).thenReturn(List.of(respostaServicoPadrao()));

        mockMvc.perform(get("/api/maker/servicos")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nome").value("Impressão FDM"))
                .andExpect(jsonPath("$[0].precoBase").value(80.0));
    }

    @Test
    @DisplayName("Listar serviços sem header X-Maker-Id deve retornar 400")
    void listarServicos_semHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/maker/servicos"))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/maker/servicos ───────────────────────────────────────────────

    @Test
    @DisplayName("Criar serviço com dados válidos deve retornar 201 com serviço criado")
    void criarServico_dadosValidos_retorna201() throws Exception {
        when(manterServicosImpressaoService.criarServico(eq(1L), any())).thenReturn(respostaServicoPadrao());

        mockMvc.perform(post("/api/maker/servicos")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoServicoPadrao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Impressão FDM"));
    }

    @Test
    @DisplayName("Criar serviço sem nome deve retornar 400")
    void criarServico_semNome_retorna400() throws Exception {
        ServicoImpressaoRequestDTO dto = dtoServicoPadrao();
        dto.setNome("");

        mockMvc.perform(post("/api/maker/servicos")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Criar serviço com preço zero deve retornar 400")
    void criarServico_precoZero_retorna400() throws Exception {
        ServicoImpressaoRequestDTO dto = ServicoImpressaoRequestDTO.builder()
                .nome("Serviço Inválido").precoBase(0.0).build();

        mockMvc.perform(post("/api/maker/servicos")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precoBase").exists());
    }

    // ── PUT /api/maker/servicos/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("Editar serviço com dados válidos deve retornar 200 com serviço atualizado")
    void editarServico_dadosValidos_retorna200() throws Exception {
        ServicoMakerRespostaDTO resposta = respostaServicoPadrao();
        resposta.setNome("Impressão FDM Premium");
        when(manterServicosImpressaoService.editarServico(eq(1L), eq(10L), any())).thenReturn(resposta);

        mockMvc.perform(put("/api/maker/servicos/10")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoServicoPadrao())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Impressão FDM Premium"));
    }

    @Test
    @DisplayName("Editar serviço de outro maker deve retornar 400")
    void editarServico_servicoDeOutroMaker_retorna400() throws Exception {
        when(manterServicosImpressaoService.editarServico(eq(1L), eq(10L), any()))
                .thenThrow(new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"));

        mockMvc.perform(put("/api/maker/servicos/10")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoServicoPadrao())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Serviço não encontrado ou não pertence a este Maker"));
    }

    // ── DELETE /api/maker/servicos/{id} ───────────────────────────────────────

    @Test
    @DisplayName("Remover serviço válido deve retornar 204 sem corpo")
    void removerServico_valido_retorna204() throws Exception {
        doNothing().when(manterServicosImpressaoService).removerServico(1L, 10L);

        mockMvc.perform(delete("/api/maker/servicos/10")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Remover serviço inexistente deve retornar 400")
    void removerServico_servicoNaoEncontrado_retorna400() throws Exception {
        doThrow(new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"))
                .when(manterServicosImpressaoService).removerServico(1L, 99L);

        mockMvc.perform(delete("/api/maker/servicos/99")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Serviço não encontrado ou não pertence a este Maker"));
    }
}
