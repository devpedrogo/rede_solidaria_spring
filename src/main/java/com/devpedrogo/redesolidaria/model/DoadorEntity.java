package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

@lombok.experimental.SuperBuilder
@Getter
@Setter
@DiscriminatorValue("DOADOR")
public class DoadorEntity extends UsuarioEntity {
    
}