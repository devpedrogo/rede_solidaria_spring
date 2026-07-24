package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.model.OperadorEntity;

// OperadorResponseDto.java
public record OperadorResponseDto(
    Integer id,
    String nome,
    String email,
    String telefone,
    String endereco,
    String matricula
) {
    // Construtor auxiliar para facilitar a conversao no Service
    public OperadorResponseDto(OperadorEntity entity) {
        this(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getEndereco(),
            entity.getMatricula()
        );
    }
}
