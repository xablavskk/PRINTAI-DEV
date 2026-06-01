package com.printai.service;

import com.printai.dto.AvaliacaoRequestDTO;
import com.printai.dto.AvaliacaoRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.AvaliacaoMaker;
import com.printai.model.Perfil;
import com.printai.model.StatusPedido;
import com.printai.model.Usuario;
import com.printai.repository.AvaliacaoRepository;
import com.printai.repository.PedidoRepository;
import com.printai.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AvaliacaoRepository avaliacaoRepository;
    @Mock private PedidoRepository pedidoRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Usuario clientePadrao;
    private Usuario makerPadrao;
    private AvaliacaoRequestDTO dtoPadrao;

    @BeforeEach
    void setUp() {
        clientePadrao = Usuario.builder()
                .id(1L).nome("Ana Cliente").email("ana@gmail.com")
                .senha("senha123").perfil(Perfil.CLIENTE)
                .build();

        makerPadrao = Usuario.builder()
                .id(2L).nome("Mario Maker").email("mario@printai.com")
                .senha("senha456").perfil(Perfil.MAKER)
                .build();

        dtoPadrao = AvaliacaoRequestDTO.builder()
                .makerId(2L).nota(5).comentario("Excelente serviço!")
                .build();
    }

    @Test
    @DisplayName("criarAvaliacao válida deve persistir e retornar DTO com mensagem de sucesso")
    void criarAvaliacao_valida_salvaERetorna() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clientePadrao));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.existsByCliente_IdAndServico_Maker_IdAndStatus(1L, 2L, StatusPedido.FINALIZADO))
                .thenReturn(true);
        when(avaliacaoRepository.existsByCliente_IdAndMaker_Id(1L, 2L)).thenReturn(false);
        when(avaliacaoRepository.save(any(AvaliacaoMaker.class))).thenAnswer(inv -> {
            AvaliacaoMaker a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });

        AvaliacaoRespostaDTO resultado = avaliacaoService.criarAvaliacao(1L, dtoPadrao);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getComentario()).isEqualTo("Excelente serviço!");
        assertThat(resultado.getClienteNome()).isEqualTo("Ana Cliente");
        assertThat(resultado.getMakerNome()).isEqualTo("Mario Maker");
        assertThat(resultado.getMensagem()).contains("sucesso");
        verify(avaliacaoRepository).save(any(AvaliacaoMaker.class));
    }

    @Test
    @DisplayName("criarAvaliacao com cliente inexistente deve lançar RegraNegocioException")
    void criarAvaliacao_clienteNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(99L, dtoPadrao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    @Test
    @DisplayName("criarAvaliacao com maker inexistente deve lançar RegraNegocioException")
    void criarAvaliacao_makerNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clientePadrao));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(1L, dtoPadrao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Maker não encontrado");
    }

    @Test
    @DisplayName("criarAvaliacao para usuário que não é Maker deve lançar RegraNegocioException")
    void criarAvaliacao_usuarioNaoEMaker_lancaExcecao() {
        Usuario naoMaker = Usuario.builder()
                .id(2L).nome("Outro Cliente").email("outro@gmail.com")
                .senha("abc123").perfil(Perfil.CLIENTE).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clientePadrao));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(naoMaker));

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(1L, dtoPadrao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não é um Maker");
    }

    @Test
    @DisplayName("criarAvaliacao sem pedido finalizado com o maker deve lançar RegraNegocioException")
    void criarAvaliacao_semPedidoFinalizado_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clientePadrao));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.existsByCliente_IdAndServico_Maker_IdAndStatus(1L, 2L, StatusPedido.FINALIZADO))
                .thenReturn(false);

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(1L, dtoPadrao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("após finalizar um pedido");
    }

    @Test
    @DisplayName("criarAvaliacao duplicada deve lançar RegraNegocioException")
    void criarAvaliacao_duplicada_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(clientePadrao));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(makerPadrao));
        when(pedidoRepository.existsByCliente_IdAndServico_Maker_IdAndStatus(1L, 2L, StatusPedido.FINALIZADO))
                .thenReturn(true);
        when(avaliacaoRepository.existsByCliente_IdAndMaker_Id(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(1L, dtoPadrao))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já avaliou");
    }
}
