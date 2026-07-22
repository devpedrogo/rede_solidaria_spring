package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.service.BeneficiarioService;
import com.devpedrogo.redesolidaria.service.DoadorService;
import com.devpedrogo.redesolidaria.service.OperadorService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para criação e gestão de usuários")
public class UsuarioController {
    private final DoadorService doadorService;
    private final OperadorService operadorService;
    private final BeneficiarioService beneficiarioService;

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

    @GetMapping("/doadores/{id}")
    public ResponseEntity<DoadorResponseDto> listarDoadorPorId(@PathVariable Integer id) {
        DoadorResponseDto doador = doadorService.listarPorId(id);
        return ResponseEntity.ok(doador);
    }

    @PostMapping("/beneficiarios")
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarBeneficiario(@Valid @RequestBody BeneficiarioDto beneficiarioDto) throws Exception {
        beneficiarioService.criarBeneficiario(beneficiarioDto);
    }

    @GetMapping("/beneficiarios")
    @ResponseStatus(HttpStatus.OK)
    public List<BeneficiarioEntity> listarBeneficiarios() {
        return beneficiarioService.listarBeneficiarios();
    }

    @GetMapping("/beneficiarios/{id}")
    public ResponseEntity<BeneficiarioResponseDto> listarBenefPorId(@PathVariable Integer id) {
        BeneficiarioResponseDto benef = beneficiarioService.listarPorId(id);
        return ResponseEntity.ok(benef);
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

    @GetMapping("/operadores/{id}")
    public ResponseEntity<OperadorResponseDto> listarOpPorId(@PathVariable Integer id) {
        OperadorResponseDto operador = operadorService.listarPorId(id);
        return ResponseEntity.ok(operador);
    }
}
