package com.devpedrogo.redesolidaria.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devpedrogo.redesolidaria.dto.SolicitacaoCreateDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoResponseDto;
import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;
import com.devpedrogo.redesolidaria.service.SolicitacaoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
@Tag(name = "Solicitações", description = "Endpoints para criação e gestão de solicitações de doação")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDto> criarSolicitacao(@RequestBody @Valid SolicitacaoCreateDto dto) {
        SolicitacaoResponseDto resposta = solicitacaoService.criarSolicitacao(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.id())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }

    @GetMapping
    public List<SolicitacaoEntity> listarSolicitacoes(){
        return solicitacaoService.listarSolicitacoes();
    }
}
