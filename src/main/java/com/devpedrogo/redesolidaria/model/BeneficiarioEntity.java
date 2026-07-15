package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;

import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "beneficiarios")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Schema(description = "Representa um Beneficiário com critérios de prioridade e tipo de entidade")
public class BeneficiarioEntity extends UsuarioEntity {

    @NotNull(message = "O tipo de beneficiário é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_beneficiario")
    @Schema(description = "Categoria do beneficiário", example = "ONG")
    private TipoBeneficiario tipoBeneficiario; // Enum: FAMILIA, ONG, ESCOLA, ABRIGO, etc.

    @NotNull(message = "O nível de prioridade é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "nivel_prioridade")
    @Schema(description = "Nível de urgência para atendimento prioritário", example = "CRITICA")
    private NivelPrioridade nivelPrioridade;

}