package com.printai.controller;

import com.printai.dto.ServicoRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.Perfil;
import com.printai.service.ServicoImpressaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisualizarDetalhesController.class)
class VisualizarDetalhesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicoImpressaoService servicoImpressaoService;

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
}
