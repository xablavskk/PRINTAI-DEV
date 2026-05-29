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

        // Comparação de senha em plaintext para simplicidade acadêmica do projeto atual
        if (!usuario.getSenha().equals(dto.getSenha())) {
            throw new RegraNegocioException("E-mail ou senha incorretos");
        }

        if (usuario.getPerfil() != Perfil.CLIENTE) {
            throw new RegraNegocioException("Acesso restrito para perfis de CLIENTE neste canal.");
        }

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
