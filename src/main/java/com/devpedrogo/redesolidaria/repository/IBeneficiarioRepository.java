package com.devpedrogo.redesolidaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;

public interface IBeneficiarioRepository extends JpaRepository<BeneficiarioEntity, Integer> {
    Optional<BeneficiarioEntity> findByEmail(String email);
}
