package com.printai.config;

import com.printai.repository.TipoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TipoRepository tipoRepository;

    @Override
    public void run(String... args) {
        long totalTipos = tipoRepository.count();
        if (totalTipos == 0) {
            log.warn("=======================================================");
            log.warn("ATENÇÃO: Nenhum tipo de impressão encontrado no banco.");
            log.warn("Execute o script de seed antes de usar o sistema:");
            log.warn("  src/main/resources/db/seed.sql");
            log.warn("=======================================================");
        } else {
            log.info("Dados de referência OK: {} tipo(s) de impressão carregado(s).", totalTipos);
        }
    }
}
