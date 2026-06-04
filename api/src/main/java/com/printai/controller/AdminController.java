package com.printai.controller;

import com.printai.dto.AprovarMakerRequestDTO;
import com.printai.dto.LoginRequestDTO;
import com.printai.dto.LoginRespostaDTO;
import com.printai.dto.MakerPendenteRespostaDTO;
import com.printai.service.AdminService;
import com.printai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.loginAdmin(dto));
    }

    @GetMapping("/makers/pendentes")
    public ResponseEntity<List<MakerPendenteRespostaDTO>> listarPendentes(
            @RequestHeader("X-Admin-Id") Long adminId) {
        return ResponseEntity.ok(adminService.listarMakersPendentes(adminId));
    }

    @GetMapping("/makers")
    public ResponseEntity<List<MakerPendenteRespostaDTO>> listarTodos(
            @RequestHeader("X-Admin-Id") Long adminId) {
        return ResponseEntity.ok(adminService.listarTodosMakers(adminId));
    }

    @GetMapping("/makers/{makerId}")
    public ResponseEntity<MakerPendenteRespostaDTO> buscarMaker(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long makerId) {
        return ResponseEntity.ok(adminService.buscarMaker(adminId, makerId));
    }

    @PatchMapping("/makers/{makerId}/processar")
    public ResponseEntity<MakerPendenteRespostaDTO> processarSolicitacao(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long makerId,
            @RequestBody @Valid AprovarMakerRequestDTO dto) {
        return ResponseEntity.ok(adminService.processarSolicitacao(adminId, makerId, dto));
    }
}
