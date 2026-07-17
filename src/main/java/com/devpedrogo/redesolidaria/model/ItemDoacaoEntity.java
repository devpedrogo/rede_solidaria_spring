package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import com.devpedrogo.redesolidaria.enums.CategoriaItem;
import com.devpedrogo.redesolidaria.enums.StatusItem;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "itens_doacao")
public class ItemDoacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaItem categoria;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusItem status;
}