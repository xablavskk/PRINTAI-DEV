package com.printai.service;

import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ServicoRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.ServicoImpressao;
import com.printai.repository.ServicoImpressaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoImpressaoService {

    private final ServicoImpressaoRepository repository;

    public List<ServicoRespostaDTO> buscarServicos(BuscaServicoRequestDTO buscaDTO) {
        List<ServicoImpressao> servicos;

        if (buscaDTO.getBuscaSimplificada() != null && !buscaDTO.getBuscaSimplificada().isBlank()) {
            String simplificada = buscaDTO.getBuscaSimplificada().toLowerCase();
            boolean pecasPequenas = simplificada.contains("peça pequena") || simplificada.contains("pequeno") || simplificada.contains("pequena");
            boolean decorativos = simplificada.contains("decorativo") || simplificada.contains("decoração") || simplificada.contains("enfeite");
            boolean prototipos = simplificada.contains("protótipo") || simplificada.contains("prototipo") || simplificada.contains("teste");

            servicos = repository.buscarSimplificado(pecasPequenas, decorativos, prototipos);
        } else {
            String tecnologia = (buscaDTO.getTecnologia() != null && !buscaDTO.getTecnologia().isBlank()) ? buscaDTO.getTecnologia() : null;
            String material = (buscaDTO.getMaterial() != null && !buscaDTO.getMaterial().isBlank()) ? buscaDTO.getMaterial() : null;
            String modelo = (buscaDTO.getModelo() != null && !buscaDTO.getModelo().isBlank()) ? buscaDTO.getModelo() : null;
            servicos = repository.buscarAvancado(tecnologia, material, modelo);
        }

        return servicos.stream().map(this::converterParaDTO).collect(Collectors.toList());
    }

    private ServicoRespostaDTO converterParaDTO(ServicoImpressao entidade) {
        UsuarioRespostaDTO makerDTO = null;
        if (entidade.getMaker() != null) {
            makerDTO = UsuarioRespostaDTO.builder()
                    .id(entidade.getMaker().getId())
                    .nome(entidade.getMaker().getNome())
                    .tipo("MAKER")
                    .latitude(entidade.getMaker().getLatitude())
                    .longitude(entidade.getMaker().getLongitude())
                    .telefone(entidade.getMaker().getTelefone())
                    .build();
        }

        return ServicoRespostaDTO.builder()
                .id(entidade.getId())
                .nome(entidade.getNome())
                .descricao(entidade.getDescricao())
                .tecnologia(entidade.getTecnologia())
                .material(entidade.getMaterial())
                .precoBase(entidade.getPrecoBase())
                .maker(makerDTO)
                .build();
    }
}
