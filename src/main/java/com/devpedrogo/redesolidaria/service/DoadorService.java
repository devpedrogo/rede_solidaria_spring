package com.devpedrogo.redesolidaria.service;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final IDoadorRepository doadorRepository;

    public void criarDoador(DoadorDto doadorDto) throws Exception {
        DoadorEntity doador = doadorRepository.findByEmail(doadorDto.getEmail()).orElse(null);

        if (doador != null) {
            throw new Exception("Usuário já cadastrado com este e-mail.");
        }

        doador = DoadorEntity.builder()
                .nome(doadorDto.getNome())
                .email(doadorDto.getEmail())
                .senha(doadorDto.getSenha())
                .telefone(doadorDto.getTelefone())
                .endereco(doadorDto.getEndereco())
                .build();

        doadorRepository.save(doador);
    }
}
