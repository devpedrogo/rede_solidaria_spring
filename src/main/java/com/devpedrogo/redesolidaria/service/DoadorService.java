package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final IDoadorRepository doadorRepository;

    public DoadorResponseDto criarDoador(DoadorDto doadorDto) throws Exception {
        // UsuarioEntity usuario = usuarioRepository.findByEmail(doadorDto.getEmail()).orElse(null);

        // if (usuario != null) {
        //     throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        // }

        DoadorEntity novoDoador = DoadorEntity.builder()
                .nome(doadorDto.getNome())
                .telefone(doadorDto.getTelefone())
                .endereco(doadorDto.getEndereco())
                .build();

        if (novoDoador.getPerfis() == null) {
            novoDoador.setPerfis(new HashSet<>());
        }

        novoDoador.getPerfis().add(Perfil.ROLE_DOADOR);

        return new DoadorResponseDto(doadorRepository.save(novoDoador));
    }

    public List<DoadorResponseDto> listarDoadores() {
        return doadorRepository.findAll()
                .stream()
                .map(DoadorResponseDto::new) // ou entity -> new DoadorResponseDto(entity)
                .toList();
    }

    public DoadorResponseDto listarPorId(Integer id){
        return doadorRepository.findById(id)
                    .map(entity -> new DoadorResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }

    @Transactional
    public DoadorResponseDto atualizarDoador(Integer id, DoadorDto doadorDto){
        DoadorEntity doador = doadorRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Doador com id [" + id + "] não encontrado!"));

        doador.setNome(doadorDto.getNome());
        doador.setTelefone(doadorDto.getTelefone());
        doador.setEndereco(doadorDto.getEndereco());
        
        DoadorEntity doadorAtualizado = doadorRepository.save(doador);

        return new DoadorResponseDto(doadorAtualizado);
    }
}
