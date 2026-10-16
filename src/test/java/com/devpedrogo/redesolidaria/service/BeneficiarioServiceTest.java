package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devpedrogo.redesolidaria.dto.BeneficiarioDto;
import com.devpedrogo.redesolidaria.dto.BeneficiarioResponseDto;
import com.devpedrogo.redesolidaria.enums.NivelPrioridade;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.enums.TipoBeneficiario;

import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.BeneficiarioEntity;
import com.devpedrogo.redesolidaria.repository.IBeneficiarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class BeneficiarioServiceTest {

    @Mock
    private IBeneficiarioRepository beneficiarioRepository;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    @Captor
    private ArgumentCaptor<BeneficiarioEntity> beneficiarioCaptor;

    private BeneficiarioDto beneficiarioDto;
    private BeneficiarioEntity beneficiarioEntity;

    @BeforeEach
    void setUp() {
        beneficiarioDto = new BeneficiarioDto();
        beneficiarioDto.setNome("Associação Esperança");
        beneficiarioDto.setTelefone("79977776666");
        beneficiarioDto.setEndereco("Avenida Central, 500");
        beneficiarioDto.setTipoBeneficiario(TipoBeneficiario.ONG);
        beneficiarioDto.setNivelPrioridade(NivelPrioridade.CRITICA);

        beneficiarioEntity = BeneficiarioEntity.builder()
                .nome("Associação Esperança")
                .telefone("79977776666")
                .endereco("Avenida Central, 500")
                .tipoBeneficiario(TipoBeneficiario.ONG)
                .nivelPrioridade(NivelPrioridade.CRITICA)
                .build();
        beneficiarioEntity.setId(1);
        beneficiarioEntity.setStatus(StatusUsuario.ATIVO);
    }

    @Nested
    @DisplayName("Testes do método criarBeneficiario")
    class CriarBeneficiarioTests {

        @Test
        @DisplayName("Deve criar um beneficiário com sucesso e atribuir o perfil ROLE_BENEFICIARIO")
        void deveCriarBeneficiarioComSucesso() throws Exception {
            // Arrange
            when(beneficiarioRepository.save(any(BeneficiarioEntity.class))).thenAnswer(invocation -> {
                BeneficiarioEntity entity = invocation.getArgument(0);
                entity.setId(1);
                return entity;
            });

            // Act
            BeneficiarioResponseDto resultado = beneficiarioService.criarBeneficiario(beneficiarioDto);

            // Assert
            assertNotNull(resultado);
            verify(beneficiarioRepository).save(beneficiarioCaptor.capture());

            BeneficiarioEntity beneficiarioSalvo = beneficiarioCaptor.getValue();
            assertEquals("Associação Esperança", beneficiarioSalvo.getNome());
            assertEquals("79977776666", beneficiarioSalvo.getTelefone());
            assertEquals("Avenida Central, 500", beneficiarioSalvo.getEndereco());
            assertEquals(TipoBeneficiario.ONG, beneficiarioSalvo.getTipoBeneficiario());
            assertEquals(NivelPrioridade.CRITICA, beneficiarioSalvo.getNivelPrioridade());

            assertNotNull(beneficiarioSalvo.getPerfis());
            assertTrue(beneficiarioSalvo.getPerfis().contains(Perfil.ROLE_BENEFICIARIO));
        }
    }

    @Nested
    @DisplayName("Testes do método listarBeneficiarios")
    class ListarBeneficiariosTests {

        @Test
        @DisplayName("Deve retornar uma lista de BeneficiarioResponseDto quando existirem registros")
        void deveRetornarListaDeBeneficiarios() {
            // Arrange
            when(beneficiarioRepository.findAll()).thenReturn(List.of(beneficiarioEntity));

            // Act
            List<BeneficiarioResponseDto> resultado = beneficiarioService.listarBeneficiarios();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(beneficiarioRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não houver beneficiários cadastrados")
        void deveRetornarListaVazia() {
            // Arrange
            when(beneficiarioRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<BeneficiarioResponseDto> resultado = beneficiarioService.listarBeneficiarios();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(beneficiarioRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar BeneficiarioResponseDto quando o ID existir")
        void deveRetornarBeneficiarioQuandoIdExistir() {
            // Arrange
            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));

            // Act
            BeneficiarioResponseDto resultado = beneficiarioService.listarPorId(1);

            // Assert
            assertNotNull(resultado);
            verify(beneficiarioRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(beneficiarioRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> beneficiarioService.listarPorId(99)
            );

            assertEquals("Doador não encontrado com ID: 99", exception.getMessage());
            verify(beneficiarioRepository).findById(99);
        }
    }

    @Nested
    @DisplayName("Testes do método atualizarBeneficiario")
    class AtualizarBeneficiarioTests {

        @Test
        @DisplayName("Deve atualizar os dados do beneficiário com sucesso")
        void deveAtualizarBeneficiarioComSucesso() {
            // Arrange
            BeneficiarioDto dtoAtualizacao = new BeneficiarioDto();
            dtoAtualizacao.setNome("Associação Esperança Editada");
            dtoAtualizacao.setTelefone("79911112222");
            dtoAtualizacao.setEndereco("Rua Nova, 10");
            dtoAtualizacao.setTipoBeneficiario(TipoBeneficiario.FAMILIA);
            dtoAtualizacao.setNivelPrioridade(NivelPrioridade.MEDIA);

            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));
            when(beneficiarioRepository.save(any(BeneficiarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            BeneficiarioResponseDto resultado = beneficiarioService.atualizarBeneficiario(1, dtoAtualizacao);

            // Assert
            assertNotNull(resultado);
            verify(beneficiarioRepository).findById(1);
            verify(beneficiarioRepository).save(beneficiarioCaptor.capture());

            BeneficiarioEntity beneficiarioAtualizado = beneficiarioCaptor.getValue();
            assertEquals("Associação Esperança Editada", beneficiarioAtualizado.getNome());
            assertEquals("79911112222", beneficiarioAtualizado.getTelefone());
            assertEquals("Rua Nova, 10", beneficiarioAtualizado.getEndereco());
            assertEquals(TipoBeneficiario.FAMILIA, beneficiarioAtualizado.getTipoBeneficiario());
            assertEquals(NivelPrioridade.MEDIA, beneficiarioAtualizado.getNivelPrioridade());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar atualizar beneficiário inexistente")
        void deveLancarExcecaoAoAtualizarInexistente() {
            // Arrange
            when(beneficiarioRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> beneficiarioService.atualizarBeneficiario(99, beneficiarioDto)
            );

            assertEquals("Beneficiario com id [99] não encontrado!", exception.getMessage());
            verify(beneficiarioRepository).findById(99);
            verify(beneficiarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método inativarBeneficiario")
    class InativarBeneficiarioTests {

        @Test
        @DisplayName("Deve alterar o status do beneficiário para INATIVO com sucesso")
        void deveInativarBeneficiarioComSucesso() {
            // Arrange
            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));

            // Act
            BeneficiarioResponseDto resultado = beneficiarioService.inativarBeneficiario(1);

            // Assert
            assertNotNull(resultado);
            assertEquals(StatusUsuario.INATIVO, beneficiarioEntity.getStatus());
            verify(beneficiarioRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o beneficiário já estiver INATIVO")
        void deveLancarExcecaoSeJaEstiverInativo() {
            // Arrange
            beneficiarioEntity.setStatus(StatusUsuario.INATIVO);
            when(beneficiarioRepository.findById(1)).thenReturn(Optional.of(beneficiarioEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> beneficiarioService.inativarBeneficiario(1)
            );

            assertEquals("Beneficiário com id [1] já está INATIVO no sistema", exception.getMessage());
            verify(beneficiarioRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o beneficiário não for encontrado")
        void deveLancarExcecaoSeBeneficiarioNaoEncontrado() {
            // Arrange
            when(beneficiarioRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> beneficiarioService.inativarBeneficiario(99)
            );

            assertEquals("Beneficiário não encontrado com o id: 99", exception.getMessage());
            verify(beneficiarioRepository).findById(99);
        }
    }
}