package com.devpedrogo.redesolidaria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.devpedrogo.redesolidaria.dto.LoginRequestDto;
import com.devpedrogo.redesolidaria.dto.TokenResponseDto;
import com.devpedrogo.redesolidaria.enums.Perfil;
import com.devpedrogo.redesolidaria.enums.StatusUsuario;
import com.devpedrogo.redesolidaria.exception.RegraDeNegocioException;
import com.devpedrogo.redesolidaria.model.UsuarioEntity;
import com.devpedrogo.redesolidaria.config.TokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private LoginRequestDto loginRequestDto;
    private UsuarioEntity usuarioEntity;
    private final long EXPIRATION_TIME = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        // Injeta a propriedade @Value("${jwt.expiration}") diretamente na instância sob teste
        ReflectionTestUtils.setField(authenticationService, "expirationTime", EXPIRATION_TIME);

        loginRequestDto = new LoginRequestDto("pedro@email.com", "123456");

        usuarioEntity = new UsuarioEntity() {};
        usuarioEntity.setId(1);
        usuarioEntity.setNome("Pedro Gouveia");
        usuarioEntity.setEmail("pedro@email.com");
        usuarioEntity.setStatus(StatusUsuario.ATIVO);
        usuarioEntity.setPerfis(Set.of(Perfil.ROLE_OPERADOR));
    }

    @Nested
    @DisplayName("Testes do método login")
    class LoginTests {

        @Test
        @DisplayName("Deve realizar login com sucesso para usuário ativo e retornar TokenResponseDto")
        void deveRealizarLoginComSucesso() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioEntity);
            when(tokenProvider.gerarToken(authentication)).thenReturn("jwt.token.mock");

            // Act
            TokenResponseDto response = authenticationService.login(loginRequestDto);

            // Assert
            assertNotNull(response);
            assertEquals("jwt.token.mock", response.token());
            assertEquals(1, response.id());
            assertEquals("Pedro Gouveia", response.nome());
            assertEquals("pedro@email.com", response.email());
            assertEquals("ROLE_OPERADOR", response.role());
            assertEquals(EXPIRATION_TIME, response.expiresIn());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider).gerarToken(authentication);
        }

        @Test
        @DisplayName("Deve priorizar ROLE_ADMIN quando o usuário tiver múltiplos perfis")
        void devePriorizarRoleAdmin() {
            // Arrange
            usuarioEntity.setPerfis(Set.of(Perfil.ROLE_OPERADOR, Perfil.ROLE_ADMIN));

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioEntity);
            when(tokenProvider.gerarToken(authentication)).thenReturn("jwt.token.mock");

            // Act
            TokenResponseDto response = authenticationService.login(loginRequestDto);

            // Assert
            assertNotNull(response);
            assertEquals("ROLE_ADMIN", response.role());
        }

        @Test
        @DisplayName("Deve utilizar ROLE_OPERADOR como fallback se a coleção de perfis estiver vazia")
        void deveUsarFallbackRoleOperadorSePerfisVazios() {
            // Arrange
            usuarioEntity.setPerfis(Set.of());

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioEntity);
            when(tokenProvider.gerarToken(authentication)).thenReturn("jwt.token.mock");

            // Act
            TokenResponseDto response = authenticationService.login(loginRequestDto);

            // Assert
            assertNotNull(response);
            assertEquals("ROLE_OPERADOR", response.role());
        }

        @Test
        @DisplayName("Deve lançar RegraDeNegocioException quando o usuário estiver INATIVO")
        void deveLancarExcecaoQuandoUsuarioEstiverInativo() {
            // Arrange
            usuarioEntity.setStatus(StatusUsuario.INATIVO);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(usuarioEntity);

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> authenticationService.login(loginRequestDto)
            );

            assertEquals("Usuario INATIVO no sistema, não é possivel logar", exception.getMessage());
        }

        @Test
        @DisplayName("Deve capturar BadCredentialsException e lançar RegraDeNegocioException com 'Credencias Inválidas'")
        void deveLancarExcecaoQuandoCredenciaisForemInvalidas() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            RegraDeNegocioException exception = assertThrows(
                    RegraDeNegocioException.class,
                    () -> authenticationService.login(loginRequestDto)
            );

            assertEquals("Credencias Inválidas", exception.getMessage());
        }
    }
}