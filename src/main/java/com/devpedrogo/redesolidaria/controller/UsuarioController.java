package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devpedrogo.redesolidaria.dto.AdminDto;
import com.devpedrogo.redesolidaria.dto.AdminResponseDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.service.AdminService;
import com.devpedrogo.redesolidaria.service.BeneficiarioService;
import com.devpedrogo.redesolidaria.service.DoadorService;
import com.devpedrogo.redesolidaria.service.OperadorService;

import io.swagger.v3.oas.annotations.Operation;
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
    private final AdminService adminService;

    @PostMapping("/doadores")
    @Operation(
        summary = "Cadastrar novo doador", 
        description = "Cadastra um novo doador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarDoador(@Valid @RequestBody DoadorDto doadorDto) throws Exception {
        doadorService.criarDoador(doadorDto);
    }

    @GetMapping("/doadores")
    @Operation(
        summary = "Listar doadores", 
        description = "Lista todos os doadores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<DoadorResponseDto> listarDoadores() {
        return doadorService.listarDoadores();
    }

    @GetMapping("/doadores/{id}")
    @Operation(
        summary = "Listar doadores por ID", 
        description = "Lista os doadores cadastrados no sistema por ID."
    )
    public ResponseEntity<DoadorResponseDto> listarDoadorPorId(@PathVariable Integer id) {
        DoadorResponseDto doador = doadorService.listarPorId(id);
        return ResponseEntity.ok(doador);
    }

    @PostMapping("/beneficiarios")
    @Operation(
        summary = "Cadastrar novo beneficiário", 
        description = "Cadastra um novo beneficiário no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarBeneficiario(@Valid @RequestBody BeneficiarioDto beneficiarioDto) throws Exception {
        beneficiarioService.criarBeneficiario(beneficiarioDto);
    }

    @GetMapping("/beneficiarios")
    @Operation(
        summary = "Listar beneficiários", 
        description = "Lista todos os beneficiários cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<BeneficiarioResponseDto> listarBeneficiarios() {
        return beneficiarioService.listarBeneficiarios();
    }

    @GetMapping("/beneficiarios/{id}")
    @Operation(
        summary = "Listar beneficiarios por ID", 
        description = "Lista os beneficiarios cadastrados no sistema por ID."
    )
    public ResponseEntity<BeneficiarioResponseDto> listarBenefPorId(@PathVariable Integer id) {
        BeneficiarioResponseDto benef = beneficiarioService.listarPorId(id);
        return ResponseEntity.ok(benef);
    }

    @PostMapping("/operadores")
    @Operation(
        summary = "Cadastrar novo operador", 
        description = "Cadastra um novo operador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarOperador(@Valid @RequestBody OperadorDto operadorDto) throws Exception {
        operadorService.cadastrarOperador(operadorDto);
    }
    
    @GetMapping("/operadores")
    @Operation(
        summary = "Listar operadores", 
        description = "Lista todos os operadores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<OperadorResponseDto> listarOperadores() {
        return operadorService.listarOperadores();
    }

    @GetMapping("/operadores/{id}")
    @Operation(
        summary = "Listar operadores por ID", 
        description = "Lista os operadores cadastrados no sistema por ID."
    )
    public ResponseEntity<OperadorResponseDto> listarOpPorId(@PathVariable Integer id) {
        OperadorResponseDto operador = operadorService.listarPorId(id);
        return ResponseEntity.ok(operador);
    }

    @PostMapping("/admins")
    @Operation(
        summary = "Cadastrar novo admin", 
        description = "Cadastra um novo administrador no sistema."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public AdminResponseDto cadastrarAdmin(AdminDto adminDto){
        return adminService.cadastrarAdmin(adminDto);
    }

    @GetMapping("/admins")
    @Operation(
        summary = "Listar administradores", 
        description = "Lista todos os administradores cadastrados no sistema."
    )
    @ResponseStatus(HttpStatus.OK)
    public List<AdminResponseDto> listarAdmins() {
        return adminService.listarAdmins();
    }

    @GetMapping("/admins/{id}")
    @Operation(
        summary = "Listar administradores por ID", 
        description = "Lista os operadores cadastrados no sistema por ID."
    )
    public ResponseEntity<AdminResponseDto> listarAdmPorId(@PathVariable Integer id) {
        AdminResponseDto admin = adminService.listarPorId(id);
        return ResponseEntity.ok(admin);
    }
}
