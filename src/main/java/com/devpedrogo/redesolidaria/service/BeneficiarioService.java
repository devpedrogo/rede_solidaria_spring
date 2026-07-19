package com.devpedrogo.redesolidaria.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {
    private final IBeneficiarioRepository beneficiarioRepository;

    public void criarBeneficiario(BeneficiarioDto beneficiarioDto) throws Exception {
        BeneficiarioEntity beneficiario = beneficiarioRepository.findByEmail(beneficiarioDto.getEmail()).orElse(null);
        
        if (beneficiario != null) {
            throw new Exception("Já existe um beneficiário cadastrado com este e-mail.");
        }

        beneficiario = BeneficiarioEntity.builder()
                .nome(beneficiarioDto.getNome())
                .telefone(beneficiarioDto.getTelefone())
                .email(beneficiarioDto.getEmail())
                .endereco(beneficiarioDto.getEndereco())
                .tipoBeneficiario(beneficiarioDto.getTipoBeneficiario())
                .nivelPrioridade(beneficiarioDto.getNivelPrioridade())
                .build();

        beneficiarioRepository.save(beneficiario);
    }

    public List<BeneficiarioEntity> listarBeneficiarios() {
        return beneficiarioRepository.findAll();
    }
}
