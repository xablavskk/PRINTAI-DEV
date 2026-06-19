package com.printai.controller;

import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ImpressoraRespostaDTO;
import com.printai.dto.ServicoRespostaDTO;
import com.printai.service.Impressora3DService;
import com.printai.service.ServicoImpressaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busca")
@RequiredArgsConstructor
public class BuscarImpressoras3DController {

    private final ServicoImpressaoService servicoImpressaoService;
    private final Impressora3DService impressora3DService;

    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoRespostaDTO>> buscarServicos(
            BuscaServicoRequestDTO buscaDTO,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        List<ServicoRespostaDTO> resultados = servicoImpressaoService.buscarServicos(buscaDTO, lat, lon);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/impressoras")
    public ResponseEntity<List<ImpressoraRespostaDTO>> buscarImpressoras(
            BuscaServicoRequestDTO buscaDTO,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, defaultValue = "10.0") Double raio) {
        List<ImpressoraRespostaDTO> resultados = impressora3DService.buscarImpressoras(buscaDTO, lat, lon, raio);
        return ResponseEntity.ok(resultados);
    }
}
