package com.printai.service;

import com.printai.dto.LoginRequestDTO;
import com.printai.dto.LoginRespostaDTO;
import com.printai.exception.RegraNegocioException;
import com.printai.model.Perfil;
import com.printai.model.Usuario;
import com.printai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public LoginRespostaDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha incorretos"));

        if (!usuario.getSenha().equals(dto.getSenha())) {
            throw new RegraNegocioException("E-mail ou senha incorretos");
        }

        if (usuario.getPerfil() == Perfil.ADMINISTRADOR) {
            throw new RegraNegocioException("Acesso restrito. Use o painel administrativo.");
        }

        if (usuario.getPerfil() == Perfil.MAKER && !Boolean.TRUE.equals(usuario.getStatusAprovacao())) {
            throw new RegraNegocioException("Sua conta de Maker ainda não foi aprovada pelo administrador.");
        }

        return toLoginResposta(usuario);
    }

    public LoginRespostaDTO loginAdmin(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha incorretos"));

        if (!usuario.getSenha().equals(dto.getSenha())) {
            throw new RegraNegocioException("E-mail ou senha incorretos");
        }

        if (usuario.getPerfil() != Perfil.ADMINISTRADOR) {
            throw new RegraNegocioException("Acesso restrito ao painel administrativo.");
        }

        return toLoginResposta(usuario);
    }

    private LoginRespostaDTO toLoginResposta(Usuario usuario) {
        return LoginRespostaDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .cidade(usuario.getCidade())
                .estado(usuario.getEstado())
                .build();
    }
}
