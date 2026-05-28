package com.printai.controller;

import com.printai.dto.CadastroClienteRequestDTO;
import com.printai.dto.CadastroClienteRespostaDTO;
import com.printai.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroClienteRespostaDTO> realizarCadastro(
            @Valid @RequestBody CadastroClienteRequestDTO dto) {
        CadastroClienteRespostaDTO resposta = usuarioService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
