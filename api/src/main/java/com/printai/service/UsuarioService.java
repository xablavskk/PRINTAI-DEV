package com.printai.service;

import com.printai.dto.CadastroClienteRequestDTO;
import com.printai.dto.CadastroClienteRespostaDTO;
import com.printai.dto.CadastroMakerRequestDTO;
import com.printai.dto.CadastroMakerRespostaDTO;
import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.model.*;
import com.printai.repository.MaterialRepository;
import com.printai.repository.ServicoImpressaoRepository;
import com.printai.repository.TipoRepository;
import com.printai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoImpressaoRepository servicoImpressaoRepository;
    private final TipoRepository tipoRepository;
    private final GeocodificacaoService geocodificacaoService;
    private final MaterialRepository materialRepository;

    @Transactional
    public CadastroMakerRespostaDTO cadastrarMaker(CadastroMakerRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + dto.getEmail());
        }

        Double latitude = null;
        Double longitude = null;

        double[] coordenadas = geocodificacaoService.geocodificar(
                dto.getLogradouro(),
                dto.getNumero(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                dto.getCep(),
                dto.getPais()
        );

        if (coordenadas != null) {
            latitude = coordenadas[0];
            longitude = coordenadas[1];
        } else {
            log.warn("Não foi possível geocodificar o endereço do Maker '{}'. Cadastro salvo sem coordenadas.", dto.getNome());
        }

        Endereco endereco = Endereco.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .cep(dto.getCep())
                .pais(dto.getPais() != null ? dto.getPais() : "Brasil")
                .build();

        Usuario maker = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha()) 
                .telefone(dto.getTelefone())
                .documentoCpfCnpj(dto.getDocumentoCpfCnpj())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .endereco(endereco)
                .latitude(latitude)
                .longitude(longitude)
                .perfil(Perfil.MAKER)
                .statusAprovacao(null) // pendente — aguarda aprovação do administrador
                .build();

        Usuario salvo = usuarioRepository.save(maker);

        int totalServicos = 0;
        if (dto.getServicos() != null && !dto.getServicos().isEmpty()) {
            List<ServicoImpressao> servicos = new ArrayList<>();
            for (ServicoImpressaoRequestDTO s : dto.getServicos()) {
                Tipo tipo = null;
                if (s.getTipoId() != null) {
                    tipo = tipoRepository.findById(s.getTipoId())
                            .orElseThrow(() -> new IllegalArgumentException("Tipo de impressão não encontrado: id=" + s.getTipoId()));
                }

                Material materialObj = null;
                if (s.getMaterial() != null && !s.getMaterial().isBlank()) {
                    try {
                        MaterialTipo materialTipo = MaterialTipo.valueOf(s.getMaterial().toUpperCase().trim());
                        materialObj = materialRepository.findByNome(materialTipo)
                                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado: " + s.getMaterial()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Material inválido: " + s.getMaterial());
                    }
                }

                servicos.add(ServicoImpressao.builder()
                        .nome(s.getNome())
                        .precoBase(s.getPrecoBase())
                        .descricao(s.getDescricao())
                        .condicoesServico(s.getCondicoesServico())
                        .tipo(tipo)
                        .tecnologia(s.getTecnologia())
                        .material(materialObj)
                        .suportaPecasPequenas(s.isSuportaPecasPequenas())
                        .suportaDecorativos(s.isSuportaDecorativos())
                        .suportaPrototipos(s.isSuportaPrototipos())
                        .maker(salvo)
                        .build());
            }
            servicoImpressaoRepository.saveAll(servicos);
            totalServicos = servicos.size();
            log.info("Maker '{}' cadastrado com {} serviço(s) de impressão.", salvo.getNome(), totalServicos);
        }

        String mensagem = totalServicos > 0
                ? String.format("Solicitação enviada! %d serviço(s) cadastrado(s). Aguarde a aprovação do administrador para ativar sua conta.", totalServicos)
                : "Solicitação enviada! Aguarde a aprovação do administrador para ativar sua conta.";

        return CadastroMakerRespostaDTO.builder()
                .id(salvo.getId())
                .nome(salvo.getNome())
                .email(salvo.getEmail())
                .telefone(salvo.getTelefone())
                .cidade(salvo.getCidade())
                .estado(salvo.getEstado())
                .latitude(salvo.getLatitude())
                .longitude(salvo.getLongitude())
                .statusAprovacao(salvo.getStatusAprovacao())
                .totalServicos(totalServicos)
                .mensagem(mensagem)
                .build();
    }

    @Transactional
    public CadastroClienteRespostaDTO cadastrarCliente(CadastroClienteRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + dto.getEmail());
        }

        Double latitude = null;
        Double longitude = null;

        // Endereço é opcional para cliente — geocodifica só se cidade foi informada
        if (dto.getCidade() != null && !dto.getCidade().isBlank()) {
            double[] coordenadas = geocodificacaoService.geocodificar(
                    dto.getLogradouro(), dto.getNumero(), dto.getBairro(),
                    dto.getCidade(), dto.getEstado(), dto.getCep(),
                    dto.getPais() != null ? dto.getPais() : "Brasil"
            );
            if (coordenadas != null) {
                latitude = coordenadas[0];
                longitude = coordenadas[1];
            } else {
                log.warn("Não foi possível geocodificar o endereço do Cliente '{}'.", dto.getNome());
            }
        }

        Endereco endereco = Endereco.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .cep(dto.getCep())
                .pais(dto.getPais() != null ? dto.getPais() : "Brasil")
                .build();

        Usuario cliente = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .endereco(endereco)
                .latitude(latitude)
                .longitude(longitude)
                .perfil(Perfil.CLIENTE)
                .statusAprovacao(true)
                .build();

        Usuario salvo = usuarioRepository.save(cliente);
        log.info("Cliente '{}' cadastrado com sucesso.", salvo.getNome());

        return CadastroClienteRespostaDTO.builder()
                .id(salvo.getId())
                .nome(salvo.getNome())
                .email(salvo.getEmail())
                .telefone(salvo.getTelefone())
                .cidade(salvo.getCidade())
                .estado(salvo.getEstado())
                .latitude(salvo.getLatitude())
                .longitude(salvo.getLongitude())
                .mensagem("Cadastro realizado com sucesso!")
                .build();
    }
}
