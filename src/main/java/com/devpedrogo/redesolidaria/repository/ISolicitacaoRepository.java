package com.devpedrogo.redesolidaria.repository;

import com.devpedrogo.redesolidaria.model.SolicitacaoEntity;
import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISolicitacaoRepository extends JpaRepository<SolicitacaoEntity, Integer> {
    
    // Query Method útil: Buscar todas as solicitações de um beneficiário específico
    List<SolicitacaoEntity> findByBeneficiarioId(Integer beneficiarioId);
    
    // Query Method útil: Buscar solicitações pendentes para a tela de aprovação do Admin
    List<SolicitacaoEntity> findByStatus(StatusSolicitacao status);
}