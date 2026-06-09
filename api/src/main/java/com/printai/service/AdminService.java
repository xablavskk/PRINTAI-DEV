package com.printai.service;

import com.printai.dto.AprovarMakerRequestDTO;
import com.printai.dto.MakerPendenteRespostaDTO;
import com.printai.dto.ServicoMakerRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Perfil;
import com.printai.model.Usuario;
import com.printai.repository.ServicoImpressaoRepository;
import com.printai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoImpressaoRepository servicoRepository;

    @Transactional(readOnly = true)
    public List<MakerPendenteRespostaDTO> listarMakersPendentes(Long adminId) {
        validarAdmin(adminId);
        return usuarioRepository.findByPerfil(Perfil.MAKER).stream()
                .filter(u -> u.getStatusAprovacao() == null)
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MakerPendenteRespostaDTO> listarTodosMakers(Long adminId) {
        validarAdmin(adminId);
        return usuarioRepository.findByPerfil(Perfil.MAKER).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MakerPendenteRespostaDTO buscarMaker(Long adminId, Long makerId) {
        validarAdmin(adminId);
        Usuario maker = usuarioRepository.findById(makerId)
                .orElseThrow(() -> new RegraNegocioException("Maker não encontrado"));
        if (maker.getPerfil() != Perfil.MAKER) {
            throw new RegraNegocioException("Usuário não é um Maker");
        }
        return toDTO(maker);
    }

    @Transactional
    public MakerPendenteRespostaDTO processarSolicitacao(Long adminId, Long makerId, AprovarMakerRequestDTO dto) {
        validarAdmin(adminId);

        Usuario maker = usuarioRepository.findById(makerId)
                .orElseThrow(() -> new RegraNegocioException("Maker não encontrado"));

        if (maker.getPerfil() != Perfil.MAKER) {
            throw new RegraNegocioException("Usuário não é um Maker");
        }

        if (Boolean.TRUE.equals(maker.getStatusAprovacao()) || Boolean.FALSE.equals(maker.getStatusAprovacao())) {
            throw new RegraNegocioException(
                    "Esta solicitação já foi processada. Status atual: "
                    + (Boolean.TRUE.equals(maker.getStatusAprovacao()) ? "APROVADO" : "REJEITADO")
            );
        }

        if (Boolean.FALSE.equals(dto.getAprovado())
                && (dto.getMotivoRejeicao() == null || dto.getMotivoRejeicao().isBlank())) {
            throw new RegraNegocioException("Informe o motivo da rejeição");
        }

        maker.setStatusAprovacao(dto.getAprovado());
        return toDTO(usuarioRepository.save(maker));
    }

    private void validarAdmin(Long adminId) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RegraNegocioException("Administrador não encontrado"));
        if (admin.getPerfil() != Perfil.ADMINISTRADOR) {
            throw new RegraNegocioException("Acesso restrito para administradores");
        }
    }

    private MakerPendenteRespostaDTO toDTO(Usuario maker) {
        List<ServicoMakerRespostaDTO> servicos = servicoRepository.findByMaker_Id(maker.getId())
                .stream()
                .map(s -> ServicoMakerRespostaDTO.builder()
                        .id(s.getId())
                        .nome(s.getNome())
                        .precoBase(s.getPrecoBase())
                        .descricao(s.getDescricao())
                        .tipoNome(s.getTipo() != null ? s.getTipo().getNome() : null)
                        .tecnologia(s.getTecnologia())
                        .material(s.getMaterial() != null ? s.getMaterial().getNome().name() : null)
                        .suportaPecasPequenas(s.isSuportaPecasPequenas())
                        .suportaDecorativos(s.isSuportaDecorativos())
                        .suportaPrototipos(s.isSuportaPrototipos())
                        .build())
                .toList();

        return MakerPendenteRespostaDTO.builder()
                .id(maker.getId())
                .nome(maker.getNome())
                .email(maker.getEmail())
                .telefone(maker.getTelefone())
                .documentoCpfCnpj(maker.getDocumentoCpfCnpj())
                .cidade(maker.getCidade())
                .estado(maker.getEstado())
                .latitude(maker.getLatitude())
                .longitude(maker.getLongitude())
                .statusAprovacao(maker.getStatusAprovacao())
                .totalServicos(servicos.size())
                .servicos(servicos)
                .build();
    }
}
