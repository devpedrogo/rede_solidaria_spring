package com.devpedrogo.redesolidaria.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IOperadorRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperadorService {
    private final IOperadorRepository operadorRepository;
    private final IUsuarioRepository usuarioRepository;
    
    public void cadastrarOperador(OperadorDto operadorDto) throws Exception {
        UsuarioEntity usuario = usuarioRepository.findByEmail(operadorDto.getEmail()).orElse(null);

        if (usuario != null) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        String anoAtual = String.valueOf(LocalDate.now().getYear());
        String numeroAleatorio = String.format("%04d", new Random().nextInt(10000));
        String matriculaGerada = "OP-" + anoAtual + "-" + numeroAleatorio; // Ex: OP-2026-8741

        OperadorEntity novoOperador = OperadorEntity.builder()
                .nome(operadorDto.getNome())
                .telefone(operadorDto.getTelefone())
                .email(operadorDto.getEmail())
                .endereco(operadorDto.getEndereco())
                .senha(operadorDto.getSenha())
                .matricula(matriculaGerada)
                .build();

        if (novoOperador.getPerfis() == null) {
            novoOperador.setPerfis(new HashSet<>());
        }

        novoOperador.getPerfis().add(Perfil.ROLE_OPERADOR);

        operadorRepository.save(novoOperador);
    }

    public List<OperadorEntity> listarOperadores() {
        return operadorRepository.findAll();
    }

    public OperadorResponseDto listarPorId(Integer id){
        return operadorRepository.findById(id)
                    .map(entity -> new OperadorResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }
}
