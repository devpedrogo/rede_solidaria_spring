package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("OPERADOR")
public class OperadorEntity extends UsuarioEntity {
    
    @Column(name = "matricula", unique = true)
    private String matricula;

}