package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED )
    public void criarDoador(@Valid @RequestBody DoadorDto doadorDto) throws Exception {
        usuarioService.criarDoador(doadorDto);
    }

    @GetMapping
    public List<UsuarioEntity> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable Integer id) throws Exception {
        usuarioService.deletarUsuario(id);
    }
}
