package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.devpedrogo.redesolidaria.enums.StatusSolicitacao;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "solicitacoes")
public class SolicitacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // --- RELACIONAMENTO COM QUEM PEDIU ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private BeneficiarioEntity beneficiario; // Vincula ao beneficiário logado/solicitante

    // --- RELACIONAMENTO COM O PRODUTO GLOBAL ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemDoacaoEntity item; // Vincula ao item solicitado no estoque global

    @Column(name = "quantidade_solicitada", nullable = false)
    private Integer quantidadeSolicitada;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status; // Ex: PENDENTE, APROVADA, REJEITADA
}