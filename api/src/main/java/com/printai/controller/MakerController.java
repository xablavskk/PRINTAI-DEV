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

}
