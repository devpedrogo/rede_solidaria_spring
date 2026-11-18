package com.devpedrogo.redesolidaria.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiarioService {
    private final IBeneficiarioRepository beneficiarioRepository;

    /**
     * Cria um beneficiário com o perfil correspondente e persiste seus dados.
     *
     * @param beneficiarioDto dados do beneficiário a cadastrar
     * @return dados do beneficiário criado
     * @throws Exception se ocorrer uma falha durante a criação ou persistência
     */
    public BeneficiarioResponseDto criarBeneficiario(BeneficiarioDto beneficiarioDto) throws Exception {
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

        return new BeneficiarioResponseDto(beneficiarioRepository.save(novoBeneficiario));
    }

    /**
     * Recupera todos os beneficiários cadastrados.
     *
     * @return lista de beneficiários convertidos para DTOs de resposta
     */
    public List<BeneficiarioResponseDto> listarBeneficiarios() {
        return beneficiarioRepository.findAll()
                .stream()
                .map(BeneficiarioResponseDto::new) // ou entity -> new BeneficiarioResponseDto(entity)
                .toList();
    }

    /**
     * Busca um beneficiário pelo identificador.
     *
     * @param id identificador do beneficiário
     * @return dados do beneficiário encontrado
     * @throws EntityNotFoundException se não houver beneficiário com o identificador informado
     */
    public BeneficiarioResponseDto listarPorId(Integer id){
        return beneficiarioRepository.findById(id)
                    .map(entity -> new BeneficiarioResponseDto(entity))
                    .orElseThrow(() -> new EntityNotFoundException("Doador não encontrado com ID: " + id));
    }

    /**
     * Atualiza os dados cadastrais e de prioridade de um beneficiário.
     *
     * @param id identificador do beneficiário
     * @param beneficiarioDto novos dados do beneficiário
     * @return dados atualizados do beneficiário
     * @throws RegraDeNegocioException se o beneficiário não for encontrado
     */
    @Transactional
    public BeneficiarioResponseDto atualizarBeneficiario(Integer id, BeneficiarioDto beneficiarioDto){
        BeneficiarioEntity beneficiario = beneficiarioRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Beneficiario com id [" + id + "] não encontrado!"));

        beneficiario.setNome(beneficiarioDto.getNome());
        beneficiario.setTelefone(beneficiarioDto.getTelefone());
        beneficiario.setEndereco(beneficiarioDto.getEndereco());
        beneficiario.setTipoBeneficiario(beneficiarioDto.getTipoBeneficiario());
        beneficiario.setNivelPrioridade(beneficiarioDto.getNivelPrioridade());

        BeneficiarioEntity beneficiarioAtualizado = beneficiarioRepository.save(beneficiario);

        return new BeneficiarioResponseDto(beneficiarioAtualizado);
    }

    /**
     * Marca um beneficiário como inativo; a alteração é persistida pela transação.
     *
     * @param id identificador do beneficiário
     * @return dados do beneficiário após a inativação
     * @throws RegraDeNegocioException se o beneficiário não existir ou já estiver inativo
     */
    @Transactional // COM ESSA ANOTACAO NAO PRECISA REALIZAR O SAVE() NO BANCO
    public BeneficiarioResponseDto inativarBeneficiario(Integer id){
        BeneficiarioEntity beneficiario = beneficiarioRepository.findById(id)
                    .orElseThrow(() -> new RegraDeNegocioException("Beneficiário não encontrado com o id: " + id));

        if(beneficiario.getStatus().equals(StatusUsuario.INATIVO)){
            throw new RegraDeNegocioException("Beneficiário com id [" + id + "] já está INATIVO no sistema");
        }

        beneficiario.setStatus(StatusUsuario.INATIVO);

        return new BeneficiarioResponseDto(beneficiario);
    }
}
