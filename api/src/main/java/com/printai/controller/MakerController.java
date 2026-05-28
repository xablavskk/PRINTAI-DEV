package com.printai.controller;

import com.printai.dto.CadastroMakerRequestDTO;
import com.printai.dto.CadastroMakerRespostaDTO;
import com.printai.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maker")
@RequiredArgsConstructor
public class MakerController {

    private final UsuarioService usuarioService;

    /**
     * UC: Solicitar Cadastro (Maker)
     * POST /api/maker/cadastro
     */
    @PostMapping("/cadastro")
    public ResponseEntity<CadastroMakerRespostaDTO> solicitarCadastro(
            @Valid @RequestBody CadastroMakerRequestDTO dto) {
        CadastroMakerRespostaDTO resposta = usuarioService.cadastrarMaker(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
