package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.model.AdminEntity;

public record AdminResponseDto(Integer id, String nome, String email, String telefone, String endereco, StatusUsuario status) {
    public AdminResponseDto(AdminEntity entity){
        this(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getEndereco(),
            entity.getStatus()
        );
    }
}
