package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.model.DoadorEntity;

// DoadorResponseDto.java
public record DoadorResponseDto(
    Integer id,
    String nome,
    String telefone
) {
    // Construtor auxiliar para facilitar a conversao no Service
    public DoadorResponseDto(DoadorEntity entity) {
        this(
            entity.getId(),
            entity.getNome(),
            entity.getTelefone()
        );
    }
}
