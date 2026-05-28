package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.CadastroMakerRequestDTO;
import com.printai.dto.CadastroMakerRespostaDTO;
import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MakerController.class)
class MakerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private UsuarioService usuarioService;

    private CadastroMakerRequestDTO dtoPadrao() {
        return CadastroMakerRequestDTO.builder()
                .nome("Mario Maker")
                .email("mario@printai.com")
                .senha("senha123")
                .telefone("11999990000")
                .documentoCpfCnpj("12345678900")
                .logradouro("Rua das Impressoras")
                .numero("42")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310100")
                .pais("Brasil")
                .build();
    }

    // ===================== POST /api/maker/cadastro =====================

    @Test
    @DisplayName("Cadastro válido sem serviços deve retornar 201 com dados do maker")
    void cadastro_dadosValidos_retorna201() throws Exception {
        CadastroMakerRespostaDTO resposta = CadastroMakerRespostaDTO.builder()
                .id(1L)
                .nome("Mario Maker")
                .email("mario@printai.com")
                .cidade("São Paulo")
                .estado("SP")
                .latitude(-23.55)
                .longitude(-46.63)
                .statusAprovacao(true)
                .totalServicos(0)
                .mensagem("Cadastro realizado com sucesso!")
                .build();

        when(usuarioService.cadastrarMaker(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mario Maker"))
                .andExpect(jsonPath("$.email").value("mario@printai.com"))
                .andExpect(jsonPath("$.statusAprovacao").value(true))
                .andExpect(jsonPath("$.totalServicos").value(0))
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("Cadastro com serviços deve retornar 201 com totalServicos correto")
    void cadastro_comServicos_retorna201ComTotalServicos() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setServicos(List.of(
                ServicoImpressaoRequestDTO.builder()
                        .nome("Impressão FDM").precoBase(80.0)
                        .tipoId(1L).tecnologia("FDM").material("PLA")
                        .suportaPrototipos(true).build()
        ));

        CadastroMakerRespostaDTO resposta = CadastroMakerRespostaDTO.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .statusAprovacao(true).totalServicos(1)
                .mensagem("Cadastro realizado com sucesso! 1 serviço(s) cadastrado(s).")
                .build();

        when(usuarioService.cadastrarMaker(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalServicos").value(1))
                .andExpect(jsonPath("$.mensagem").value("Cadastro realizado com sucesso! 1 serviço(s) cadastrado(s)."));
    }

    @Test
    @DisplayName("Cadastro sem nome deve retornar 400 com erro de validação")
    void cadastro_semNome_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setNome("");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Cadastro com e-mail inválido deve retornar 400")
    void cadastro_emailInvalido_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setEmail("nao-e-um-email");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("Cadastro com senha curta deve retornar 400")
    void cadastro_senhaCurta_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setSenha("123");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senha").exists());
    }

    @Test
    @DisplayName("E-mail duplicado deve retornar 409")
    void cadastro_emailDuplicado_retorna409() throws Exception {
        when(usuarioService.cadastrarMaker(any()))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado: mario@printai.com"));

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("E-mail já cadastrado: mario@printai.com"));
    }

    @Test
    @DisplayName("Cadastro sem logradouro deve retornar 400")
    void cadastro_semLogradouro_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setLogradouro("");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.logradouro").exists());
    }

    @Test
    @DisplayName("Cadastro com estado inválido (mais de 2 chars) deve retornar 400")
    void cadastro_estadoInvalido_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoPadrao();
        dto.setEstado("SPP");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").exists());
    }
}
