package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IAdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminService adminService;

    @Captor
    private ArgumentCaptor<AdminEntity> adminCaptor;

    private AdminDto adminDto;
    private AdminEntity adminEntity;

    @BeforeEach
    void setUp() {
        adminDto = new AdminDto(
                "Lucas Mendes",
                "lucas@email.com",
                "123456",
                "79999990000",
                "Rua Principal, 10"
        );

        adminEntity = AdminEntity.builder()
                .nome("Lucas Mendes")
                .email("lucas@email.com")
                .senha("senha_criptografada")
                .telefone("79999990000")
                .endereco("Rua Principal, 10")
                .build();
        adminEntity.setId(1);
        adminEntity.setStatus(StatusUsuario.ATIVO);
    }

    @Nested
    @DisplayName("Testes do método cadastrarAdmin")
    class CadastrarAdminTests {

        @Test
        @DisplayName("Deve cadastrar admin com sucesso e atribuir ROLE_ADMIN")
        void deveCadastrarAdminComSucesso() {
            // Arrange
            when(usuarioRepository.existsByEmail("lucas@email.com")).thenReturn(false);
            when(passwordEncoder.encode("123456")).thenReturn("senha_criptografada");
            when(adminRepository.save(any(AdminEntity.class))).thenAnswer(invocation -> {
                AdminEntity entity = invocation.getArgument(0);
                entity.setId(1);
                return entity;
            });

            // Act
            AdminResponseDto resultado = adminService.cadastrarAdmin(adminDto);

            // Assert
            assertNotNull(resultado);
            verify(usuarioRepository).existsByEmail("lucas@email.com");
            verify(passwordEncoder).encode("123456");
            verify(adminRepository).save(adminCaptor.capture());

            AdminEntity adminSalvo = adminCaptor.getValue();
            assertEquals("Lucas Mendes", adminSalvo.getNome());
            assertEquals("lucas@email.com", adminSalvo.getEmail());
            assertEquals("senha_criptografada", adminSalvo.getSenha());
            assertTrue(adminSalvo.getPerfis().contains(Perfil.ROLE_ADMIN));
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException se o e-mail for nulo ou em branco")
        void deveLancarExcecaoQuandoEmailForInvalido() {
            // Arrange
            AdminDto dtoEmailNulo = new AdminDto("Lucas", null, "123", "79999990000", "Rua A");

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.cadastrarAdmin(dtoEmailNulo)
            );

            assertEquals("O e-mail é obrigatório para cadastrar um administrador.", exception.getMessage());
            verify(usuarioRepository, never()).existsByEmail(anyString());
            verify(adminRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o e-mail já pertencer a outro usuário")
        void deveLancarExcecaoQuandoEmailJaExistir() {
            // Arrange
            when(usuarioRepository.existsByEmail("lucas@email.com")).thenReturn(true);

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.cadastrarAdmin(adminDto)
            );

            assertEquals("E-mail já pertence a outro usuário", exception.getMessage());
            verify(usuarioRepository).existsByEmail("lucas@email.com");
            verify(passwordEncoder, never()).encode(anyString());
            verify(adminRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método listarAdmins")
    class ListarAdminsTests {

        @Test
        @DisplayName("Deve retornar lista de AdminResponseDto quando existirem registros")
        void deveRetornarListaDeAdmins() {
            // Arrange
            when(adminRepository.findAll()).thenReturn(List.of(adminEntity));

            // Act
            List<AdminResponseDto> resultado = adminService.listarAdmins();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(adminRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver administradores")
        void deveRetornarListaVazia() {
            // Arrange
            when(adminRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<AdminResponseDto> resultado = adminService.listarAdmins();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(adminRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método listarPorId")
    class ListarPorIdTests {

        @Test
        @DisplayName("Deve retornar AdminResponseDto quando o ID existir")
        void deveRetornarAdminQuandoIdExistir() {
            // Arrange
            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));

            // Act
            AdminResponseDto resultado = adminService.listarPorId(1);

            // Assert
            assertNotNull(resultado);
            verify(adminRepository).findById(1);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(adminRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> adminService.listarPorId(99)
            );

            assertEquals("Admin não encontrado com ID: 99", exception.getMessage());
            verify(adminRepository).findById(99);
        }
    }

    @Nested
    @DisplayName("Testes do método atualizarAdmin")
    class AtualizarAdminTests {

        @Test
        @DisplayName("Deve atualizar dados e re-criptografar senha quando informado novo valor")
        void deveAtualizarComSucessoEAtualizarSenha() {
            // Arrange
            AdminUpdateDto updateDto = new AdminUpdateDto("Lucas Editado", "79988887777", "Rua B", "lucas.novo@email.com", "novasenha");

            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));
            when(passwordEncoder.encode("novasenha")).thenReturn("nova_senha_cripto");
            when(adminRepository.save(any(AdminEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            AdminResponseDto resultado = adminService.atualizarAdmin(1, updateDto);

            // Assert
            assertNotNull(resultado);
            verify(passwordEncoder).encode("novasenha");
            verify(adminRepository).save(adminCaptor.capture());

            AdminEntity adminSalvo = adminCaptor.getValue();
            assertEquals("Lucas Editado", adminSalvo.getNome());
            assertEquals("nova_senha_cripto", adminSalvo.getSenha());
        }

        @Test
        @DisplayName("Deve atualizar dados sem alterar a senha quando a nova senha for nula ou em branco")
        void deveAtualizarSemAlterarSenha() {
            // Arrange
            AdminUpdateDto updateDto = new AdminUpdateDto("Lucas Editado", "79988887777", "Rua B", "lucas.novo@email.com", "");

            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));
            when(adminRepository.save(any(AdminEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            AdminResponseDto resultado = adminService.atualizarAdmin(1, updateDto);

            // Assert
            assertNotNull(resultado);
            verify(passwordEncoder, never()).encode(anyString());
            verify(adminRepository).save(adminCaptor.capture());

            AdminEntity adminSalvo = adminCaptor.getValue();
            assertEquals("senha_criptografada", adminSalvo.getSenha());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar atualizar admin inexistente")
        void deveLancarExcecaoAoAtualizarInexistente() {
            // Arrange
            AdminUpdateDto updateDto = new AdminUpdateDto("Nome", "123", "Rua", "email@email.com", "123");
            when(adminRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.atualizarAdmin(99, updateDto)
            );

            assertEquals("Admin com id [99] não encontrado!", exception.getMessage());
            verify(adminRepository).findById(99);
            verify(adminRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método inativarAdmin")
    class InativarAdminTests {

        private void mockUsuarioLogado(String email) {
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(email);
            SecurityContextHolder.setContext(securityContext);
        }

        @Test
        @DisplayName("Deve inativar admin com sucesso quando não for a própria conta e houver mais de 2 ativos")
        void deveInativarAdminComSucesso() {
            // Arrange
            mockUsuarioLogado("outro.admin@email.com");
            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));
            when(adminRepository.countByStatus(StatusUsuario.ATIVO)).thenReturn(3L);

            // Act
            AdminResponseDto resultado = adminService.inativarAdmin(1);

            // Assert
            assertNotNull(resultado);
            assertEquals(StatusUsuario.INATIVO, adminEntity.getStatus());
            verify(adminRepository).findById(1);
            verify(adminRepository).countByStatus(StatusUsuario.ATIVO);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException ao tentar inativar a própria conta")
        void deveLancarExcecaoAoTentarInativarPropriaConta() {
            // Arrange
            mockUsuarioLogado("lucas@email.com"); // Mesmo e-mail do adminEntity
            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.inativarAdmin(1)
            );

            assertEquals("Operação negada: Você não pode inativar a sua própria conta.", exception.getMessage());
            verify(adminRepository, never()).countByStatus(any());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando houver 2 ou menos administradores ativos")
        void deveLancarExcecaoQuandoQuantidadeMinimaDeAdminsAtingida() {
            // Arrange
            mockUsuarioLogado("outro.admin@email.com");
            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));
            when(adminRepository.countByStatus(StatusUsuario.ATIVO)).thenReturn(2L);

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.inativarAdmin(1)
            );

            assertEquals("Operação negada: O sistema exige o mínimo de 2 administradores ativos para segurança. Cadastre outro administrador antes de inativar este.", exception.getMessage());
            verify(adminRepository).countByStatus(StatusUsuario.ATIVO);
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o admin já estiver INATIVO")
        void deveLancarExcecaoSeJaEstiverInativo() {
            // Arrange
            adminEntity.setStatus(StatusUsuario.INATIVO);
            mockUsuarioLogado("outro.admin@email.com");
            when(adminRepository.findById(1)).thenReturn(Optional.of(adminEntity));
            when(adminRepository.countByStatus(StatusUsuario.ATIVO)).thenReturn(3L);

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.inativarAdmin(1)
            );

            assertEquals("Admin com id [1] já está INATIVO no sistema", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando admin não for encontrado")
        void deveLancarExcecaoSeAdminNaoEncontrado() {
            // Arrange
            when(adminRepository.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> adminService.inativarAdmin(99)
            );

            assertEquals("Admin não encontrado com o id: 99", exception.getMessage());
            verify(adminRepository).findById(99);
        }
    }
}