package com.devpedrogo.redesolidaria.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devpedrogo.redesolidaria.dto.ItemDoacaoDto;
import com.devpedrogo.redesolidaria.dto.ItemDoacaoResponseDto;
import com.devpedrogo.redesolidaria.dto.ItemFiltroDto;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.service.ItemDoacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/doacaos")
@RequiredArgsConstructor
public class ItemDoacaoController {
    private final ItemDoacaoService itemDoacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarItemDoacao(@Valid @RequestBody ItemDoacaoDto dto) {
        itemDoacaoService.receberDoacao(dto);
    }

    // @GetMapping
    // @ResponseStatus(HttpStatus.OK)
    // public List <ItemDoacaoEntity> listarItens() {
    //     return itemDoacaoService.listarItens();
    // }

    @GetMapping("/disponiveis")
    @ResponseStatus(HttpStatus.OK)
    public List <ItemDoacaoEntity> listarItensDisponiveis() {
        return itemDoacaoService.listarItensDisponiveis();
    }

    @GetMapping
    public ResponseEntity<Page<ItemDoacaoResponseDto>> listarItensComFiltros(
            @ParameterObject ItemFiltroDto filtro,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        
        Page<ItemDoacaoResponseDto> resultado = itemDoacaoService.buscarComFiltro(filtro, pageable);
        return ResponseEntity.ok(resultado);
    }
}
