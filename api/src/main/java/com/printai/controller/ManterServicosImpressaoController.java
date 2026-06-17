package com.printai.controller;

import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.dto.ServicoMakerRespostaDTO;
import com.printai.service.ManterServicosImpressaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maker/servicos")
@RequiredArgsConstructor
public class ManterServicosImpressaoController {

    private final ManterServicosImpressaoService manterServicosImpressaoService;

    @GetMapping
    public ResponseEntity<List<ServicoMakerRespostaDTO>> listarServicos(
            @RequestHeader("X-Maker-Id") Long makerId) {
        return ResponseEntity.ok(manterServicosImpressaoService.listarServicos(makerId));
    }

    @PostMapping
    public ResponseEntity<ServicoMakerRespostaDTO> criarServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @Valid @RequestBody ServicoImpressaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manterServicosImpressaoService.criarServico(makerId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoMakerRespostaDTO> editarServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @PathVariable Long id,
            @Valid @RequestBody ServicoImpressaoRequestDTO dto) {
        return ResponseEntity.ok(manterServicosImpressaoService.editarServico(makerId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @PathVariable Long id) {
        manterServicosImpressaoService.removerServico(makerId, id);
        return ResponseEntity.noContent().build();
    }
}
