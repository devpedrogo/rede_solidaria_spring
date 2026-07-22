package com.devpedrogo.redesolidaria.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devpedrogo.redesolidaria.dto.AtualizarStatusSolicitacaoDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoCreateDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoResponseDto;
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoResponseDto> alterarStatus(
            @PathVariable Integer id,
            @RequestBody @Valid AtualizarStatusSolicitacaoDto dto) {
        
        SolicitacaoResponseDto resposta = solicitacaoService.alterarStatus(id, dto.status());
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public List<SolicitacaoResponseDto> listarSolicitacoes(){
        return solicitacaoService.listarSolicitacoes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDto> buscarPorId(@PathVariable Integer id) {
        SolicitacaoResponseDto dto = solicitacaoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }
}
