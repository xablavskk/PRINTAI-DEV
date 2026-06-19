package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.CadastroClienteRequestDTO;
import com.printai.dto.CadastroClienteRespostaDTO;
import com.printai.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private UsuarioService usuarioService;

    private CadastroClienteRequestDTO dtoPadrao() {
        return CadastroClienteRequestDTO.builder()
                .nome("João Cliente")
                .email("joao@printai.com")
                .senha("senha123")
                .telefone("11988880000")
                .build();
    }

    @Test
    @DisplayName("Cadastro válido deve retornar 201 com dados do cliente")
    void cadastro_dadosValidos_retorna201() throws Exception {
        CadastroClienteRespostaDTO resposta = CadastroClienteRespostaDTO.builder()
                .id(1L).nome("João Cliente").email("joao@printai.com")
                .mensagem("Cadastro realizado com sucesso!")
                .build();

        when(usuarioService.cadastrarCliente(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/cliente/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Cliente"))
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("Cadastro sem nome deve retornar 400")
    void cadastro_semNome_retorna400() throws Exception {
        CadastroClienteRequestDTO dto = dtoPadrao();
        dto.setNome("");

        mockMvc.perform(post("/api/cliente/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    @DisplayName("Cadastro com e-mail inválido deve retornar 400")
    void cadastro_emailInvalido_retorna400() throws Exception {
        CadastroClienteRequestDTO dto = dtoPadrao();
        dto.setEmail("nao-e-email");

        mockMvc.perform(post("/api/cliente/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("E-mail duplicado deve retornar 409")
    void cadastro_emailDuplicado_retorna409() throws Exception {
        when(usuarioService.cadastrarCliente(any()))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado: joao@printai.com"));

        mockMvc.perform(post("/api/cliente/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoPadrao())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Cadastro com senha curta deve retornar 400")
    void cadastro_senhaCurta_retorna400() throws Exception {
        CadastroClienteRequestDTO dto = dtoPadrao();
        dto.setSenha("123");

        mockMvc.perform(post("/api/cliente/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senha").exists());
    }
}
