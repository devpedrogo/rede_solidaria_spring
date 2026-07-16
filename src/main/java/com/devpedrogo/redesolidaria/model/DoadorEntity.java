package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("DOADOR")
@Entity
public class DoadorEntity extends UsuarioEntity {
    
}