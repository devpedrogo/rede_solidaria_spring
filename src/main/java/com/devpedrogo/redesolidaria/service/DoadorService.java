package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
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

    /**
     * Cadastra um doador com o perfil correspondente.
     *
     * @param doadorDto dados do doador a cadastrar
     * @return dados do doador persistido
     * @throws Exception se ocorrer uma falha durante a criação ou persistência
     */
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

    /**
     * Recupera todos os doadores cadastrados.
     *
     * @return lista de doadores convertidos para DTOs de resposta
     */
    public List<DoadorResponseDto> listarDoadores() {
        return doadorRepository.findAll()
                .stream()
                .map(DoadorResponseDto::new) // ou entity -> new DoadorResponseDto(entity)
                .toList();
    }

    /**
     * Busca um doador pelo identificador.
     *
     * @param id identificador do doador
     * @return dados do doador encontrado
     * @throws EntityNotFoundException se não houver doador com o identificador informado
     */
    public DoadorResponseDto listarPorId(Integer id){
        return doadorRepository.findById(id)
                    .map(entity -> new DoadorResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }

    /**
     * Atualiza os dados cadastrais de um doador.
     *
     * @param id identificador do doador
     * @param doadorDto novos dados do doador
     * @return dados atualizados do doador
     * @throws RegraDeNegocioException se o doador não for encontrado
     */
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

    /**
     * Marca um doador como inativo; a alteração é persistida pela transação.
     *
     * @param id identificador do doador
     * @return dados do doador após a inativação
     * @throws RegraDeNegocioException se o doador não existir ou já estiver inativo
     */
    @Transactional // COM ESSA ANOTACAO NAO PRECISA REALIZAR O SAVE() NO BANCO
    public DoadorResponseDto inativarDoador(Integer id){
        DoadorEntity doador = doadorRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Doador não encontrado com o id: " + id));

        if(doador.getStatus().equals(StatusUsuario.INATIVO)){
            throw new RegraDeNegocioException("Doador com id [" + id + "] já está INATIVO no sistema");
        }

        doador.setStatus(StatusUsuario.INATIVO);

        return new DoadorResponseDto(doador);
    }
}
