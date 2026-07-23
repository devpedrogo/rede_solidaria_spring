package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.dto.TokenResponseDto;
import com.devpedrogo.redesolidaria.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para gestão de autenticações no sistema")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
        summary = "Efetua o login do usuário", 
        description = "Recebe as credenciais (email e senha) e devolve o token JWT caso sejam válidas."
    )
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        return authenticationService.login(loginRequestDto);
    }
}
