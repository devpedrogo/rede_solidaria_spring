package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
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
                .senha(beneficiarioDto.getSenha())
                .tipoBeneficiario(beneficiarioDto.getTipoBeneficiario())
                .nivelPrioridade(beneficiarioDto.getNivelPrioridade())
                .build();

        if (beneficiario.getPerfis() == null) {
            beneficiario.setPerfis(new HashSet<>());
        }

        beneficiario.getPerfis().add(Perfil.ROLE_BENEFICIARIO);

        beneficiarioRepository.save(beneficiario);
    }

    public List<BeneficiarioEntity> listarBeneficiarios() {
        return beneficiarioRepository.findAll();
    }
}
