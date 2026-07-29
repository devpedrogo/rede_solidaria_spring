package com.devpedrogo.redesolidaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO específica para atualização no Spring Boot
public record AdminUpdateDto(
    @NotBlank(message = "Nome é obrigatório") String nome,
    String telefone,
    @Email @NotBlank(message = "E-mail é obrigatório") String email,
    String endereco,
    String senha // Sem @NotBlank nem @NotNull para ser opcional
) {}
