package com.printai.service;

import com.printai.dto.AvaliacaoDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final UsuarioRepository usuarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public AvaliacaoRespostaDTO criarAvaliacao(Long clienteId, AvaliacaoRequestDTO dto) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado"));

        Usuario maker = usuarioRepository.findById(dto.getMakerId())
                .orElseThrow(() -> new RegraNegocioException("Maker não encontrado"));

        if (maker.getPerfil() != Perfil.MAKER) {
            throw new RegraNegocioException("Usuário informado não é um Maker");
        }

        boolean temPedidoFinalizado = pedidoRepository
                .existsByCliente_IdAndServico_Maker_IdAndStatus(clienteId, dto.getMakerId(), StatusPedido.FINALIZADO);
        if (!temPedidoFinalizado) {
            throw new RegraNegocioException("Você só pode avaliar um Maker após finalizar um pedido com ele");
        }

        if (avaliacaoRepository.existsByCliente_IdAndMaker_Id(clienteId, dto.getMakerId())) {
            throw new RegraNegocioException("Você já avaliou este Maker");
        }

        AvaliacaoMaker avaliacao = AvaliacaoMaker.builder()
                .nota(dto.getNota())
                .comentario(dto.getComentario())
                .dataAvaliacao(new Date())
                .cliente(cliente)
                .maker(maker)
                .build();

        avaliacao = avaliacaoRepository.save(avaliacao);

        return AvaliacaoRespostaDTO.builder()
                .id(avaliacao.getId())
                .clienteNome(cliente.getNome())
                .makerNome(maker.getNome())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .dataAvaliacao(avaliacao.getDataAvaliacao())
                .mensagem("Avaliação enviada com sucesso!")
                .build();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarAvaliacoesRecebidas(Long makerId) {
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
}
