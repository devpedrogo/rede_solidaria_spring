package com.devpedrogo.redesolidaria.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@DiscriminatorValue("BENEFICIARIO")
public class BeneficiarioEntity extends UsuarioEntity {
 
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_beneficiario")
    private TipoBeneficiario tipoBeneficiario;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_prioridade")
    private NivelPrioridade nivelPrioridade;

}