package com.devpedrogo.redesolidaria.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.SolicitacaoCreateDto;
import com.devpedrogo.redesolidaria.dto.SolicitacaoResponseDto;
import com.devpedrogo.redesolidaria.enums.StatusItem;
import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.model.ItemDoacaoEntity;
import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;
import com.devpedrogo.redesolidaria.repository.IItemDoacaoRepository;
import com.devpedrogo.redesolidaria.repository.ISolicitacaoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final ISolicitacaoRepository solicitacaoRepository;
    private final IItemDoacaoRepository itemRepository;
    private final IBeneficiarioRepository beneficiarioRepository;

    @Transactional
    public SolicitacaoResponseDto criarSolicitacao(SolicitacaoCreateDto dto) {
        // 1. Buscar o Item
        ItemDoacaoEntity item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Item de doação não encontrado com o ID: " + dto.itemId()));

        // 2. Validação de estoque e disponibilidade
        if (item.getStatus() != StatusItem.DISPONIVEL) {
            throw new RegraDeNegocioException("Este item não está disponível para solicitação.");
        }

        if (item.getQuantidade() < dto.quantidadeSolicitada()) {
            throw new RegraDeNegocioException("Quantidade solicitada (" + dto.quantidadeSolicitada() + 
                    ") é maior que o estoque disponível (" + item.getQuantidade() + ").");
        }

        // 3. Buscar Beneficiário
        BeneficiarioEntity beneficiario = beneficiarioRepository.findById(dto.beneficiarioId())
                .orElseThrow(() -> new EntityNotFoundException("Beneficiário não encontrado com o ID: " + dto.beneficiarioId()));

        // 4. Abater estoque / atualizar status do item
        item.setQuantidade(item.getQuantidade() - dto.quantidadeSolicitada());
        if (item.getQuantidade() == 0) {
            item.setStatus(StatusItem.RESERVADO);
        }
        itemRepository.save(item);

        // 5. Montar a entidade usando o Builder
        SolicitacaoEntity solicitacao = SolicitacaoEntity.builder()
                .item(item)
                .beneficiario(beneficiario)
                .quantidadeSolicitada(dto.quantidadeSolicitada())
                .justificativa(dto.justificativa())
                .status(StatusSolicitacao.PENDENTE)
                .build();

        SolicitacaoEntity salva = solicitacaoRepository.save(solicitacao);

        return new SolicitacaoResponseDto(salva);
    }

    @Transactional
    public SolicitacaoResponseDto alterarStatus(Integer id, StatusSolicitacao novoStatus) {
        SolicitacaoEntity solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com ID: " + id));

        // Validação de transição repetida
        if (solicitacao.getStatus() == novoStatus) {
            throw new RegraDeNegocioException("A solicitação já está no status: " + novoStatus);
        }

        // Regra de validação ao tentar mudar o status de uma solicitação CONCLUIDA OU REJEITADA
        if(solicitacao.getStatus().equals(StatusSolicitacao.CONCLUIDA)){
            throw new RegraDeNegocioException("A solicitação de id [" + id + "] encontra-se CONCLUÍDA, não é possível alterar seu status.");
        }

        if(solicitacao.getStatus().equals(StatusSolicitacao.REJEITADA)){
            throw new RegraDeNegocioException("A solicitação de id [" + id + "] foi REJEITADA, não é possível alterar seu status.");
        }

        // Regra ao CANCELAR: Devolver itens ao estoque
        if (novoStatus == StatusSolicitacao.REJEITADA) {
            ItemDoacaoEntity item = solicitacao.getItem();
            item.setQuantidade(item.getQuantidade() + solicitacao.getQuantidadeSolicitada());
            itemRepository.save(item);
        }

        // Regra ao CONCLUIR uma solicitacão e o item no estoque zerar
        if (novoStatus == StatusSolicitacao.CONCLUIDA && solicitacao.getItem().getQuantidade().equals(0)) {
            solicitacao.getItem().setStatus(StatusItem.ESGOTADO);
        }

        solicitacao.setStatus(novoStatus);
        solicitacaoRepository.save(solicitacao);

        return new SolicitacaoResponseDto(solicitacao); 
    }

    public List<SolicitacaoResponseDto> listarSolicitacoes() {
        return solicitacaoRepository.findAll()
                .stream()
                .map(SolicitacaoResponseDto::new) // ou entity -> new SolicitacaoResponseDto(entity)
                .toList();
    }

    public SolicitacaoResponseDto buscarPorId(Integer id) {
        return solicitacaoRepository.findById(id)
                .map(entity -> new SolicitacaoResponseDto(entity))
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com ID: " + id));
    }
}
