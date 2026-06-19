package com.printai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class GeocodificacaoService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converte um endereço em coordenadas geográficas usando a API Nominatim (OpenStreetMap).
     * Tenta primeiro com endereço completo, depois faz fallback progressivo até cidade+estado.
     * Retorna null se não encontrar resultado em nenhuma tentativa.
     */
    public double[] geocodificar(String logradouro, String numero, String bairro,
                                  String cidade, String estado, String cep, String pais) {
        String paisFinal = pais != null && !pais.isBlank() ? pais : "Brasil";
        String street = logradouro + (numero != null && !numero.isBlank() ? " " + numero : "");

        double[] resultado = tentarEstruturado(street, cidade, estado, cep, paisFinal);
        if (resultado != null) return resultado;

        resultado = tentarEstruturado(logradouro, cidade, estado, cep, paisFinal);
        if (resultado != null) return resultado;

        resultado = tentarEstruturado(null, cidade, estado, null, paisFinal);
        if (resultado != null) {
            log.warn("Geocodificação usou fallback cidade+estado para: {} - {}", cidade, estado);
            return resultado;
        }

        log.warn("Nominatim não retornou resultados para nenhuma tentativa. Cidade: {}, Estado: {}", cidade, estado);
        return null;
    }

    private double[] tentarEstruturado(String street, String city, String state, String postalcode, String country) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NOMINATIM_URL)
                    .queryParam("format", "json")
                    .queryParam("limit", "1")
                    .queryParam("countrycodes", "br")
                    .queryParam("addressdetails", "0");

            if (street != null && !street.isBlank())      builder.queryParam("street", street);
            if (city != null && !city.isBlank())          builder.queryParam("city", city);
            if (state != null && !state.isBlank())        builder.queryParam("state", state);
            if (postalcode != null && !postalcode.isBlank()) builder.queryParam("postalcode", postalcode.replaceAll("\\D", ""));
            builder.queryParam("country", country);

            URI uri = builder.build().toUri();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", "PrintAI/1.0 (projeto academico)")
                    .header("Accept-Language", "pt-BR")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode results = objectMapper.readTree(response.body());
                if (results.isArray() && !results.isEmpty()) {
                    JsonNode first = results.get(0);
                    double lat = first.get("lat").asDouble();
                    double lon = first.get("lon").asDouble();
                    log.info("Geocodificação OK [street={}, city={}, state={}]: lat={}, lon={}", street, city, state, lat, lon);
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            log.error("Erro ao chamar Nominatim: {}", e.getMessage());
        }
        return null;
    }
}
