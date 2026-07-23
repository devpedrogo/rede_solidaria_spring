package com.devpedrogo.redesolidaria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoCreateDto(
    @Schema(description = "ID do item solicitado", example = "3")
    @NotNull(message = "O ID do item é obrigatório")
    Integer itemId,

    @Schema(description = "ID do Beneficiário solicitante", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID do beneficiário é obrigatório")
    Integer beneficiarioId,

    @NotNull(message = "A quantidade solicitada é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1")
    Integer quantidadeSolicitada,

    @Schema(description = "Descrição detalhada da necessidade", example = "Cesta básica para família com 4 pessoas")
    String justificativa
) {}
