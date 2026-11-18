package com.devpedrogo.redesolidaria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.config.TokenProvider;
import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.dto.TokenResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Autentica o usuário, impede o acesso de contas inativas e gera o token JWT
     * com os dados básicos e o perfil principal do usuário.
     *
     * @param loginRequestDto credenciais de e-mail e senha
     * @return token de acesso e informações do usuário autenticado
     * @throws RegraDeNegocioException se as credenciais forem inválidas ou a conta estiver inativa
     */
    @SuppressWarnings("null") // pra parar o warning no vscode
    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.senha()));

            //authentication provider -> userdetailsservice -> passwordEncoder.matches() -> autenticado

            UsuarioEntity usuario = (UsuarioEntity) authentication.getPrincipal();

            // Verificando se o usuario está inativo e impedindo o login
            if(usuario.getStatus().equals(StatusUsuario.INATIVO)){
                throw new RegraDeNegocioException("Usuario INATIVO no sistema, não é possivel logar");
            }

            // 1. Pega a role principal da coleção de perfis
            String role = usuario.getPerfis().stream()
                .map(Perfil::getAuthority) // Pega a String "ROLE_ADMIN", etc.
                .filter(r -> r.equals("ROLE_ADMIN")) // Dá prioridade se for Admin
                .findFirst()
                .orElseGet(() -> usuario.getPerfis().stream()
                    .map(Perfil::getAuthority)
                    .findFirst()
                    .orElse("ROLE_OPERADOR")); // Fallback caso esteja vazio

            String token = tokenProvider.gerarToken(authentication);

            return new TokenResponseDto(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                role,
                expirationTime
            );
        }catch(BadCredentialsException e){
            throw new RegraDeNegocioException("Credencias Inválidas");
        }catch(Exception e){
            throw e;
        }
    }
}

