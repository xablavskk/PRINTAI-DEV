package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.AvaliacaoDTO;
import com.printai.dto.AvaliacaoRequestDTO;
import com.printai.dto.AvaliacaoRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.service.AvaliacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvaliacaoController.class)
class AvaliacaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private AvaliacaoService avaliacaoService;

    private AvaliacaoRequestDTO dtoPadrao() {
        return AvaliacaoRequestDTO.builder()
                .makerId(2L).nota(5).comentario("Excelente serviço!")
                .build();
    }

    private AvaliacaoRespostaDTO respostaPadrao() {
        return AvaliacaoRespostaDTO.builder()
                .id(10L).clienteNome("Ana Cliente").makerNome("Mario Maker")
                .nota(5).comentario("Excelente serviço!")
                .dataAvaliacao(new Date())
                .mensagem("Avaliação enviada com sucesso!")
                .build();
    }

    // ── POST /api/avaliacoes ───────────────────────────────────────────────────

    @Test
    @DisplayName("Avaliação válida deve retornar 201 com dados da avaliação")
    void avaliarMaker_valida_retorna201() throws Exception {
        when(avaliacaoService.criarAvaliacao(eq(1L), any())).thenReturn(respostaPadrao());

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.clienteNome").value("Ana Cliente"))
                .andExpect(jsonPath("$.makerNome").value("Mario Maker"))
                .andExpect(jsonPath("$.mensagem").value("Avaliação enviada com sucesso!"));
    }

    @Test
    @DisplayName("Avaliação sem header X-Cliente-Id deve retornar 400")
    void avaliarMaker_semHeader_retorna400() throws Exception {
        mockMvc.perform(post("/api/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Avaliação sem makerId deve retornar 400")
    void avaliarMaker_semMakerId_retorna400() throws Exception {
        AvaliacaoRequestDTO dto = AvaliacaoRequestDTO.builder()
                .nota(4).comentario("Bom serviço").build();

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.makerId").exists());
    }

    @Test
    @DisplayName("Avaliação com nota menor que 1 deve retornar 400")
    void avaliarMaker_notaMenorQueUm_retorna400() throws Exception {
        AvaliacaoRequestDTO dto = AvaliacaoRequestDTO.builder()
                .makerId(2L).nota(0).comentario("Comentário").build();

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nota").exists());
    }

    @Test
    @DisplayName("Avaliação com nota maior que 5 deve retornar 400")
    void avaliarMaker_notaMaiorQueCinco_retorna400() throws Exception {
        AvaliacaoRequestDTO dto = AvaliacaoRequestDTO.builder()
                .makerId(2L).nota(6).comentario("Comentário").build();

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nota").exists());
    }

    @Test
    @DisplayName("Avaliação sem comentário deve retornar 400")
    void avaliarMaker_semComentario_retorna400() throws Exception {
        AvaliacaoRequestDTO dto = AvaliacaoRequestDTO.builder()
                .makerId(2L).nota(3).comentario("").build();

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comentario").exists());
    }

    @Test
    @DisplayName("Avaliação duplicada deve retornar 400 com mensagem de erro")
    void avaliarMaker_duplicada_retorna400() throws Exception {
        when(avaliacaoService.criarAvaliacao(eq(1L), any()))
                .thenThrow(new RegraNegocioException("Você já avaliou este Maker"));

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Você já avaliou este Maker"));
    }

    @Test
    @DisplayName("Avaliação sem pedido finalizado deve retornar 400 com mensagem de erro")
    void avaliarMaker_semPedidoFinalizado_retorna400() throws Exception {
        when(avaliacaoService.criarAvaliacao(eq(1L), any()))
                .thenThrow(new RegraNegocioException("Você só pode avaliar um Maker após finalizar um pedido com ele"));

        mockMvc.perform(post("/api/avaliacoes")
                        .header("X-Cliente-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Você só pode avaliar um Maker após finalizar um pedido com ele"));
    }

    // ── GET /api/avaliacoes/maker ────────────────────────────────────────────

    @Test
    @DisplayName("Listar avaliações com header válido deve retornar 200 com lista")
    void listarAvaliacoesRecebidas_headerValido_retorna200() throws Exception {
        AvaliacaoDTO avaliacao = AvaliacaoDTO.builder()
                .id(1L).clienteNome("Ana Cliente").nota(5)
                .comentario("Excelente serviço!").dataAvaliacao(new Date())
                .build();
        when(avaliacaoService.listarAvaliacoesRecebidas(1L)).thenReturn(List.of(avaliacao));

        mockMvc.perform(get("/api/avaliacoes/maker")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nota").value(5))
                .andExpect(jsonPath("$[0].clienteNome").value("Ana Cliente"))
                .andExpect(jsonPath("$[0].comentario").value("Excelente serviço!"));
    }

    @Test
    @DisplayName("Listar avaliações sem header X-Maker-Id deve retornar 400")
    void listarAvaliacoesRecebidas_semHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/avaliacoes/maker"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Listar avaliações com maker inexistente deve retornar 400")
    void listarAvaliacoesRecebidas_makerNaoEncontrado_retorna400() throws Exception {
        when(avaliacaoService.listarAvaliacoesRecebidas(99L))
                .thenThrow(new RegraNegocioException("Maker não encontrado"));

        mockMvc.perform(get("/api/avaliacoes/maker")
                        .header("X-Maker-Id", "99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Maker não encontrado"));
    }
}
