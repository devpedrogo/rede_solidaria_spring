package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final IDoadorRepository doadorRepository;
    private final IUsuarioRepository usuarioRepository;

    public void criarDoador(DoadorDto doadorDto) throws Exception {
        UsuarioEntity usuario = usuarioRepository.findByEmail(doadorDto.getEmail()).orElse(null);

        if (usuario != null) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        DoadorEntity novoDoador = DoadorEntity.builder()
                .nome(doadorDto.getNome())
                .email(doadorDto.getEmail())
                .senha(doadorDto.getSenha())
                .telefone(doadorDto.getTelefone())
                .endereco(doadorDto.getEndereco())
                .build();

        if (novoDoador.getPerfis() == null) {
            novoDoador.setPerfis(new HashSet<>());
        }

        novoDoador.getPerfis().add(Perfil.ROLE_DOADOR);

        doadorRepository.save(novoDoador);
    }

    public List<DoadorEntity> listarDoadores() {
        return doadorRepository.findAll();
    }
}
