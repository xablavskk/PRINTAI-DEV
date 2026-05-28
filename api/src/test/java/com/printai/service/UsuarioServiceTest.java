package com.printai.service;

import com.printai.dto.CadastroMakerRequestDTO;
import com.printai.dto.CadastroMakerRespostaDTO;
import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.model.*;
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
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ServicoImpressaoRepository servicoImpressaoRepository;
    @Mock private TipoRepository tipoRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private GeocodificacaoService geocodificacaoService;

    @InjectMocks
    private UsuarioService usuarioService;

    private CadastroMakerRequestDTO dtoPadrao;

    @BeforeEach
    void setUp() {
        dtoPadrao = CadastroMakerRequestDTO.builder()
                .nome("Mario Maker")
                .email("mario@printai.com")
                .senha("senha123")
                .telefone("11999990000")
                .documentoCpfCnpj("12345678900")
                .logradouro("Rua das Impressoras")
                .numero("42")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310100")
                .pais("Brasil")
                .build();
    }

    // ===================== Cadastro básico =====================

    @Test
    @DisplayName("Cadastro sem serviços deve salvar maker e retornar resposta correta")
    void cadastrarMaker_semServicos_salvaMakerComSucesso() {
        when(usuarioRepository.findByEmail("mario@printai.com")).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new double[]{-23.55, -46.63});

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .telefone("11999990000").cidade("São Paulo").estado("SP")
                .latitude(-23.55).longitude(-46.63)
                .perfil(Perfil.MAKER).statusAprovacao(true)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(makerSalvo);

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getId()).isEqualTo(1L);
        assertThat(resposta.getNome()).isEqualTo("Mario Maker");
        assertThat(resposta.getEmail()).isEqualTo("mario@printai.com");
        assertThat(resposta.getLatitude()).isEqualTo(-23.55);
        assertThat(resposta.getLongitude()).isEqualTo(-46.63);
        assertThat(resposta.getStatusAprovacao()).isTrue();
        assertThat(resposta.getTotalServicos()).isEqualTo(0);
        assertThat(resposta.getMensagem()).contains("sucesso");

        verify(usuarioRepository).save(argThat(u ->
                u.getPerfil() == Perfil.MAKER &&
                Boolean.TRUE.equals(u.getStatusAprovacao())
        ));
        verify(servicoImpressaoRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Cadastro com serviços deve salvar maker e serviços vinculados")
    void cadastrarMaker_comServicos_salvaMakerEServicos() {
        // materialId referencia a entidade Material — não mais String livre
        ServicoImpressaoRequestDTO servico = ServicoImpressaoRequestDTO.builder()
                .nome("Impressão FDM")
                .precoBase(80.0)
                .descricao("Serviço FDM de alta qualidade")
                .tipoId(1L)
                .materialId(1L)
                .suportaPecasPequenas(true)
                .suportaPrototipos(true)
                .build();

        dtoPadrao.setServicos(List.of(servico));

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new double[]{-23.55, -46.63});

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .cidade("São Paulo").estado("SP")
                .latitude(-23.55).longitude(-46.63)
                .perfil(Perfil.MAKER).statusAprovacao(true)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(makerSalvo);

        Tipo tipoFilamento = Tipo.builder().id(1L).nome("Filamento").build();
        when(tipoRepository.findById(1L)).thenReturn(Optional.of(tipoFilamento));

        Material materialPla = Material.builder().id(1L).nome(MaterialTipo.PLA).build();
        when(materialRepository.findById(1L)).thenReturn(Optional.of(materialPla));

        when(servicoImpressaoRepository.saveAll(any())).thenReturn(List.of());

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getTotalServicos()).isEqualTo(1);
        assertThat(resposta.getMensagem()).contains("1 serviço(s)");

        verify(tipoRepository).findById(1L);
        verify(materialRepository).findById(1L);
        verify(servicoImpressaoRepository).saveAll(argThat(lista ->
                ((List<?>) lista).size() == 1
        ));
    }

    @Test
    @DisplayName("Cadastro com serviço sem tipo e sem material deve salvar normalmente")
    void cadastrarMaker_comServico_semTipoESemMaterial_salvaNormalmente() {
        ServicoImpressaoRequestDTO servico = ServicoImpressaoRequestDTO.builder()
                .nome("Serviço Genérico")
                .precoBase(50.0)
                .build();

        dtoPadrao.setServicos(List.of(servico));

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new double[]{-23.55, -46.63});

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .perfil(Perfil.MAKER).statusAprovacao(true).build();
        when(usuarioRepository.save(any())).thenReturn(makerSalvo);
        when(servicoImpressaoRepository.saveAll(any())).thenReturn(List.of());

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getTotalServicos()).isEqualTo(1);
        // tipoId e materialId nulos — não deve consultar os repositórios
        verify(tipoRepository, never()).findById(any());
        verify(materialRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Cadastro deve criar maker com statusAprovacao true (aprovação automática)")
    void cadastrarMaker_statusAprovacaoDeveSerTrue() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .perfil(Perfil.MAKER).statusAprovacao(true).build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(makerSalvo);

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getStatusAprovacao()).isTrue();

        // Verifica que o objeto salvo no banco tem statusAprovacao = true
        verify(usuarioRepository).save(argThat(u ->
                Boolean.TRUE.equals(u.getStatusAprovacao())
        ));
    }

    @Test
    @DisplayName("Cadastro com e-mail duplicado deve lançar IllegalArgumentException")
    void cadastrarMaker_emailDuplicado_lancaExcecao() {
        when(usuarioRepository.findByEmail("mario@printai.com"))
                .thenReturn(Optional.of(Usuario.builder().build()));

        assertThatThrownBy(() -> usuarioService.cadastrarMaker(dtoPadrao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cadastro com tipoId inexistente deve lançar IllegalArgumentException")
    void cadastrarMaker_tipoInexistente_lancaExcecao() {
        dtoPadrao.setServicos(List.of(
                ServicoImpressaoRequestDTO.builder()
                        .nome("Serviço X").precoBase(50.0).tipoId(99L).build()
        ));

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        Usuario makerSalvo = Usuario.builder().id(1L).nome("Mario Maker")
                .email("mario@printai.com").perfil(Perfil.MAKER).statusAprovacao(true).build();
        when(usuarioRepository.save(any())).thenReturn(makerSalvo);
        when(tipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.cadastrarMaker(dtoPadrao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de impressão não encontrado");
    }

    @Test
    @DisplayName("Cadastro com materialId inexistente deve lançar IllegalArgumentException")
    void cadastrarMaker_materialInexistente_lancaExcecao() {
        dtoPadrao.setServicos(List.of(
                ServicoImpressaoRequestDTO.builder()
                        .nome("Serviço X").precoBase(50.0).materialId(99L).build()
        ));

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        Usuario makerSalvo = Usuario.builder().id(1L).nome("Mario Maker")
                .email("mario@printai.com").perfil(Perfil.MAKER).statusAprovacao(true).build();
        when(usuarioRepository.save(any())).thenReturn(makerSalvo);
        when(materialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.cadastrarMaker(dtoPadrao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Material não encontrado");
    }

    @Test
    @DisplayName("Falha na geocodificação não deve impedir o cadastro")
    void cadastrarMaker_geocodificacaoFalha_salvaSemCoordenadas() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .latitude(null).longitude(null)
                .perfil(Perfil.MAKER).statusAprovacao(true)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(makerSalvo);

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getLatitude()).isNull();
        assertThat(resposta.getLongitude()).isNull();
        assertThat(resposta.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Cadastro com lista de serviços vazia não deve chamar saveAll")
    void cadastrarMaker_listaServicosVazia_naoSalvaServicos() {
        dtoPadrao.setServicos(List.of());

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(geocodificacaoService.geocodificar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new double[]{-23.55, -46.63});

        Usuario makerSalvo = Usuario.builder()
                .id(1L).nome("Mario Maker").email("mario@printai.com")
                .perfil(Perfil.MAKER).statusAprovacao(true).build();
        when(usuarioRepository.save(any())).thenReturn(makerSalvo);

        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dtoPadrao);

        assertThat(resposta.getTotalServicos()).isEqualTo(0);
        verify(servicoImpressaoRepository, never()).saveAll(any());
    }
}
