package com.printai.controller;

import com.printai.dto.*;
import com.printai.service.MakerService;
import com.printai.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController {

    private final UsuarioService usuarioService;
    private final MakerService makerService;

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroMakerRespostaDTO> solicitarCadastro(
            @Valid @RequestBody CadastroMakerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrarMaker(dto));
    }

    // -------------------------------------------------------------------------
    // Pedidos recebidos
    // -------------------------------------------------------------------------

    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoMakerRespostaDTO>> listarPedidos(
            @RequestHeader("X-Maker-Id") Long makerId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(makerService.listarPedidos(makerId, status));
    }

    @PatchMapping("/pedidos/{id}/status")
    public ResponseEntity<PedidoMakerRespostaDTO> atualizarStatusPedido(
            @RequestHeader("X-Maker-Id") Long makerId,
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoRequestDTO dto) {
        return ResponseEntity.ok(makerService.atualizarStatusPedido(makerId, id, dto));
    }

    // -------------------------------------------------------------------------
    // Serviços de impressão
    // -------------------------------------------------------------------------

    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoMakerRespostaDTO>> listarServicos(
            @RequestHeader("X-Maker-Id") Long makerId) {
        return ResponseEntity.ok(makerService.listarServicos(makerId));
    }

    @PostMapping("/servicos")
    public ResponseEntity<ServicoMakerRespostaDTO> criarServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @Valid @RequestBody ServicoImpressaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(makerService.criarServico(makerId, dto));
    }

    @PutMapping("/servicos/{id}")
    public ResponseEntity<ServicoMakerRespostaDTO> editarServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @PathVariable Long id,
            @Valid @RequestBody ServicoImpressaoRequestDTO dto) {
        return ResponseEntity.ok(makerService.editarServico(makerId, id, dto));
    }

    @DeleteMapping("/servicos/{id}")
    public ResponseEntity<Void> removerServico(
            @RequestHeader("X-Maker-Id") Long makerId,
            @PathVariable Long id) {
        makerService.removerServico(makerId, id);
        return ResponseEntity.noContent().build();
    }

}
