package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@DiscriminatorValue("ADMIN")
public class AdminEntity extends UsuarioEntity {
  
}