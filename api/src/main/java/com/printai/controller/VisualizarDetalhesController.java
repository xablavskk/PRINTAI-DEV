package com.printai.controller;

import com.printai.dto.ServicoRespostaDTO;
import com.printai.service.ServicoImpressaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/busca")
@RequiredArgsConstructor
public class VisualizarDetalhesController {

    private final ServicoImpressaoService servicoImpressaoService;

    @GetMapping("/detalhe/{id}")
    public ResponseEntity<ServicoRespostaDTO> buscarDetalhes(@PathVariable Long id) {
        ServicoRespostaDTO detalhes = servicoImpressaoService.buscarPorId(id);
        return ResponseEntity.ok(detalhes);
    }
}
