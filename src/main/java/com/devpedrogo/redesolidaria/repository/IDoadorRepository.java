package com.devpedrogo.redesolidaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.DoadorEntity;

public interface IDoadorRepository extends JpaRepository<DoadorEntity, Integer> {

}
