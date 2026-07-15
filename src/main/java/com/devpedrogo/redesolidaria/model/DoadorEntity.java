package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doadores")
@PrimaryKeyJoinColumn(name = "usuario_id") // Nome da coluna FK que se une à tabela base
@Schema(description = "Representa um Doador no sistema, herdando todos os dados de Usuário")
public class DoadorEntity extends UsuarioEntity {
    
}