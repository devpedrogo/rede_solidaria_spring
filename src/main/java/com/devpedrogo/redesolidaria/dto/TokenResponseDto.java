package com.devpedrogo.redesolidaria.dto;

public record TokenResponseDto(
    String token,
    Integer id,
    String nome,
    String email,
    String role, // Ex: "ROLE_ADMIN" ou "ROLE_OPERADOR"
    long expiresIn
) {
}