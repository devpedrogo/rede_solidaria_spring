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

import com.devpedrogo.redesolidaria.dto.DoadorDto;
import com.devpedrogo.redesolidaria.dto.DoadorResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.DoadorEntity;
import com.devpedrogo.redesolidaria.repository.IDoadorRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class DoadorServiceTest {

    @Mock
    private IDoadorRepository doadorRepository;

    @InjectMocks
    private DoadorService doadorService;

    @Captor
    private ArgumentCaptor<DoadorEntity> doadorCaptor;

    private DoadorDto doadorDto;
    private DoadorEntity doadorEntity;

    @BeforeEach
    void setUp() {
        doadorDto = new DoadorDto();
        doadorDto.setNome("Maria Silva");
        doadorDto.setTelefone("79988887777");
        doadorDto.setEndereco("Rua das Flores, 100");

        doadorEntity = DoadorEntity.builder()
                .nome("Maria Silva")
                .telefone("79988887777")
                .endereco("Rua das Flores, 100")
                .build();
        doadorEntity.setId(1);
        doadorEntity.setStatus(StatusUsuario.ATIVO);
    }

    @Nested
    @DisplayName("Testes do método criarDoador")
    class CriarDoadorTests {

        @Test
        @DisplayName("Deve criar um doador com sucesso e atribuir o perfil ROLE_DOADOR")
        void deveCriarDoadorComSucesso() throws Exception {
            // Arrange
            when(doadorRepository.save(any(DoadorEntity.class))).thenAnswer(invocation -> {
                DoadorEntity entity = invocation.getArgument(0);
                entity.setId(1);
                return entity;
            });

            // Act
            DoadorResponseDto resultado = doadorService.criarDoador(doadorDto);

            // Assert
            assertNotNull(resultado);
            verify(doadorRepository).save(doadorCaptor.capture());

            DoadorEntity doadorSalvo = doadorCaptor.getValue();
            assertEquals("Maria Silva", doadorSalvo.getNome());
            assertEquals("79988887777", doadorSalvo.getTelefone());
            assertEquals("Rua das Flores, 100", doadorSalvo.getEndereco());

            assertNotNull(doadorSalvo.getPerfis());
            assertTrue(doadorSalvo.getPerfis().contains(Perfil.ROLE_DOADOR));
        }
    }

    @Nested
    @DisplayName("Testes do método listarDoadores")
    class ListarDoadoresTests {

        @Test
        @DisplayName("Deve retornar uma lista de DoadorResponseDto quando existirem registros")
        void deveRetornarListaDeDoadores() {
            // Arrange
            when(doadorRepository.findAll()).thenReturn(List.of(doadorEntity));

            // Act
            List<DoadorResponseDto> resultado = doadorService.listarDoadores();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(doadorRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia quando não existirem registros")
        void deveRetornarListaVaziaQuandoNaoHouverDoadores() {
            // Arrange
            when(doadorRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<DoadorResponseDto> resultado = doadorService.listarDoadores();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(doadorRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar DoadorResponseDto quando encontrar o ID informado")
        void deveRetornarDoadorQuandoIdExistir() {
            // Arrange
            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));

            // Act
            DoadorResponseDto resultado = doadorService.listarPorId(1);

            // Assert
            assertNotNull(resultado);
            verify(doadorRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não existir")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(doadorRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> doadorService.listarPorId(99)
            );

            assertEquals("Doador não encontrado com ID: 99", exception.getMessage());
            verify(doadorRepository).findById(99);
        }
    }

    @Nested
    @DisplayName("Testes do método atualizarDoador")
    class AtualizarDoadorTests {

        @Test
        @DisplayName("Deve atualizar os dados do doador com sucesso")
        void deveAtualizarDoadorComSucesso() {
            // Arrange
            DoadorDto dtoAtualizacao = new DoadorDto();
            dtoAtualizacao.setNome("Maria Silva Editada");
            dtoAtualizacao.setTelefone("79999998888");
            dtoAtualizacao.setEndereco("Nova Rua, 200");

            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));
            when(doadorRepository.save(any(DoadorEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            DoadorResponseDto resultado = doadorService.atualizarDoador(1, dtoAtualizacao);

            // Assert
            assertNotNull(resultado);
            verify(doadorRepository).findById(1);
            verify(doadorRepository).save(doadorCaptor.capture());

            DoadorEntity doadorAtualizado = doadorCaptor.getValue();
            assertEquals("Maria Silva Editada", doadorAtualizado.getNome());
            assertEquals("79999998888", doadorAtualizado.getTelefone());
            assertEquals("Nova Rua, 200", doadorAtualizado.getEndereco());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar atualizar um doador inexistente")
        void deveLancarExcecaoAoAtualizarDoadorInexistente() {
            // Arrange
            when(doadorRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> doadorService.atualizarDoador(99, doadorDto)
            );

            assertEquals("Doador com id [99] não encontrado!", exception.getMessage());
            verify(doadorRepository).findById(99);
            verify(doadorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método inativarDoador")
    class InativarDoadorTests {

        @Test
        @DisplayName("Deve alterar o status para INATIVO quando o doador estiver ATIVO")
        void deveInativarDoadorComSucesso() {
            // Arrange
            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));

            // Act
            DoadorResponseDto resultado = doadorService.inativarDoador(1);

            // Assert
            assertNotNull(resultado);
            assertEquals(StatusUsuario.INATIVO, doadorEntity.getStatus());
            verify(doadorRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o doador já estiver INATIVO")
        void deveLancarExcecaoSeJaEstiverInativo() {
            // Arrange
            doadorEntity.setStatus(StatusUsuario.INATIVO);
            when(doadorRepository.findById(1)).thenReturn(Optional.of(doadorEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> doadorService.inativarDoador(1)
            );

            assertEquals("Doador com id [1] já está INATIVO no sistema", exception.getMessage());
            verify(doadorRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o doador não for encontrado")
        void deveLancarExcecaoSeDoadorNaoEncontrado() {
            // Arrange
            when(doadorRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> doadorService.inativarDoador(99)
            );

            assertEquals("Doador não encontrado com o id: 99", exception.getMessage());
            verify(doadorRepository).findById(99);
        }
    }
}