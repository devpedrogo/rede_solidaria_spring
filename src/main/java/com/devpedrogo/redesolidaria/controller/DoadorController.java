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

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.service.DoadorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doadores")
@Tag(name = "Doadores", description = "Endpoints para criação e gestão de doadores")
public class DoadorController {

    private final DoadorService doadorService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo doador", 
        description = "Cadastra um novo doador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarDoador(@Valid @RequestBody DoadorDto doadorDto) throws Exception {
        doadorService.criarDoador(doadorDto);
    }

    @GetMapping
    @Operation(
        summary = "Listar doadores", 
        description = "Lista todos os doadores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<DoadorResponseDto> listarDoadores() {
        return doadorService.listarDoadores();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Listar doadores por ID", 
        description = "Lista os doadores cadastrados no sistema por ID."
    )
    public ResponseEntity<DoadorResponseDto> listarDoadorPorId(@PathVariable Integer id) {
        DoadorResponseDto doador = doadorService.listarPorId(id);
        return ResponseEntity.ok(doador);
    }
}
