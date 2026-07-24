package com.devpedrogo.redesolidaria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.service.OperadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operadores")
@Tag(name = "Operadores", description = "Endpoints para criação e gestão de operadores")
public class OperadorController {

    private final OperadorService operadorService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo operador", 
        description = "Cadastra um novo operador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarOperador(@Valid @RequestBody OperadorDto operadorDto) throws Exception {
        operadorService.cadastrarOperador(operadorDto);
    }
    
    @GetMapping
    @Operation(
        summary = "Listar operadores", 
        description = "Lista todos os operadores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OperadorResponseDto> listarOperadores() {
        return operadorService.listarOperadores();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Listar operadores por ID", 
        description = "Lista os operadores cadastrados no sistema por ID."
    )
    public ResponseEntity<OperadorResponseDto> listarOpPorId(@PathVariable Integer id) {
        OperadorResponseDto operador = operadorService.listarPorId(id);
        return ResponseEntity.ok(operador);
    }
}
