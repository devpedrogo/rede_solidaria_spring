package com.devpedrogo.redesolidaria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.service.BeneficiarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/beneficiarios")
@Tag(name = "Beneficiários", description = "Endpoints para criação e gestão de beneficiarios")
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo beneficiário", 
        description = "Cadastra um novo beneficiário no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarBeneficiario(@Valid @RequestBody BeneficiarioDto beneficiarioDto) throws Exception {
        beneficiarioService.criarBeneficiario(beneficiarioDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Atualizar beneficiário por ID", 
        description = "Atualiza um beneficiário no sistema a partir de um ID e um DTO."
    )
    public BeneficiarioResponseDto atualizarBeneficiario(@PathVariable Integer id, @RequestBody @Valid BeneficiarioDto beneficiarioDto){
        return beneficiarioService.atualizarBeneficiario(id, beneficiarioDto);
    }

    @GetMapping
    @Operation(
        summary = "Listar beneficiários", 
        description = "Lista todos os beneficiários cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<BeneficiarioResponseDto> listarBeneficiarios() {
        return beneficiarioService.listarBeneficiarios();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Listar beneficiarios por ID", 
        description = "Lista os beneficiarios cadastrados no sistema por ID."
    )
    public ResponseEntity<BeneficiarioResponseDto> listarBenefPorId(@PathVariable Integer id) {
        BeneficiarioResponseDto benef = beneficiarioService.listarPorId(id);
        return ResponseEntity.ok(benef);
    }
}
