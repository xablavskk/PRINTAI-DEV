package com.printai.service;

import com.printai.dto.TipoRespostaDTO;
import com.printai.model.Tipo;
import com.printai.repository.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoService {

    private final TipoRepository tipoRepository;

    @Transactional(readOnly = true)
    public List<TipoRespostaDTO> listar() {
        return tipoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TipoRespostaDTO toDTO(Tipo tipo) {
        List<String> tecnologias = tipo.getTecnologias() != null
                ? tipo.getTecnologias().stream().map(t -> t.getNome().name()).collect(Collectors.toList())
                : List.of();
        return TipoRespostaDTO.builder()
                .id(tipo.getId())
                .nome(tipo.getNome())
                .descricao(tipo.getDescricao())
                .tecnologias(tecnologias)
                .build();
    }
}
