package com.devpedrogo.redesolidaria.dto;

import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.StatusItem;

public record ItemFiltroDto(
    StatusItem status,
    CategoriaItem categoria
) {}
