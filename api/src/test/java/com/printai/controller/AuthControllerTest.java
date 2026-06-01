package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.LoginRequestDTO;
import com.printai.dto.LoginRespostaDTO;
import com.printai.exception.GlobalExceptionHandler;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Perfil;
import com.printai.service.AuthService;
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

@WebMvcTest({AuthController.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private AuthService authService;

    @Test
    @DisplayName("Login válido deve retornar 200 com dados de sessão")
    void login_valido_retorna200() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder().email("joao@printai.com").senha("senha123").build();
        LoginRespostaDTO resposta = LoginRespostaDTO.builder()
                .id(1L).nome("João Cliente").email("joao@printai.com").perfil(Perfil.CLIENTE)
                .build();

        when(authService.login(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Cliente"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));
    }

    @Test
    @DisplayName("Login com credenciais inválidas deve retornar 400")
    void login_credenciaisInvalidas_retorna400() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder().email("joao@printai.com").senha("senhaErrada").build();

        when(authService.login(any())).thenThrow(new RegraNegocioException("E-mail ou senha incorretos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("E-mail ou senha incorretos"));
    }
}
