package com.printai.controller;

import com.printai.dto.TipoRespostaDTO;
import com.printai.service.TipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
@RequiredArgsConstructor
public class TipoController {

    private final TipoService tipoService;

    @GetMapping
    public ResponseEntity<List<TipoRespostaDTO>> listar() {
        return ResponseEntity.ok(tipoService.listar());
    }
}
