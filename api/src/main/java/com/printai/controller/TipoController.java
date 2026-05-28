package com.printai.controller;

import com.printai.dto.TipoRespostaDTO;
import com.printai.model.Tecnologia;
import com.printai.model.Tipo;
import com.printai.repository.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos")
@RequiredArgsConstructor
public class TipoController {

    private final TipoRepository tipoRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<TipoRespostaDTO>> listar() {
        List<TipoRespostaDTO> tipos = tipoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tipos);
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
