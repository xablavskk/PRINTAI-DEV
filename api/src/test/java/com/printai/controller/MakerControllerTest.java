package com.printai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.printai.dto.*;
import com.printai.exception.RegraNegocioException;
import com.printai.service.MakerService;
import com.printai.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MakerController.class)
class MakerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UsuarioService usuarioService;
    @MockitoBean private MakerService makerService;

    // ── Helpers ────────────────────────────────────────────────────────────────

    private CadastroMakerRequestDTO dtoCadastroPadrao() {
        return CadastroMakerRequestDTO.builder()
                .nome("Mario Maker").email("mario@printai.com")
                .senha("senha123").telefone("11999990000")
                .documentoCpfCnpj("12345678900")
                .logradouro("Rua das Impressoras").numero("42")
                .cidade("São Paulo").estado("SP").cep("01310100").pais("Brasil")
                .build();
    }

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

    private PedidoMakerRespostaDTO respostaPedidoPadrao() {
        return PedidoMakerRespostaDTO.builder()
                .id(100L).status("AGUARDANDO_ANALISE")
                .tipoPedido("SEM_MODELO").dataPedido(new Date())
                .clienteNome("Ana Cliente").clienteEmail("ana@gmail.com")
                .servicoNome("Impressão FDM").quantidade(1)
                .build();
    }

    // ── POST /api/maker/cadastro ───────────────────────────────────────────────

    @Test
    @DisplayName("Cadastro válido sem serviços deve retornar 201 com dados do maker")
    void cadastro_dadosValidos_retorna201() throws Exception {
        CadastroMakerRespostaDTO resposta = CadastroMakerRespostaDTO.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .cidade("São Paulo").estado("SP")
                .latitude(-23.55).longitude(-46.63)
                .statusAprovacao(true).totalServicos(0)
                .mensagem("Cadastro realizado com sucesso!")
                .build();

        when(usuarioService.cadastrarMaker(any())).thenReturn(resposta);

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoCadastroPadrao())))
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
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
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
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
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
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
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
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
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
                        .content(objectMapper.writeValueAsString(dtoCadastroPadrao())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("E-mail já cadastrado: mario@printai.com"));
    }

    @Test
    @DisplayName("Cadastro sem logradouro deve retornar 400")
    void cadastro_semLogradouro_retorna400() throws Exception {
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
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
        CadastroMakerRequestDTO dto = dtoCadastroPadrao();
        dto.setEstado("SPP");

        mockMvc.perform(post("/api/maker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").exists());
    }

    // ── GET /api/maker/pedidos ─────────────────────────────────────────────────

    @Test
    @DisplayName("Listar pedidos com header válido deve retornar 200 com lista")
    void listarPedidos_headerValido_retorna200() throws Exception {
        when(makerService.listarPedidos(1L, null)).thenReturn(List.of(respostaPedidoPadrao()));

        mockMvc.perform(get("/api/maker/pedidos")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO_ANALISE"))
                .andExpect(jsonPath("$[0].clienteNome").value("Ana Cliente"));
    }

    @Test
    @DisplayName("Listar pedidos com filtro de status deve retornar 200")
    void listarPedidos_comFiltroStatus_retorna200() throws Exception {
        when(makerService.listarPedidos(1L, "APROVADO")).thenReturn(List.of());

        mockMvc.perform(get("/api/maker/pedidos")
                        .header("X-Maker-Id", "1")
                        .param("status", "APROVADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Listar pedidos sem header X-Maker-Id deve retornar 400")
    void listarPedidos_semHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/maker/pedidos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Listar pedidos com maker inexistente deve retornar 400")
    void listarPedidos_makerNaoEncontrado_retorna400() throws Exception {
        when(makerService.listarPedidos(eq(99L), any()))
                .thenThrow(new RegraNegocioException("Maker não encontrado"));

        mockMvc.perform(get("/api/maker/pedidos")
                        .header("X-Maker-Id", "99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Maker não encontrado"));
    }

    // ── PATCH /api/maker/pedidos/{id}/status ──────────────────────────────────

    @Test
    @DisplayName("Atualizar status de pedido válido deve retornar 200 com pedido atualizado")
    void atualizarStatusPedido_valido_retorna200() throws Exception {
        PedidoMakerRespostaDTO resposta = respostaPedidoPadrao();
        resposta.setStatus("APROVADO");
        when(makerService.atualizarStatusPedido(eq(1L), eq(100L), any())).thenReturn(resposta);

        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("APROVADO").build();

        mockMvc.perform(patch("/api/maker/pedidos/100/status")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    @Test
    @DisplayName("Atualizar status sem campo status deve retornar 400")
    void atualizarStatusPedido_semStatus_retorna400() throws Exception {
        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("").build();

        mockMvc.perform(patch("/api/maker/pedidos/100/status")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @DisplayName("Atualizar status de pedido já finalizado deve retornar 400")
    void atualizarStatusPedido_pedidoFinalizado_retorna400() throws Exception {
        when(makerService.atualizarStatusPedido(eq(1L), eq(100L), any()))
                .thenThrow(new RegraNegocioException("Pedido já finalizado não pode ser alterado"));

        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("EM_PRODUCAO").build();

        mockMvc.perform(patch("/api/maker/pedidos/100/status")
                        .header("X-Maker-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Pedido já finalizado não pode ser alterado"));
    }

    // ── GET /api/maker/servicos ────────────────────────────────────────────────

    @Test
    @DisplayName("Listar serviços com header válido deve retornar 200 com lista")
    void listarServicos_headerValido_retorna200() throws Exception {
        when(makerService.listarServicos(1L)).thenReturn(List.of(respostaServicoPadrao()));

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
        when(makerService.criarServico(eq(1L), any())).thenReturn(respostaServicoPadrao());

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
        when(makerService.editarServico(eq(1L), eq(10L), any())).thenReturn(resposta);

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
        when(makerService.editarServico(eq(1L), eq(10L), any()))
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
        doNothing().when(makerService).removerServico(1L, 10L);

        mockMvc.perform(delete("/api/maker/servicos/10")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Remover serviço inexistente deve retornar 400")
    void removerServico_servicoNaoEncontrado_retorna400() throws Exception {
        doThrow(new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"))
                .when(makerService).removerServico(1L, 99L);

        mockMvc.perform(delete("/api/maker/servicos/99")
                        .header("X-Maker-Id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Serviço não encontrado ou não pertence a este Maker"));
    }

    // ── GET /api/maker/avaliacoes ──────────────────────────────────────────────

    @Test
    @DisplayName("Listar avaliações com header válido deve retornar 200 com lista")
    void listarAvaliacoes_headerValido_retorna200() throws Exception {
        AvaliacaoDTO avaliacao = AvaliacaoDTO.builder()
                .id(1L).clienteNome("Ana Cliente").nota(5)
                .comentario("Excelente serviço!").dataAvaliacao(new Date())
                .build();
        when(makerService.listarAvaliacoes(1L)).thenReturn(List.of(avaliacao));

        mockMvc.perform(get("/api/maker/avaliacoes")
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
    void listarAvaliacoes_semHeader_retorna400() throws Exception {
        mockMvc.perform(get("/api/maker/avaliacoes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Listar avaliações com maker inexistente deve retornar 400")
    void listarAvaliacoes_makerNaoEncontrado_retorna400() throws Exception {
        when(makerService.listarAvaliacoes(99L))
                .thenThrow(new RegraNegocioException("Maker não encontrado"));

        mockMvc.perform(get("/api/maker/avaliacoes")
                        .header("X-Maker-Id", "99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Maker não encontrado"));
    }
}
