package com.printai.config;

import com.printai.model.*;
import com.printai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServicoImpressaoRepository servicoImpressaoRepository;
    private final Impressora3DRepository impressora3DRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final TipoRepository tipoRepository;
    private final TecnologiaRepository tecnologiaRepository;
    private final MaterialRepository materialRepository;

    @Override
    public void run(String... args) throws Exception {
        Tipo tipoFilamento = tipoRepository.save(Tipo.builder()
                .nome("Filamento")
                .descricao("Impressão por deposição de material fundido. Ideal para peças mecânicas e protótipos.")
                .build());

        tecnologiaRepository.saveAll(List.of(
                Tecnologia.builder().nome(TecnologiaTipo.FDM).tipo(tipoFilamento).build(),
                Tecnologia.builder().nome(TecnologiaTipo.MJF).tipo(tipoFilamento).build()
        ));

        Tipo tipoResina = tipoRepository.save(Tipo.builder()
                .nome("Resina")
                .descricao("Impressão com resina fotopolimerizável. Alta resolução para miniaturas e detalhes finos.")
                .build());

        tecnologiaRepository.saveAll(List.of(
                Tecnologia.builder().nome(TecnologiaTipo.SLA).tipo(tipoResina).build(),
                Tecnologia.builder().nome(TecnologiaTipo.DLP).tipo(tipoResina).build()
        ));

        Tipo tipoPo = tipoRepository.save(Tipo.builder()
                .nome("Pó")
                .descricao("Impressão por sinterização ou aglutinação de pó. Alta resistência e geometrias complexas.")
                .build());

        tecnologiaRepository.saveAll(List.of(
                Tecnologia.builder().nome(TecnologiaTipo.SLS).tipo(tipoPo).build(),
                Tecnologia.builder().nome(TecnologiaTipo.BINDER_JETTING).tipo(tipoPo).build()
        ));

        Material matPLA = materialRepository.save(Material.builder()
                .nome(MaterialTipo.PLA)
                .descricao("Material mais comum. Fácil de imprimir, biodegradável, ideal para protótipos.")
                .build());

        Material matABS = materialRepository.save(Material.builder()
                .nome(MaterialTipo.ABS)
                .descricao("Alta resistência mecânica e térmica. Requer ambiente controlado.")
                .build());

        Material matResina = materialRepository.save(Material.builder()
                .nome(MaterialTipo.RESINA)
                .descricao("Alta resolução e detalhamento. Usado em impressoras SLA e DLP.")
                .build());

        materialRepository.saveAll(List.of(
                Material.builder().nome(MaterialTipo.PETG).descricao("Resistente e flexível. Boa adesão entre camadas.").build(),
                Material.builder().nome(MaterialTipo.TPU).descricao("Material flexível e elástico.").build(),
                Material.builder().nome(MaterialTipo.NYLON).descricao("Alta resistência ao impacto e desgaste.").build(),
                Material.builder().nome(MaterialTipo.ASA).descricao("Resistente a UV. Indicado para uso externo.").build(),
                Material.builder().nome(MaterialTipo.PEEK).descricao("Alta performance. Resistência química e térmica extrema.").build(),
                Material.builder().nome(MaterialTipo.CARBON_FIBER).descricao("Fibra de carbono. Leveza e rigidez elevadas.").build()
        ));

        Usuario maker1 = usuarioRepository.save(Usuario.builder()
                .nome("Adriano Maker")
                .email("adriano@printai.com")
                .senha("senha123")
                .perfil(Perfil.MAKER)
                .statusAprovacao(true)
                .documentoCpfCnpj("123.456.789-00")
                .latitude(-23.550520)
                .longitude(-46.633308)
                .telefone("+55 11 99999-1111")
                .cidade("São Paulo")
                .estado("SP")
                .build());

        Usuario maker2 = usuarioRepository.save(Usuario.builder()
                .nome("Lucas Maker")
                .email("lucas@printai.com")
                .senha("senha123")
                .perfil(Perfil.MAKER)
                .statusAprovacao(true)
                .documentoCpfCnpj("00.111.222/0001-33")
                .latitude(-23.561684)
                .longitude(-46.655981)
                .telefone("+55 11 98888-2222")
                .cidade("São Paulo")
                .estado("SP")
                .build());

        impressora3DRepository.saveAll(List.of(
                Impressora3D.builder()
                        .modelo("Ender 3")
                        .material(matPLA)
                        .tipo(tipoFilamento)
                        .tecnologia("FDM")
                        .volumeImpressao("220x220x250mm")
                        .descricao("Impressora FDM versátil para peças mecânicas.")
                        .disponibilidade(true)
                        .maker(maker1)
                        .build(),
                Impressora3D.builder()
                        .modelo("Anycubic Photon Mono")
                        .material(matResina)
                        .tipo(tipoResina)
                        .tecnologia("SLA")
                        .volumeImpressao("130x80x165mm")
                        .descricao("Impressora de resina para miniaturas de alta resolução.")
                        .disponibilidade(true)
                        .maker(maker2)
                        .build()
        ));

        servicoImpressaoRepository.saveAll(List.of(
                ServicoImpressao.builder()
                        .nome("Impressão FDM de Alta Precisão")
                        .descricao("Serviço ideal para protótipos e peças mecânicas.")
                        .condicoesServico("Prazo de 3 a 5 dias. Orçamento final após análise do arquivo STL.")
                        .precoBase(50.0)
                        .tipo(tipoFilamento)
                        .tecnologia("FDM")
                        .material("PLA")
                        .suportaPecasPequenas(true)
                        .suportaDecorativos(false)
                        .suportaPrototipos(true)
                        .maker(maker1)
                        .build(),
                ServicoImpressao.builder()
                        .nome("Impressão em Resina SLA")
                        .descricao("Perfeito para miniaturas e objetos decorativos.")
                        .condicoesServico("Prazo de 2 dias. Acabamento premium incluso.")
                        .precoBase(120.0)
                        .tipo(tipoResina)
                        .tecnologia("SLA")
                        .material("Resina")
                        .suportaPecasPequenas(true)
                        .suportaDecorativos(true)
                        .suportaPrototipos(false)
                        .maker(maker2)
                        .build()
        ));

        Usuario cliente1 = usuarioRepository.save(Usuario.builder()
                .nome("João Cliente")
                .email("joao@cliente.com")
                .senha("senha123")
                .perfil(Perfil.CLIENTE)
                .cidade("São Paulo")
                .estado("SP")
                .build());

        avaliacaoRepository.saveAll(List.of(
                AvaliacaoMaker.builder()
                        .nota(5)
                        .comentario("Excelente trabalho! Peça ficou muito resistente.")
                        .maker(maker1)
                        .cliente(cliente1)
                        .build(),
                AvaliacaoMaker.builder()
                        .nota(4)
                        .comentario("Gostei da qualidade da resina, muito detalhado.")
                        .maker(maker2)
                        .cliente(cliente1)
                        .build()
        ));
    }
}
