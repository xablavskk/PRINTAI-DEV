package com.printai.service;

import com.printai.dto.*;
import com.printai.exception.RegraNegocioException;
import com.printai.model.*;
import com.printai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MakerService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ServicoImpressaoRepository servicoRepository;
    private final TipoRepository tipoRepository;
    private final MaterialRepository materialRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    // -------------------------------------------------------------------------
    // Pedidos
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<PedidoMakerRespostaDTO> listarPedidos(Long makerId, String status) {
        validarMaker(makerId);
        List<Pedido> pedidos = (status != null && !status.isBlank())
                ? pedidoRepository.findByServico_Maker_IdAndStatus(makerId, StatusPedido.valueOf(status))
                : pedidoRepository.findByServico_Maker_Id(makerId);

        return pedidos.stream().map(this::toRespostaMaker).toList();
    }

    @Transactional
    public PedidoMakerRespostaDTO atualizarStatusPedido(Long makerId, Long pedidoId, AtualizarStatusPedidoRequestDTO dto) {
        validarMaker(makerId);

        Pedido pedido = pedidoRepository.findByIdAndServico_Maker_Id(pedidoId, makerId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado ou não pertence a este Maker"));

        if (StatusPedido.FINALIZADO == pedido.getStatus()) {
            throw new RegraNegocioException("Pedido já finalizado não pode ser alterado");
        }

        StatusPedido novoStatus;
        try {
            novoStatus = StatusPedido.valueOf(dto.getStatus().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Status inválido: " + dto.getStatus()
                + ". Valores aceitos: AGUARDANDO_ANALISE, APROVADO, EM_PRODUCAO, FINALIZADO, CANCELADO");
        }

        pedido.setStatus(novoStatus);
        return toRespostaMaker(pedidoRepository.save(pedido));
    }

    // -------------------------------------------------------------------------
    // Avaliações
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarAvaliacoes(Long makerId) {
        validarMaker(makerId);
        return avaliacaoRepository.findByMaker_Id(makerId).stream()
                .map(a -> AvaliacaoDTO.builder()
                        .id(a.getId())
                        .clienteNome(a.getCliente().getNome())
                        .nota(a.getNota())
                        .comentario(a.getComentario())
                        .dataAvaliacao(a.getDataAvaliacao())
                        .build())
                .toList();
    }

    // -------------------------------------------------------------------------
    // Serviços
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ServicoMakerRespostaDTO> listarServicos(Long makerId) {
        validarMaker(makerId);
        return servicoRepository.findByMaker_Id(makerId).stream()
                .map(this::toServicoResposta)
                .toList();
    }

    @Transactional
    public ServicoMakerRespostaDTO criarServico(Long makerId, ServicoImpressaoRequestDTO dto) {
        Usuario maker = validarMaker(makerId);
        ServicoImpressao servico = construirServico(dto, maker);
        return toServicoResposta(servicoRepository.save(servico));
    }

    @Transactional
    public ServicoMakerRespostaDTO editarServico(Long makerId, Long servicoId, ServicoImpressaoRequestDTO dto) {
        validarMaker(makerId);

        ServicoImpressao servico = servicoRepository.findByIdAndMaker_Id(servicoId, makerId)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"));

        Tipo tipo = dto.getTipoId() != null
                ? tipoRepository.findById(dto.getTipoId()).orElse(null)
                : null;

        servico.setNome(dto.getNome());
        servico.setPrecoBase(dto.getPrecoBase());
        servico.setDescricao(dto.getDescricao());
        servico.setCondicoesServico(dto.getCondicoesServico());
        servico.setTipo(tipo);
        servico.setTecnologia(dto.getTecnologia());
        servico.setMaterial(resolverMaterial(dto.getMaterial()));
        servico.setSuportaPecasPequenas(dto.isSuportaPecasPequenas());
        servico.setSuportaDecorativos(dto.isSuportaDecorativos());
        servico.setSuportaPrototipos(dto.isSuportaPrototipos());

        return toServicoResposta(servicoRepository.save(servico));
    }

    @Transactional
    public void removerServico(Long makerId, Long servicoId) {
        validarMaker(makerId);
        ServicoImpressao servico = servicoRepository.findByIdAndMaker_Id(servicoId, makerId)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"));
        servicoRepository.delete(servico);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Usuario validarMaker(Long makerId) {
        Usuario maker = usuarioRepository.findById(makerId)
                .orElseThrow(() -> new RegraNegocioException("Maker não encontrado"));

        if (maker.getPerfil() != Perfil.MAKER) {
            throw new RegraNegocioException("Acesso restrito para Makers");
        }

        if (!Boolean.TRUE.equals(maker.getStatusAprovacao())) {
            throw new RegraNegocioException(
                "Sua conta ainda não foi aprovada pelo administrador. " +
                "Aguarde a análise da sua solicitação de cadastro."
            );
        }

        return maker;
    }

    private Material resolverMaterial(String materialNome) {
        if (materialNome == null || materialNome.isBlank()) return null;
        try {
            MaterialTipo tipo = MaterialTipo.valueOf(materialNome.toUpperCase().trim());
            return materialRepository.findByNome(tipo)
                    .orElseThrow(() -> new RegraNegocioException("Material não encontrado: " + materialNome));
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Material inválido: " + materialNome);
        }
    }

    private ServicoImpressao construirServico(ServicoImpressaoRequestDTO dto, Usuario maker) {
        Tipo tipo = dto.getTipoId() != null
                ? tipoRepository.findById(dto.getTipoId()).orElse(null)
                : null;

        return ServicoImpressao.builder()
                .nome(dto.getNome())
                .precoBase(dto.getPrecoBase())
                .descricao(dto.getDescricao())
                .condicoesServico(dto.getCondicoesServico())
                .tipo(tipo)
                .tecnologia(dto.getTecnologia())
                .material(resolverMaterial(dto.getMaterial()))
                .suportaPecasPequenas(dto.isSuportaPecasPequenas())
                .suportaDecorativos(dto.isSuportaDecorativos())
                .suportaPrototipos(dto.isSuportaPrototipos())
                .maker(maker)
                .build();
    }

    private PedidoMakerRespostaDTO toRespostaMaker(Pedido p) {
        return PedidoMakerRespostaDTO.builder()
                .id(p.getId())
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .tipoPedido(p.getTipoPedido() != null ? p.getTipoPedido().name() : null)
                .dataPedido(p.getDataPedido())
                .clienteNome(p.getCliente().getNome())
                .clienteEmail(p.getCliente().getEmail())
                .clienteTelefone(p.getCliente().getTelefone())
                .servicoNome(p.getServico().getNome())
                .arquivo3D(p.getArquivo3D())
                .descricaoNecessidade(p.getDescricaoNecessidade())
                .material(p.getMaterial())
                .quantidade(p.getQuantidade())
                .observacoes(p.getObservacoes())
                .impressoraId(p.getImpressora() != null ? p.getImpressora().getId() : null)
                .impressoraModelo(p.getImpressora() != null ? p.getImpressora().getModelo() : null)
                .build();
    }

    private ServicoMakerRespostaDTO toServicoResposta(ServicoImpressao s) {
        return ServicoMakerRespostaDTO.builder()
                .id(s.getId())
                .nome(s.getNome())
                .precoBase(s.getPrecoBase())
                .descricao(s.getDescricao())
                .condicoesServico(s.getCondicoesServico())
                .tipoId(s.getTipo() != null ? s.getTipo().getId() : null)
                .tipoNome(s.getTipo() != null ? s.getTipo().getNome() : null)
                .tecnologia(s.getTecnologia())
                .material(s.getMaterial() != null ? s.getMaterial().getNome().name() : null)
                .suportaPecasPequenas(s.isSuportaPecasPequenas())
                .suportaDecorativos(s.isSuportaDecorativos())
                .suportaPrototipos(s.isSuportaPrototipos())
                .build();
    }
}
