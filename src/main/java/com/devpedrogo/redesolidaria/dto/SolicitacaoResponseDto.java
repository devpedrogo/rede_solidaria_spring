package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;
import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;

public record SolicitacaoResponseDto(
    Integer id,
    Integer itemId,
    String nomeItem,
    Integer beneficiarioId,
    String nomeBeneficiario,
    Integer quantidade,
    String justificativa,
    StatusSolicitacao status
) {
    public SolicitacaoResponseDto(SolicitacaoEntity entity) {
        this(
            entity.getId(),
            entity.getItem().getId(),
            entity.getItem().getNome(),
            entity.getBeneficiario().getId(),
            entity.getBeneficiario().getNome(),
            entity.getQuantidadeSolicitada(),
            entity.getJustificativa(),
            entity.getStatus()
        );
    }
}