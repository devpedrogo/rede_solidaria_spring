package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.service.DoadorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/doadores")
@RequiredArgsConstructor
public class DoadorController {
    private final DoadorService doadorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarDoador(@Valid @RequestBody DoadorDto doadorDto) throws Exception {
        doadorService.criarDoador(doadorDto);
    }
}
