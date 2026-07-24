package com.devpedrogo.redesolidaria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devpedrogo.redesolidaria.dto.AdminDto;
import com.devpedrogo.redesolidaria.dto.AdminResponseDto;
import com.devpedrogo.redesolidaria.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "Endpoints para criação e gestão de administradores")
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo admin", 
        description = "Cadastra um novo administrador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public AdminResponseDto cadastrarAdmin(AdminDto adminDto){
        return adminService.cadastrarAdmin(adminDto);
    }

    @GetMapping
    @Operation(
        summary = "Listar administradores", 
        description = "Lista todos os administradores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<AdminResponseDto> listarAdmins() {
        return adminService.listarAdmins();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Listar administradores por ID", 
        description = "Lista os operadores cadastrados no sistema por ID."
    )
    public ResponseEntity<AdminResponseDto> listarAdmPorId(@PathVariable Integer id) {
        AdminResponseDto admin = adminService.listarPorId(id);
        return ResponseEntity.ok(admin);
    }
}
