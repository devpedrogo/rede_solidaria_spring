package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {
    private final IBeneficiarioRepository beneficiarioRepository;
    private final IUsuarioRepository usuarioRepository;

    public void criarBeneficiario(BeneficiarioDto beneficiarioDto) throws Exception {
        UsuarioEntity usuario = usuarioRepository.findByEmail(beneficiarioDto.getEmail()).orElse(null);
        
        if (usuario != null) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        BeneficiarioEntity novoBeneficiario = BeneficiarioEntity.builder()
                .nome(beneficiarioDto.getNome())
                .telefone(beneficiarioDto.getTelefone())
                .email(beneficiarioDto.getEmail())
                .endereco(beneficiarioDto.getEndereco())
                .senha(beneficiarioDto.getSenha())
                .tipoBeneficiario(beneficiarioDto.getTipoBeneficiario())
                .nivelPrioridade(beneficiarioDto.getNivelPrioridade())
                .build();

        if (novoBeneficiario.getPerfis() == null) {
            novoBeneficiario.setPerfis(new HashSet<>());
        }

        novoBeneficiario.getPerfis().add(Perfil.ROLE_BENEFICIARIO);

        beneficiarioRepository.save(novoBeneficiario);
    }

    public List<BeneficiarioEntity> listarBeneficiarios() {
        return beneficiarioRepository.findAll();
    }

    public BeneficiarioResponseDto listarPorId(Integer id){
        return beneficiarioRepository.findById(id)
                    .map(entity -> new BeneficiarioResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }
}
