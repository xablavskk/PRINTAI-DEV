package com.printai.service;

import com.printai.dto.PedidoRequestDTO;
import com.printai.dto.PedidoRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Pedido;
import com.printai.model.Perfil;
import com.printai.model.ServicoImpressao;
import com.printai.model.StatusPedido;
import com.printai.model.Usuario;
import com.printai.repository.PedidoRepository;
import com.printai.repository.ServicoImpressaoRepository;
import com.printai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoImpressaoRepository servicoImpressaoRepository;

    @Transactional
    public PedidoRespostaDTO criarPedido(Long clienteId, PedidoRequestDTO dto) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado"));

        if (cliente.getPerfil() != Perfil.CLIENTE) {
            throw new RegraNegocioException("Somente usuários com perfil de CLIENTE podem solicitar pedidos.");
        }

        ServicoImpressao servico = servicoImpressaoRepository.findById(dto.getServicoId())
                .orElseThrow(() -> new RegraNegocioException("Serviço de impressão não encontrado"));

        if (!Boolean.TRUE.equals(servico.getMaker().getStatusAprovacao())) {
            throw new RegraNegocioException("Este serviço não está disponível no momento.");
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .servico(servico)
                .tipoPedido(dto.getTipoPedido())
                .arquivo3D(dto.getArquivo3D())
                .descricaoNecessidade(dto.getDescricaoNecessidade())
                .material(dto.getMaterial())
                .quantidade(dto.getQuantidade())
                .observacoes(dto.getObservacoes())
                .dataPedido(new Date())
                .status(StatusPedido.AGUARDANDO_ANALISE)
                .build();

        pedido = pedidoRepository.save(pedido);

        return PedidoRespostaDTO.builder()
                .id(pedido.getId())
                .status(pedido.getStatus() != null ? pedido.getStatus().name() : null)
                .tipoPedido(pedido.getTipoPedido())
                .dataPedido(pedido.getDataPedido())
                .servicoNome(servico.getNome())
                .makerNome(servico.getMaker().getNome())
                .mensagem("Pedido solicitado com sucesso! Aguarde a análise do Maker.")
                .build();
    }

    @Transactional(readOnly = true)
    public java.util.List<PedidoRespostaDTO> listarPedidosCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado"));

        if (cliente.getPerfil() != Perfil.CLIENTE) {
            throw new RegraNegocioException("Acesso restrito para clientes.");
        }

        return pedidoRepository.findByCliente_Id(clienteId).stream()
                .map(pedido -> PedidoRespostaDTO.builder()
                        .id(pedido.getId())
                        .status(pedido.getStatus() != null ? pedido.getStatus().name() : null)
                        .tipoPedido(pedido.getTipoPedido())
                        .dataPedido(pedido.getDataPedido())
                        .servicoNome(pedido.getServico() != null ? pedido.getServico().getNome() : "N/A")
                        .makerId(pedido.getServico() != null && pedido.getServico().getMaker() != null ? pedido.getServico().getMaker().getId() : null)
                        .makerNome(pedido.getServico() != null && pedido.getServico().getMaker() != null ? pedido.getServico().getMaker().getNome() : "N/A")
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }
}
