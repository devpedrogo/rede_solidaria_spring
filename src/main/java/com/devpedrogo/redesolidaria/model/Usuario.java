package com.devpedrogo.redesolidaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

import com.devpedrogo.redesolidaria.enums.Perfil;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) //Define a estratégia de herança relacional
@Schema(description = "Classe base que representa qualquer usuário do sistema")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autoincrementado gerado pelo banco", example = "1")
    private Integer id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Insira um e-mail válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "O endereço é obrigatório")
    @Column(nullable = false)
    private String endereco;

    @NotBlank(message = "A senha é obrigatória")
    @Column(nullable = false)
    private String senha;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_perfis", 
        joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "perfil")
    @Enumerated(EnumType.STRING)
    private Set<Perfil> perfis;

}