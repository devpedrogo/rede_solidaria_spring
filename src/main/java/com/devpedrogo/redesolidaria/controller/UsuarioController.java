package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.service.DoadorService;
import com.devpedrogo.redesolidaria.service.OperadorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final DoadorService doadorService;
    private final OperadorService operadorService;

    @PostMapping("/doadores")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarDoador(@Valid @RequestBody DoadorDto doadorDto) throws Exception {
        doadorService.criarDoador(doadorDto);
    }

    @GetMapping("/doadores")
    @ResponseStatus(HttpStatus.OK)
    public List<DoadorEntity> listarDoadores() {
        return doadorService.listarDoadores();
    }

    @PostMapping("/operadores")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarOperador(@Valid @RequestBody OperadorDto operadorDto) throws Exception {
        operadorService.cadastrarOperador(operadorDto);
    }
    
    @GetMapping("/operadores")
    @ResponseStatus(HttpStatus.OK)
    public List<OperadorEntity> listarOperadores() {
        return operadorService.listarOperadores();
    }
}
