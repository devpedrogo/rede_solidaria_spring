package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;

// DoadorResponseDto.java
public record BeneficiarioResponseDto(
    Integer id,
    String nome,
    String email,
    String telefone,
    TipoBeneficiario tipoBeneficiario,
    NivelPrioridade nivelPrioridade
) {
    // Construtor auxiliar para facilitar a conversao no Service
    public BeneficiarioResponseDto(BeneficiarioEntity entity) {
        this(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getTipoBeneficiario(),
            entity.getNivelPrioridade()
        );
    }
}