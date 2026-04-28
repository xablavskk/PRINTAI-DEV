package com.printai.config;

import com.printai.model.Avaliacao;
import com.printai.model.Cliente;
import com.printai.model.Impressora3D;
import com.printai.model.Maker;
import com.printai.model.ServicoImpressao;
import com.printai.repository.AvaliacaoRepository;
import com.printai.repository.ClienteRepository;
import com.printai.repository.Impressora3DRepository;
import com.printai.repository.MakerRepository;
import com.printai.repository.ServicoImpressaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MakerRepository makerRepository;
    private final ServicoImpressaoRepository servicoImpressaoRepository;
    private final Impressora3DRepository impressora3DRepository;
    private final ClienteRepository clienteRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Override
    public void run(String... args) throws Exception {
        Maker maker1 = Maker.builder()
                .nome("Adriano Maker")
                .email("adriano@printai.com")
                .senha("senha123")
                .statusAprovacao(true)
                .documentoCpfCnpj("123.456.789-00")
                .latitude(-23.550520) // Centro de SP
                .longitude(-46.633308)
                .telefone("+55 11 99999-1111")
                .build();

        Maker maker2 = Maker.builder()
                .nome("Lucas Maker")
                .email("lucas@printai.com")
                .senha("senha123")
                .statusAprovacao(true)
                .documentoCpfCnpj("00.111.222/0001-33")
                .latitude(-23.561684) // Av Paulista
                .longitude(-46.655981)
                .telefone("+55 11 98888-2222")
                .build();

        List<Maker> savedMakers = makerRepository.saveAll(List.of(maker1, maker2));
        maker1 = savedMakers.get(0);
        maker2 = savedMakers.get(1);

        Impressora3D impressora1 = Impressora3D.builder()
                .modelo("Ender 3")
                .material("PLA, ABS")
                .tecnologia("FDM")
                .volumeImpressao("220x220x250mm")
                .descricao("Impressora FDM versátil para peças mecânicas.")
                .disponibilidade(true)
                .maker(maker1)
                .build();

        Impressora3D impressora2 = Impressora3D.builder()
                .modelo("Anycubic Photon Mono")
                .material("Resina")
                .tecnologia("SLA")
                .volumeImpressao("130x80x165mm")
                .descricao("Impressora de resina para miniaturas de alta resolução.")
                .disponibilidade(true)
                .maker(maker2)
                .build();

        impressora3DRepository.saveAll(List.of(impressora1, impressora2));

        ServicoImpressao servico1 = ServicoImpressao.builder()
                .nome("Impressão FDM de Alta Precisão")
                .descricao("Serviço ideal para protótipos e peças mecânicas.")
                .condicoesServico("Prazo de 3 a 5 dias. Orçamento final após análise do arquivo STL.")
                .precoBase(50.0)
                .tecnologia("FDM")
                .material("PLA")
                .suportaPecasPequenas(true)
                .suportaDecorativos(false)
                .suportaPrototipos(true)
                .maker(maker1)
                .build();

        ServicoImpressao servico2 = ServicoImpressao.builder()
                .nome("Impressão em Resina SLA")
                .descricao("Perfeito para miniaturas e objetos decorativos.")
                .condicoesServico("Prazo de 2 dias. Acabamento premium incluso.")
                .precoBase(120.0)
                .tecnologia("SLA")
                .material("Resina")
                .suportaPecasPequenas(true)
                .suportaDecorativos(true)
                .suportaPrototipos(false)
                .maker(maker2)
                .build();

        servicoImpressaoRepository.saveAll(List.of(servico1, servico2));

        Cliente cliente1 = Cliente.builder()
                .nome("João Cliente")
                .email("joao@cliente.com")
                .senha("senha123")
                .build();
        clienteRepository.save(cliente1);

        Avaliacao aval1 = Avaliacao.builder()
                .nota(5)
                .comentario("Excelente trabalho! Peça ficou muito resistente.")
                .maker(maker1)
                .cliente(cliente1)
                .build();

        Avaliacao aval2 = Avaliacao.builder()
                .nota(4)
                .comentario("Gostei da qualidade da resina, muito detalhado.")
                .maker(maker2)
                .cliente(cliente1)
                .build();

        avaliacaoRepository.saveAll(List.of(aval1, aval2));
    }
}
