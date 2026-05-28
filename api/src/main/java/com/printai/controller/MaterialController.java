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
    public ResponseEntity<List<Map<String, String>>> listar() {
        List<Map<String, String>> materiais = materialRepository.findAll().stream()
                .map(m -> Map.of(
                        "nome", m.getNome().name(),
                        "descricao", m.getDescricao() != null ? m.getDescricao() : ""
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(materiais);
    }
}
