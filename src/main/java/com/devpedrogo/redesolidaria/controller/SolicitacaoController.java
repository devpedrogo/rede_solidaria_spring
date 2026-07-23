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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
        summary = "Criar nova solicitação", 
        description = "Cadastra uma solicitação de doação associada a um beneficiário."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado (token ausente ou inválido)"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta operação")
    })
    public ResponseEntity<SolicitacaoResponseDto> criarSolicitacao(@RequestBody @Valid SolicitacaoCreateDto dto) {
        SolicitacaoResponseDto resposta = solicitacaoService.criarSolicitacao(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resposta.id())
                .toUri();

        return ResponseEntity.created(uri).body(resposta);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Atualizar status da solicitação", 
        description = "Altera o status de uma solicitação (ex: PENDENTE, CONCLUIDA, REJEITADA)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Mudança de status inválida")
    })
    public ResponseEntity<SolicitacaoResponseDto> alterarStatus(
            @Parameter(description = "ID da solicitação", example = "1") @PathVariable Integer id,
            @RequestBody @Valid AtualizarStatusSolicitacaoDto dto) {
        
        SolicitacaoResponseDto resposta = solicitacaoService.alterarStatus(id, dto.status());
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    @Operation(
        summary = "Retornar todas as solicitações ", 
        description = "Retorna as solicitações cadastradas no sistema."
    )
    public List<SolicitacaoResponseDto> listarSolicitacoes(){
        return solicitacaoService.listarSolicitacoes();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Retornar as solicitação por ID", 
        description = "Retorna as solicitações cadastradas no sistema por ID."
    )
    public ResponseEntity<SolicitacaoResponseDto> buscarPorId(@PathVariable Integer id) {
        SolicitacaoResponseDto dto = solicitacaoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }
}
