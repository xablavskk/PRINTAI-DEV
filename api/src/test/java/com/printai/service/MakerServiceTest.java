package com.printai.service;

import com.printai.dto.*;
import com.printai.exception.RegraNegocioException;
import com.printai.model.*;
import com.printai.model.StatusPedido;
import com.printai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakerServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PedidoRepository pedidoRepository;

    @InjectMocks
    private MakerService makerService;

    private Usuario makerPadrao;
    private ServicoImpressao servicoPadrao;
    private Pedido pedidoPadrao;

    @BeforeEach
    void setUp() {
        makerPadrao = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .senha("senha123").perfil(Perfil.MAKER)
                .statusAprovacao(true)   // aprovado — pré-condição para usar o painel
                .build();

        servicoPadrao = ServicoImpressao.builder()
                .id(10L).nome("Impressão FDM").precoBase(80.0)
                .tecnologia("FDM").suportaPecasPequenas(true)
                .maker(makerPadrao)
                .build();

        Usuario clientePadrao = Usuario.builder()
                .id(2L).nome("Ana Cliente").email("ana@gmail.com")
                .telefone("11999991111").senha("senha456").perfil(Perfil.CLIENTE)
                .build();

        pedidoPadrao = Pedido.builder()
                .id(100L).status(StatusPedido.AGUARDANDO_ANALISE)
                .tipoPedido(TipoPedido.SEM_MODELO)
                .descricaoNecessidade("Peça de reposição").quantidade(1)
                .dataPedido(new Date())
                .cliente(clientePadrao).servico(servicoPadrao)
                .build();
    }

    // ── listarPedidos ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPedidos sem filtro deve retornar todos os pedidos do maker")
    void listarPedidos_semFiltro_retornaTodosPedidos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.findByServico_Maker_Id(1L)).thenReturn(List.of(pedidoPadrao));

        List<PedidoMakerRespostaDTO> resultado = makerService.listarPedidos(1L, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(100L);
        assertThat(resultado.get(0).getStatus()).isEqualTo("AGUARDANDO_ANALISE");
        assertThat(resultado.get(0).getClienteNome()).isEqualTo("Ana Cliente");
        assertThat(resultado.get(0).getServicoNome()).isEqualTo("Impressão FDM");
        verify(pedidoRepository).findByServico_Maker_Id(1L);
        verify(pedidoRepository, never()).findByServico_Maker_IdAndStatus(any(), any());
    }

    @Test
    @DisplayName("listarPedidos com filtro de status deve usar repositório filtrado")
    void listarPedidos_comFiltroStatus_usaRepositorioFiltrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.findByServico_Maker_IdAndStatus(1L, StatusPedido.APROVADO)).thenReturn(List.of());

        List<PedidoMakerRespostaDTO> resultado = makerService.listarPedidos(1L, "APROVADO");

        assertThat(resultado).isEmpty();
        verify(pedidoRepository).findByServico_Maker_IdAndStatus(1L, StatusPedido.APROVADO);
        verify(pedidoRepository, never()).findByServico_Maker_Id(any());
    }

    @Test
    @DisplayName("listarPedidos com maker inexistente deve lançar RegraNegocioException")
    void listarPedidos_makerNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> makerService.listarPedidos(99L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Maker não encontrado");
    }

    @Test
    @DisplayName("listarPedidos com usuário de perfil CLIENTE deve lançar RegraNegocioException")
    void listarPedidos_usuarioNaoEMaker_lancaExcecao() {
        Usuario cliente = Usuario.builder()
                .id(2L).nome("Ana").email("ana@test.com").senha("123456")
                .perfil(Perfil.CLIENTE).build();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> makerService.listarPedidos(2L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Acesso restrito para Makers");
    }

    @Test
    @DisplayName("listarPedidos com maker não aprovado deve lançar RegraNegocioException")
    void listarPedidos_makerNaoAprovado_lancaExcecao() {
        Usuario makerPendente = Usuario.builder()
                .id(3L).nome("Pendente").email("pendente@test.com").senha("123456")
                .perfil(Perfil.MAKER).statusAprovacao(null).build();
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(makerPendente));

        assertThatThrownBy(() -> makerService.listarPedidos(3L, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ainda não foi aprovada pelo administrador");
    }

    // ── atualizarStatusPedido ──────────────────────────────────────────────────

    @Test
    @DisplayName("atualizarStatusPedido válido deve alterar status e retornar DTO atualizado")
    void atualizarStatusPedido_valido_retornaPedidoAtualizado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.findByIdAndServico_Maker_Id(100L, 1L)).thenReturn(Optional.of(pedidoPadrao));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("APROVADO").build();

        PedidoMakerRespostaDTO resultado = makerService.atualizarStatusPedido(1L, 100L, dto);

        assertThat(resultado.getStatus()).isEqualTo("APROVADO");
        verify(pedidoRepository).save(argThat(p -> StatusPedido.APROVADO == p.getStatus()));
    }

    @Test
    @DisplayName("atualizarStatusPedido em pedido FINALIZADO deve lançar RegraNegocioException")
    void atualizarStatusPedido_pedidoFinalizado_lancaExcecao() {
        pedidoPadrao.setStatus(StatusPedido.FINALIZADO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.findByIdAndServico_Maker_Id(100L, 1L)).thenReturn(Optional.of(pedidoPadrao));

        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("EM_PRODUCAO").build();

        assertThatThrownBy(() -> makerService.atualizarStatusPedido(1L, 100L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já finalizado");
    }

    @Test
    @DisplayName("atualizarStatusPedido com pedido de outro maker deve lançar RegraNegocioException")
    void atualizarStatusPedido_pedidoDeOutroMaker_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.findByIdAndServico_Maker_Id(100L, 1L)).thenReturn(Optional.empty());

        AtualizarStatusPedidoRequestDTO dto = AtualizarStatusPedidoRequestDTO.builder()
                .status("APROVADO").build();

        assertThatThrownBy(() -> makerService.atualizarStatusPedido(1L, 100L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pertence a este Maker");
    }

}
