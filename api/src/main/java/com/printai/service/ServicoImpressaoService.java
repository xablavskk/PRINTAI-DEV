package com.printai.service;

import com.printai.dto.AvaliacaoDTO;
import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ServicoRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.AvaliacaoMaker;
import com.printai.model.MaterialTipo;
import com.printai.model.ServicoImpressao;
import com.printai.model.Tecnologia;
import com.printai.model.TecnologiaTipo;
import com.printai.repository.AvaliacaoRepository;
import com.printai.repository.ServicoImpressaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoImpressaoService {

    private final ServicoImpressaoRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ServicoRespostaDTO> buscarServicos(BuscaServicoRequestDTO buscaDTO, Double lat, Double lon) {
        List<ServicoImpressao> servicos;

        if (buscaDTO.getBuscaSimplificada() != null && !buscaDTO.getBuscaSimplificada().isBlank()) {
            String s = buscaDTO.getBuscaSimplificada().toLowerCase();
            boolean pecasPequenas = s.contains("peça pequena") || s.contains("pequeno") || s.contains("pequena");
            boolean decorativos = s.contains("decorativo") || s.contains("decoração") || s.contains("enfeite");
            boolean prototipos = s.contains("protótipo") || s.contains("prototipo") || s.contains("teste");
            servicos = (!pecasPequenas && !decorativos && !prototipos)
                    ? repository.findAll()
                    : repository.buscarSimplificado(pecasPequenas, decorativos, prototipos);
        } else if (buscaDTO.getTecnologia() != null ||
                   (buscaDTO.getMaterial() != null && !buscaDTO.getMaterial().isBlank()) ||
                   (buscaDTO.getModelo() != null && !buscaDTO.getModelo().isBlank())) {
            MaterialTipo materialTipo = null;
            if (buscaDTO.getMaterial() != null && !buscaDTO.getMaterial().isBlank()) {
                try {
                    materialTipo = MaterialTipo.valueOf(buscaDTO.getMaterial().trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            servicos = repository.buscarAvancado(buscaDTO.getTecnologia(), materialTipo, buscaDTO.getModelo());
        } else {
            servicos = repository.findAll();
        }

        return servicos.stream()
                .filter(s -> s.getMaker() != null && Boolean.TRUE.equals(s.getMaker().getStatusAprovacao()))
                .map(s -> {
            ServicoRespostaDTO dto = converterParaDTO(s);
            if (lat != null && lon != null && s.getMaker().getLatitude() != null && s.getMaker().getLongitude() != null) {
                double dist = calcularDistancia(lat, lon, s.getMaker().getLatitude(), s.getMaker().getLongitude());
                dto.setDistanciaKm(Math.round(dist * 10.0) / 10.0);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
                    .cidade(entidade.getMaker().getCidade())
                    .estado(entidade.getMaker().getEstado())
                    .build();
        }

        List<TecnologiaTipo> tecnologias = entidade.getTipo() != null && entidade.getTipo().getTecnologias() != null
                ? entidade.getTipo().getTecnologias().stream().map(Tecnologia::getNome).collect(Collectors.toList())
                : Collections.emptyList();

        String materialNome = entidade.getMaterial() != null
                ? entidade.getMaterial().getNome().name()
                : null;

        return ServicoRespostaDTO.builder()
                .id(entidade.getId())
                .nome(entidade.getNome())
                .descricao(entidade.getDescricao())
                .condicoesServico(entidade.getCondicoesServico())
                .volumeImpressao("Não informado")
                .tipoNome(entidade.getTipo() != null ? entidade.getTipo().getNome() : null)
                .tipoDescricao(entidade.getTipo() != null ? entidade.getTipo().getDescricao() : null)
                .tecnologias(tecnologias)
                .material(materialNome)
                .precoBase(entidade.getPrecoBase())
                .maker(makerDTO)
                .build();
    }
}
