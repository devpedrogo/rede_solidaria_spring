package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {
    private final IBeneficiarioRepository beneficiarioRepository;

    public void criarBeneficiario(BeneficiarioDto beneficiarioDto) throws Exception {
        // UsuarioEntity usuario = usuarioRepository.findByEmail(beneficiarioDto.getEmail()).orElse(null);
        
        // if (usuario != null) {
        //     throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        // }

        BeneficiarioEntity novoBeneficiario = BeneficiarioEntity.builder()
                .nome(beneficiarioDto.getNome())
                .telefone(beneficiarioDto.getTelefone())
                .endereco(beneficiarioDto.getEndereco())
                .tipoBeneficiario(beneficiarioDto.getTipoBeneficiario())
                .nivelPrioridade(beneficiarioDto.getNivelPrioridade())
                .build();

        if (novoBeneficiario.getPerfis() == null) {
            novoBeneficiario.setPerfis(new HashSet<>());
        }

        novoBeneficiario.getPerfis().add(Perfil.ROLE_BENEFICIARIO);

        beneficiarioRepository.save(novoBeneficiario);
    }

    public List<BeneficiarioResponseDto> listarBeneficiarios() {
        return beneficiarioRepository.findAll()
                .stream()
                .map(BeneficiarioResponseDto::new) // ou entity -> new BeneficiarioResponseDto(entity)
                .toList();
    }

    public BeneficiarioResponseDto listarPorId(Integer id){
        return beneficiarioRepository.findById(id)
                    .map(entity -> new BeneficiarioResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }
}
