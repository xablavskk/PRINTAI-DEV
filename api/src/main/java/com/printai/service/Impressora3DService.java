package com.printai.service;

import com.printai.dto.BuscaServicoRequestDTO;
import com.printai.dto.ImpressoraRespostaDTO;
import com.printai.dto.UsuarioRespostaDTO;
import com.printai.model.Impressora3D;
import com.printai.repository.Impressora3DRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Impressora3DService {

    private final Impressora3DRepository impressora3DRepository;

    public List<ImpressoraRespostaDTO> buscarImpressoras(BuscaServicoRequestDTO buscaDTO, Double lat, Double lon, Double raioKm) {
        List<Impressora3D> impressoras = impressora3DRepository.findAll();

        if (lat != null && lon != null && raioKm != null) {
            double deltaLat = raioKm / 111.0;
            double deltaLon = raioKm / (111.0 * Math.cos(Math.toRadians(lat)));

            double latMin = lat - deltaLat;
            double latMax = lat + deltaLat;
            double lonMin = lon - deltaLon;
            double lonMax = lon + deltaLon;

            impressoras = impressoras.stream()
                    .filter(i -> i.getMaker().getLatitude() >= latMin && i.getMaker().getLatitude() <= latMax)
                    .filter(i -> i.getMaker().getLongitude() >= lonMin && i.getMaker().getLongitude() <= lonMax)
                    .filter(i -> calcularDistancia(lat, lon, i.getMaker().getLatitude(), i.getMaker().getLongitude()) <= raioKm)
                    .collect(Collectors.toList());
        }

        if (buscaDTO.getModelo() != null && !buscaDTO.getModelo().isBlank()) {
            impressoras = impressoras.stream()
                    .filter(i -> i.getModelo().toLowerCase().contains(buscaDTO.getModelo().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (buscaDTO.getTecnologia() != null && !buscaDTO.getTecnologia().isBlank()) {
            impressoras = impressoras.stream()
                    .filter(i -> i.getTecnologia().equalsIgnoreCase(buscaDTO.getTecnologia()))
                    .collect(Collectors.toList());
        }

        if (buscaDTO.getMaterial() != null && !buscaDTO.getMaterial().isBlank()) {
            impressoras = impressoras.stream()
                    .filter(i -> i.getMaterial().toLowerCase().contains(buscaDTO.getMaterial().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (buscaDTO.getVolumeMaximo() != null) {
            impressoras = impressoras.stream()
                    .filter(i -> calcularVolumeCm3(i.getVolumeImpressao()) <= buscaDTO.getVolumeMaximo())
                    .collect(Collectors.toList());
        }

        return impressoras.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private double calcularVolumeCm3(String volumeImpressao) {
        if (volumeImpressao == null || volumeImpressao.isBlank()) return Double.MAX_VALUE;
        try {
            String limpo = volumeImpressao.toLowerCase().replace("mm", "").trim();
            String[] partes = limpo.split("x");
            if (partes.length != 3) return Double.MAX_VALUE;
            double x = Double.parseDouble(partes[0].trim());
            double y = Double.parseDouble(partes[1].trim());
            double z = Double.parseDouble(partes[2].trim());
            return (x * y * z) / 1000.0;
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private ImpressoraRespostaDTO convertToDTO(Impressora3D impressora) {
        return ImpressoraRespostaDTO.builder()
                .id(impressora.getId())
                .modelo(impressora.getModelo())
                .material(impressora.getMaterial())
                .tecnologia(impressora.getTecnologia())
                .descricao(impressora.getDescricao())
                .disponibilidade(impressora.isDisponibilidade())
                .maker(UsuarioRespostaDTO.builder()
                        .id(impressora.getMaker().getId())
                        .nome(impressora.getMaker().getNome())
                        .tipo("MAKER")
                        .latitude(impressora.getMaker().getLatitude())
                        .longitude(impressora.getMaker().getLongitude())
                        .telefone(impressora.getMaker().getTelefone())
                        .build())
                .build();
    }
}
