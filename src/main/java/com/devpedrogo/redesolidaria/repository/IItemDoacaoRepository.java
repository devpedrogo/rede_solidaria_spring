package com.devpedrogo.redesolidaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;

public interface IItemDoacaoRepository extends JpaRepository<ItemDoacaoEntity, Integer> {
    Optional<ItemDoacaoEntity> findByNomeIgnoreCaseAndCategoria(String nome, CategoriaItem categoria);
}
