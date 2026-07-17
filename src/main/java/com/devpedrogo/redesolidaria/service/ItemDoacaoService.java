package com.devpedrogo.redesolidaria.service;

import com.devpedrogo.redesolidaria.dto.ItemDoacaoDto;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.DoacaoEfetivadaEntity;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;
import com.devpedrogo.redesolidaria.repository.IDoacaoEfetivadaRepository;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemDoacaoService {

    private final IItemDoacaoRepository itemDoacaoRepository;
    private final IDoacaoEfetivadaRepository doacaoEfetivadaRepository;
    private final IDoadorRepository doadorRepository;

    @Transactional
    public void receberDoacao(ItemDoacaoDto dto) {
        
        // 1. Busca o doador no banco pelo ID enviado no DTO
        // Lança uma exceção personalizada caso o ID não exista (tratamento de erro de mercado)
        DoadorEntity doador = doadorRepository.findById(dto.getDoadorId())
                .orElseThrow(() -> new IllegalArgumentException("Doador não encontrado com o ID: " + dto.getDoadorId()));
        
        // 2. Busca ou inicializa o item no Estoque Global
        ItemDoacaoEntity estoque = itemDoacaoRepository
                .findByNomeIgnoreCaseAndCategoria(dto.getNome(), dto.getCategoria())
                .orElseGet(() -> ItemDoacaoEntity.builder()
                        .nome(dto.getNome())
                        .categoria(dto.getCategoria())
                        .quantidade(0)
                        .status(StatusItem.DISPONIVEL)
                        .build());

        // 3. Incrementa o saldo global com os dados vindos do DTO
        estoque.setQuantidade(estoque.getQuantidade() + dto.getQuantidadeDoada());
        estoque.setDataAtualizacao(LocalDateTime.now());
        
        if (estoque.getStatus() == StatusItem.ESGOTADO) {
            estoque.setStatus(StatusItem.DISPONIVEL);
        }

        ItemDoacaoEntity estoqueSalvo = itemDoacaoRepository.save(estoque);

        // 4. Cria o registro histórico de auditoria
        DoacaoEfetivadaEntity historico = DoacaoEfetivadaEntity.builder()
                .item(estoqueSalvo)
                .doador(doador) // Entidade recuperada no passo 1
                .quantidade(dto.getQuantidadeDoada())
                .data(LocalDateTime.now())
                .observacoes("Entrada de estoque via API.")
                .build();

        doacaoEfetivadaRepository.save(historico);
    }

    public List <ItemDoacaoEntity> listarItensDisponiveis() {
        return itemDoacaoRepository.findAll();
    }
}