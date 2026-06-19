package com.printai.service;

import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.dto.ServicoMakerRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Perfil;
import com.printai.model.ServicoImpressao;
import com.printai.model.Usuario;
import com.printai.repository.MaterialRepository;
import com.printai.repository.ServicoImpressaoRepository;
import com.printai.repository.TipoRepository;
import com.printai.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManterServicosImpressaoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ServicoImpressaoRepository servicoRepository;
    @Mock private TipoRepository tipoRepository;
    @Mock private MaterialRepository materialRepository;

    @InjectMocks
    private ManterServicosImpressaoService manterServicosImpressaoService;

    private Usuario makerPadrao;
    private ServicoImpressao servicoPadrao;

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
    }

    // ── listarServicos ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarServicos deve retornar todos os serviços cadastrados pelo maker")
    void listarServicos_retornaServicosDoMaker() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByMaker_Id(1L)).thenReturn(List.of(servicoPadrao));

        List<ServicoMakerRespostaDTO> resultado = manterServicosImpressaoService.listarServicos(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(10L);
        assertThat(resultado.get(0).getNome()).isEqualTo("Impressão FDM");
        assertThat(resultado.get(0).getPrecoBase()).isEqualTo(80.0);
        assertThat(resultado.get(0).isSuportaPecasPequenas()).isTrue();
    }

    @Test
    @DisplayName("listarServicos sem serviços cadastrados deve retornar lista vazia")
    void listarServicos_semServicos_retornaListaVazia() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByMaker_Id(1L)).thenReturn(List.of());

        assertThat(manterServicosImpressaoService.listarServicos(1L)).isEmpty();
    }

    // ── criarServico ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("criarServico com dados válidos deve persistir e retornar serviço criado")
    void criarServico_dadosValidos_salvaERetornaServico() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.save(any(ServicoImpressao.class))).thenAnswer(inv -> {
            ServicoImpressao s = inv.getArgument(0);
            s.setId(11L);
            return s;
        });

        ServicoImpressaoRequestDTO dto = ServicoImpressaoRequestDTO.builder()
                .nome("Impressão SLA").precoBase(120.0)
                .tecnologia("SLA").suportaPrototipos(true)
                .build();

        ServicoMakerRespostaDTO resultado = manterServicosImpressaoService.criarServico(1L, dto);

        assertThat(resultado.getId()).isEqualTo(11L);
        assertThat(resultado.getNome()).isEqualTo("Impressão SLA");
        assertThat(resultado.getPrecoBase()).isEqualTo(120.0);
        assertThat(resultado.isSuportaPrototipos()).isTrue();
        verify(servicoRepository).save(argThat(s ->
                "Impressão SLA".equals(s.getNome()) && s.getMaker().getId().equals(1L)));
    }

    @Test
    @DisplayName("criarServico com maker inexistente deve lançar RegraNegocioException")
    void criarServico_makerNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        ServicoImpressaoRequestDTO dto = ServicoImpressaoRequestDTO.builder()
                .nome("Serviço X").precoBase(50.0).build();

        assertThatThrownBy(() -> manterServicosImpressaoService.criarServico(99L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Maker não encontrado");
    }

    // ── editarServico ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("editarServico com dados válidos deve atualizar os campos do serviço")
    void editarServico_dadosValidos_atualizaCampos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByIdAndMaker_Id(10L, 1L)).thenReturn(Optional.of(servicoPadrao));
        when(servicoRepository.save(any(ServicoImpressao.class))).thenAnswer(inv -> inv.getArgument(0));

        ServicoImpressaoRequestDTO dto = ServicoImpressaoRequestDTO.builder()
                .nome("Impressão FDM Premium").precoBase(100.0)
                .descricao("Serviço de alta precisão").tecnologia("FDM")
                .suportaDecorativos(true).suportaPrototipos(true)
                .build();

        ServicoMakerRespostaDTO resultado = manterServicosImpressaoService.editarServico(1L, 10L, dto);

        assertThat(resultado.getNome()).isEqualTo("Impressão FDM Premium");
        assertThat(resultado.getPrecoBase()).isEqualTo(100.0);
        assertThat(resultado.getDescricao()).isEqualTo("Serviço de alta precisão");
        assertThat(resultado.isSuportaDecorativos()).isTrue();
        assertThat(resultado.isSuportaPrototipos()).isTrue();
        verify(servicoRepository).save(any(ServicoImpressao.class));
    }

    @Test
    @DisplayName("editarServico com serviço de outro maker deve lançar RegraNegocioException")
    void editarServico_servicoDeOutroMaker_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByIdAndMaker_Id(10L, 1L)).thenReturn(Optional.empty());

        ServicoImpressaoRequestDTO dto = ServicoImpressaoRequestDTO.builder()
                .nome("Novo nome").precoBase(50.0).build();

        assertThatThrownBy(() -> manterServicosImpressaoService.editarServico(1L, 10L, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pertence a este Maker");
    }

    // ── removerServico ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("removerServico válido deve deletar o serviço do repositório")
    void removerServico_valido_deletaServico() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByIdAndMaker_Id(10L, 1L)).thenReturn(Optional.of(servicoPadrao));

        manterServicosImpressaoService.removerServico(1L, 10L);

        verify(servicoRepository).delete(servicoPadrao);
    }

    @Test
    @DisplayName("removerServico com serviço inexistente deve lançar RegraNegocioException")
    void removerServico_servicoNaoEncontrado_lancaExcecao() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(makerPadrao));
        when(servicoRepository.findByIdAndMaker_Id(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manterServicosImpressaoService.removerServico(1L, 10L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pertence a este Maker");
    }
}
