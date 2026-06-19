package com.printai.service;

import com.printai.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    @Transactional(readOnly = true)
    public List<Map<String, String>> listar() {
        return materialRepository.findAll().stream()
                .map(m -> Map.of(
                        "nome", m.getNome().name(),
                        "descricao", m.getDescricao() != null ? m.getDescricao() : ""
                ))
                .collect(Collectors.toList());
    }
}
