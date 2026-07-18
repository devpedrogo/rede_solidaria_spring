package com.devpedrogo.redesolidaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.OperadorEntity;

public interface OperadorRepository extends JpaRepository<OperadorEntity, Integer> {
    Optional<OperadorEntity> findByMatricula(String matricula);

    Optional<OperadorEntity> findByEmail(String email);
}
