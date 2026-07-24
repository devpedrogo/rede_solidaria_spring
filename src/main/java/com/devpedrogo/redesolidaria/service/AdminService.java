package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.AdminDto;
import com.devpedrogo.redesolidaria.dto.AdminResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.AdminEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IAdminRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final IUsuarioRepository usuarioRepository;
    private final IAdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminResponseDto cadastrarAdmin(AdminDto adminDto){
        UsuarioEntity usuario = usuarioRepository.findByEmail(adminDto.email()).orElse(null);

        if(usuario != null){
            throw new RegraDeNegocioException("Email já pertence a outro usuário");
        }

        AdminEntity novoAdmin = AdminEntity.builder()
                .nome(adminDto.nome())
                .email(adminDto.email())
                .senha(passwordEncoder.encode(adminDto.senha()))
                .telefone(adminDto.telefone())
                .endereco(adminDto.endereco())
                .build();

        if (novoAdmin.getPerfis() == null) {
            novoAdmin.setPerfis(new HashSet<>());
        }

        novoAdmin.getPerfis().add(Perfil.ROLE_ADMIN);

        return new AdminResponseDto(adminRepository.save(novoAdmin));
    }

    public List<AdminResponseDto> listarAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(AdminResponseDto::new) // ou entity -> new AdminResponseDto(entity)
                .toList();
    }

    public AdminResponseDto listarPorId(Integer id){
        return adminRepository.findById(id)
                    .map(entity -> new AdminResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado com ID: " + id));
    }

    @Transactional
    public AdminResponseDto atualizarAdmin(Integer id, AdminDto adminDto){
        AdminEntity admin = adminRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Admin com id [" + id + "] não encontrado!"));

        admin.setNome(adminDto.nome());
        admin.setTelefone(adminDto.telefone());
        admin.setEndereco(adminDto.endereco());
        admin.setEmail(adminDto.email());
        admin.setSenha(passwordEncoder.encode(adminDto.senha()));
        
        AdminEntity adminAtualizado = adminRepository.save(admin);

        return new AdminResponseDto(adminAtualizado);
    }
}
