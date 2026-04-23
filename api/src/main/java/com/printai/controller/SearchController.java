package com.printai.controller;

import com.printai.dto.ServiceResponseDTO;
import com.printai.dto.ServiceSearchRequestDTO;
import com.printai.service.PrintServiceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class SearchController {

    private final PrintServiceQueryService queryService;

    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponseDTO>> searchServices(ServiceSearchRequestDTO searchDTO) {
        List<ServiceResponseDTO> results = queryService.searchServices(searchDTO);
        return ResponseEntity.ok(results);
    }
}
