package com.devpedrogo.redesolidaria.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoCreateDto(
    @NotNull(message = "O ID do item é obrigatório")
    Integer itemId,

    @NotNull(message = "O ID do beneficiário é obrigatório")
    Integer beneficiarioId,

    @NotNull(message = "A quantidade solicitada é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1")
    Integer quantidadeSolicitada,

    String justificativa
) {}
