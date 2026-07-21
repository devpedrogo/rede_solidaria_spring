package com.devpedrogo.redesolidaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import java.util.Optional;


public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Integer>{
    Optional<UsuarioEntity> findByEmail(String email);
}
