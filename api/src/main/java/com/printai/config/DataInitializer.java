package com.printai.config;

import com.printai.model.PrintService;
import com.printai.model.User;
import com.printai.repository.PrintServiceRepository;
import com.printai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PrintServiceRepository printServiceRepository;

    @Override
    public void run(String... args) throws Exception {
        User maker1 = User.builder()
                .name("Adriano Maker")
                .email("adriano@printai.com")
                .role("MAKER")
                .latitude(-23.550520) // Centro de SP
                .longitude(-46.633308)
                .phone("+55 11 99999-1111")
                .build();

        User maker2 = User.builder()
                .name("Lucas Maker")
                .email("lucas@printai.com")
                .role("MAKER")
                .latitude(-23.561684) // Av Paulista
                .longitude(-46.655981)
                .phone("+55 11 98888-2222")
                .build();

        userRepository.saveAll(List.of(maker1, maker2));

        PrintService service1 = PrintService.builder()
                .name("Impressão FDM de Alta Precisão")
                .description("Serviço ideal para protótipos e peças mecânicas.")
                .technology("FDM")
                .material("PLA")
                .isSmallPieceCapable(true)
                .isDecorativeCapable(false)
                .isPrototypeCapable(true)
                .maker(maker1)
                .build();

        PrintService service2 = PrintService.builder()
                .name("Impressão em Resina SLA")
                .description("Perfeito para miniaturas e objetos decorativos.")
                .technology("SLA")
                .material("Resin")
                .isSmallPieceCapable(true)
                .isDecorativeCapable(true)
                .isPrototypeCapable(false)
                .maker(maker2)
                .build();

        printServiceRepository.saveAll(List.of(service1, service2));
    }
}
