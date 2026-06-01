package com.printai.controller;

import com.printai.dto.AvaliacaoRequestDTO;
import com.printai.dto.AvaliacaoRespostaDTO;
import com.printai.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoRespostaDTO> avaliarMaker(
            @RequestHeader("X-Cliente-Id") Long clienteId,
            @Valid @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(avaliacaoService.criarAvaliacao(clienteId, dto));
    }
}
