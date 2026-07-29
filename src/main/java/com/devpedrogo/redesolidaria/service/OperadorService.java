package com.devpedrogo.redesolidaria.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.dto.OperadorUpdateDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IOperadorRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperadorService {
    private final IOperadorRepository operadorRepository;
    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    
    public OperadorResponseDto cadastrarOperador(OperadorDto operadorDto) throws Exception {
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
                .senha(passwordEncoder.encode(operadorDto.getSenha()))
                .matricula(matriculaGerada)
                .build();

        if (novoOperador.getPerfis() == null) {
            novoOperador.setPerfis(new HashSet<>());
        }

        novoOperador.getPerfis().add(Perfil.ROLE_OPERADOR);

        return new OperadorResponseDto(operadorRepository.save(novoOperador));
    }

    public List<OperadorResponseDto> listarOperadores() {
        return operadorRepository.findAll()
                .stream()
                .map(OperadorResponseDto::new) // ou entity -> new OperadorResponseDto(entity)
                .toList();
    }

    public OperadorResponseDto listarPorId(Integer id){
        return operadorRepository.findById(id)
                    .map(entity -> new OperadorResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }

    @Transactional
    public OperadorResponseDto atualizarOperador(Integer id, OperadorUpdateDto operadorDto){
        OperadorEntity operador = operadorRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Operador com id [" + id + "] não encontrado!"));

        operador.setNome(operadorDto.nome());
        operador.setTelefone(operadorDto.telefone());
        operador.setEndereco(operadorDto.endereco());
        operador.setEmail(operadorDto.email());
        operador.setSenha(passwordEncoder.encode(operadorDto.senha()));
        
        OperadorEntity operadorAtualizado = operadorRepository.save(operador);

        return new OperadorResponseDto(operadorAtualizado);
    }

    @Transactional // COM ESSA ANOTACAO NAO PRECISA REALIZAR O SAVE() NO BANCO
    public OperadorResponseDto inativarOperador(Integer id){
        OperadorEntity operador = operadorRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Operador não encontrado com o id: " + id));

        if(operador.getStatus().equals(StatusUsuario.INATIVO)){
            throw new RegraDeNegocioException("Operador com id [" + id + "] já está INATIVO no sistema");
        }

        operador.setStatus(StatusUsuario.INATIVO);

        return new OperadorResponseDto(operador);
    }
}
