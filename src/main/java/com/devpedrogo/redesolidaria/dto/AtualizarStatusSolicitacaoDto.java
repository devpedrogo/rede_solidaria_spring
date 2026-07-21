package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;

import jakarta.validation.constraints.NotNull;

public record AtualizarStatusSolicitacaoDto(
    @NotNull(message = "O novo status é obrigatório")
    StatusSolicitacao status
) {}
