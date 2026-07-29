package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.AdminDto;
import com.devpedrogo.redesolidaria.dto.AdminResponseDto;
import com.devpedrogo.redesolidaria.dto.AdminUpdateDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.AdminEntity;
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
        if (adminDto.email() == null || adminDto.email().isBlank()) {
            throw new RegraDeNegocioException("O e-mail é obrigatório para cadastrar um administrador.");
        }

        if (usuarioRepository.existsByEmail(adminDto.email().trim())) {
            throw new RegraDeNegocioException("E-mail já pertence a outro usuário");
        }

        AdminEntity novoAdmin = AdminEntity.builder()
                .nome(adminDto.nome().trim())
                .email(adminDto.email().trim())
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
    public AdminResponseDto atualizarAdmin(Integer id, AdminUpdateDto adminDto){
        AdminEntity admin = adminRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Admin com id [" + id + "] não encontrado!"));

        admin.setNome(adminDto.nome());
        admin.setTelefone(adminDto.telefone());
        admin.setEndereco(adminDto.endereco());
        admin.setEmail(adminDto.email());
        
        if (adminDto.senha() != null && !adminDto.senha().isBlank()) {
            admin.setSenha(passwordEncoder.encode(adminDto.senha()));
        }
        
        AdminEntity adminAtualizado = adminRepository.save(admin);

        return new AdminResponseDto(adminAtualizado);
    }

    @Transactional // COM ESSA ANOTACAO NAO PRECISA REALIZAR O SAVE() NO BANCO
    public AdminResponseDto inativarAdmin(Integer id){
        AdminEntity admin = adminRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Admin não encontrado com o id: " + id));

        // 1. Recupera o usuário logado direto do contexto de segurança do Spring
        UserDetails usuarioLogado = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
                
        String emailUsuarioLogado = usuarioLogado.getUsername();

        // 2. Realiza a validação de auto-inativação
        if (admin.getEmail().equals(emailUsuarioLogado)) {
            throw new RegraDeNegocioException("Operação negada: Você não pode inativar a sua própria conta.");
        }

        // 3. NOVA VALIDAÇÃO: Impede deixar o sistema sem nenhum Administrador ativo
        long totalAdminsAtivos = adminRepository.countByStatus(StatusUsuario.ATIVO);
        if (totalAdminsAtivos <= 2) {
            throw new RegraDeNegocioException("Operação negada: O sistema exige o mínimo de 2 administradores ativos para segurança. Cadastre outro administrador antes de inativar este.");
        }

        if(admin.getStatus().equals(StatusUsuario.INATIVO)){
            throw new RegraDeNegocioException("Admin com id [" + id + "] já está INATIVO no sistema");
        }

        admin.setStatus(StatusUsuario.INATIVO);

        return new AdminResponseDto(admin);
    }
}
