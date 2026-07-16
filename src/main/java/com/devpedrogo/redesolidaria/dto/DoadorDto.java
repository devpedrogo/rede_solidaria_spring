package com.devpedrogo.redesolidaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoadorDto {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    @Size(max = 20, message = "O telefone não pode passar de 20 caracteres")
    private String telefone;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Insira um e-mail com formato válido")
    @Size(max = 100, message = "O e-mail não pode passar de 100 caracteres")
    private String email;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;
}