package com.printai.service;

import com.printai.dto.AvaliacaoDTO;
import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ServicoRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.AvaliacaoMaker;
import com.printai.model.ServicoImpressao;
import com.printai.model.Tecnologia;
import com.printai.model.TecnologiaTipo;
import com.printai.repository.AvaliacaoRepository;
import com.printai.repository.ServicoImpressaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoImpressaoService {

    private final ServicoImpressaoRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;

    public ServicoRespostaDTO buscarPorId(Long id) {
        ServicoImpressao servico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        ServicoRespostaDTO dto = converterParaDTO(servico);

        if (servico.getMaker() != null) {
            Long makerId = servico.getMaker().getId();
            List<AvaliacaoMaker> avaliacoes = avaliacaoRepository.findByMaker_Id(makerId);

            dto.setAvaliacoes(avaliacoes.stream().map(a -> AvaliacaoDTO.builder()
                    .id(a.getId())
                    .clienteNome(a.getCliente().getNome())
                    .nota(a.getNota())
                    .comentario(a.getComentario())
                    .build()).collect(Collectors.toList()));

            double media = avaliacoes.stream().mapToInt(AvaliacaoMaker::getNota).average().orElse(0.0);
            dto.setMediaAvaliacao(media);
            dto.setTotalAvaliacoes(avaliacoes.size());
        }

        return dto;
    }

    public List<ServicoRespostaDTO> buscarServicos(BuscaServicoRequestDTO buscaDTO) {
        List<ServicoImpressao> servicos;

        if (buscaDTO.getBuscaSimplificada() != null && !buscaDTO.getBuscaSimplificada().isBlank()) {
            String s = buscaDTO.getBuscaSimplificada().toLowerCase();
            boolean pecasPequenas = s.contains("peça pequena") || s.contains("pequeno") || s.contains("pequena");
            boolean decorativos = s.contains("decorativo") || s.contains("decoração") || s.contains("enfeite");
            boolean prototipos = s.contains("protótipo") || s.contains("prototipo") || s.contains("teste");
            if (!pecasPequenas && !decorativos && !prototipos) {
                servicos = repository.findAll();
            } else {
                servicos = repository.buscarSimplificado(pecasPequenas, decorativos, prototipos);
            }
        } else if (buscaDTO.getTecnologia() != null ||
                   (buscaDTO.getMaterial() != null && !buscaDTO.getMaterial().isBlank()) ||
                   (buscaDTO.getModelo() != null && !buscaDTO.getModelo().isBlank())) {
            servicos = repository.buscarAvancado(buscaDTO.getTecnologia(), buscaDTO.getMaterial(), buscaDTO.getModelo());
        } else {
            servicos = repository.findAll();
        }

        return servicos.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    private ServicoRespostaDTO converterParaDTO(ServicoImpressao entidade) {
        UsuarioRespostaDTO makerDTO = null;
        if (entidade.getMaker() != null) {
            makerDTO = UsuarioRespostaDTO.builder()
                    .id(entidade.getMaker().getId())
                    .nome(entidade.getMaker().getNome())
                    .perfil(entidade.getMaker().getPerfil())
                    .latitude(entidade.getMaker().getLatitude())
                    .longitude(entidade.getMaker().getLongitude())
                    .telefone(entidade.getMaker().getTelefone())
                    .build();
        }

        String volumeMaximo = "Não informado";
        if (entidade.getMaker() != null && entidade.getMaker().getImpressoras() != null
                && !entidade.getMaker().getImpressoras().isEmpty()) {
            volumeMaximo = entidade.getMaker().getImpressoras().get(0).getVolumeImpressao();
        }

        List<TecnologiaTipo> tecnologias = entidade.getTipo() != null && entidade.getTipo().getTecnologias() != null
                ? entidade.getTipo().getTecnologias().stream().map(Tecnologia::getNome).collect(Collectors.toList())
                : Collections.emptyList();

        return ServicoRespostaDTO.builder()
                .id(entidade.getId())
                .nome(entidade.getNome())
                .descricao(entidade.getDescricao())
                .condicoesServico(entidade.getCondicoesServico())
                .volumeImpressao(volumeMaximo)
                .tipoNome(entidade.getTipo() != null ? entidade.getTipo().getNome() : null)
                .tipoDescricao(entidade.getTipo() != null ? entidade.getTipo().getDescricao() : null)
                .tecnologias(tecnologias)
                .material(entidade.getMaterial())
                .precoBase(entidade.getPrecoBase())
                .maker(makerDTO)
                .build();
    }
}
