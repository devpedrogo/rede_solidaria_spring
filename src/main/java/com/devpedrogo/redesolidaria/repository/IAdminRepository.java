package com.devpedrogo.redesolidaria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devpedrogo.redesolidaria.model.AdminEntity;

public interface IAdminRepository extends JpaRepository<AdminEntity, Integer>{
}
