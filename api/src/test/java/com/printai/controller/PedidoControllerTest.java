package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.PedidoRequestDTO;
import com.printai.dto.PedidoRespostaDTO;
import com.printai.exception.GlobalExceptionHandler;
import com.printai.exception.RegraNegocioException;
import com.printai.model.TipoPedido;
import com.printai.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest({PedidoController.class, GlobalExceptionHandler.class})
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private PedidoService pedidoService;

    @Test
    @DisplayName("Pedido válido deve retornar 201 com dados do pedido")
    void criarPedido_valido_retorna201() throws Exception {
        PedidoRequestDTO request = PedidoRequestDTO.builder()
                .servicoId(1L)
                .tipoPedido(TipoPedido.COM_MODELO)
                .arquivo3D("modelo.stl")
                .material("PLA")
                .quantidade(2)
                .build();

        PedidoRespostaDTO resposta = PedidoRespostaDTO.builder()
                .id(100L)
                .status("AGUARDANDO_ANALISE")
                .tipoPedido(TipoPedido.COM_MODELO)
                .servicoNome("Serviço PLA Estiloso")
                .makerNome("José Maker")
                .mensagem("Pedido solicitado com sucesso! Aguarde a análise do Maker.")
                .build();

        when(pedidoService.criarPedido(eq(1L), any())).thenReturn(resposta);

        mockMvc.perform(post("/api/pedidos")
                        .header("X-Cliente-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_ANALISE"))
                .andExpect(jsonPath("$.mensagem").exists());
    }


    @Test
    @DisplayName("Pedido sem header X-Cliente-Id deve retornar 400")
    void criarPedido_semHeaderCliente_retorna400() throws Exception {
        PedidoRequestDTO request = PedidoRequestDTO.builder()
                .servicoId(1L)
                .tipoPedido(TipoPedido.COM_MODELO)
                .build();

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
