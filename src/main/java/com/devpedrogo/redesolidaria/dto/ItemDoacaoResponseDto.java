package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;

public record ItemDoacaoResponseDto(
    String nome,
    CategoriaItem categoria,
    StatusItem status, // 🟢 Adicionado para refletir o momento do ciclo de vida
    Integer quantidade
) {
    // Caso queira usar o mapeamento direto via construtor compacto
    public ItemDoacaoResponseDto(ItemDoacaoEntity item) {
        this(
            item.getNome(),
            item.getCategoria(),
            item.getStatus(), // Garanta que a ItemDoacaoEntity tenha esse enum/campo
            item.getQuantidade()
        );
    }
}
