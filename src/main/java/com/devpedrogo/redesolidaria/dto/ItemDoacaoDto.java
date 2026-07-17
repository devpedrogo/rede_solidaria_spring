package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDoacaoDto {

    @NotBlank(message = "O nome do item é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 and 100 caracteres")
    private String nome;

    @NotNull(message = "A categoria é obrigatória")
    private CategoriaItem categoria;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade doada deve ser de pelo menos 1 unidade")
    private Integer quantidadeDoada;

    @NotNull(message = "O ID do doador é obrigatório")
    private Integer doadorId;
}