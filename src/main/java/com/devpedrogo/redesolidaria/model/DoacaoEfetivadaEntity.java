package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "doacoes_efetivadas")
public class DoacaoEfetivadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemDoacaoEntity item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doador_id", nullable = false)
    private DoadorEntity doador; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id") 
    private BeneficiarioEntity beneficiario;

    @Column(nullable = false)
    private Integer quantidade; 

    @Column(nullable = false)
    private LocalDateTime data;

    private String observacoes;
}