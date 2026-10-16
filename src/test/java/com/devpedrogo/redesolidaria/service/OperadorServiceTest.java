package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.dto.OperadorResponseDto;
import com.devpedrogo.redesolidaria.dto.OperadorUpdateDto;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.repository.IOperadorRepository;
import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class OperadorServiceTest {

    @Mock
    private IOperadorRepository operadorRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OperadorService operadorService;

    private OperadorEntity operadorBase;

    @BeforeEach
    void setUp() {
        operadorBase = OperadorEntity.builder()
                .id(1)
                .nome("Pedro Operador")
                .email("operador@teste.com")
                .telefone("79999999999")
                .endereco("Rua Teste, 123")
                .senha("senhaCriptografada")
                .matricula("OP-2026-1234")
                .status(StatusUsuario.ATIVO)
                .perfis(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("Testes do método cadastrarOperador")
    class CadastrarOperadorTests {

        @Test
        @DisplayName("Deve cadastrar um operador com sucesso")
        void deveCadastrarOperadorComSucesso() throws Exception {
            OperadorDto dto = new OperadorDto();
            dto.setNome("Pedro Operador");
            dto.setEmail("operador@teste.com");
            dto.setTelefone("79999999999");
            dto.setEndereco("Rua Teste, 123");
            dto.setSenha("123456");

            when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(dto.getSenha())).thenReturn("senhaCriptografada");
            when(operadorRepository.save(any(OperadorEntity.class))).thenAnswer(invocation -> {
                OperadorEntity entity = invocation.getArgument(0);
                entity.setId(1);
                return entity;
            });

            OperadorResponseDto resultado = operadorService.cadastrarOperador(dto);

            assertNotNull(resultado);
            verify(usuarioRepository).findByEmail(dto.getEmail());
            verify(passwordEncoder).encode("123456");
            verify(operadorRepository).save(any(OperadorEntity.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar cadastrar operador com e-mail já existente")
        void deveLancarExcecaoQuandoEmailJaExiste() {
            OperadorDto dto = new OperadorDto();
            dto.setEmail("existente@teste.com");

            when(usuarioRepository.findByEmail(dto.getEmail()))
                    .thenReturn(Optional.of(new OperadorEntity()));

            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> operadorService.cadastrarOperador(dto)
            );

            assertEquals("Já existe um usuário cadastrado com este e-mail.", exception.getMessage());
            verify(operadorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método listarOperadores")
    class ListarOperadoresTests {

        @Test
        @DisplayName("Deve retornar uma lista de operadores")
        void deveRetornarListaDeOperadores() {
            when(operadorRepository.findAll()).thenReturn(List.of(operadorBase));

            List<OperadorResponseDto> resultado = operadorService.listarOperadores();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(operadorRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar operador quando ID existir")
        void deveRetornarOperadorQuandoIdExistir() {
            when(operadorRepository.findById(1)).thenReturn(Optional.of(operadorBase));

            OperadorResponseDto resultado = operadorService.listarPorId(1);

            assertNotNull(resultado);
            verify(operadorRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando ID não existir")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            when(operadorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(
                    EntityNotFoundException.class,
                    () -> operadorService.listarPorId(99)
            );
        }
    }

    @Nested
    @DisplayName("Testes do método atualizarOperador")
    class AtualizarOperadorTests {

        @Test
        @DisplayName("Deve atualizar dados do operador com sucesso")
        void deveAtualizarOperadorComSucesso() {
            OperadorUpdateDto updateDto = new OperadorUpdateDto(
                    "Pedro Atualizado",
                    "79888888888",
                    "Rua Nova, 456",
                    "novo@teste.com",
                    "novasenha"
            );

            when(operadorRepository.findById(1)).thenReturn(Optional.of(operadorBase));
            when(passwordEncoder.encode("novasenha")).thenReturn("senhaNovaCriptografada");
            when(operadorRepository.save(any(OperadorEntity.class))).thenAnswer(i -> i.getArgument(0));

            OperadorResponseDto resultado = operadorService.atualizarOperador(1, updateDto);

            assertNotNull(resultado);
            verify(operadorRepository).findById(1);
            verify(passwordEncoder).encode("novasenha");
            verify(operadorRepository).save(operadorBase);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar operador inexistente")
        void deveLancarExcecaoAoAtualizarInexistente() {
            OperadorUpdateDto updateDto = new OperadorUpdateDto(
                    "Nome", "Telefone", "Endereco", "email@teste.com", "senha"
            );

            when(operadorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(
                    RegraDeNegocioException.class,
                    () -> operadorService.atualizarOperador(99, updateDto)
            );
            verify(operadorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método inativarOperador")
    class InativarOperadorTests {

        @Test
        @DisplayName("Deve inativar operador com sucesso")
        void deveInativarOperadorComSucesso() {
            when(operadorRepository.findById(1)).thenReturn(Optional.of(operadorBase));

            OperadorResponseDto resultado = operadorService.inativarOperador(1);

            assertNotNull(resultado);
            assertEquals(StatusUsuario.INATIVO, operadorBase.getStatus());
            verify(operadorRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar operador que já está inativo")
        void deveLancarExcecaoAoInativarJaInativo() {
            operadorBase.setStatus(StatusUsuario.INATIVO);
            when(operadorRepository.findById(1)).thenReturn(Optional.of(operadorBase));

            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> operadorService.inativarOperador(1)
            );

            assertTrue(exception.getMessage().contains("já está INATIVO"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar operador não encontrado")
        void deveLancarExcecaoAoInativarNaoEncontrado() {
            when(operadorRepository.findById(99)).thenReturn(Optional.empty());

            assertThrows(
                    RegraDeNegocioException.class,
                    () -> operadorService.inativarOperador(99)
            );
        }
    }
}