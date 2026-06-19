package com.printai.service;

import com.printai.dto.ServicoImpressaoRequestDTO;
import com.printai.dto.ServicoMakerRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Material;
import com.printai.model.MaterialTipo;
import com.printai.model.Perfil;
import com.printai.model.ServicoImpressao;
import com.printai.model.Tipo;
import com.printai.model.Usuario;
import com.printai.repository.MaterialRepository;
import com.printai.repository.ServicoImpressaoRepository;
import com.printai.repository.TipoRepository;
import com.printai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManterServicosImpressaoService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoImpressaoRepository servicoRepository;
    private final TipoRepository tipoRepository;
    private final MaterialRepository materialRepository;

    @Transactional(readOnly = true)
    public List<ServicoMakerRespostaDTO> listarServicos(Long makerId) {
        validarMaker(makerId);
        return servicoRepository.findByMaker_Id(makerId).stream()
                .map(this::toServicoResposta)
                .toList();
    }

    @Transactional
    public ServicoMakerRespostaDTO criarServico(Long makerId, ServicoImpressaoRequestDTO dto) {
        Usuario maker = validarMaker(makerId);
        ServicoImpressao servico = construirServico(dto, maker);
        return toServicoResposta(servicoRepository.save(servico));
    }

    @Transactional
    public ServicoMakerRespostaDTO editarServico(Long makerId, Long servicoId, ServicoImpressaoRequestDTO dto) {
        validarMaker(makerId);

        ServicoImpressao servico = servicoRepository.findByIdAndMaker_Id(servicoId, makerId)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"));

        Tipo tipo = dto.getTipoId() != null
                ? tipoRepository.findById(dto.getTipoId()).orElse(null)
                : null;

        servico.setNome(dto.getNome());
        servico.setPrecoBase(dto.getPrecoBase());
        servico.setDescricao(dto.getDescricao());
        servico.setCondicoesServico(dto.getCondicoesServico());
        servico.setTipo(tipo);
        servico.setTecnologia(dto.getTecnologia());
        servico.setMaterial(resolverMaterial(dto.getMaterial()));
        servico.setSuportaPecasPequenas(dto.isSuportaPecasPequenas());
        servico.setSuportaDecorativos(dto.isSuportaDecorativos());
        servico.setSuportaPrototipos(dto.isSuportaPrototipos());

        return toServicoResposta(servicoRepository.save(servico));
    }

    @Transactional
    public void removerServico(Long makerId, Long servicoId) {
        validarMaker(makerId);
        ServicoImpressao servico = servicoRepository.findByIdAndMaker_Id(servicoId, makerId)
                .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado ou não pertence a este Maker"));
        servicoRepository.delete(servico);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Usuario validarMaker(Long makerId) {
        Usuario maker = usuarioRepository.findById(makerId)
                .orElseThrow(() -> new RegraNegocioException("Maker não encontrado"));

        if (maker.getPerfil() != Perfil.MAKER) {
            throw new RegraNegocioException("Acesso restrito para Makers");
        }

        if (!Boolean.TRUE.equals(maker.getStatusAprovacao())) {
            throw new RegraNegocioException(
                "Sua conta ainda não foi aprovada pelo administrador. " +
                "Aguarde a análise da sua solicitação de cadastro."
            );
        }

        return maker;
    }

    private Material resolverMaterial(String materialNome) {
        if (materialNome == null || materialNome.isBlank()) return null;
        try {
            MaterialTipo tipo = MaterialTipo.valueOf(materialNome.toUpperCase().trim());
            return materialRepository.findByNome(tipo)
                    .orElseThrow(() -> new RegraNegocioException("Material não encontrado: " + materialNome));
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Material inválido: " + materialNome);
        }
    }

    private ServicoImpressao construirServico(ServicoImpressaoRequestDTO dto, Usuario maker) {
        Tipo tipo = dto.getTipoId() != null
                ? tipoRepository.findById(dto.getTipoId()).orElse(null)
                : null;

        return ServicoImpressao.builder()
                .nome(dto.getNome())
                .precoBase(dto.getPrecoBase())
                .descricao(dto.getDescricao())
                .condicoesServico(dto.getCondicoesServico())
                .tipo(tipo)
                .tecnologia(dto.getTecnologia())
                .material(resolverMaterial(dto.getMaterial()))
                .suportaPecasPequenas(dto.isSuportaPecasPequenas())
                .suportaDecorativos(dto.isSuportaDecorativos())
                .suportaPrototipos(dto.isSuportaPrototipos())
                .maker(maker)
                .build();
    }

    private ServicoMakerRespostaDTO toServicoResposta(ServicoImpressao s) {
        return ServicoMakerRespostaDTO.builder()
                .id(s.getId())
                .nome(s.getNome())
                .precoBase(s.getPrecoBase())
                .descricao(s.getDescricao())
                .condicoesServico(s.getCondicoesServico())
                .tipoId(s.getTipo() != null ? s.getTipo().getId() : null)
                .tipoNome(s.getTipo() != null ? s.getTipo().getNome() : null)
                .tecnologia(s.getTecnologia())
                .material(s.getMaterial() != null ? s.getMaterial().getNome().name() : null)
                .suportaPecasPequenas(s.isSuportaPecasPequenas())
                .suportaDecorativos(s.isSuportaDecorativos())
                .suportaPrototipos(s.isSuportaPrototipos())
                .build();
    }
}
