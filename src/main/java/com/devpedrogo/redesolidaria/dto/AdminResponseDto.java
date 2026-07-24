package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;

public record AdminResponseDto(String nome, String email, String telefone, StatusUsuario status) {
    public AdminResponseDto(AdminEntity entity){
        this(
            entity.getNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getStatus()
        );
    }
}
