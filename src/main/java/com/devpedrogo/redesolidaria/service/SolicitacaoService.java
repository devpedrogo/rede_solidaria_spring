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
            item.setStatus(StatusItem.RESERVADO); // Ou INDISPONIVEL, dependendo do seu Enum
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

    public List<SolicitacaoEntity> listarSolicitacoes(){
        return solicitacaoRepository.findAll();
    }
}
