package com.printai.controller;

import com.printai.model.Material;
import com.printai.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materiais")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialRepository materialRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Map<String, Object>> materiais = materialRepository.findAll().stream()
                .map(m -> {
                    Map<String, Object> entry = new java.util.LinkedHashMap<>();
                    entry.put("id", m.getId());
                    entry.put("nome", m.getNome().name());
                    entry.put("descricao", m.getDescricao() != null ? m.getDescricao() : "");
                    return entry;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(materiais);
    }
}
