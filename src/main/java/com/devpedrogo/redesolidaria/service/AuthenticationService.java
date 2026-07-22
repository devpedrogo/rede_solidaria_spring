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
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    @Value("${jwt.expiration}")
    private long expirationTime;

    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.senha()));

            //authentication provider -> userdetailsservice -> passwordEncoder.matches() -> autenticado

            String token = tokenProvider.gerarToken(authentication);

            return new TokenResponseDto(token, expirationTime);
        }catch(BadCredentialsException e){
            throw new RegraDeNegocioException("Credencias Inválidas");
        }catch(Exception e){
            throw e;
        }
    }
}

