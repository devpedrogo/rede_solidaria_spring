package com.devpedrogo.redesolidaria.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.devpedrogo.redesolidaria.dto.ItemFiltroDto;

import jakarta.persistence.criteria.Predicate;

public class ItemSpecification {

    public static Specification<ItemDoacaoEntity> comFiltros(ItemFiltroDto filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.status()));
            }

            if (filtro.categoria() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria"), filtro.categoria()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
