package com.printai.controller;

import com.printai.dto.LoginRequestDTO;
import com.printai.dto.LoginRespostaDTO;
import com.printai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        LoginRespostaDTO resposta = authService.login(dto);
        return ResponseEntity.ok(resposta);
    }
}
