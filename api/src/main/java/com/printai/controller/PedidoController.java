package com.printai.controller;

import com.printai.dto.PedidoRequestDTO;
import com.printai.dto.PedidoRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoRespostaDTO> criarPedido(
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteId,
            @RequestBody @Valid PedidoRequestDTO dto) {
        
        if (clienteId == null) {
            throw new RegraNegocioException("Usuário não autenticado. Identificador do cliente (X-Cliente-Id) ausente.");
        }

        PedidoRespostaDTO resposta = pedidoService.criarPedido(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<java.util.List<PedidoRespostaDTO>> listarPedidos(
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteId) {
        
        if (clienteId == null) {
            throw new RegraNegocioException("Usuário não autenticado. Identificador do cliente (X-Cliente-Id) ausente.");
        }

        java.util.List<PedidoRespostaDTO> pedidos = pedidoService.listarPedidosCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }
}
