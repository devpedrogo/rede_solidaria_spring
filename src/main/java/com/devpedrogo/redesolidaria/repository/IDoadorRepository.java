package com.devpedrogo.redesolidaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.DoadorEntity;

public interface IDoadorRepository extends JpaRepository<DoadorEntity, Integer> {
    Optional<DoadorEntity> findByEmail(String email);
}
