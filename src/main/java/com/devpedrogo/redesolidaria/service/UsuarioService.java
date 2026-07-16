package com.devpedrogo.redesolidaria.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final IUsuarioRepository usuarioRepository;
    
    public void criarDoador(DoadorDto doadorDto) throws Exception {
        UsuarioEntity doador = usuarioRepository.findByEmail(doadorDto.getEmail()).orElse(null);

        if (doador != null) {
            throw new Exception("Usuário já cadastrado com este e-mail.");
        }


        DoadorEntity novoDoador = DoadorEntity.builder()
                .nome(doadorDto.getNome())
                .email(doadorDto.getEmail())
                .senha(doadorDto.getSenha())
                .telefone(doadorDto.getTelefone())
                .endereco(doadorDto.getEndereco())
                .perfis(Set.of(Perfil.ROLE_DOADOR))
                .build();
                
        usuarioRepository.save(novoDoador);
    }
}
