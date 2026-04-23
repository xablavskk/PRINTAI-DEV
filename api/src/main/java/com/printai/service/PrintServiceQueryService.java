package com.printai.service;

import com.printai.dto.ServiceResponseDTO;
import com.printai.dto.ServiceSearchRequestDTO;
import com.printai.dto.UserResponseDTO;
import com.printai.model.PrintService;
import com.printai.repository.PrintServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintServiceQueryService {

    private final PrintServiceRepository repository;

    public List<ServiceResponseDTO> searchServices(ServiceSearchRequestDTO searchDTO) {
        List<PrintService> services;

        if (searchDTO.getSimplifiedSearch() != null && !searchDTO.getSimplifiedSearch().isBlank()) {
            // Busca Simplificada
            String simplified = searchDTO.getSimplifiedSearch().toLowerCase();
            boolean isSmallPiece = simplified.contains("peça pequena") || simplified.contains("pequeno") || simplified.contains("pequena");
            boolean isDecorative = simplified.contains("decorativo") || simplified.contains("decoração") || simplified.contains("enfeite");
            boolean isPrototype = simplified.contains("protótipo") || simplified.contains("prototipo") || simplified.contains("teste");

            // Se nada bater, podemos apenas buscar se tem algo verdadeiro ou retornar todos/vazio. Vamos passar as flags.
            services = repository.searchSimplified(isSmallPiece, isDecorative, isPrototype);
        } else {
            // Busca Avançada
            services = repository.searchAdvanced(searchDTO.getTechnology(), searchDTO.getMaterial());
        }

        return services.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ServiceResponseDTO toDTO(PrintService entity) {
        UserResponseDTO makerDTO = null;
        if (entity.getMaker() != null) {
            makerDTO = UserResponseDTO.builder()
                    .id(entity.getMaker().getId())
                    .name(entity.getMaker().getName())
                    .role(entity.getMaker().getRole())
                    .latitude(entity.getMaker().getLatitude())
                    .longitude(entity.getMaker().getLongitude())
                    .phone(entity.getMaker().getPhone())
                    .build();
        }

        return ServiceResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .technology(entity.getTechnology())
                .material(entity.getMaterial())
                .maker(makerDTO)
                .build();
    }
}
