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
}
